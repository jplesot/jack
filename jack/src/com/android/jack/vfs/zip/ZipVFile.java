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

package com.android.jack.vfs.zip;

import com.android.jack.vfs.VFile;
import com.android.sched.util.config.FileLocation;
import com.android.sched.util.config.Location;
import com.android.sched.util.config.ZipLocation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.annotation.Nonnull;

class ZipVFile implements VFile {

  @Nonnull
  private final String name;
  @Nonnull
  private final ZipFile zip;
  @Nonnull
  private final ZipEntry entry;

  ZipVFile(@Nonnull String name, @Nonnull ZipFile zip, @Nonnull ZipEntry entry) {
    this.name = name;
    this.zip = zip;
    this.entry = entry;
  }

  @Nonnull
  @Override
  public String getName() {
    return name;
  }

  @Nonnull
  @Override
  public InputStream openRead() throws IOException {
    return zip.getInputStream(entry);
  }

  @Nonnull
  @Override
  public String toString() {
    return name;
  }

  @Override
  @Nonnull
  public Location getLocation() {
    return new ZipLocation(new FileLocation(new File(zip.getName())), entry);
  }

}
