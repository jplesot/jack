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

package com.android.sched.util.log.tracer.filter;

import com.android.sched.util.HasDescription;
import com.android.sched.util.log.EventType;

import javax.annotation.Nonnull;

/**
 * Interface of a {@link EventType} filter.
 */
public interface EventFilter extends HasDescription {
  public boolean isEnabled(@Nonnull EventType type);
}
