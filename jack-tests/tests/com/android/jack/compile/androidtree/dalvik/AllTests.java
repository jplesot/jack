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

package com.android.jack.compile.androidtree.dalvik;


import com.android.jack.compile.androidtree.dalvik.compilerregressions.CompilerRegressionsTest;
import com.android.jack.compile.androidtree.dalvik.finalizer.FinalizerTest;
import com.android.jack.compile.androidtree.dalvik.omnibus.OmnibusCompilationTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(value = {CompilerRegressionsTest.class,
    FinalizerTest.class,
    OmnibusCompilationTest.class})
public class AllTests {
}