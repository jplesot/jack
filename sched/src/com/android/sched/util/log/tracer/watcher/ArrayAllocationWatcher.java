/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.android.sched.util.log.tracer.watcher;

import com.android.sched.util.codec.ImplementationName;
import com.android.sched.util.log.EventType;
import com.android.sched.util.log.Tracer;
import com.android.sched.util.log.TracerFactory;
import com.android.sched.util.log.stats.ArrayAlloc;
import com.android.sched.util.log.stats.ArrayAllocImpl;
import com.android.sched.util.log.stats.Statistic;
import com.android.sched.util.log.stats.StatisticId;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * Class to watch arrays creation and make detailed statistics about allocation.
 */
public class ArrayAllocationWatcher implements ObjectWatcher<Object> {
  static class Statistics implements ObjectWatcher.Statistics {
    @Override
    public Iterator<Statistic> iterator() {
      throw new AssertionError();
    }
  }

  @Nonnull
  private static final Map<Class<?>, StatisticId<ArrayAlloc>> arrayStats =
      new ConcurrentHashMap<Class<?>, StatisticId<ArrayAlloc>>();

  @Override
  public boolean notifyInstantiation(
      @Nonnull Object object,
      @Nonnegative long size,
      int count,
      @Nonnull EventType eventType,
      @CheckForNull StackTraceElement site) {
    Class<?> type = object.getClass();

    if (count >= 0) {
      notifyArray(type, size, count, eventType);
    }

    return false;
  }

  private void notifyArray(@Nonnull Class<?> type, @Nonnegative long size,
      @Nonnegative int count, @Nonnull EventType eventType) {
    StatisticId<ArrayAlloc> id;

    synchronized (ArrayAllocationWatcher.class) {
      id = arrayStats.get(type);
      if (id == null) {
        String name = type.getName();

        id = new StatisticId<ArrayAlloc>("jack.allocation.array." + name,
            "Array allocation of type " + name, ArrayAllocImpl.class, ArrayAlloc.class);
        arrayStats.put(type, id);
      }
    }

    try {
      TracerFactory.getTracer().getStatistic(id).recordAllocation(count, size, eventType);
    } catch (RuntimeException e) {
      // Do best effort here
    }
  }

  @Override
  @Nonnull
  public ObjectWatcher.Statistics addSample(@Nonnull Object node,
      @CheckForNull ObjectWatcher.Statistics raw, @Nonnull EventType type) {
    throw new AssertionError();
  }

  /**
   * Install a {@link ArrayAllocationWatcher}
   */
  @ImplementationName(iface = WatcherInstaller.class, name = "array-alloc",
      description = "record array allocations type by type")
  public static class DetailedAllocationWatcherInstaller implements WatcherInstaller {
    @Override
    public void install(@Nonnull Tracer tracer) {
      tracer.registerWatcher(Object.class, ArrayAllocationWatcher.class);
    }
  }
}
