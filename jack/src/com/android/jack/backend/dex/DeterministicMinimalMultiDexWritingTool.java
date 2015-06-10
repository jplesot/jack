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
import com.android.sched.util.codec.ImplementationName;

import java.util.ArrayList;
import java.util.Collections;

import javax.annotation.Nonnull;

/**
 * A {@link DexWritingTool} that merges dex files, each one corresponding to a type, in several dex
 * files, keeping the main dex as small as possible and maintaining determinism.
 */
@ImplementationName(iface = DexWritingTool.class, name = "deter-minimal-multidex", description =
    "allow emitting several dex files, keeping the first dex (main dex) as small as possible" +
    " and maintaining determinism")
public class DeterministicMinimalMultiDexWritingTool extends MinimalMultiDexWritingTool {

  @Override
  @Nonnull
  protected MergingManager getManager() {
    return new DeterministicMergingManager();
  }

  @Override
  protected void sortAndNumberInternal(@Nonnull ArrayList<JDefinedClassOrInterface> defaultList,
      @Nonnull ArrayList<JDefinedClassOrInterface> mainList) {
    Collections.sort(defaultList, nameComp);
    int number = 0;
    for (JDefinedClassOrInterface type : mainList) {
      type.addMarker(new NumberMarker(number++));
    }
    number = 0;
    for (JDefinedClassOrInterface type : defaultList) {
      type.addMarker(new NumberMarker(number++));
    }
  }

}
