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

package com.android.sched;

import com.android.jack.Options;
import com.android.jack.TestTools;
import com.android.jack.ir.JavaSourceIr;
import com.android.jack.ir.ast.JDefinedClassOrInterface;
import com.android.jack.ir.ast.JNode;
import com.android.jack.ir.ast.JProgram;
import com.android.jack.scheduling.adapter.JDefinedClassOrInterfaceAdaptor;
import com.android.sched.scheduler.PlanBuilder;
import com.android.sched.scheduler.Request;
import com.android.sched.scheduler.Scheduler;
import com.android.sched.scheduler.SubPlanBuilder;

import junit.framework.Assert;

import javax.annotation.Nonnull;

/**
 * Executable class to run the jack compiler.
 *
 */
public class Main {
  @Nonnull
  private static final String[] EXTRA_ARGS = new String[]{"-noExit", "-source", "1.6"};

  private Main() {
  }

  static void runCompilation(@Nonnull String[] compilerArgs) throws Exception {
    run(com.android.jack.Main.parseCommandLine(compilerArgs));
  }

  /**
   * Runs the jack compiler on source files and generates a dex file.
   *
   * @throws Exception if any exception is thrown during the process of compilation.
   */
  static void run(@Nonnull Options options)
      throws Exception {

    // Build the plan, verify it and run it onto the program
    Scheduler scheduler = Scheduler.getScheduler();
    Request sr = scheduler.createScheduleRequest();

    sr.addSchedulables(scheduler.getAllSchedulable());
    sr.addInitialTagOrMarker(JavaSourceIr.class);

    JProgram jprogram = TestTools.buildProgram(options);
    Assert.assertNotNull(jprogram);

    // Currently, plan is manually built by adding RunnableSchedulable(s) and sub-plans
    // corresponding to visitors.
    PlanBuilder<JProgram> progPlan = sr.getPlanBuilder(JProgram.class);
    SubPlanBuilder<JDefinedClassOrInterface> typePlan = progPlan.appendSubPlan(JDefinedClassOrInterfaceAdaptor.class);
    SubPlanBuilder<JNode> nodePlan = typePlan.appendSubPlan(JNodeAdapter.class);
    nodePlan.append(JNodeVisitor1.class);
    nodePlan.append(JNodeVisitor2.class);

    progPlan.getPlan().getScheduleInstance().process(jprogram);
  }

  @Nonnull
  static String[] toEcjArgs(@Nonnull String[] args) {
    String[] ecjArgs = new String[args.length + EXTRA_ARGS.length];
    System.arraycopy(args, 0, ecjArgs, 0, args.length);
    System.arraycopy(EXTRA_ARGS, 0, ecjArgs, args.length, EXTRA_ARGS.length);
    return ecjArgs;
  }
}
