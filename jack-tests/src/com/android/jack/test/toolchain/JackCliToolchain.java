/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.android.jack.test.toolchain;

import com.google.common.base.Joiner;

import com.android.jack.Options;
import com.android.jack.Options.VerbosityLevel;
import com.android.jack.test.util.ExecFileException;
import com.android.jack.test.util.ExecuteFile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * This class implements a {@link JackBasedToolchain} by calling Jack via command line.
 */
public class JackCliToolchain extends JackBasedToolchain {

  @Nonnull
  protected File jackPrebuilt;

  @Nonnull
  protected List<String> extraJackArgs = new ArrayList<String>(0);
  @CheckForNull
  protected File incrementalFolder;
  @Nonnull
  protected Options.VerbosityLevel verbosityLevel = VerbosityLevel.WARNING;
  @Nonnull
  protected final Map<String, String> properties = new HashMap<String, String>();

  protected boolean sanityChecks = true;

  JackCliToolchain(@Nonnull File prebuilt) {
    this.jackPrebuilt = prebuilt;
    addProperty(Options.USE_DEFAULT_LIBRARIES.getName(), "false");
  }

  @Override
  @Nonnull
  public JackCliToolchain setVerbose(boolean isVerbose) {
    super.setVerbose(isVerbose);
    verbosityLevel = isVerbose ? VerbosityLevel.INFO : VerbosityLevel.WARNING;
    return this;
  }

  @Override
  public void srcToExe(@Nonnull File out, boolean zipFile, @Nonnull File... sources)
      throws Exception {

    List<String> args = new ArrayList<String>();

    srcToCommon(args, sources);

    if (zipFile) {
      args.add("--output-dex-zip");
    } else {
      args.add("--output-dex");
    }
    args.add(out.getAbsolutePath());

    args.addAll(extraJackArgs);

    if (withDebugInfos) {
      args.add("-g");
    }

    AbstractTestTools.addFile(args, /* mustExist = */ false, sources);

    ExecuteFile exec = new ExecuteFile(args.toArray(new String[args.size()]));
    exec.setErr(errRedirectStream);
    exec.setOut(outRedirectStream);
    exec.setVerbose(isVerbose);

    try {
      if (exec.run() != 0) {
        throw new RuntimeException("Jack compiler exited with an error");
      }
    } catch (ExecFileException e) {
      throw new RuntimeException("An error occurred while running Jack", e);
    }
  }

  @Override
  public void srcToLib(@Nonnull File out, boolean zipFiles, @Nonnull File... sources)
      throws Exception {

    List<String> args = new ArrayList<String>();

    srcToCommon(args, sources);

    if (zipFiles) {
      args.add("--output-jack");
    } else {
      args.add("--output-jack-dir");
    }
    args.add(out.getAbsolutePath());

    AbstractTestTools.addFile(args, /* mustExist = */ false, sources);

    ExecuteFile exec = new ExecuteFile(args.toArray(new String[args.size()]));
    exec.setErr(errRedirectStream);
    exec.setOut(outRedirectStream);
    exec.setVerbose(isVerbose);

    try {
      if (exec.run() != 0) {
        throw new RuntimeException("Jack compiler exited with an error");
      }
    } catch (ExecFileException e) {
      throw new RuntimeException("An error occurred while running Jack", e);
    }
  }

  private void srcToCommon(@Nonnull List<String> args, @Nonnull File... sources) {
    boolean assertEnable = false;
    assert true == (assertEnable = true);

    args.add("java");
    args.add(assertEnable ? "-ea" : "-da");
    args.add("-jar");
    args.add(jackPrebuilt.getAbsolutePath());

    args.add("--verbose");
    args.add(verbosityLevel.name());

    args.add("--sanity-checks");
    args.add(Boolean.toString(sanityChecks));

    if (incrementalFolder != null) {
      args.add("--incremental-folder");
      args.add(incrementalFolder.getAbsolutePath());
    }

    addProperties(properties, args);

    if (classpath.size() > 0) {
      args.add("--classpath");
      args.add(getClasspathAsString());
    }

    for (File res : resImport) {
      args.add("--import-resource");
      args.add(res.getPath());
    }

    for (File meta : metaImport) {
      args.add("--import-meta");
      args.add(meta.getPath());
    }

    args.addAll(extraJackArgs);

    for (File jarjarFile : jarjarRules) {
      args.add("--config-jarjar");
      args.add(jarjarFile.getAbsolutePath());
    }

    for (File flags : proguardFlags) {
      args.add("--config-proguard");
      args.add(flags.getAbsolutePath());
    }

    if (withDebugInfos) {
      args.add("-g");
    }

    addAnnotationProcessorArgs(args);

    for (File staticLib : staticLibs) {
      args.add("--import");
      args.add(staticLib.getAbsolutePath());
    }
  }

  @Override
  public void libToExe(@Nonnull File[] in, @Nonnull File out, boolean zipFile) throws Exception {
    List<String> args = new ArrayList<String>();

    libToCommon(args, getClasspathAsString(), in);

    if (zipFile) {
      args.add("--output-dex-zip");
    } else {
      args.add("--output-dex");
    }

    args.add(out.getAbsolutePath());

    ExecuteFile exec = new ExecuteFile(args.toArray(new String[args.size()]));
    exec.setErr(errRedirectStream);
    exec.setOut(outRedirectStream);
    exec.setVerbose(isVerbose);

    try {
      if (exec.run() != 0) {
        throw new RuntimeException("Jack compiler exited with an error");
      }
    } catch (ExecFileException e) {
      throw new RuntimeException("An error occurred while running Jack", e);
    }
  }

  @Override
  public void libToLib(@Nonnull File[] in, @Nonnull File out, boolean zipFiles) throws Exception {
    List<String> args = new ArrayList<String>();

    libToCommon(args, getClasspathAsString(), in);

    if (zipFiles) {
      args.add("--output-jack");
    } else {
      args.add("--output-jack-dir");
    }
    args.add(out.getAbsolutePath());

    ExecuteFile exec = new ExecuteFile(args.toArray(new String[args.size()]));
    exec.setErr(errRedirectStream);
    exec.setOut(outRedirectStream);
    exec.setVerbose(isVerbose);

    try {
      if (exec.run() != 0) {
        throw new RuntimeException("Jack compiler exited with an error");
      }
    } catch (ExecFileException e) {
      throw new RuntimeException("An error occurred while running Jack", e);
    }
  }

  protected void libToCommon(@Nonnull List<String> args, @Nonnull String classpath,
      @Nonnull File[] in) throws Exception {
    boolean assertEnable = false;
    assert true == (assertEnable = true);

    args.add("java");
    args.add(assertEnable ? "-ea" : "-da");
    args.add("-jar");
    args.add(jackPrebuilt.getAbsolutePath());

    args.add("--verbose");
    args.add(verbosityLevel.name());

    args.add("--sanity-checks");
    args.add(Boolean.toString(sanityChecks));

    if (incrementalFolder != null) {
      args.add("--incremental-folder");
      args.add(incrementalFolder.getAbsolutePath());
    }

    for (File res : resImport) {
      args.add("--import-resource");
      args.add(res.getPath());
    }

    for (File meta : metaImport) {
      args.add("--import-meta");
      args.add(meta.getPath());
    }

    addProperties(properties, args);

    if (!classpath.equals("")) {
      args.add("--classpath");
      args.add(classpath);
    }

    for (File jarjarFile : jarjarRules) {
      args.add("--config-jarjar");
      args.add(jarjarFile.getAbsolutePath());
    }

    for (File flags : proguardFlags) {
      args.add("--config-proguard");
      args.add(flags.getAbsolutePath());
    }

    if (withDebugInfos) {
      args.add("-g");
    }

    libToImportStaticLibs(args, in);

  }

  protected void libToImportStaticLibs(@Nonnull List<String> args, @Nonnull File[] in)
      throws Exception {
    for (File staticlib : in) {
      args.add("--import");
      args.add(staticlib.getAbsolutePath());
    }

    for (File staticLib : staticLibs) {
      args.add("--import");
      args.add(staticLib.getAbsolutePath());
    }
  }

  @Nonnull
  public JackCliToolchain addJackArg(@Nonnull String arg) {
    extraJackArgs.add(arg);
    return this;
  }

  @Override
  @Nonnull
  public JackCliToolchain setIncrementalFolder(@Nonnull File incrementalFolder) {
    this.incrementalFolder = incrementalFolder;
    return this;
  }

  @Override
  @Nonnull
  public JackBasedToolchain addProperty(@Nonnull String propertyName,
      @Nonnull String propertyValue) {
    properties.put(propertyName, propertyValue);
    return this;
  }

  protected static void addProperties(@Nonnull Map<String, String> properties,
      @Nonnull List<String> args) {
    for (Entry<String, String> entry : properties.entrySet()) {
      args.add("-D");
      args.add(entry.getKey() + "=" + entry.getValue());
    }
  }

  @Override
  @Nonnull
  public JackBasedToolchain setSanityChecks(boolean sanityChecks){
    this.sanityChecks = sanityChecks;
    return this;
  }

  private void addAnnotationProcessorArgs(@Nonnull List<String> args) {
    for (Entry<String, String> entry : annotationProcessorOptions.entrySet()) {
        args.add("-A");
        args.add(entry.getKey() + "=" + entry.getValue());
      }

    if (annotationProcessorClasses != null) {
      args.add("--processor");
      args.add(Joiner.on(',').join(annotationProcessorClasses));
    }

    if (processorPath != null) {
        args.add("--processorpath");
        args.add(processorPath);
    }
  }

}
