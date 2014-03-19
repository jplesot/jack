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

package com.android.jack.inner.test015.jack;

import com.android.jack.inner.test015.lib.A;

public class B extends A {
  public C c = new C();

  public B() {
    f = "B";
  }

  public String getf() {
    return this.f;
  }

  public class C extends A {
    public C() {
      f = "C";
    }

    public String getf() {
      return this.f;
    }
  }
}