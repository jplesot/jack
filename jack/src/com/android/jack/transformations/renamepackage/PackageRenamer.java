/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.jack.transformations.renamepackage;

import com.android.jack.JackAbortException;
import com.android.jack.backend.dex.TypeReferenceCollector;
import com.android.jack.ir.ast.JAbstractStringLiteral;
import com.android.jack.ir.ast.JAnnotation;
import com.android.jack.ir.ast.JDefinedClassOrInterface;
import com.android.jack.ir.ast.JMethod;
import com.android.jack.ir.ast.JNode;
import com.android.jack.ir.ast.JPackage;
import com.android.jack.ir.ast.JPhantomClassOrInterface;
import com.android.jack.ir.ast.JSession;
import com.android.jack.ir.ast.JStringLiteral;
import com.android.jack.ir.ast.JType;
import com.android.jack.ir.ast.JVisitor;
import com.android.jack.ir.ast.Resource;
import com.android.jack.ir.formatter.BinaryQualifiedNameFormatter;
import com.android.jack.library.DumpInLibrary;
import com.android.jack.lookup.JLookup;
import com.android.jack.lookup.JPhantomLookup;
import com.android.jack.reporting.Reporter.Severity;
import com.android.jack.transformations.Jarjar;
import com.android.jack.transformations.request.ChangeEnclosingPackage;
import com.android.jack.transformations.request.Replace;
import com.android.jack.transformations.request.TransformationRequest;
import com.android.jack.util.NamingTools;
import com.android.sched.item.Description;
import com.android.sched.item.Name;
import com.android.sched.schedulable.RunnableSchedulable;
import com.android.sched.schedulable.Support;
import com.android.sched.schedulable.Transform;
import com.android.sched.util.codec.ReaderFileCodec;
import com.android.sched.util.config.HasKeyId;
import com.android.sched.util.config.ThreadConfig;
import com.android.sched.util.config.id.BooleanPropertyId;
import com.android.sched.util.config.id.ListPropertyId;
import com.android.sched.util.file.ReaderFile;
import com.android.sched.util.location.FileLocation;
import com.android.sched.util.log.LoggerFactory;
import com.android.sched.vfs.VPath;
import com.tonicsystems.jarjar.PackageRemapper;
import com.tonicsystems.jarjar.PatternElement;
import com.tonicsystems.jarjar.RulesFileParser;
import com.tonicsystems.jarjar.Wildcard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

/**
 * This {@code RunnableSchedulable} uses jarjar rules file of modules to rename packages.
 */
@HasKeyId
@Description("Uses jarjar rules file of modules to rename packages.")
@Name("PackageRenamer")
@Support(Jarjar.class)
@Transform(add = {JStringLiteral.class, JPackage.class}, modify = JDefinedClassOrInterface.class)
public class PackageRenamer implements RunnableSchedulable<JSession>{
  @Nonnull
  private static final Logger logger = LoggerFactory.getLogger();

  @Nonnull
  public static final BooleanPropertyId JARJAR_ENABLED = BooleanPropertyId.create(
      "jack.repackaging", "Enable repackaging")
      .addDefaultValue(false).addCategory(DumpInLibrary.class);

  @Nonnull
  public static final ListPropertyId<ReaderFile> JARJAR_FILES =
      new ListPropertyId<ReaderFile>("jack.repackaging.files", "Jarjar rules files",
          new ReaderFileCodec().allowCharset()).requiredIf(JARJAR_ENABLED.getValue().isTrue());

  @Nonnull
  private final List<ReaderFile> jarjarRulesFiles = ThreadConfig.get(JARJAR_FILES);

  private static class Visitor extends JVisitor {

    @Nonnull
    private final PackageRemapper remapper;
    @Nonnull
    private final Stack<JNode> transformationRequestRoot = new Stack<JNode>();

    @Nonnull
    private final JLookup lookup;

    public Visitor(@Nonnull JLookup lookup, @Nonnull PackageRemapper remapper) {
      this.lookup = lookup;
      this.remapper = remapper;
    }

    @Override
    public void endVisit(@Nonnull JDefinedClassOrInterface type) {
      String binaryName =
          remapper.mapValue(BinaryQualifiedNameFormatter.getFormatter().getName(type));
      String simpleName = NamingTools.getSimpleClassNameFromBinaryName(binaryName);
      type.setName(simpleName);
      lookup.removeType(type);
      type.getEnclosingPackage().remove(type);
      String packageName = NamingTools.getPackageNameFromBinaryName(binaryName);
      JPackage newPackage = lookup.getOrCreatePackage(packageName);
      type.setEnclosingPackage(newPackage);
      newPackage.addType(type);
    }

    @Override
    public boolean visit(@Nonnull JAnnotation annotationLiteral) {
      transformationRequestRoot.push(annotationLiteral);
      return super.visit(annotationLiteral);
    }

    @Override
    public void endVisit(@Nonnull JAnnotation annotation) {
      assert annotation == transformationRequestRoot.peek();
      transformationRequestRoot.pop();
      super.endVisit(annotation);
    }

    @Override
    public boolean visit(@Nonnull JMethod method) {
      transformationRequestRoot.push(method);
      return super.visit(method);
    }

    @Override
    public void endVisit(@Nonnull JMethod x) {
      assert x == transformationRequestRoot.peek();
      transformationRequestRoot.pop();
      super.endVisit(x);
    }

    @Override
    public void endVisit(@Nonnull JAbstractStringLiteral x) {
      assert !transformationRequestRoot.isEmpty();

      TransformationRequest tr = new TransformationRequest(transformationRequestRoot.peek());
      String newValue = remapper.mapValue(x.getValue());
      tr.append(new Replace(x, new JStringLiteral(x.getSourceInfo(), newValue)));
      tr.commit();

      super.endVisit(x);
    }
  }

  @Override
  public void run(@Nonnull JSession session) {
    List<PatternElement> result = new ArrayList<PatternElement>();
    for (ReaderFile jarjarFile : jarjarRulesFiles) {
      try {
        result.addAll(RulesFileParser.parse(jarjarFile));
        jarjarFile.getBufferedReader().close();
      } catch (IllegalArgumentException e) {
        PackageRenamingParsingException ex =
            new PackageRenamingParsingException((FileLocation) jarjarFile.getLocation(), e);
        session.getReporter().report(Severity.FATAL, ex);
        throw new JackAbortException(ex);
      } catch (IOException e) {
        logger.log(Level.WARNING, "Failed to close ''{0}''", jarjarFile.getPath());
      }
    }
    List<Wildcard> wildcards = PatternElement.createWildcards(result);
    PackageRemapper remapper = new PackageRemapper(wildcards);

    Collection<JDefinedClassOrInterface> typesToEmit = session.getTypesToEmit();

    final Collection<JDefinedClassOrInterface> typesToVisit =
        new HashSet<JDefinedClassOrInterface>(typesToEmit);
    final Collection<JPhantomClassOrInterface> phantomsToRemap =
        new HashSet<JPhantomClassOrInterface>();

    new TypeReferenceCollector() {
      @Override
      protected void collect(@Nonnull JType type) {
        if (type instanceof JDefinedClassOrInterface) {
          typesToVisit.add((JDefinedClassOrInterface) type);
        } else if (type instanceof JPhantomClassOrInterface) {
          phantomsToRemap.add((JPhantomClassOrInterface) type);
        }
      }
    }.accept(typesToEmit);

    new Visitor(session.getLookup(), remapper).accept(typesToVisit);

    JPhantomLookup phantomLookup = session.getPhantomLookup();
    TransformationRequest request = new TransformationRequest(session);
    for (JPhantomClassOrInterface jPhantomClassOrInterface : phantomsToRemap) {
      remapPhantom(jPhantomClassOrInterface, remapper, phantomLookup, request);
    }
    request.commit();

    for (Resource res : session.getResources()) {
      String pathToTransform = res.getPath().getPathAsString('/');
      String transformedPath = remapper.mapValue(pathToTransform);
      res.setPath(new VPath(transformedPath, '/'));
    }
  }

  private void remapPhantom(
      @Nonnull JPhantomClassOrInterface type,
      @Nonnull PackageRemapper remapper,
      @Nonnull JPhantomLookup lookup,
      @Nonnull TransformationRequest request) {
    String binaryName = remapper.mapValue(
        BinaryQualifiedNameFormatter.getFormatter().getName(type));
    String simpleName = NamingTools.getSimpleClassNameFromBinaryName(binaryName);
    type.setName(simpleName);
    lookup.removeType(type);
    String packageName = NamingTools.getPackageNameFromBinaryName(binaryName);
    JPackage newPackage = lookup.getOrCreatePackage(packageName);
    request.append(new ChangeEnclosingPackage(type, newPackage));

  }
}
