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

package com.android.jack.backend.dex;

import com.android.jack.analysis.tracer.Tracer;
import com.android.jack.ir.ast.JAnnotation;
import com.android.jack.ir.ast.JDefinedClassOrInterface;
import com.android.jack.ir.ast.JSession;
import com.android.jack.scheduling.feature.SourceVersion8;
import com.android.sched.item.Description;
import com.android.sched.schedulable.Access;
import com.android.sched.schedulable.Constraint;
import com.android.sched.schedulable.Optional;
import com.android.sched.schedulable.RunnableSchedulable;
import com.android.sched.schedulable.ToSupport;
import com.android.sched.schedulable.Use;

import javax.annotation.Nonnull;

/**
 * Trace for main dex.
 */
@Description("Trace for main dex.")
@Use({Tracer.class, MultiDexLegacyTracerBrush.class})
@Access(JSession.class)
@Optional(@ToSupport(feature = SourceVersion8.class,
    add = @Constraint(need = JAnnotation.RepeatedAnnotation.class)))
public class MainDexTracer implements RunnableSchedulable<JDefinedClassOrInterface> {

  @Nonnull
  private final Tracer tracer = new Tracer(new MultiDexLegacyTracerBrush());

  @Override
  public void run(@Nonnull JDefinedClassOrInterface type) {
    tracer.run(type);
  }

}
