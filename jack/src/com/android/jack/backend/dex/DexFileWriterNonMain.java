/*
 * Copyright (C) 2015 The Android Open Source Project
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

import com.android.jack.ir.ast.JDefinedClassOrInterface;
import com.android.jack.util.filter.Filter;
import com.android.sched.item.Description;
import com.android.sched.item.Name;
import com.android.sched.schedulable.RunnableSchedulable;

import javax.annotation.Nonnull;

/**
 * Merge dexes not marked with MainDexMarker.
 */
@Description("Merge dexes not marked with MainDexMarker.")
@Name("DexFileWriterNonMain")
public class DexFileWriterNonMain extends DexFileWriter {

  @Nonnull
  private static final Filter<JDefinedClassOrInterface> filter =
    new Filter<JDefinedClassOrInterface>() {
      @Override
      public boolean accept(Class<? extends RunnableSchedulable<?>> runnableSchedulable,
          JDefinedClassOrInterface t) {
        return t.getMarker(MainDexMarker.class) == null;
      }
    };

  @Override
  protected Filter<JDefinedClassOrInterface> getFilter() {
    return filter;
  }

}
