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

package com.android.jack.java8.gwt.test012.jack;

import org.junit.Test;

import junit.framework.Assert;

public class Java8Test {

  static class ArrayElem {
  }

  interface ArrayCtor {
    ArrayElem [][][] copy(int i);
  }

  @Test
  public void testArrayConstructorReference() {
    ArrayCtor ctor = ArrayElem[][][]::new;
    ArrayElem[][][] array = ctor.copy(100);
    Assert.assertEquals(100, array.length);
  }
}
