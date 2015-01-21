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

package com.android.sched.util.file;

import java.io.IOException;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * Exception when a path is not from the expected file or directory kind.
 */
public abstract class NotFileOrDirectoryException extends IOException {
  private static final long serialVersionUID = 1L;

  public NotFileOrDirectoryException(@Nonnull String string) {
    super(string);
  }

  public NotFileOrDirectoryException(@Nonnull String string, @CheckForNull Throwable cause) {
    super(string, cause);
  }
}
