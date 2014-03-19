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

package com.android.jack.shrob.test017.jack;

import com.android.jack.shrob.test017.jack.Reflect2.A;
import com.android.jack.shrob.test017.jack.Reflect2.B;

public class Reflect {

  void keep() throws ClassNotFoundException {
    Class.forName("com.android.jack.shrob.test017.jack.Reflect2");
    Class.forName("ClassThatDoesNotExists");
  }

  void keep2() throws NoSuchFieldException, SecurityException {
    Reflect2.class.getField("fieldPublic");
    Reflect2.class.getField("fieldPrivate");
  }

  void keep3() throws NoSuchFieldException, SecurityException {
    Reflect2.class.getDeclaredField("fieldPublic2");
    Reflect2.class.getDeclaredField("fieldPrivate2");
  }

  void keep4() throws SecurityException, NoSuchMethodException {
    Reflect2.class.getMethod("m", new Class[]{A.class});
    Reflect2.class.getMethod("m", new Class[]{B.class});
    Reflect2.class.getMethod("methodThatDoesNotExist", new Class[]{A.class});
  }

  void keep5() throws SecurityException, NoSuchMethodException {
    Reflect2.class.getDeclaredMethod("m2", new Class[]{A.class});
    Reflect2.class.getDeclaredMethod("m2", new Class[]{B.class});
  }

//  void keep6() throws SecurityException {
//    AtomicIntegerFieldUpdater.newUpdater(Reflect2.class, "fieldPublic3");
//    AtomicIntegerFieldUpdater.newUpdater(Reflect2.class, "fieldLong5");
//    AtomicIntegerFieldUpdater.newUpdater(Reflect2.class, "fieldFloat");
//  }
//
//  void keep7() throws SecurityException {
//    AtomicLongFieldUpdater.newUpdater(Reflect2.class, "fieldPublic");
//    AtomicLongFieldUpdater.newUpdater(Reflect2.class, "fieldLong");
//    AtomicLongFieldUpdater.newUpdater(Reflect2.class, "fieldLong2");
//    AtomicLongFieldUpdater.newUpdater(Reflect2.class, "fieldLong3");
//    AtomicLongFieldUpdater.newUpdater(Reflect2.class, "fieldLong4");
//    AtomicLongFieldUpdater.newUpdater(Reflect2.class, "fieldFloat2");
//  }
//
//  void keep8() throws SecurityException {
//    AtomicReferenceFieldUpdater.newUpdater(Reflect2.class, Reflect2.A.class, "a");
//    AtomicReferenceFieldUpdater.newUpdater(Reflect2.class, Reflect2.A.class, "b");
//    AtomicReferenceFieldUpdater.newUpdater(Reflect2.class, Object.class, "c");
//  }
}
