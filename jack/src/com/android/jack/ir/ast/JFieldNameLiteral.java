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

package com.android.jack.ir.ast;

import com.android.jack.ir.naming.FieldName;
import com.android.jack.ir.sourceinfo.SourceInfo;
import com.android.sched.item.Component;
import com.android.sched.item.Description;
import com.android.sched.scheduler.ScheduleInstance;
import com.android.sched.transform.TransformRequest;

import javax.annotation.Nonnull;

/**
 * String representing the name of a field.
 */
@Description("String representing the name of a field")
public class JFieldNameLiteral extends JAbstractStringLiteral {

  @Nonnull
  private final FieldName fieldName;

  public JFieldNameLiteral(@Nonnull SourceInfo sourceInfo, @Nonnull JField field) {
    super(sourceInfo);
    this.fieldName = new FieldName(field);
  }

  @Override
  @Nonnull
  public String getValue() {
    return fieldName.toString();
  }

  @Nonnull
  public JField getField() {
    return fieldName.getField();
  }

  @Override
  @Nonnull
  public JFieldNameLiteral clone() {
    return (JFieldNameLiteral) super.clone();
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
}
