/*
 * Copyright 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.android.jack.java8.gwt.test007.jack;

import org.junit.Test;

import junit.framework.Assert;

public class Java8Test {
  int local = 42;

  interface Lambda<T> {
    T run(int a, int b);
  }

  class AcceptsLambda<T> {
    public T accept(Lambda<T> foo) {
      return foo.run(10, 20);
    }
  }

  static class Static {
    static int staticField;
    static {
      staticField = 99;
    }
    static Integer staticMethod(int x, int y) { return x + y + staticField; }
  }

  static class StaticFailIfClinitRuns {
    static {
      Assert.fail("clinit() shouldn't run from just taking a reference to a method");
    }

    public static Integer staticMethod(int x, int y) {
      return null;
    }
  }

  private static Lambda<Integer> dummyMethodToMakeCheckStyleHappy(Lambda<Integer> l) {
    return l;
  }

  @Test
  public void testStaticReferenceBinding() throws Exception {
    Assert.assertEquals(129, new AcceptsLambda<Integer>().accept(Static::staticMethod).intValue());
    // if this next line runs a clinit, it fails
    Lambda l = dummyMethodToMakeCheckStyleHappy(StaticFailIfClinitRuns::staticMethod);
    try {
      // but now it should fail
      l.run(1,2);
      Assert.fail("Clinit should have run for the first time");
    } catch (AssertionError ae) {
      // success, it was supposed to throw!
    }
  }
}
