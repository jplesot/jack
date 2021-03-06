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

import com.android.jack.JackEventType;
import com.android.jack.tools.merger.JackMerger;
import com.android.jack.tools.merger.MergingOverflowException;
import com.android.sched.util.codec.ImplementationName;
import com.android.sched.util.log.Event;
import com.android.sched.vfs.OutputVFS;
import com.android.sched.vfs.OutputVFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

/**
 * A {@link DexWritingTool} that merges dex files, each one corresponding to a type, in several dex
 * files, keeping the main dex as small as possible.
 */
@ImplementationName(iface = DexWritingTool.class, name = "minimal-multidex", description =
    "allow emitting several dex files, keeping the first dex (main dex) as small as possible")
public class MinimalMultiDexWritingTool extends DexWritingTool implements MultiDexWritingTool {
  @Override
  public void write(@Nonnull OutputVFS outputVDir) throws DexWritingException {
    int dexCount = 1;
    Set<MatchableInputVFile> mainDexList = new HashSet<MatchableInputVFile>();
    List<MatchableInputVFile> anyDexList = new ArrayList<MatchableInputVFile>();
    fillDexLists(mainDexList, anyDexList);

    try (Event event = tracer.open(JackEventType.DEX_MERGER)) {
      JackMerger merger = new JackMerger(createDexFile());
      OutputVFile outputDex = getOutputDex(outputVDir, dexCount++);

      for (MatchableInputVFile currentDex : mainDexList) {
        try {
          mergeDex(merger, currentDex.getInputVFile());
        } catch (MergingOverflowException e) {
          throw new DexWritingException(new MainDexOverflowException(e));
        }
      }

      finishMerge(merger, outputDex);

      outputDex = getOutputDex(outputVDir, dexCount++);
      merger = new JackMerger(createDexFile());

      for (MatchableInputVFile currentDex : anyDexList) {
        try {
          mergeDex(merger, currentDex.getInputVFile());
        } catch (MergingOverflowException e) {
          finishMerge(merger, outputDex);
          outputDex = getOutputDex(outputVDir, dexCount++);
          merger = new JackMerger(createDexFile());
          try {
            mergeDex(merger, currentDex.getInputVFile());
          } catch (MergingOverflowException e1) {
            // This should not happen, the type is not too big, we've just read it from a dex.
            throw new AssertionError(e1);
          }
        }
      }

      finishMerge(merger, outputDex);
    }
  }

}
