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

package com.android.jack.error;

import com.google.common.io.Files;

import com.android.jack.JackUserException;
import com.android.jack.Main;
import com.android.jack.errorhandling.annotationprocessor.ResourceAnnotationProcessor;
import com.android.jack.errorhandling.annotationprocessor.ResourceAnnotationTest;
import com.android.jack.errorhandling.annotationprocessor.SourceAnnotationProcessor;
import com.android.jack.errorhandling.annotationprocessor.SourceAnnotationTest;
import com.android.jack.errorhandling.annotationprocessor.SourceErrorAnnotationTest;
import com.android.jack.frontend.FrontendCompilationException;
import com.android.jack.test.helper.ErrorTestHelper;
import com.android.jack.test.toolchain.AbstractTestTools;
import com.android.jack.test.toolchain.JackApiToolchain;

import junit.framework.Assert;

import org.jf.dexlib.ClassDefItem;
import org.jf.dexlib.DexFile;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * JUnit test checking Jack behavior when using annotation processor.
 */
public class AnnotationProcessorErrorTest {

  @BeforeClass
  public static void setUpClass() {
    Main.class.getClassLoader().setDefaultAssertionStatus(true);
  }

  /**
   * Checks that compilation fails correctly when annotation processor is called without specifying
   * output folder.
   */
  @Test
  public void testAnnotationProcessorError001() throws Exception {
    ErrorTestHelper te = new ErrorTestHelper();

    AbstractTestTools.createJavaFile(te.getSourceFolder(),"jack.incremental", "A.java",
        "package jack.incremental; \n"+
        "public class A {} \n");

    JackApiToolchain jackApiToolchain = AbstractTestTools.getCandidateToolchain(JackApiToolchain.class);
    jackApiToolchain.setAnnotationProcessorClass(ResourceAnnotationProcessor.class);

    try {
      jackApiToolchain.srcToExe(
          AbstractTestTools.getClasspathAsString(jackApiToolchain.getDefaultBootClasspath()),
          te.getOutputDexFolder(), te.getSourceFolder());
      Assert.fail();
    } catch (JackUserException e) {
      // Failure is ok since output for annotation processor is not specify.
      Assert.assertTrue(e.getMessage().contains("Unknown location"));
    }
  }

  /**
   * Checks that compilation succeed when running annotation processor to generate resource file.
   */
  @Test
  public void testAnnotationProcessorError002() throws Exception {
    runAnnotProcBuildingResource(new ErrorTestHelper());
  }

  /**
   * Checks that last compilation failed since the resource created by annotation processor already
   * exist.
   */
  @Test
  public void testAnnotationProcessorError003() throws Exception {
    ErrorTestHelper te = new ErrorTestHelper();

    runAnnotProcBuildingResource(te);

    JackApiToolchain jackApiToolchain = AbstractTestTools.getCandidateToolchain(JackApiToolchain.class);
    jackApiToolchain.setAnnotationProcessorClass(ResourceAnnotationProcessor.class);
    jackApiToolchain.setAnnotationProcessorOutDir(te.getTestingFolder());
    ByteArrayOutputStream errOut = new ByteArrayOutputStream();
    jackApiToolchain.setErrorStream(errOut);

    try {

      jackApiToolchain.srcToExe(
          AbstractTestTools.getClasspathAsString(jackApiToolchain.getDefaultBootClasspath())
          + File.pathSeparator + te.getJackFolder(), te.getOutputDexFolder(), te.getSourceFolder());

      Assert.fail();
    } catch (FrontendCompilationException e) {
      // Failure is ok since created file already exists
    } finally {
      Assert.assertTrue(errOut.toString().contains("Resource already created"));
    }
  }

  /**
   * Checks that compilation failed since the source file generated by the annotation processor
   * does not compile.
   */
  @Test
  public void testAnnotationProcessorError004() throws Exception {
    ErrorTestHelper te = new ErrorTestHelper();

    buildAnnotationRequiredByAnnotationProc(te, new Class<?>[] {SourceAnnotationTest.class,
        SourceErrorAnnotationTest.class});

    AbstractTestTools.createJavaFile(te.getSourceFolder(), "jack.incremental", "A.java", "package jack.incremental;\n"
        + "import " + SourceErrorAnnotationTest.class.getName() + ";\n"
        + "@" + SourceErrorAnnotationTest.class.getSimpleName() + "\n"
        + "public class A {}\n");


    JackApiToolchain jackApiToolchain = AbstractTestTools.getCandidateToolchain(JackApiToolchain.class);
    jackApiToolchain.setAnnotationProcessorClass(SourceAnnotationProcessor.class);
    jackApiToolchain.setAnnotationProcessorOutDir(te.getTestingFolder());
    ByteArrayOutputStream errOut = new ByteArrayOutputStream();
    jackApiToolchain.setErrorStream(errOut);

    try {
      jackApiToolchain.srcToExe(
          AbstractTestTools.getClasspathAsString(jackApiToolchain.getDefaultBootClasspath())
          + File.pathSeparator + te.getJackFolder(), te.getOutputDexFolder(), te.getSourceFolder());
      Assert.fail();
    } catch (FrontendCompilationException ex) {
      // Failure is ok since source generated by annotation processor does not compile.
    } finally {
      Assert.assertTrue(errOut.toString().contains("Syntax error on tokens, delete these tokens"));
    }
  }

  /**
   * Checks that compilation succeed to compile source file generated by the annotation processor.
   */
  @Test
  public void testAnnotationProcessorError005() throws Exception {
    ErrorTestHelper te = new ErrorTestHelper();

    buildAnnotationRequiredByAnnotationProc(te, new Class<?>[] {SourceAnnotationTest.class,
        SourceErrorAnnotationTest.class});

    AbstractTestTools.createJavaFile(te.getSourceFolder(), "jack.incremental", "A.java", "package jack.incremental;\n"
        + "import " + SourceAnnotationTest.class.getName() + ";\n"
        + "@" + SourceAnnotationTest.class.getSimpleName() + "\n"
        + "public class A {}\n");

    JackApiToolchain jackApiToolchain = AbstractTestTools.getCandidateToolchain(JackApiToolchain.class);
    jackApiToolchain.setAnnotationProcessorClass(SourceAnnotationProcessor.class);
    jackApiToolchain.setAnnotationProcessorOutDir(te.getTestingFolder());

    File dexOutput = te.getOutputDexFolder();
    jackApiToolchain.srcToExe(
        AbstractTestTools.getClasspathAsString(jackApiToolchain.getDefaultBootClasspath())
        + File.pathSeparator + te.getJackFolder(), dexOutput, te.getSourceFolder());

    DexFile dexFile = new DexFile(new File(dexOutput, jackApiToolchain.getBinaryFileName()));
    List<String> sourceFileInDex = new ArrayList<String>();
    for (ClassDefItem classDef : dexFile.ClassDefsSection.getItems()) {
      sourceFileInDex.add(classDef.getSourceFile().getStringValue());
    }

    Assert.assertTrue(sourceFileInDex.contains("ADuplicated.java"));
    Assert.assertTrue(sourceFileInDex.contains("A.java"));
  }

  private void runAnnotProcBuildingResource(@Nonnull ErrorTestHelper te) throws Exception {

    buildAnnotationRequiredByAnnotationProc(te, new Class<?>[] {ResourceAnnotationTest.class});

    AbstractTestTools.createJavaFile(te.getSourceFolder(), "jack.incremental", "A.java", "package jack.incremental;\n"
        + "import " + ResourceAnnotationTest.class.getName() + ";\n"
        + "@" + ResourceAnnotationTest.class.getSimpleName() + "\n"
        + "public class A {}\n");

    JackApiToolchain jackApiToolchain = AbstractTestTools.getCandidateToolchain(JackApiToolchain.class);
    jackApiToolchain.setAnnotationProcessorClass(ResourceAnnotationProcessor.class);
    jackApiToolchain.setAnnotationProcessorOutDir(te.getTestingFolder());

    jackApiToolchain.srcToExe(
        AbstractTestTools.getClasspathAsString(jackApiToolchain.getDefaultBootClasspath())
        + File.pathSeparator + te.getJackFolder(), te.getOutputDexFolder(), te.getSourceFolder());

    File discoverFile = new File(te.getTestingFolder(), ResourceAnnotationProcessor.FILENAME);
    Assert.assertTrue(discoverFile.exists());
    LineNumberReader lnr = new LineNumberReader(new FileReader(discoverFile));
    Assert.assertEquals(ResourceAnnotationTest.class.getName(), lnr.readLine());
    Assert.assertEquals("jack.incremental.A", lnr.readLine());
    Assert.assertNull(lnr.readLine());
    lnr.close();
  }

  private void buildAnnotationRequiredByAnnotationProc(@Nonnull ErrorTestHelper te,
      Class<?>[] annotationClasses) throws Exception {
    File targetAnnotationFileFolder =
        new File(te.getSourceFolder(), "com/android/jack/errorhandling/annotationprocessor/");
    if (!targetAnnotationFileFolder.mkdirs()) {
      Assert.fail("Fail to create folder " + targetAnnotationFileFolder.getAbsolutePath());
    }

    for (Class<?> annotationClass : annotationClasses) {
    Files.copy(new File(AbstractTestTools.getJackRootDir(),
        "toolchain/jack/jack/tests/com/android/jack/errorhandling/annotationprocessor/"
        + annotationClass.getSimpleName() + ".java"), new File(
        targetAnnotationFileFolder, annotationClass.getSimpleName() + ".java"));
    }

    // Compile annotation to a jack file
    JackApiToolchain jackApiToolchain = AbstractTestTools.getCandidateToolchain(JackApiToolchain.class);
    jackApiToolchain.srcToLib(
        AbstractTestTools.getClasspathAsString(jackApiToolchain.getDefaultBootClasspath()),
        te.getJackFolder(), false /* zipFiles = */, te.getSourceFolder());

    AbstractTestTools.deleteTempDir(te.getSourceFolder());
  }
}
