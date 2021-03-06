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

package com.android.jack.server;

/**
 * Server execution mode.
 */
public enum ServerMode {
  /**
   * Server has a currently running service or administrative task.
   */
  WORK,
  /**
   * Server has no currently running task and is ready to run new task in no delay and at maximum
   * speed.
   */
  WAIT,
  /**
   * Server has no currently running task but it may require a delay to start new tasks,
   * task will run at maximum speed from the start.
   */
  IDLE,
  /**
   * Server has no currently running task and may require a delay to start new tasks, task
   * may not run at maximum speed from the start.
   */
  DEEP_IDLE,
  /**
   * Server has no currently running task and is using as few resources as possible.
   * New tasks may require some loading and warming up.
   */
  SLEEP,
  /**
   * Server is in the process of automatic shutdown.
   */
  SHUTDOWN;
}
