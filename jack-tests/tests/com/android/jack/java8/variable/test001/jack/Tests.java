/*
 * Copyright (C) 2016 The Android Open Source Project
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

package com.android.jack.java8.variable.test001.jack;

import junit.framework.Assert;

import org.junit.Test;

public class Tests {

  public static interface I {
    int get();
  }

  @Test
  public void test001() {
    int value = 2;
    int value2 = callGet(() -> value);
    value = 1;
    Assert.fail();
  }

  private static int callGet(I i) {
    return i.get();
  }

}
