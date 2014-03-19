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

package com.android.jack.transformations.request;

import com.android.jack.ir.ast.Annotable;
import com.android.jack.ir.ast.JAnnotationLiteral;
import com.android.jack.ir.ast.JNode;
import com.android.sched.transform.TransformStep;

import javax.annotation.Nonnull;

/**
 * A {@code TransformationStep} allowing to add a {@link JAnnotationLiteral}
 * as an annotation of a {@link Annotable}.
 */
public class AddAnnotation implements TransformationStep, TransformStep {
  @Nonnull
  private final JAnnotationLiteral annotation;
  @Nonnull
  private final Annotable annotable;

  public AddAnnotation(@Nonnull JAnnotationLiteral annotation, @Nonnull Annotable annotable) {
    this.annotation = annotation;
    this.annotable = annotable;
  }

  @Override
  public void apply() throws UnsupportedOperationException {
    annotable.addAnnotation(annotation);
    annotation.updateParents((JNode) annotable);
  }

  @Override
  @Nonnull
  public String toString() {
    StringBuilder sb = new StringBuilder("Add ");
    sb.append(annotation.toSource());
    sb.append(" on ");
    sb.append(((JNode) annotable).toSource());
    return sb.toString();
  }
}
