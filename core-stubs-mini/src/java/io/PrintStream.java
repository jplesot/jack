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

package java.io;

public class PrintStream extends java.io.FilterOutputStream implements java.lang.Appendable,
    java.io.Closeable {
  public PrintStream(java.io.OutputStream out) {
    super((java.io.OutputStream) null);
    throw new RuntimeException("Stub!");
  }

  public PrintStream(java.io.OutputStream out, boolean autoFlush) {
    super((java.io.OutputStream) null);
    throw new RuntimeException("Stub!");
  }

  public PrintStream(java.io.OutputStream out, boolean autoFlush, java.lang.String charsetName)
      throws java.io.UnsupportedEncodingException {
    super((java.io.OutputStream) null);
    throw new RuntimeException("Stub!");
  }

  public PrintStream(java.io.File file) throws java.io.FileNotFoundException {
    super((java.io.OutputStream) null);
    throw new RuntimeException("Stub!");
  }

  public PrintStream(java.io.File file, java.lang.String charsetName)
      throws java.io.FileNotFoundException, java.io.UnsupportedEncodingException {
    super((java.io.OutputStream) null);
    throw new RuntimeException("Stub!");
  }

  public PrintStream(java.lang.String fileName) throws java.io.FileNotFoundException {
    super((java.io.OutputStream) null);
    throw new RuntimeException("Stub!");
  }

  public PrintStream(java.lang.String fileName, java.lang.String charsetName)
      throws java.io.FileNotFoundException, java.io.UnsupportedEncodingException {
    super((java.io.OutputStream) null);
    throw new RuntimeException("Stub!");
  }

  public boolean checkError() {
    throw new RuntimeException("Stub!");
  }

  protected void clearError() {
    throw new RuntimeException("Stub!");
  }

  public synchronized void close() {
    throw new RuntimeException("Stub!");
  }

  public synchronized void flush() {
    throw new RuntimeException("Stub!");
  }

  public java.io.PrintStream format(java.lang.String format, java.lang.Object... args) {
    throw new RuntimeException("Stub!");
  }

  public java.io.PrintStream format(java.util.Locale l, java.lang.String format,
      java.lang.Object... args) {
    throw new RuntimeException("Stub!");
  }

  public java.io.PrintStream printf(java.lang.String format, java.lang.Object... args) {
    throw new RuntimeException("Stub!");
  }

  public java.io.PrintStream printf(java.util.Locale l, java.lang.String format,
      java.lang.Object... args) {
    throw new RuntimeException("Stub!");
  }

  public void print(char[] chars) {
    throw new RuntimeException("Stub!");
  }

  public void print(char c) {
    throw new RuntimeException("Stub!");
  }

  public void print(double d) {
    throw new RuntimeException("Stub!");
  }

  public void print(float f) {
    throw new RuntimeException("Stub!");
  }

  public void print(int i) {
    throw new RuntimeException("Stub!");
  }

  public void print(long l) {
    throw new RuntimeException("Stub!");
  }

  public void print(java.lang.Object o) {
    throw new RuntimeException("Stub!");
  }

  public synchronized void print(java.lang.String str) {
    throw new RuntimeException("Stub!");
  }

  public void print(boolean b) {
    throw new RuntimeException("Stub!");
  }

  public void println() {
    throw new RuntimeException("Stub!");
  }

  public void println(char[] chars) {
    throw new RuntimeException("Stub!");
  }

  public void println(char c) {
    throw new RuntimeException("Stub!");
  }

  public void println(double d) {
    throw new RuntimeException("Stub!");
  }

  public void println(float f) {
    throw new RuntimeException("Stub!");
  }

  public void println(int i) {
    throw new RuntimeException("Stub!");
  }

  public void println(long l) {
    throw new RuntimeException("Stub!");
  }

  public void println(java.lang.Object o) {
    throw new RuntimeException("Stub!");
  }

  public synchronized void println(java.lang.String str) {
    throw new RuntimeException("Stub!");
  }

  public void println(boolean b) {
    throw new RuntimeException("Stub!");
  }

  protected void setError() {
    throw new RuntimeException("Stub!");
  }

  public void write(byte[] buffer, int offset, int length) {
    throw new RuntimeException("Stub!");
  }

  public synchronized void write(int oneByte) {
    throw new RuntimeException("Stub!");
  }

  public java.io.PrintStream append(char c) {
    throw new RuntimeException("Stub!");
  }

  public java.io.PrintStream append(java.lang.CharSequence charSequence) {
    throw new RuntimeException("Stub!");
  }

  public java.io.PrintStream append(java.lang.CharSequence charSequence, int start, int end) {
    throw new RuntimeException("Stub!");
  }
}
