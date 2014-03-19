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

package com.android.jack.bridge.test003.jack;

import java.lang.reflect.Method;

public class Data {

  static class A {
    public void unchanged() {}
    public final void unchangedFinal() {}
    public synchronized void unchangedSynchronized() {}
 }

  public static class B extends A {}

  public Method getMethod1() {
    Method[] methods = B.class.getMethods();
    for (Method method : methods) {
        if (method.getName().equals("unchanged")) {
            return method;
        }
    }
    return null;
  }

  public Method getMethod2() {
    Method[] methods = B.class.getMethods();
    for (Method method : methods) {
        if (method.getName().equals("unchangedFinal")) {
            return method;
        }
    }
    return null;
  }

  public Method getMethod3() {
    Method[] methods = B.class.getMethods();
    for (Method method : methods) {
        if (method.getName().equals("unchangedSynchronized")) {
            return method;
        }
    }
    return null;
  }
}
