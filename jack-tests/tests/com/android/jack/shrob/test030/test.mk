# Copyright (C) 2013 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

private_path:= $(call my-dir)

include $(JACK_CLEAR_VARS)

JACKTEST_MODULE := shrob/test030
JACKTEST_ARGS := --proguard-flags $(private_path)/proguard.flags001 --proguard-flags $(private_path)/../dontobfuscate.flags
JACKTEST_WITHJACK_SRC := $(call all-java-files-under, $(abspath $(private_path)/jack/))
JACKTEST_WITHDX_SRC := $(private_path)/dx/Tests.java
JACKTEST_JUNIT := com.android.jack.$(subst /,.,$(JACKTEST_MODULE)).dx.Tests

include $(JACK_RUN_TEST)
