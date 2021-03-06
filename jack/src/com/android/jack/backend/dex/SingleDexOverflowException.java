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

import com.android.jack.tools.merger.MergingOverflowException;

import javax.annotation.Nonnull;

/**
 * An {@link Exception} meaning that in single dex mode, the dex has overflowed.
 */
public class SingleDexOverflowException extends Exception {

  private static final long serialVersionUID = 1L;

  public SingleDexOverflowException(@Nonnull MergingOverflowException cause) {
    super(cause);
  }

  @Override
  public String getMessage() {
    return DexFileWriter.DEX_FILENAME + " has too many IDs. Try using multi-dex";
  }
}
