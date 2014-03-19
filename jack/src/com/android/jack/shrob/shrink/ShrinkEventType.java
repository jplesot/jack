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

package com.android.jack.shrob.shrink;

import com.android.sched.util.log.EventType;

import javax.annotation.Nonnull;

/**
 * Events for the shrink feature
 */
public enum ShrinkEventType implements EventType {

  FINDING_SEEDS("Finding seeds", "Blue"),
  OVERRIDING_METHODS("Searching for overridding methods", "Red");

  @Nonnull
  private final String cssColor;
  @Nonnull
  private final String name;

  ShrinkEventType(@Nonnull String name, @Nonnull String cssColor) {
    this.name = name;
    this.cssColor = cssColor;
  }

  @Override
  @Nonnull
  public String getColor() {
    return cssColor;
  }

  @Override
  @Nonnull
  public String getName() {
    return name;
  }
}
