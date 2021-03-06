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

package com.android.sched.util.log.tracer.probe;

import com.android.sched.util.codec.ImplementationName;
import com.android.sched.util.findbugs.SuppressFBWarnings;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * Probe which count the heap memory usage.
 */
@ImplementationName(iface = Probe.class, name = "heap-usage")
public class HeapMemoryProbe extends MemoryBytesProbe {
  @Nonnull
  private final MemoryMXBean mmMXBean;

  public HeapMemoryProbe() {
    super("Heap Memory Usage", MIN_PRIORITY - 1);

    try {
      mmMXBean = ManagementFactory.getMemoryMXBean();
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  // We want to call System.gc() for memory measurement purposes
  @SuppressFBWarnings("DM_GC")
  @Override
  @Nonnegative
  public long read() {
    System.gc(); // Try our best
    return mmMXBean.getHeapMemoryUsage().getUsed();
  }

  @Override
  public void start() {
  }

  @Override
  public void stop() {
  }
}
