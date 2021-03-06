/*
 * Copyright 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.android.jack.ir.ast;

import com.android.jack.ir.JNodeInternalError;
import com.android.jack.ir.sourceinfo.SourceInfo;
import com.android.sched.item.Component;
import com.android.sched.item.Description;
import com.android.sched.scheduler.ScheduleInstance;
import com.android.sched.transform.TransformRequest;

import javax.annotation.Nonnull;

/**
 * Java class literal expression.
 *
 * NOTE: This class is modeled as if it were a JFieldRef to a field declared in
 * ClassLiteralHolder. That field contains the class object allocation
 * initializer.
 */
@Description("Java class literal expression")
public class JClassLiteral extends JLiteral {

  @Nonnull
  private final JClass javaLangClass;

  @Nonnull
  private final JType refType;

  public JClassLiteral(@Nonnull SourceInfo sourceInfo, @Nonnull JType type,
      @Nonnull JClass javaLangClass) {
    super(sourceInfo);
    assert type != null;
    assert javaLangClass != null;
    refType = type;
    this.javaLangClass = javaLangClass;
  }

  @Nonnull
  public JType getRefType() {
    return refType;
  }

  @Override
  @Nonnull
  public JType getType() {
    return javaLangClass;
  }

  @Override
  public boolean canThrow() {
    return true;
  }

  @Override
  public void traverse(@Nonnull JVisitor visitor) {
    if (visitor.visit(this)) {
    }
    visitor.endVisit(this);
  }

  @Override
  public void traverse(@Nonnull ScheduleInstance<? super Component> schedule) throws Exception {
    schedule.process(this);
  }

  @Override
  public void visit(@Nonnull JVisitor visitor, @Nonnull TransformRequest transformRequest)
      throws Exception {
    visitor.visit(this, transformRequest);
  }

  @Override
  public void checkValidity() {
    if (!(parent instanceof JExpression
        || parent instanceof JNameValuePair
        || parent instanceof JAnnotationMethod
        || parent instanceof JFieldInitializer
        || parent instanceof JReturnStatement
        || parent instanceof JSynchronizedBlock)) {
      throw new JNodeInternalError(this, "Invalid parent");
    }
  }
}
