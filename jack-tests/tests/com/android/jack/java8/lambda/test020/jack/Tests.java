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

package com.android.jack.java8.lambda.test020.jack;


import junit.framework.Assert;

import org.junit.Test;

/**
 * Test lambda expression embedded in a class define inside a method.
 */
public class Tests {

  @Test
  public void test001() {
    Lambda lambda = new Lambda();
    Assert.assertEquals(5, lambda.test1(4));
    Assert.assertEquals(6, lambda.test2(4));
    Assert.assertEquals(7, lambda.test3(4));
  }
}
