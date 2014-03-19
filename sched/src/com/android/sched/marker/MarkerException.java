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

package com.android.sched.marker;

import javax.annotation.Nonnull;

/**
 * A {@code RuntimeException} related to a {@link Marker} in general.
 */
public class MarkerException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public MarkerException() {
    super();
  }

  public MarkerException(@Nonnull String message) {
    super(message);
  }

  public MarkerException(@Nonnull String message, @Nonnull Throwable cause) {
    super(message, cause);
  }

  public MarkerException(@Nonnull Throwable cause) {
    super(cause);
  }
}
