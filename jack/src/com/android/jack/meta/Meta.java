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

package com.android.jack.meta;

import com.android.jack.resource.ResourceOrMeta;
import com.android.sched.item.Description;
import com.android.sched.util.location.Location;
import com.android.sched.vfs.InputVFile;
import com.android.sched.vfs.VPath;

import javax.annotation.Nonnull;

/**
 * Represents a meta.
 */
@Description("Represents a meta")
public class Meta extends ResourceOrMeta {

  public Meta(@Nonnull VPath path, @Nonnull InputVFile vFile, @Nonnull Location location) {
    super(path, vFile, location);
  }

}
