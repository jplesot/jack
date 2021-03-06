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

package com.android.jack.jayce;

import com.android.jack.LibraryException;
import com.android.jack.ir.ast.JAnnotationType;
import com.android.jack.ir.ast.JMethod;
import com.android.jack.ir.ast.JNode;
import com.android.jack.ir.ast.JSession;
import com.android.jack.library.HasInputLibrary;
import com.android.jack.library.InputLibrary;
import com.android.jack.library.LibraryFormatException;
import com.android.jack.library.LibraryIOException;
import com.android.jack.load.JackLoadingException;
import com.android.jack.load.MethodLoader;
import com.android.jack.lookup.JLookupException;
import com.android.sched.marker.Marker;
import com.android.sched.util.location.Location;
import com.android.sched.util.log.stats.Counter;
import com.android.sched.util.log.stats.CounterImpl;
import com.android.sched.util.log.stats.StatisticId;

import java.lang.ref.SoftReference;

import javax.annotation.Nonnull;

/**
 * A loader for method loaded from a jayce file.
 */
public class JayceMethodLoader implements MethodLoader, HasInputLibrary {
  @Nonnull
  private static final StatisticId<Counter> BODY_LOAD_COUNT = new StatisticId<Counter>(
      "jack.nnode-to-jnode.body", "Body converted from a NNode in a JNode",
          CounterImpl.class, Counter.class);

  @Nonnull
  private final JayceClassOrInterfaceLoader enclosingClassLoader;

  @Nonnull
  private SoftReference<MethodNode> nnode;

  @Nonnull
  private final String methodId;

  private boolean isBodyLoaded = false;

  private boolean isAnnotationsLoaded = false;

  public JayceMethodLoader(@Nonnull MethodNode nnode, @Nonnull String methodId,
      @Nonnull JayceClassOrInterfaceLoader enclosingClassLoader) {
    this.enclosingClassLoader = enclosingClassLoader;
    this.nnode = new SoftReference<MethodNode>(nnode);
    this.methodId = methodId;
  }

  @Override
  public void ensureBody(@Nonnull JMethod loaded) {
    synchronized (this) {
      if (isBodyLoaded) {
        return;
      }
      MethodNode methodNode;
      try {
        methodNode = getNNode(NodeLevel.FULL);
      } catch (LibraryException e) {
        throw new JackLoadingException(getLocation(loaded), e);
      }
      JNode body;
      try {
        body = methodNode.loadBody(loaded, this);
      } catch (JLookupException e) {
        throw new JackLoadingException(getLocation(loaded), e);
      }
      if (body != null) {
        body.updateParents(loaded);
      }
      isBodyLoaded = true;
      enclosingClassLoader.tracer.getStatistic(BODY_LOAD_COUNT).incValue();
      if (isAnnotationsLoaded) {
        loaded.removeLoader();
      }
    }
  }

  @Override
  public void ensureAnnotations(@Nonnull JMethod loaded) {
    synchronized (this) {
      if (isAnnotationsLoaded) {
        return;
      }
      MethodNode node;
      try {
        node = getNNode(NodeLevel.STRUCTURE);
        node.loadAnnotations(loaded, this);
      } catch (LibraryException e) {
        throw new JackLoadingException(getLocation(loaded), e);
      }
      isAnnotationsLoaded = true;
      if (isBodyLoaded) {
        loaded.removeLoader();
      }
    }
  }

  @Nonnull
  MethodNode getNNode(@Nonnull NodeLevel minimumLevel) throws LibraryFormatException,
      LibraryIOException {
    MethodNode methodNode = nnode.get();
    if (methodNode == null || !methodNode.getLevel().keep(minimumLevel)) {
      DeclaredTypeNode declaredTypeNode = enclosingClassLoader.getNNode(minimumLevel);
      methodNode = declaredTypeNode.getMethodNode(methodId);
      nnode = new SoftReference<MethodNode>(methodNode);
    }
    return methodNode;
  }

  @Override
  @Nonnull
  public Location getLocation(@Nonnull JMethod loaded) {
    return enclosingClassLoader.getLocation();
  }

  @Override
  public void ensureMarkers(@Nonnull JMethod loaded) {
    // Nothing to do, markers are loaded at creation.
  }

  @Override
  @Nonnull
  public InputLibrary getInputLibrary() {
    return enclosingClassLoader.getInputLibrary();
  }

  @Override
  public void ensureMarker(@Nonnull JMethod loaded, @Nonnull Class<? extends Marker> cls) {
    ensureMarkers(loaded);
  }

  @Override
  public void ensureAnnotation(@Nonnull JMethod loaded, @Nonnull JAnnotationType annotation) {
    ensureAnnotations(loaded);
  }

  @Nonnull
  public JSession getSession() {
    return enclosingClassLoader.getSession();
  }
}
