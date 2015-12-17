/*
 * Copyright (C) 2015 The Android Open Source Project
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

package com.android.jack.java8;

import com.android.jack.test.helper.RuntimeTestHelper;
import com.android.jack.test.runtime.RuntimeTestInfo;
import com.android.jack.test.toolchain.AbstractTestTools;
import com.android.jack.test.toolchain.Toolchain.SourceLevel;

import org.junit.Test;


public class TypeInferenceTest {

  private RuntimeTestInfo INFERENCE001 = new RuntimeTestInfo(
      AbstractTestTools.getTestRootDir("com.android.jack.java8.inference.test001"),
      "com.android.jack.java8.inference.test001.jack.Tests");

  private RuntimeTestInfo INFERENCE002 = new RuntimeTestInfo(
      AbstractTestTools.getTestRootDir("com.android.jack.java8.inference.test002"),
      "com.android.jack.java8.inference.test002.jack.Tests");

  private RuntimeTestInfo INFERENCE003 = new RuntimeTestInfo(
      AbstractTestTools.getTestRootDir("com.android.jack.java8.inference.test003"),
      "com.android.jack.java8.inference.test003.jack.Tests");

  @Test
  public void testTypeInference001() throws Exception {
    new RuntimeTestHelper(INFERENCE001)
    .setSourceLevel(SourceLevel.JAVA_8)
    .compileAndRunTest();
  }

  @Test
  public void testTypeInference002() throws Exception {
    new RuntimeTestHelper(INFERENCE002)
    .setSourceLevel(SourceLevel.JAVA_8)
    .compileAndRunTest();
  }

  @Test
  public void testTypeInference003() throws Exception {
    new RuntimeTestHelper(INFERENCE003)
    .setSourceLevel(SourceLevel.JAVA_8)
    .compileAndRunTest();
  }

}
