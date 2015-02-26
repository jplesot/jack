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

package com.android.jack.incremental;

import com.android.jack.library.InputLibrary;
import com.android.jack.library.OutputJackLibrary;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

/**
 * Interface that filter some inputs of Jack.
 */
public interface InputFilter {

  @Nonnull
  public Set<String> getFileNamesToCompile();

  @Nonnull
  public List<? extends InputLibrary> getClasspath();

  @Nonnull
  public OutputJackLibrary getOutputJackLibrary();

  @Nonnull
  public List<? extends InputLibrary> getImportedLibrary();
}
