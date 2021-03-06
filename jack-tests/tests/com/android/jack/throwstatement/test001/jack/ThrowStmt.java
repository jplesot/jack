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
package com.android.jack.throwstatement.test001.jack;

/**
 * Throw tests.
 */
public class ThrowStmt {

  public static void throwException(RuntimeException e) {
    throw e;
  }

  public static void reThrowException(RuntimeException e) {
    try {
      throw e;
    } catch (RuntimeException e1) {
      throw e1;
    }
  }

  public static void reThrowException(RuntimeException e1, RuntimeException e2) {
    try {
      try {
        throw e1;
      } catch (RuntimeException e3) {
        throw e3;
      }
    } catch (RuntimeException e4) {
      throw e2;
    }
  }
}
