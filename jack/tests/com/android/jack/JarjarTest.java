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

package com.android.jack;

import com.android.jack.category.RedundantTests;
import com.tonicsystems.jarjar.PackageRemapper;
import com.tonicsystems.jarjar.PatternElement;
import com.tonicsystems.jarjar.Rule;
import com.tonicsystems.jarjar.Wildcard;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.util.Collections;

import javax.annotation.Nonnull;

/**
 * JUnit test for compilation of field access.
 */
public class JarjarTest {

  @Nonnull
  private static PackageRemapper remapper;

  @BeforeClass
  public static void setUpClass() {
    Main.class.getClassLoader().setDefaultAssertionStatus(true);
    Rule rule = new Rule();
    rule.setPattern("org.**");
    rule.setResult("foo.@1");
    remapper = new PackageRemapper(PatternElement.createWildcards(Collections.singletonList(rule)));
  }

  @Test
  public void test001() throws Exception {
    String testName = "jarjar/test001";
    Options ecjArgs = TestTools.buildCommandLineArgs(
        TestTools.getJackTestsWithJackFolder(testName),
        new File(TestTools.getJackTestFolder(testName), "jarjar-rules.txt"));
    TestTools.runCompilation(ecjArgs);
  }

  @Test
  @Category(RedundantTests.class)
  public void test003() throws Exception {
    String testName = "jarjar/test003";
    Options ecjArgs = TestTools.buildCommandLineArgs(
        TestTools.getJackTestsWithJackFolder(testName),
        new File(TestTools.getJackTestFolder(testName), "jarjar-rules.txt"));
    TestTools.runCompilation(ecjArgs);
  }

  @Test
  public void test003_1() throws Exception {
    String testName = "jarjar/test003";
    Options options = new Options();
    options.jarjarRulesFile = new File(TestTools.getJackTestFolder(testName), "jarjar-rules.txt");
    File lib = TestTools.createTempFile("jarjarTest003Jack", ".zip");
    TestTools.compileSourceToJack(options, TestTools.getJackTestsWithJackFolder(testName),
        TestTools.getDefaultBootclasspathString(), lib, true);
    TestTools.compileSourceToJack(new Options(),
        new File(TestTools.getJackTestFolder(testName),
            "dontcompile/TestWithRelocatedReference.java"),
        TestTools.getDefaultBootclasspathString() + File.pathSeparator + lib.getAbsolutePath(),
        TestTools.createTempFile("jarjarTest003dx", ".zip"),
        true);

  }

  @Test
  public void testMapValue() {
    assertUnchangedValue("[^\\s;/@&=,.?:+$]");
    assertUnchangedValue("[Ljava/lang/Object;");
    assertUnchangedValue("[Lorg/example/Object;");
    assertUnchangedValue("[Ljava.lang.Object;");
    assertUnchangedValue("[Lorg.example/Object;");
    assertUnchangedValue("[L;");
    assertUnchangedValue("[Lorg.example.Object;;");
    assertUnchangedValue("[Lorg.example.Obj ct;");
    assertUnchangedValue("org.example/Object");

    Assert.assertEquals("[Lfoo.example.Object;", remapper.mapValue("[Lorg.example.Object;"));
    Assert.assertEquals("foo.example.Object", remapper.mapValue("org.example.Object"));
    Assert.assertEquals("foo/example/Object", remapper.mapValue("org/example/Object"));
    Assert.assertEquals("foo/example.Object", remapper.mapValue("org/example.Object")); // path match

    Assert.assertEquals("foo.example.package-info", remapper.mapValue("org.example.package-info"));
    Assert.assertEquals("foo/example/package-info", remapper.mapValue("org/example/package-info"));
    Assert.assertEquals("foo/example.package-info", remapper.mapValue("org/example.package-info"));
  }

  private void assertUnchangedValue(String value) {
    Assert.assertEquals(value, remapper.mapValue(value));
  }

  @Test
  public void testWildcards() {
    wildcard("net/sf/cglib/**", "foo/@1", "net/sf/cglib/proxy/Mixin$Generator",
        "foo/proxy/Mixin$Generator");
    wildcard("net/sf/cglib/**", "foo/@1", "net/sf/cglib/Bar", "foo/Bar");
    wildcard("net/sf/cglib/**", "foo/@1", "net/sf/cglib/Bar/Baz", "foo/Bar/Baz");
    wildcard("net/sf/cglib/**", "foo/@1", "net/sf/cglib/", "foo/");
    wildcard("net/sf/cglib/**", "foo/@1", "net/sf/cglib/!", null);
    wildcard("net/sf/cglib/*", "foo/@1", "net/sf/cglib/Bar", "foo/Bar");
    wildcard("net/sf/cglib/*/*", "foo/@2/@1", "net/sf/cglib/Bar/Baz", "foo/Baz/Bar");
}

  private void wildcard(String pattern, String result, String value, String expect) {
      Wildcard wc = new Wildcard(pattern, result);
      Assert.assertEquals(expect, wc.replace(value));
  }
}
