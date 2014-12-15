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

package com.android.jack.library;

import com.android.jack.Jack;
import com.android.sched.util.codec.CodecContext;
import com.android.sched.util.codec.ParsingException;
import com.android.sched.util.codec.StringCodec;
import com.android.sched.util.codec.ZipInputOutputVDirCodec;
import com.android.sched.util.config.ConfigurationError;
import com.android.sched.util.file.FileOrDirectory.Existence;
import com.android.sched.vfs.InputOutputVFS;

import java.util.Collections;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * This {@link StringCodec} is used to create an instance of {@link OutputJackLibrary}.
 */
public class OutputJackLibraryCodec implements StringCodec<OutputJackLibrary> {
  @Nonnull
  private final ZipInputOutputVDirCodec codec;

  public OutputJackLibraryCodec() {
    codec = new ZipInputOutputVDirCodec(Existence.MAY_EXIST);
  }

  @Override
  @Nonnull
  public OutputJackLibrary parseString(@Nonnull CodecContext context, @Nonnull String string) {
    try {
      return checkString(context, string);
    } catch (ParsingException e) {
      throw new ConfigurationError(e);
    }
  }

  @Override
  @CheckForNull
  public OutputJackLibrary checkString(@Nonnull CodecContext context, @Nonnull String string)
      throws ParsingException {
    InputOutputVFS vfs = codec.checkString(context, string);

    return JackLibraryFactory.getOutputLibrary(vfs, Jack.getEmitterId(), Jack.getVersionString());
  }

  @Override
  @Nonnull
  public String getUsage() {
    return "a path to a jack library (" + codec.getUsageDetails() + ")";
  }

  @Override
  @Nonnull
  public List<ValueDescription> getValueDescriptions() {
    return Collections.<ValueDescription> emptyList();
  }

  @Override
  @Nonnull
  public String formatValue(@Nonnull OutputJackLibrary data) {
    return data.getPath();
  }

  @Override
  public void checkValue(@Nonnull CodecContext context, @Nonnull OutputJackLibrary data) {
  }
}