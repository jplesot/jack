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

import com.android.jack.ir.ast.JAbstractMethodBody;
import com.android.jack.ir.ast.JMethod;
import com.android.jack.ir.ast.JTypeLookupException;
import com.android.jack.lookup.JMethodLookupException;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * Common interface for {@link Node} representing a {@link JMethod}.
 */
public interface MethodNode extends Node {

  @CheckForNull
  JAbstractMethodBody loadBody(@Nonnull JMethod loading) throws JTypeLookupException,
      JMethodLookupException;

  void loadAnnotations(@Nonnull JMethod loading);

  @Nonnull
  NodeLevel getLevel();

  @Nonnull
  ParameterNode getParameterNode(@Nonnegative int parameterNodeIndex);

}
