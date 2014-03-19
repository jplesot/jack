/*
 * Copyright 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.android.jack.ir.ast;


import com.android.jack.ir.SourceInfo;
import com.android.sched.item.Component;
import com.android.sched.item.Description;
import com.android.sched.scheduler.ScheduleInstance;
import com.android.sched.transform.TransformRequest;

import javax.annotation.Nonnull;

/**
 * A synchronization statement.
 */
@Description("A synchronization statement")
public abstract class JSynchronize extends JStatement {

  private static final long serialVersionUID = 1L;

  @Nonnull
  private JExpression lockExpr;

  public JSynchronize(@Nonnull SourceInfo info, @Nonnull JExpression lockExpr) {
    super(info);
    this.lockExpr = lockExpr;
  }


  /**
   * @return the lockExpr
   */
  @Nonnull
  public JExpression getLockExpr() {
    return lockExpr;
  }


  @Override
  public void traverse(@Nonnull JVisitor visitor) {
    visitor.accept(lockExpr);
  }

  @Override
  public void traverse(@Nonnull ScheduleInstance<? super Component> schedule) throws Exception {
    lockExpr.traverse(schedule);
  }

  @Override
  protected void replaceImpl(@Nonnull JNode existingNode, @Nonnull JNode newNode)
      throws UnsupportedOperationException {
    assert newNode != null;

    if (lockExpr == existingNode) {
      lockExpr = (JExpression) newNode;
    } else {
      super.replaceImpl(existingNode, newNode);
    }
  }

  @Override
  public void visit(@Nonnull JVisitor visitor, @Nonnull TransformRequest transformRequest)
      throws Exception {
    visitor.visit(this, transformRequest);
  }
}
