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

import com.android.jack.vfs.VDir;
import com.android.jack.vfs.VElement;
import com.android.sched.util.config.FileLocation;
import com.android.sched.util.config.Location;
import com.android.sched.util.config.ZipLocation;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.zip.ZipEntry;

import javax.annotation.Nonnull;

class ZipDir implements VDir {

  @Nonnull
  protected final HashMap<String, VElement> subs = new HashMap<String, VElement>();
  @Nonnull
  private final String name;

  @Nonnull
  private final Location location;

  ZipDir(@Nonnull String name, @Nonnull File zip, @Nonnull ZipEntry entry) {
    this.name = name;
    this.location = new ZipLocation(new FileLocation(zip), entry);
  }

  @Nonnull
  @Override
  public String getName() {
    return name;
  }

  @Nonnull
  @Override
  public Collection<? extends VElement> list() {
    return subs.values();
  }

  @Nonnull
  @Override
  public String toString() {
    return location.getDescription();
  }

  @Override
  @Nonnull
  public Location getLocation() {
    return location;
  }

}
