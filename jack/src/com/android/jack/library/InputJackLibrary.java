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

package com.android.jack.library;

import com.android.jack.Jack;
import com.android.jack.jayce.JayceProperties;
import com.android.sched.util.log.LoggerFactory;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;


/**
 * Interface representing an input jack library.
 */
public abstract class InputJackLibrary  extends CommonJackLibrary implements InputLibrary {

  @Nonnull
  protected static Logger logger = LoggerFactory.getLogger();

  @Nonnegative
  private final int minorVersion;

  @Nonnull
  private final Constructor<?> jayceReaderConstructor;

  @Nonnegative
  private final int jayceMajorVersion;
  @Nonnegative
  private final int jayceMinorVersion;

  public InputJackLibrary(@Nonnull Properties libraryProperties) throws LibraryFormatException {
    super(libraryProperties);

    try {
      minorVersion = Integer.parseInt(getProperty(KEY_LIB_MINOR_VERSION));
    } catch (NumberFormatException e) {
      logger.log(Level.SEVERE, "Fails to parse the property " + KEY_LIB_MINOR_VERSION
          + " from " + getLocation().getDescription(), e);
      throw new LibraryFormatException(getLocation());
    }

    String jayceMajorVersionStr = getProperty(JayceProperties.KEY_JAYCE_MAJOR_VERSION);
    try {
      jayceMajorVersion = Integer.parseInt(jayceMajorVersionStr);
    } catch (NumberFormatException e) {
      logger.log(Level.SEVERE, "Failed to parse the property "
          + JayceProperties.KEY_JAYCE_MAJOR_VERSION + " from "
          + getLocation().getDescription(), e);
      throw new LibraryFormatException(getLocation());
    }

    try {
      jayceMinorVersion = Integer.parseInt(getProperty(JayceProperties.KEY_JAYCE_MINOR_VERSION));
    } catch (NumberFormatException e) {
      logger.log(Level.SEVERE, "Failed to parse the property "
          + JayceProperties.KEY_JAYCE_MINOR_VERSION + " from "
          + getLocation().getDescription(), e);
      throw new LibraryFormatException(getLocation());
    }

    String className = "com.android.jack.jayce.v"
        + JackLibraryFactory.getVersionString(jayceMajorVersion) + ".io.JayceInternalReaderImpl";

    Class<?> jayceReaderClass;
    try {
      jayceReaderClass = Class.forName(className);
    } catch (ClassNotFoundException e) {
      logger.log(Level.SEVERE, "Library " + getLocation().getDescription()
          + " is invalid: Jayce version " + jayceMajorVersionStr + " not supported", e);
      throw new LibraryFormatException(getLocation());
    }
    try {
      jayceReaderConstructor = jayceReaderClass.getConstructor(new Class[] {InputStream.class});
    } catch (SecurityException e) {
      throw new AssertionError("Security issue with Jayce stream");
    } catch (NoSuchMethodException e) {
      throw new AssertionError("Jayce processing method not found for version " +
          jayceMajorVersionStr);
    }

  }

  @Nonnull
  public final Constructor<?> getJayceReaderConstructor() {
    return jayceReaderConstructor;
  }

  @Nonnegative
  public final int getJayceMajorVersion() {
    return jayceMajorVersion;
  }

  @Nonnegative
  public final int getJayceMinorVersion() {
    return jayceMinorVersion;
  }

  @Override
  @Nonnegative
  public final int getMinorVersion() {
    return minorVersion;
  }

  protected void check() throws LibraryVersionException, LibraryFormatException {
    getProperty(JackLibrary.KEY_LIB_EMITTER);
    getProperty(JackLibrary.KEY_LIB_EMITTER_VERSION);
    getProperty(JackLibrary.KEY_LIB_MAJOR_VERSION);
    getProperty(JackLibrary.KEY_LIB_MINOR_VERSION);

    int majorVersion = getMajorVersion();
    int minorVersion = getMinorVersion();
    int supportedMinorMin = getSupportedMinorMin();
    int supportedMinor = getSupportedMinor();

    if (minorVersion < supportedMinorMin) {
      throw new LibraryVersionException("The version of the library "
          + getLocation().getDescription() + " is not supported anymore." + "Library version: "
          + majorVersion + "." + minorVersion + " - Current version: " + majorVersion + "."
          + supportedMinor + " - Minimum compatible version: " + majorVersion + "."
          + supportedMinorMin);
    } else if (minorVersion > supportedMinor) {
      throw new LibraryVersionException("The version of the library "
          + getLocation().getDescription() + " is too recent." + "Library version: " + majorVersion
          + "." + minorVersion + " - Current version: " + majorVersion + "." + supportedMinor);
    } else if (minorVersion < supportedMinor) {
      Jack.getSession().getUserLogger().log(Level.WARNING, "The version of the library "
          + getLocation().getDescription() + " is older than the current version but is "
          + "supported. File version: {0}.{1} - Current version: {2}.{3}", new Object[] {
          Integer.valueOf(majorVersion), Integer.valueOf(minorVersion),
          Integer.valueOf(majorVersion), Integer.valueOf(supportedMinor)});
    }

    for (FileType ft : fileTypes) {
      ft.check();
    }
  }

  @Nonnegative
  public abstract int getSupportedMinor();

  @Nonnegative
  public abstract int getSupportedMinorMin();
}
