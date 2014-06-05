/*
* Copyright (C) 2014 The Android Open Source Project
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

package java.lang.reflect;

public interface AnnotatedElement {
  public abstract <T extends java.lang.annotation.Annotation> T getAnnotation(
      java.lang.Class<T> annotationType);

  public abstract java.lang.annotation.Annotation[] getAnnotations();

  public abstract java.lang.annotation.Annotation[] getDeclaredAnnotations();

  public abstract boolean isAnnotationPresent(
      java.lang.Class<? extends java.lang.annotation.Annotation> annotationType);
}
