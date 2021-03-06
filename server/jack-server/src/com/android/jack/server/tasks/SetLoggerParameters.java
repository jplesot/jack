/*
 * Copyright (C) 2015 The Android Open Source Project
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

package com.android.jack.server.tasks;

import com.android.jack.server.JackHttpServer;
import com.android.jack.server.ServerLogConfiguration;
import com.android.sched.util.codec.ParsingException;
import com.android.sched.util.log.LoggerFactory;

import org.simpleframework.http.Part;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.Status;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;


/**
 * Administrative task: Parameter server log.
 */
public class SetLoggerParameters extends SynchronousAdministrativeTask {

  @Nonnull
  private static Logger logger = LoggerFactory.getLogger();

  public SetLoggerParameters(JackHttpServer jackServer) {
    super(jackServer);
  }

  @Override
  protected void handle(long taskId, Request request, Response response) {
    logger.log(Level.INFO, "Updating logger parameters");
    response.setContentLength(0);

    ServerLogConfiguration logConfig = jackServer.getLogConfiguration();
    try {
      Part levelPart = request.getPart("level");
      assert levelPart != null;
      logConfig.setLevel(levelPart.getContent());
      logConfig.setMaxLogFileSize(((Integer) request.getAttribute("limit")).intValue());
      logConfig.setLogFileCount(((Integer) request.getAttribute("count")).intValue());
    } catch (ParsingException e) {
      logger.log(Level.WARNING, "Failed to parse request", e);
      response.setStatus(Status.BAD_REQUEST);
      return;
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Failed to read request", e);
      response.setStatus(Status.BAD_REQUEST);
      return;
    }

    try {
      jackServer.setLogConfiguration(logConfig);
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Failed to set logger configuration with pattern '"
          + logConfig.getLogFilePattern() + "'", e);
      response.setStatus(Status.BAD_REQUEST);
      return;
    }

  }

}
