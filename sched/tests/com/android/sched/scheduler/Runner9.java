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

package com.android.sched.scheduler;

import com.android.sched.item.Description;
import com.android.sched.item.Name;
import com.android.sched.item.onlyfor.OnlyFor;
import com.android.sched.item.onlyfor.SchedTest;
import com.android.sched.production.Production1;
import com.android.sched.schedulable.Constraint;
import com.android.sched.schedulable.Produce;
import com.android.sched.schedulable.Transform;
import com.android.sched.tag.Tag1;
import com.android.sched.tag.Tag2;

import javax.annotation.Nonnull;

@Name("Runner 9")
@Description("Runner 9 description")
@OnlyFor(SchedTest.class)
@Constraint(need = Tag1.class)
@Produce(Production1.class)
@Transform(add = Tag2.class, remove = Tag1.class)
public class Runner9 extends RunnerTest implements RunnerInterface {
  @Override
  public void run(@Nonnull Component0 v) throws Exception {
    need(Tag1.class);
    add(Tag2.class);
    remove(Tag1.class);
    produce(Production1.class);
  }
}
