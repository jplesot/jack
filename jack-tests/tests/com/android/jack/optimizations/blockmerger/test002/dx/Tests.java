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

package com.android.jack.optimizations.blockmerger.test002.dx;

import com.android.jack.optimizations.blockmerger.test002.jack.*;

import junit.framework.Assert;
import org.junit.Test;

/** Just touch all the classes */
public class Tests {
  @Test
  public void test001() {
    A a = new A();
    Assert.assertEquals("1|0|0|0|-1", a.testA());
    Assert.assertEquals("11|20|30|-40|-51", a.testB());
    Assert.assertEquals("1|0|-1", a.testC());
  }
}
