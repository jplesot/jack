; Copyright (C) 2016 The Android Open Source Project
;
; Licensed under the Apache License, Version 2.0 (the "License");
; you may not use this file except in compliance with the License.
; You may obtain a copy of the License at
;
;      http://www.apache.org/licenses/LICENSE-2.0
;
; Unless required by applicable law or agreed to in writing, software
; distributed under the License is distributed on an "AS IS" BASIS,
; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
; See the License for the specific language governing permissions and
; limitations under the License.

.class public com/android/jack/jill/test003/jack/External
.super java/lang/Object

.method public <init>()V
    aload_0
    invokespecial java/lang/Object/<init>()V
    return
.end method

.method public static greaterThan1(DD)Z
        .limit locals 4
        .limit stack 3
    dload_0
    dload_2
    dcmpl
    aconst_null
    swap
    ifle if_label
    pop
    iconst_1
    ireturn
if_label:
    pop
    iconst_0
    ireturn
.end method

.method public static greaterThan2(DD)Z
        .limit locals 4
        .limit stack 5
    aconst_null
    dload_0
    dload_2
    dcmpl
    swap
    pop
    ifle if_label
    iconst_1
    ireturn
if_label:
    iconst_0
    ireturn
.end method

.method public static greaterThan3(DD)Z
        .limit locals 4
        .limit stack 3
    dload_0
    dload_2
    dcmpl
    iconst_1
    iconst_2
    iadd
    swap
    ifle if_label
    pop
    iconst_1
    ireturn
if_label:
    pop
    iconst_0
    ireturn
.end method