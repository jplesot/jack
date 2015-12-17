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

package com.android.jack.java8.gwt.test034.jack;

import org.junit.Test;

import junit.framework.Assert;

public class Java8Test {

  interface InterfaceWithTwoDefenderMethods {
  default String foo() { return "interface.foo"; }
  default String bar() { return this.foo() + " " + foo(); }
  }

  interface InterfaceImplementOneDefenderMethod extends InterfaceWithTwoDefenderMethods {
  default String foo() { return "interface1.foo"; }
  }

  interface InterfaceImplementZeroDefenderMethod extends InterfaceWithTwoDefenderMethods {
  }

  class ClassImplementsTwoInterfaces implements InterfaceImplementOneDefenderMethod,
    InterfaceImplementZeroDefenderMethod {
}

  @Test
  public void testClassImplementsTwoInterfacesWithSameDefenderMethod() {
    ClassImplementsTwoInterfaces c = new ClassImplementsTwoInterfaces();
    Assert.assertEquals("interface1.foo", c.foo());
  }
}
