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

package com.android.jack.ir.ast.cfg;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import javax.annotation.Nonnull;

/** Simplifies iterations through basic blocks */
public abstract class BasicBlockIterator {
  /** Processes basic block, returns false if the iterations should be stopped */
  public abstract boolean process(@Nonnull JBasicBlock block);

  /** Get control flow graph to work on */
  @Nonnull
  public abstract JControlFlowGraph getCfg();

  /** Iterates basic blocks depth first */
  public final void iterateDepthFirst(boolean forward) {
    Stack<JBasicBlock> stack = new Stack<>();
    Set<JBasicBlock> stacked = new HashSet<>();

    JBasicBlock start = forward ? getCfg().entry() : getCfg().exit();
    stack.push(start);
    stacked.add(start);

    while (!stack.isEmpty()) {
      JBasicBlock block = stack.pop();
      assert stacked.contains(block);

      for (JBasicBlock next : (forward ? block.successors() : block.predecessors())) {
        if (!stacked.contains(next)) {
          stack.push(next);
          stacked.add(next);
        }
      }

      process(block);
    }
  }
}
