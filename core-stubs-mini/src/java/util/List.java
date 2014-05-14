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

package java.util;

public interface List<E> extends java.util.Collection<E> {
  public abstract void add(int location, E object);

  public abstract boolean add(E object);

  public abstract boolean addAll(int location, java.util.Collection<? extends E> collection);

  public abstract boolean addAll(java.util.Collection<? extends E> collection);

  public abstract void clear();

  public abstract boolean contains(java.lang.Object object);

  public abstract boolean containsAll(java.util.Collection<?> collection);

  public abstract boolean equals(java.lang.Object object);

  public abstract E get(int location);

  public abstract int hashCode();

  public abstract int indexOf(java.lang.Object object);

  public abstract boolean isEmpty();

  public abstract java.util.Iterator<E> iterator();

  public abstract int lastIndexOf(java.lang.Object object);

  public abstract java.util.ListIterator<E> listIterator();

  public abstract java.util.ListIterator<E> listIterator(int location);

  public abstract E remove(int location);

  public abstract boolean remove(java.lang.Object object);

  public abstract boolean removeAll(java.util.Collection<?> collection);

  public abstract boolean retainAll(java.util.Collection<?> collection);

  public abstract E set(int location, E object);

  public abstract int size();

  public abstract java.util.List<E> subList(int start, int end);

  public abstract java.lang.Object[] toArray();

  public abstract <T> T[] toArray(T[] array);
}
