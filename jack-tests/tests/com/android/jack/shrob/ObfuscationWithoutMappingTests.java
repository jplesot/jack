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

package com.android.jack.shrob;

import com.android.jack.JackAbortException;
import com.android.jack.Options;
import com.android.jack.shrob.obfuscation.MappingApplier;
import com.android.jack.shrob.obfuscation.NameProviderFactory;
import com.android.jack.shrob.shrink.MappingContextException;
import com.android.jack.test.comparator.ComparatorMapping;
import com.android.jack.test.helper.RuntimeTestHelper;
import com.android.jack.test.helper.SourceToDexComparisonTestHelper;
import com.android.jack.test.junit.KnownIssue;
import com.android.jack.test.junit.Runtime;
import com.android.jack.test.runtime.RuntimeTestInfo;
import com.android.jack.test.toolchain.AbstractTestTools;
import com.android.jack.test.toolchain.DummyToolchain;
import com.android.jack.test.toolchain.IToolchain;
import com.android.jack.test.toolchain.IncrementalToolchain;
import com.android.jack.test.toolchain.JackApiToolchainBase;
import com.android.jack.test.toolchain.JackApiV01;
import com.android.jack.test.toolchain.JackBasedToolchain;
import com.android.jack.test.toolchain.JackCliToolchain;
import com.android.jack.test.toolchain.Toolchain.SourceLevel;

import junit.framework.Assert;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

public class ObfuscationWithoutMappingTests extends AbstractTest {



  @Override
  protected void runTest(
      @Nonnull String testNumber,
      @Nonnull String flagNumber,
      @Nonnull String mappingNumber)
      throws Exception {

    File testFolder = getShrobTestRootDir(testNumber);

    JackApiToolchainBase toolchain =
        AbstractTestTools.getCandidateToolchain(JackApiToolchainBase.class);
    File refFolder = new File(testFolder, "refsObfuscationWithoutMapping");

    toolchain.addProperty(NameProviderFactory.NAMEPROVIDER.getName(), "rot13");
    toolchain.addProperty(Options.METHOD_FILTER.getName(), "supported-methods");

    File candidateOutputMapping = AbstractTestTools.createTempFile("mapping", ".txt");
    File refOutputMapping = new File(refFolder, "expected-" + flagNumber + ".txt");

    File proguardFlagsFile = addOptionsToFlagsFile(
        new File(testFolder, "proguard.flags" + flagNumber),
        testFolder,
        " -printmapping " + candidateOutputMapping.getAbsolutePath());

    toolchain.addProguardFlags(proguardFlagsFile);

    SourceToDexComparisonTestHelper env =
        new SourceToDexComparisonTestHelper(new File(testFolder, "jack"));

    env.setCandidateTestTools(toolchain);
    env.setReferenceTestTools(new DummyToolchain());

    env.runTest(new ComparatorMapping(refOutputMapping, candidateOutputMapping));
  }

  @Override
  @Test
  @KnownIssue
  public void test33_001() throws Exception {
    super.test33_001();
  }

  @Override
  @Test
  @KnownIssue
  public void test34_001() throws Exception {
    super.test34_001();
  }

  @Override
  @Test
  @KnownIssue
  public void test35_001() throws Exception {
    super.test35_001();
  }

  @Override
  @Test
  @KnownIssue
  public void test44_001() throws Exception {
    super.test44_001();
  }

  @Override
  @Test
  @KnownIssue(candidate=IncrementalToolchain.class)
  public void test5_001() throws Exception {
    super.test5_001();
  }

  @Override
  @Test
  @KnownIssue(candidate=IncrementalToolchain.class)
  public void test15_001() throws Exception {
    super.test15_001();
  }

  /**
   * Test Obfuscation when a whole package is missing from the classpath.
   */
  @Test
  @Runtime
  public void test54() throws Exception {
    File testRootDir = getShrobTestRootDir("054");

    // Build the lib
    File libLib;
    {
      IToolchain toolchain =
          AbstractTestTools.getCandidateToolchain(JackBasedToolchain.class);
      libLib = AbstractTestTools.createTempFile("shrob54", "lib" + toolchain.getLibraryExtension());
      toolchain.addToClasspath(toolchain.getDefaultBootClasspath())
      .srcToLib(libLib, /* zipFiles = */ true, new File (testRootDir,"lib"));
    }
    File libDex = AbstractTestTools.createTempFile("shrob54", "lib.dex.zip");
    {
      IToolchain toolchain =
          AbstractTestTools.getCandidateToolchain(JackBasedToolchain.class);
      toolchain.addToClasspath(toolchain.getDefaultBootClasspath())
      .libToExe(libLib, libDex, /* zipFile = */ true);
    }

    // Build the jack as a lib
    File jackLib;
    File jackDir = new File(testRootDir, "jack");
    {
      IToolchain toolchain =
          AbstractTestTools.getCandidateToolchain(JackBasedToolchain.class);
      jackLib =
          AbstractTestTools.createTempFile("shrob54", "jack" + toolchain.getLibraryExtension());
      toolchain.addToClasspath(toolchain.getDefaultBootClasspath())
      .addToClasspath(libLib)
      .srcToLib(
          jackLib,
          /* zipFiles = */ true,
          jackDir);
    }

    // Build the jack as a dex from the lib but without classpath
    File jackDex = AbstractTestTools.createTempFile("shrob54", "jack.dex.zip");
    {
      IToolchain toolchain =
          AbstractTestTools.getCandidateToolchain(JackBasedToolchain.class);
      toolchain.addToClasspath(toolchain.getDefaultBootClasspath())
      .addProguardFlags(new File(jackDir, "proguard.flags001"))
      .libToExe(jackLib, jackDex, /* zipFile = */ true);
    }


    File testDex = AbstractTestTools.createTempFile("shrob54", "test.dex.zip");
    {
      IToolchain toolchain =
          AbstractTestTools.getCandidateToolchain(JackBasedToolchain.class);
      toolchain.addToClasspath(toolchain.getDefaultBootClasspath())
      .addToClasspath(libLib)
      .addToClasspath(jackLib)
      .srcToExe(testDex, /* zipFiles = */ true, new File(testRootDir, "dx"));
    }

    RuntimeTestHelper.runOnRuntimeEnvironments(
        Collections.singletonList("com.android.jack.shrob.test054.dx.Tests"),
        RuntimeTestHelper.getJunitDex(), libDex, jackDex, testDex);
  }

  @Test
  public void test055() throws Exception {
    String testPackageName = "com.android.jack.shrob.test055";
    File testFolder = AbstractTestTools.getTestRootDir(testPackageName);

    JackApiToolchainBase toolchain =
        AbstractTestTools.getCandidateToolchain(JackApiToolchainBase.class);
    File refFolder = new File(testFolder, "refsObfuscationWithoutMapping");

    toolchain.addProperty(NameProviderFactory.NAMEPROVIDER.getName(), "rot13");
    toolchain.addProperty(Options.METHOD_FILTER.getName(), "supported-methods");
    // Only difference with other tests: allows mapping collision
    toolchain.addProperty(MappingApplier.COLLISION_POLICY.getName(), "ignore");

    File candidateOutputMapping = AbstractTestTools.createTempFile("mapping", ".txt");
    File refOutputMapping = new File(refFolder, "expected-001.txt");

    File proguardFlagsFile = addOptionsToFlagsFile(
        new File(testFolder, "proguard.flags001"),
        testFolder,
        " -printmapping " + candidateOutputMapping.getAbsolutePath());

    toolchain.addProguardFlags(proguardFlagsFile);

    SourceToDexComparisonTestHelper env =
        new SourceToDexComparisonTestHelper(new File(testFolder, "jack"));

    env.setCandidateTestTools(toolchain);
    env.setReferenceTestTools(new DummyToolchain());

    env.runTest(new ComparatorMapping(refOutputMapping, candidateOutputMapping));
  }

  @Test
  public void test56_001() throws Exception {
    runTest("056", "001", "");
  }

  @Nonnull
  private static File shrobTestsDir =
      AbstractTestTools.getTestRootDir("com.android.jack.shrob");

  @Test
  @Runtime
  public void test59() throws Exception {
    RuntimeTestInfo runtimeTestInfo = new RuntimeTestInfo(
        new File(shrobTestsDir, "test059"),
        "com.android.jack.shrob.test059.dx.Tests");
    runtimeTestInfo.addProguardFlagsFileName("proguard.flags001");
    new RuntimeTestHelper(runtimeTestInfo).compileAndRunTest(/* checkStructure = */ false);
  }

  @Test
  @Runtime
  public void test62() throws Exception {
    RuntimeTestInfo runtimeTestInfo = new RuntimeTestInfo(new File(shrobTestsDir, "test062"),
        "com.android.jack.shrob.test062.jack.Tests");
    runtimeTestInfo.addProguardFlagsFileName("proguard.flags001");
    new RuntimeTestHelper(runtimeTestInfo).setSourceLevel(SourceLevel.JAVA_8)
    .addIgnoredCandidateToolchain(JackApiV01.class)
    .setWithDebugInfos(true)
    .compileAndRunTest();
  }

  @Test
  @Runtime
  public void test63_001() throws Exception {
    RuntimeTestInfo runtimeTestInfo = new RuntimeTestInfo(
        new File(shrobTestsDir, "test063"),
        "com.android.jack.shrob.test063.dx.Tests");
    runtimeTestInfo.addProguardFlagsFileName("proguard.flags001");
    new RuntimeTestHelper(runtimeTestInfo).compileAndRunTest(/* checkStructure = */ false);
  }

  @Test
  @Runtime
  public void test63_002() throws Exception {
    String testPackageName = "com.android.jack.shrob.test063";
    File testFolder = AbstractTestTools.getTestRootDir(testPackageName);
    File proguardFlagsFile = new File(testFolder, "proguard.flags002");
    ByteArrayOutputStream errOut = new ByteArrayOutputStream();

    try {
      List<Class<? extends IToolchain>> excludeList = new ArrayList<Class<? extends IToolchain>>(1);
      excludeList.add(JackCliToolchain.class);
      IToolchain toolchain =
          AbstractTestTools.getCandidateToolchain(JackBasedToolchain.class, excludeList);
      toolchain.setErrorStream(errOut);
      toolchain.addToClasspath(toolchain.getDefaultBootClasspath())
          .addProguardFlags(proguardFlagsFile).srcToExe(AbstractTestTools.createTempDir(),
              /* zipFile = */false, AbstractTestTools.getTestRootDir(testPackageName + ".jack"));
      Assert.fail();
    } catch (JackAbortException jae) {
      Assert.assertTrue(jae.getCause() instanceof MappingContextException);
      String errString = errOut.toString();
      Assert.assertTrue(errString.contains("could not be renamed to 'a' since the name was already "
          + "used"));
    }
  }

  @Test
  @Runtime
  public void test64_001() throws Exception {
    RuntimeTestInfo runtimeTestInfo = new RuntimeTestInfo(
        new File(shrobTestsDir, "test064"),
        "com.android.jack.shrob.test064.dx.Tests");
    runtimeTestInfo.addProguardFlagsFileName("proguard.flags001");
    new RuntimeTestHelper(runtimeTestInfo).compileAndRunTest(/* checkStructure = */ false);
  }

  @Test
  @Runtime
  public void test64_002() throws Exception {
    String testPackageName = "com.android.jack.shrob.test064";
    File testFolder = AbstractTestTools.getTestRootDir(testPackageName);
    File proguardFlagsFile = new File(testFolder, "proguard.flags002");
    ByteArrayOutputStream errOut = new ByteArrayOutputStream();

    try {
      List<Class<? extends IToolchain>> excludeList = new ArrayList<Class<? extends IToolchain>>(1);
      excludeList.add(JackCliToolchain.class);
      IToolchain toolchain =
          AbstractTestTools.getCandidateToolchain(JackBasedToolchain.class, excludeList);
      toolchain.setErrorStream(errOut);
      toolchain.addToClasspath(toolchain.getDefaultBootClasspath())
          .addProguardFlags(proguardFlagsFile).srcToExe(AbstractTestTools.createTempDir(),
              /* zipFile = */false, AbstractTestTools.getTestRootDir(testPackageName + ".jack"));
      Assert.fail();
    } catch (JackAbortException jae) {
      Assert.assertTrue(jae.getCause() instanceof MappingContextException);
      String errString = errOut.toString();
      Assert.assertTrue(errString.contains("could not be renamed to 'a' since the name was already "
          + "used"));
    }
  }
}
