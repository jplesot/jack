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

package com.android.sched.util.config;

import com.android.sched.util.RunnableHooks;
import com.android.sched.util.config.ChainedException.ChainedExceptionBuilder;
import com.android.sched.util.config.id.ObjectId;
import com.android.sched.util.config.id.PropertyId;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * This class is used to configure a {@link Config} object. All methods but {@link #build()} do
 * not report any problem. Problems are reported in one block through a {@link ChainedException}
 * during the {@link #build()} operation.
 */
public class GatherConfigBuilder {
  @Nonnull
  private final AsapConfigBuilder builder = new AsapConfigBuilder();

  @Nonnull
  private final ChainedExceptionBuilder<ConfigurationException> exceptions =
      new ChainedExceptionBuilder<ConfigurationException>();

  @Nonnull
  public GatherConfigBuilder load(@Nonnull InputStream is, @Nonnull Location location)
      throws IOException {
    try {
      builder.load(is, location);
    } catch (ConfigurationException e) {
      exceptions.appendException(e);
    }

    return this;
  }

  @Nonnull
  public GatherConfigBuilder set(@Nonnull String name, @Nonnull String value) {
    try {
      builder.set(name, value);
    } catch (PropertyIdException e) {
      exceptions.appendException(e);
    } catch (UnknownPropertyNameException e) {
      exceptions.appendException(e);
    }

    return this;
  }

  @Nonnull
  public GatherConfigBuilder set(
      @Nonnull String name, @Nonnull String value, @Nonnull Location location) {
    try {
      builder.set(name, value, location);
    } catch (UnknownPropertyNameException e) {
      exceptions.appendException(e);
    } catch (PropertyIdException e) {
      exceptions.appendException(e);
    }

    return this;
  }

  @Nonnull
  public GatherConfigBuilder set(@Nonnull PropertyId<?> propertyId, @Nonnull String value) {
    try {
      builder.set(propertyId, value);
    } catch (UnknownPropertyIdException e) {
      exceptions.appendException(e);
    } catch (PropertyIdException e) {
      exceptions.appendException(e);
    }

    return this;
  }

  @Nonnull
  public GatherConfigBuilder set(
      @Nonnull PropertyId<?> propertyId, @Nonnull String value, @Nonnull Location location) {
    try {
      builder.set(propertyId, value, location);
    } catch (UnknownPropertyIdException e) {
      exceptions.appendException(e);
    } catch (PropertyIdException e) {
      exceptions.appendException(e);
    }

    return this;
  }

  @Nonnull
  public <T> GatherConfigBuilder set(@Nonnull ObjectId<T> objectId, @Nonnull T value) {
    builder.set(objectId, value);

    return this;
  }

  @Nonnull
  public <T> GatherConfigBuilder set(
      @Nonnull ObjectId<T> objectId, @Nonnull T value, @Nonnull Location location) {
    builder.set(objectId, value, location);

    return this;
  }

  @Nonnull
  public GatherConfigBuilder setDebug() {
    builder.setDebug();

    return this;
  }

  @Nonnull
  public GatherConfigBuilder setStrictMode() {
    builder.setStrictMode();

    return this;
  }

  @Nonnull
  public GatherConfigBuilder setHooks(@Nonnull RunnableHooks hooks) {
    builder.setHooks(hooks);

    return this;
  }

  /**
   * Builds the {@link Config} with all defined property values.
   *
   * @return the {@link Config}.
   * @throws ConfigurationException
   */
  @Nonnull
  public Config build() throws ConfigurationException {
    try {
      return builder.build();
    } catch (ConfigurationException e) {
      exceptions.appendException(e);
      throw exceptions.getException();
    }
  }

  @Nonnull
  public Collection<PropertyId<?>> getPropertyIds() {
    return builder.getPropertyIds();
   }

  @CheckForNull
  public String getDefaultValue(@Nonnull PropertyId<?> propertyId) {
    return builder.getDefaultValue(propertyId);
  }

  @Nonnull
  public GatherConfigBuilder processEnvironmentVariables(@Nonnull String envPrefix) {
    try {
      builder.processEnvironmentVariables(envPrefix);
    } catch (ConfigurationException e) {
      exceptions.appendException(e);
    }

    return this;
  }

  public void pushDefaultLocation(@Nonnull Location location) {
    builder.pushDefaultLocation(location);
  }

  public void popDefaultLocation() {
    builder.popDefaultLocation();
  }
}
