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

import com.android.jack.backend.dex.rop.CodeItemBuilder;
import com.android.jack.dx.dex.DexOptions;
import com.android.jack.dx.dex.file.DexFile;
import com.android.sched.util.config.ThreadConfig;
import com.android.sched.vfs.OutputVDir;

import java.io.IOException;

import javax.annotation.Nonnull;

/**
 * A helper to write dex files.
 */
public abstract class DexWritingTool {

  @Nonnull
  protected final OutputVDir outputVDir;
  @Nonnull
  private final boolean forceJumbo = ThreadConfig.get(CodeItemBuilder.FORCE_JUMBO).booleanValue();

  public DexWritingTool(@Nonnull OutputVDir outputVDir) {
    this.outputVDir = outputVDir;
  }

  @Nonnull
  protected DexFile createDexFile() {
    DexOptions options = new DexOptions();
    options.forceJumbo = forceJumbo;
    return new DexFile(options);
  }

  public abstract void write() throws IOException;

}
