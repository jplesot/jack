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

import com.google.common.collect.Lists;

import com.android.jack.ir.ast.JMethod;
import com.android.jack.ir.ast.JMethodBodyCfg;
import com.android.jack.ir.ast.JNode;
import com.android.jack.ir.ast.JVisitor;
import com.android.jack.ir.sourceinfo.SourceInfo;
import com.android.jack.util.graph.IGraph;
import com.android.sched.item.Component;
import com.android.sched.item.Description;
import com.android.sched.scheduler.ScheduleInstance;
import com.android.sched.transform.TransformRequest;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import javax.annotation.Nonnull;

/** Represents method control flow graph */
@Description("Control Flow Graph")
public final class JControlFlowGraph extends JNode implements IGraph<JBasicBlock> {
  @Nonnull
  private final JEntryBasicBlock entry;
  @Nonnull
  private final JExitBasicBlock exit;

  public JControlFlowGraph(@Nonnull SourceInfo info) {
    super(info);
    this.exit = new JExitBasicBlock(this);
    this.entry = new JEntryBasicBlock(this, exit);
  }

  @Nonnull
  public final JEntryBasicBlock getEntryBlock() {
    return entry;
  }

  @Nonnull
  public final JExitBasicBlock getExitBlock() {
    return exit;
  }

  @Nonnull
  public JMethod getMethod() {
    return getMethodBody().getMethod();
  }

  @Nonnull
  public JMethodBodyCfg getMethodBody() {
    JNode parent = getParent();
    assert parent instanceof JMethodBodyCfg;
    return (JMethodBodyCfg) parent;
  }

  /**
   * Returns all basic blocks reachable from entry block in depth-first order,
   * ALSO returns basic blocks 'weakly referenced' via exception handling context.
   *
   * Note that the entry block is always the first block of the list, and the
   * exist block may not be present in this list.
   */
  @Nonnull
  public List<JBasicBlock> getReachableBlocksDepthFirst() {
    final List<JBasicBlock> blocks = new ArrayList<>();
    new BasicBlockIterator(this) {
      @Override public boolean process(@Nonnull JBasicBlock block) {
        blocks.add(block);
        return true;
      }
    }.iterateDepthFirst();
    return blocks;
  }

  /**
   * Returns all basic blocks reachable from entry or exit blocks via
   * successor/predecessor edges OR 'weakly referenced' via exception
   * handling context.
   *
   * Note that basic blocks returned by this method represent a superset
   * of the blocks returned by getReachableBlocksDepthFirst().
   *
   * The blocks are returned in stable order, i.e. two calls to this method
   * will return the same sequence of the blocks.
   */
  @Nonnull
  public List<JBasicBlock> getAllBlocksUnordered() {
    ArrayList<JBasicBlock> result = new ArrayList<>();
    result.add(this.getEntryBlock());
    result.add(this.getExitBlock());

    Set<JBasicBlock> internalBlocks = getInternalBasicBlocks();
    assert !internalBlocks.contains(this.getEntryBlock());
    assert !internalBlocks.contains(this.getExitBlock());
    result.addAll(internalBlocks);

    return result;
  }

  /**
   * Returns all basic blocks reachable from entry or exit blocks via
   * successor/predecessor edges OR 'weakly referenced' via exception
   * handling context EXCEPT entry and exit nodes themselves.
   *
   * The blocks are returned in stable order, i.e. two calls to this method
   * will return the same sequence of the blocks.
   */
  @Nonnull
  public List<JBasicBlock> getInternalBlocksUnordered() {
    return Lists.newArrayList(getInternalBasicBlocks());
  }

  @Nonnull
  private Set<JBasicBlock> getInternalBasicBlocks() {
    Set<JBasicBlock> blocks = new LinkedHashSet<>();
    Set<ExceptionHandlingContext> processedEHContexts = new LinkedHashSet<>();
    Queue<JBasicBlock> queue = new LinkedList<>(); // Contains duplicates

    JEntryBasicBlock entry = this.getEntryBlock();
    JExitBasicBlock exit = this.getExitBlock();

    queue.offer(entry);
    queue.offer(exit);

    while (!queue.isEmpty()) {
      JBasicBlock block = queue.remove();
      if (!blocks.contains(block)) {
        blocks.add(block);

        // Successors, then predecessors
        for (JBasicBlock successor : block.getSuccessors()) {
          if (!blocks.contains(successor)) {
            queue.offer(successor);
          }
        }
        for (JBasicBlock predecessor : block.getPredecessors()) {
          if (!blocks.contains(predecessor)) {
            queue.offer(predecessor);
          }
        }

        // Catch blocks referenced from EH contexts
        for (JBasicBlockElement element : block.getElements(true)) {
          ExceptionHandlingContext context = element.getEHContext();
          if (!processedEHContexts.contains(context)) {
            processedEHContexts.add(context);
            for (JCatchBasicBlock catchBlock : context.getCatchBlocks()) {
              if (!blocks.contains(catchBlock)) {
                queue.offer(catchBlock);
              }
            }
          }
        }
      }
    }

    blocks.remove(entry);
    blocks.remove(exit);
    return blocks;
  }

  /** Traverses the the blocks in the order provided by `getAllBlocksUnordered()` */
  @Override
  public void traverse(@Nonnull final JVisitor visitor) {
    if (visitor.visit(this)) {
      for (JBasicBlock block : getAllBlocksUnordered()) {
        visitor.accept(block);
      }
    }
    visitor.endVisit(this);
  }

  /** Traverses the the blocks in the order provided by `getAllBlocksUnordered()` */
  @Override
  public void traverse(
      @Nonnull final ScheduleInstance<? super Component> schedule) throws Exception {
    schedule.process(this);
    for (JBasicBlock block : getAllBlocksUnordered()) {
      block.traverse(schedule);
    }
  }

  @Override
  public void visit(@Nonnull JVisitor visitor, @Nonnull TransformRequest request) throws Exception {
    visitor.visit(this, request);
  }

  @Override
  public void checkValidity() {
    for (JBasicBlock block : this.getAllBlocksUnordered()) {
      block.checkValidity();
    }
  }

  @Override
  @Nonnull
  public List<JBasicBlock> getNodes() {
    return getAllBlocksUnordered();
  }

  @Override
  @Nonnull
  public JBasicBlock getEntryNode() {
    return getEntryBlock();
  }

  @Override
  @Nonnull
  public JBasicBlock getExitNode() {
    return getExitBlock();
  }
}