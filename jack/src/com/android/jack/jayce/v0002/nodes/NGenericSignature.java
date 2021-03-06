/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.android.jack.jayce.v0002.nodes;

import com.android.jack.ir.ast.marker.GenericSignature;
import com.android.jack.jayce.v0002.io.ExportSession;
import com.android.jack.jayce.v0002.io.ImportHelper;
import com.android.jack.jayce.v0002.io.JayceInternalReaderImpl;
import com.android.jack.jayce.v0002.io.JayceInternalWriterImpl;
import com.android.jack.jayce.v0002.io.Token;

import java.io.IOException;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * This {@link NMarker} holds generic signature retrieved from ecj.
 */
public class NGenericSignature extends NMarker {

  @Nonnull
  public static final Token TOKEN = Token.GENERIC_SIGNATURE;

  @CheckForNull
  public String genericSignature;

  @Override
  public void importFromJast(@Nonnull ImportHelper loader, @Nonnull Object node) {
    GenericSignature marker = (GenericSignature) node;
    genericSignature = marker.getGenericSignature();
  }

  @Override
  @Nonnull
  public GenericSignature exportAsJast(@Nonnull ExportSession exportSession) {
    assert genericSignature != null;
    return new GenericSignature(genericSignature);
  }

  @Override
  public void writeContent(@Nonnull JayceInternalWriterImpl out) throws IOException {
    out.writeString(genericSignature);
  }

  @Override
  public void readContent(@Nonnull JayceInternalReaderImpl in) throws IOException {
    genericSignature = in.readString();
  }

  @Override
  @Nonnull
  public Token getToken() {
    return TOKEN;
  }
}
