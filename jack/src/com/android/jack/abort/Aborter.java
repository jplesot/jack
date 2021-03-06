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

package com.android.jack.abort;

import com.android.jack.JackAbortException;
import com.android.jack.ir.ast.JSession;
import com.android.sched.item.Description;
import com.android.sched.schedulable.RunnableSchedulable;

import javax.annotation.Nonnull;

/**
 * A {@link RunnableSchedulable} that aborts the compilation if needed.
 */
@Description("Aborts the compilation if needed.")
public class Aborter implements RunnableSchedulable<JSession> {

  @Override
  public void run(@Nonnull JSession t) {
    if (t.mustAbortEventually()) {
      throw new JackAbortException();
    }
  }
}

