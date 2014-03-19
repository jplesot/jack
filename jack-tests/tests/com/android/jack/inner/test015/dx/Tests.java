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

package com.android.jack.inner.test015.dx;

import com.android.jack.inner.test015.jack.B;

import org.junit.Assert;
import org.junit.Test;

public class Tests {

  @Test
  public void test1() {
    B b = new B();
    Assert.assertEquals("B", b.getf());
    Assert.assertEquals("C", b.c.getf());
  }
}
