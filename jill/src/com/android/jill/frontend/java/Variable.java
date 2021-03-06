/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.android.jill.frontend.java;

import org.objectweb.asm.Type;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * Represents variables that are used by Jill to represent local and stack items.
 */
public class Variable {

  private static final int NO_LOCAL_IDX = -1;

  @Nonnull
  private final Type type;

  @Nonnull
  private final String id;

  @CheckForNull
  private final String signature;

  @Nonnull
  private String name;

  private boolean isThis;

  private boolean isParameter;

  private final boolean isSynthetic;

  /* Index of local variable, -1 means the variable does not represent a local variable. */
  private final int localIdx;

  public Variable(@Nonnull String id, @Nonnull String name, @Nonnull Type type,
      @CheckForNull String signature) {
    this(id, name, type, signature, NO_LOCAL_IDX, true);
  }

  public Variable(@Nonnull String id, @Nonnull String name, @Nonnull Type type,
      @CheckForNull String signature, int localIdx, boolean isSynthetic) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.signature = signature;
    this.localIdx = localIdx;
    isThis = false;
    isParameter = false;
    this.isSynthetic = isSynthetic;
  }

  public boolean hasLocalIndex() {
    return localIdx != NO_LOCAL_IDX;
  }

  @Nonnegative
  public int getLocalIndex() {
    assert hasLocalIndex();
    return localIdx;
  }

  @Nonnull
  public Type getType() {
    return type;
  }

  public void setName(@Nonnull String name) {
    this.name = name;
  }

  public boolean hasSignature() {
    return signature != null;
  }

  @Nonnull
  public String getSignature() {
    assert signature != null;
    return signature;
  }

  @Nonnull
  public String getName() {
    return name;
  }

  @Nonnull
  public String getId() {
    return id;
  }

  public boolean isThis() {
    return isThis;
  }

  public void setThis() {
    this.isThis = true;
  }

  public boolean isParameter() {
    return isParameter;
  }

  public void setParameter() {
    this.isParameter = true;
  }

  public boolean isSynthetic() {
    return isSynthetic;
  }
}
