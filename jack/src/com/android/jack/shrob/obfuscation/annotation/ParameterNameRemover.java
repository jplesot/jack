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

package com.android.jack.shrob.obfuscation.annotation;

import com.android.jack.ir.ast.JMethod;
import com.android.jack.ir.ast.JParameter;
import com.android.sched.item.Description;
import com.android.sched.schedulable.Constraint;
import com.android.sched.schedulable.RunnableSchedulable;
import com.android.sched.schedulable.Transform;
import com.android.sched.util.config.HasKeyId;

import javax.annotation.Nonnull;

/**
 * A {@link RunnableSchedulable} that removes parameter names.
 */
@HasKeyId
@Description("Removes parameters names")
@Constraint(need = JParameter.class)
@Transform(modify = JParameter.class)
public class ParameterNameRemover implements RunnableSchedulable<JMethod> {

  @Override
  public void run(@Nonnull JMethod t) throws Exception {
    for (JParameter param : t.getParams()) {
      param.setName(null);
    }
  }
}
