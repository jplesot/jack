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

package com.android.jack.frontend;

import com.android.jack.test.toolchain.AbstractTestTools;
import com.android.jack.test.toolchain.JackApiToolchainBase;

import junit.framework.Assert;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class DuplicateSourceTest {

  /**
   * Check that compilation failed properly when a source file is provided more than one time into
   * command line.
   */
  @Test
  public void test001() throws Exception {
    File output = AbstractTestTools.createTempDir();
    ByteArrayOutputStream errOut = new ByteArrayOutputStream();

    JackApiToolchainBase toolchain =
        AbstractTestTools.getCandidateToolchain(JackApiToolchainBase.class);
    toolchain.setErrorStream(errOut);

    try {
      toolchain.addToClasspath(toolchain.getDefaultBootClasspath()).srcToExe(output,
      /* zipFiles= */false,
          AbstractTestTools.getTestRootDir("com.android.jack.frontend.test007.jack"),
          AbstractTestTools.getTestRootDir("com.android.jack.frontend.test007.jackduplicate"));
      Assert.fail();
    } catch (FrontendCompilationException e) {
      Assert.assertTrue(errOut.toString().contains("The type A is already defined"));
    }
  }
}
