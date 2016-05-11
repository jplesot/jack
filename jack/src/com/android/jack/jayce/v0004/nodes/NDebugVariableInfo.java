/*
 * Copyright (C) 2016 The Android Open Source Project
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

package com.android.jack.jayce.v0004.nodes;

import com.android.jack.debug.DebugVariableInfoMarker;
import com.android.jack.jayce.v0004.io.ExportSession;
import com.android.jack.jayce.v0004.io.ImportHelper;
import com.android.jack.jayce.v0004.io.JayceInternalReaderImpl;
import com.android.jack.jayce.v0004.io.JayceInternalWriterImpl;
import com.android.jack.jayce.v0004.io.Token;

import java.io.IOException;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;


/**
 * This {@link NMarker} contains debug information related to variable.
 */
public class NDebugVariableInfo extends NMarker {

  @Nonnull
  public static final Token TOKEN = Token.DEBUG_VARIABLE_INFORMATION;

  @CheckForNull
  public String name;

  @CheckForNull
  public String type;

  @CheckForNull
  public String genericSignature;

  @Override
  public void importFromJast(@Nonnull ImportHelper loader, @Nonnull Object node) {
    DebugVariableInfoMarker debugVarInfo = (DebugVariableInfoMarker) node;
    name = debugVarInfo.getName();
    type = ImportHelper.getSignatureName(debugVarInfo.getType());
    genericSignature = debugVarInfo.getGenericSignature();
  }

  @Override
  @Nonnull
  public DebugVariableInfoMarker exportAsJast(@Nonnull ExportSession exportSession) {
    assert name != null;
    assert type != null;

    return new DebugVariableInfoMarker(name, exportSession.getLookup().getType(type),
        genericSignature);
  }

  @Override
  public void writeContent(@Nonnull JayceInternalWriterImpl out) throws IOException {
    assert name != null;
    assert type != null;
    out.writeString(name);
    out.writeId(type);
    out.writeString(genericSignature);
  }

  @Override
  public void readContent(@Nonnull JayceInternalReaderImpl in) throws IOException {
    name = in.readString();
    type = in.readId();
    genericSignature = in.readString();
  }

  @Override
  @Nonnull
  public Token getToken() {
    return TOKEN;
  }
}