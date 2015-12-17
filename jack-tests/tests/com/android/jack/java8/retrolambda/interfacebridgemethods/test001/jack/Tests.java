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
// Copyright © 2013-2015 Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package com.android.jack.java8.retrolambda.interfacebridgemethods.test001.jack;

import org.junit.Assert;
import org.junit.Test;

public class Tests {

  @Test
  @SuppressWarnings("Convert2Lambda")
  public void will_remove_bridge_methods_from_interfaces() {
      BridgeChild child = new BridgeChild() {
          @Override
          public String foo() {
              return "foo";
          }
      };
      Assert.assertEquals("direct call", child.foo(), "foo");
      Assert.assertEquals("bridged call", ((BridgeParent) child).foo(), (Object) "foo");
  }

  public interface BridgeParent {
      Object foo();
  }

  public interface BridgeChild extends BridgeParent {
      @Override
      String foo(); // refined return type
  }

}

