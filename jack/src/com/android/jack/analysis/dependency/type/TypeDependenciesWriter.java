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

package com.android.jack.analysis.dependency.type;

import com.android.jack.Jack;
import com.android.jack.JackAbortException;
import com.android.jack.JackUserException;
import com.android.jack.Options;
import com.android.jack.experimental.incremental.IncrementalException;
import com.android.jack.experimental.incremental.JackIncremental;
import com.android.jack.ir.ast.JSession;
import com.android.jack.library.FileType;
import com.android.jack.library.OutputLibrary;
import com.android.jack.reporting.Reporter.Severity;
import com.android.sched.item.Description;
import com.android.sched.item.Name;
import com.android.sched.schedulable.Constraint;
import com.android.sched.schedulable.RunnableSchedulable;
import com.android.sched.util.config.ThreadConfig;
import com.android.sched.util.file.CannotCreateFileException;
import com.android.sched.vfs.InputOutputVFS;
import com.android.sched.vfs.OutputVFile;
import com.android.sched.vfs.VPath;

import java.io.IOException;
import java.io.PrintStream;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * This {@code RunnableSchedulable} write type dependencies to the disk.
 */
@Description("Write type dependencies to the disk")
@Name("TypeDependenciesWriter")
@Constraint(need = TypeDependencies.Collected.class)
public class TypeDependenciesWriter implements RunnableSchedulable<JSession>{

  @CheckForNull
  private final OutputLibrary outputLibrary = Jack.getSession().getJackOutputLibrary();

  @CheckForNull
  protected InputOutputVFS intermediateDexDir = ThreadConfig.get(Options.INTERMEDIATE_DEX_DIR);

  private final boolean isIncrementalMode =
      ThreadConfig.get(JackIncremental.GENERATE_COMPILER_STATE).booleanValue();

  @Override
  public void run(@Nonnull JSession program) throws JackUserException {
    PrintStream ps = null;
    try {
      OutputVFile outputVFile;
      if (outputLibrary != null && !isIncrementalMode && intermediateDexDir == null) {
        outputVFile = outputLibrary.createFile(FileType.DEPENDENCIES, TypeDependencies.vpath);
      } else {
        assert intermediateDexDir != null;
        VPath typeDependencyPath = TypeDependencies.vpath.clone();
        typeDependencyPath.prependPath(new VPath("..", '/'));
        outputVFile = intermediateDexDir.getRootOutputVDir().createOutputVFile(typeDependencyPath);
      }
      ps = new PrintStream(outputVFile.openWrite());
      Jack.getSession().getTypeDependencies().write(ps);
    } catch (CannotCreateFileException e) {
      IncrementalException incrementalException = new IncrementalException(e);
      Jack.getSession().getReporter().report(Severity.FATAL, incrementalException);
      throw new JackAbortException(incrementalException);
    } catch (IOException e) {
      IncrementalException incrementalException = new IncrementalException(e);
      Jack.getSession().getReporter().report(Severity.FATAL, incrementalException);
      throw new JackAbortException(incrementalException);
    } finally {
      if (ps != null) {
        ps.close();
      }
    }
  }
}