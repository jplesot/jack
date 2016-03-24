/*
 * Copyright (C) 2016 The Android Open Source Project
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

package com.android.jack.backend.dex.compatibility;

import com.android.jack.Jack;
import com.android.jack.Options;
import com.android.jack.ir.ast.JInterface;
import com.android.jack.ir.ast.JMethod;
import com.android.jack.ir.ast.JSession;
import com.android.jack.reporting.Reportable;
import com.android.jack.reporting.Reporter.Severity;
import com.android.sched.item.Description;
import com.android.sched.schedulable.RunnableSchedulable;
import com.android.sched.schedulable.Support;
import com.android.sched.util.config.HasKeyId;
import com.android.sched.util.config.ThreadConfig;
import com.android.sched.util.config.id.BooleanPropertyId;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * A {@link RunnableSchedulable} that checks that the IR is compatible with the Dex output.
 */
@HasKeyId
@Description("Checks that the IR is compatible with the dex output.")
@Support(CheckAndroidCompatibility.class)
public class AndroidCompatibilityChecker implements RunnableSchedulable<JMethod> {

  @Nonnull
  public static final BooleanPropertyId CHECK_COMPATIBILITY =
      BooleanPropertyId.create(
              "jack.android.api-level.check",
              "Check compatibility with the Dex output")
          .addDefaultValue(Boolean.FALSE);

  @Nonnegative
  public static final long N_API_LEVEL = 24;

  private final long androidMinApiLevel =
      ThreadConfig.get(Options.ANDROID_MIN_API_LEVEL).longValue();

  @Nonnull
  private final JSession session = Jack.getSession();

  @Override
  public void run(@Nonnull JMethod m) throws Exception {
    if (androidMinApiLevel < N_API_LEVEL) {
      if (m.getEnclosingType() instanceof JInterface && !m.isAbstract() && !JMethod.isClinit(m)) {
        Reportable reportable;
        if (m.isStatic()) {
          reportable = new StaticMethodReportable(m);
        } else {
          reportable = new DefaultMethodReportable(m);
        }

        session.getReporter().report(Severity.NON_FATAL, reportable);
        session.setAbortEventually(true);
      }
    }
  }
}