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

package com.android.jack.experimental.incremental;

import com.android.jack.CommandLine;
import com.android.jack.ExitStatus;
import com.android.jack.IllegalOptionsException;
import com.android.jack.Jack;
import com.android.jack.JackIOException;
import com.android.jack.JackUserException;
import com.android.jack.NothingToDoException;
import com.android.jack.Options;
import com.android.jack.analysis.dependency.file.FileDependencies;
import com.android.jack.analysis.dependency.file.FileDependenciesWriter;
import com.android.jack.analysis.dependency.type.TypeDependencies;
import com.android.jack.analysis.dependency.type.TypeDependenciesWriter;
import com.android.jack.backend.dex.DexFileProduct;
import com.android.jack.backend.dex.DexFileWriter;
import com.android.jack.backend.jayce.JayceFileImporter;
import com.android.jack.frontend.FrontendCompilationException;
import com.android.jack.ir.ast.JSession;
import com.android.jack.ir.formatter.BinaryQualifiedNameFormatter;
import com.android.jack.ir.formatter.TypeFormatter;
import com.android.jack.library.FileType;
import com.android.jack.load.JackLoadingException;
import com.android.jack.scheduling.marker.ClassDefItemMarker;
import com.android.jack.util.TextUtils;
import com.android.sched.scheduler.IllegalRequestException;
import com.android.sched.scheduler.PlanBuilder;
import com.android.sched.scheduler.Request;
import com.android.sched.util.UnrecoverableException;
import com.android.sched.util.codec.DirectDirOutputVDirCodec;
import com.android.sched.util.config.ChainedException;
import com.android.sched.util.config.ConfigurationException;
import com.android.sched.util.config.HasKeyId;
import com.android.sched.util.config.ThreadConfig;
import com.android.sched.util.config.id.BooleanPropertyId;
import com.android.sched.util.config.id.PropertyId;
import com.android.sched.util.file.CannotCreateFileException;
import com.android.sched.util.file.CannotReadException;
import com.android.sched.util.file.CannotSetPermissionException;
import com.android.sched.util.file.Directory;
import com.android.sched.util.file.FileAlreadyExistsException;
import com.android.sched.util.file.FileOrDirectory.ChangePermission;
import com.android.sched.util.file.FileOrDirectory.Existence;
import com.android.sched.util.file.FileOrDirectory.Permission;
import com.android.sched.util.file.NoSuchFileException;
import com.android.sched.util.file.NotFileOrDirectoryException;
import com.android.sched.util.file.WrongPermissionException;
import com.android.sched.util.log.LoggerFactory;
import com.android.sched.vfs.DirectVFS;
import com.android.sched.vfs.InputVDir;
import com.android.sched.vfs.InputVFile;
import com.android.sched.vfs.OutputVFS;
import com.android.sched.vfs.VPath;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * Executable class to run the jack compiler with incremental support.
 */
@HasKeyId
public class JackIncremental extends CommandLine {

  public static final BooleanPropertyId GENERATE_COMPILER_STATE = BooleanPropertyId.create(
  "jack.experimental.compilerstate.generate", "Generate compiler state").addDefaultValue(
  Boolean.FALSE);

  @Nonnull
  public static final PropertyId<OutputVFS> COMPILER_STATE_OUTPUT_DIR = PropertyId.create(
      "jack.experimental.compilerstate.output.dir", "Compiler state output folder",
      new DirectDirOutputVDirCodec(Existence.MAY_EXIST)).requiredIf(
      GENERATE_COMPILER_STATE.getValue().isTrue());

  @Nonnull
  private static final Logger logger = LoggerFactory.getLogger();

  @CheckForNull
  private static File dexFilesFolder;

  @CheckForNull
  private static File jackFilesFolder;

  @CheckForNull
  private static final TypeFormatter formatter = BinaryQualifiedNameFormatter.getFormatter();

  @CheckForNull
  private static final char fileSeparator = '/';


  protected static void runJackAndExitOnError(@Nonnull Options options) {
    try {
      run(options);
    } catch (NothingToDoException e1) {
      // End normally since there is nothing to do
    } catch (ConfigurationException exceptions) {
      System.err.println(exceptions.getNextExceptionCount() + " error"
          + (exceptions.getNextExceptionCount() > 1 ? "s" : "")
          + " during configuration. Try --help-properties for help.");
      for (ChainedException exception : exceptions) {
        System.err.println("  " + exception.getMessage());
      }

      System.exit(ExitStatus.FAILURE_USAGE);
    } catch (IllegalOptionsException e) {
      System.err.println(e.getMessage());
      System.err.println("Try --help for help.");

      System.exit(ExitStatus.FAILURE_USAGE);
    } catch (FrontendCompilationException e) {
      // Cause exception has already been logged
      System.exit(ExitStatus.FAILURE_COMPILATION);
    } catch (JackUserException e) {
      System.err.println(e.getMessage());
      logger.log(Level.FINE, "Jack user exception:", e);
      System.exit(ExitStatus.FAILURE_COMPILATION);
    } catch (JackLoadingException e) {
      System.err.println(e.getMessage());
      logger.log(Level.FINE, "Jack loading exception:", e);
      System.exit(ExitStatus.FAILURE_COMPILATION);
    } catch (OutOfMemoryError e) {
      printExceptionMessage(e, "Out of memory error.");
      System.err.println("Try increasing heap size with java option '-Xmx<size>'");
      System.err.println(INTERRUPTED_COMPILATION_WARNING);
      logger.log(Level.FINE, "Out of memory error:", e);
      System.exit(ExitStatus.FAILURE_VM);
    } catch (StackOverflowError e) {
      printExceptionMessage(e, "Stack overflow error.");
      System.err.println("Try increasing stack size with java option '-Xss<size>'");
      System.err.println(INTERRUPTED_COMPILATION_WARNING);
      logger.log(Level.FINE, "Stack overflow error:", e);
      System.exit(ExitStatus.FAILURE_VM);
    } catch (VirtualMachineError e) {
      printExceptionMessage(e, "Virtual machine error: " + e.getClass() + ".");
      System.err.println(INTERRUPTED_COMPILATION_WARNING);
      logger.log(Level.FINE, "Virtual machine error:", e);
      System.exit(ExitStatus.FAILURE_VM);
    } catch (UnrecoverableException e) {
      System.err.println("Unrecoverable error: " + e.getMessage());
      System.err.println(INTERRUPTED_COMPILATION_WARNING);
      logger.log(Level.FINE, "Unrecoverable exception:", e);
      System.exit(ExitStatus.FAILURE_UNRECOVERABLE);
    } catch (Throwable e) {
      String info =
          "Internal incremental compiler error (version " + Jack.getVersionString() + ")";
      logger.log(Level.SEVERE, info + ':', e);
      e.printStackTrace();
      System.err.println();
      System.err.println(info + '.');
      if (e.getMessage() != null) {
        System.err.println(e.getMessage() + '.');
      }
      System.err.println(INTERRUPTED_COMPILATION_WARNING);
      System.exit(ExitStatus.FAILURE_INTERNAL);
    }
  }

  public static void run(@Nonnull Options options) throws ConfigurationException,
      IllegalOptionsException, NothingToDoException, JackUserException, IncrementalException {

    File incrementalFolder = options.getIncrementalFolder();
    assert incrementalFolder != null;

    dexFilesFolder = new File(incrementalFolder, "dexFiles");

    jackFilesFolder = new File(incrementalFolder, "jackFiles");

    // Add options to control incremental support
    assert dexFilesFolder != null;
    options.addProperty(Options.INTERMEDIATE_DEX_DIR.getName(), dexFilesFolder.getPath());
    options.addProperty(Options.GENERATE_JACK_LIBRARY.getName(), "true");
    options.addProperty(Options.LIBRARY_OUTPUT_CONTAINER_TYPE.getName(), "dir");
    assert jackFilesFolder != null;
    options.addProperty(Options.LIBRARY_OUTPUT_DIR.getName(), jackFilesFolder.getPath());

    File typeDependenciesFile =
        new File(incrementalFolder, TypeDependencies.vpath.getPathAsString(File.pathSeparatorChar));

    File fileDependenciesFile =
        new File(incrementalFolder, FileDependencies.vpath.getPathAsString(File.pathSeparatorChar));

    if (isIncrementalCompilation(options, typeDependenciesFile, fileDependenciesFile)
        && !needFullRebuild(options)) {
      logger.log(Level.FINE, "Incremental compilation");
      DirectVFS directVFS = null;
      try {
        directVFS = new DirectVFS(new Directory(incrementalFolder.getPath(), null,
            Existence.MUST_EXIST, Permission.READ, ChangePermission.NOCHANGE));
        InputVDir incrementalVDir = directVFS.getRootInputVDir();

        List<String> javaFilesNames = getJavaFilesSpecifiedOnCommandLine(options);

        TypeDependencies typeDependencies = readTypeDependencies(incrementalVDir);
        FileDependencies fileDependencies = readFileDependencies(incrementalVDir);

        Map<String, Set<String>> typeRecompileDependencies =
            typeDependencies.getRecompileDependencies();
        printDependencyStat(typeRecompileDependencies);

        Set<String> deletedFiles = getDeletedFiles(javaFilesNames, fileDependencies);
        Set<String> filesToRecompile = getFilesToRecompile(fileDependencies,
            typeRecompileDependencies, javaFilesNames, deletedFiles);

        if (!filesToRecompile.isEmpty() || !deletedFiles.isEmpty()) {
          logger.log(Level.FINE, "{0} Files to recompile {1}",
              new Object[] {Integer.valueOf(filesToRecompile.size()), filesToRecompile});
          updateOptions(options, filesToRecompile);

          logger.log(Level.FINE, "Ecj options {0}", options.getEcjArguments());

          try {
            Jack.run(options, typeDependencies, fileDependencies);
          } catch (NothingToDoException e) {
            // Even if there is nothing to compile, the output dex file must be rebuild from all dex
            // (one dex per types) since some dex files could be removed. To rebuild output dex
            // file, a specific plan is used.
            ThreadConfig.setConfig(options.getConfig());

            JSession session = Jack.getSession();
            session.setTypeDependencies(typeDependencies);
            session.setFileDependencies(fileDependencies);

            Request request = Jack.createInitialRequest();
            request.addProduction(DexFileProduct.class);
            request.addInitialTagOrMarker(ClassDefItemMarker.Complete.class);
            request.addInitialTagOrMarker(TypeDependencies.Collected.class);
            request.addInitialTagOrMarker(FileDependencies.Collected.class);

            PlanBuilder<JSession> planBuilder;
            try {
              planBuilder = request.getPlanBuilder(JSession.class);
            } catch (IllegalRequestException illegalRequest) {
              throw new AssertionError(illegalRequest);
            }

            planBuilder.append(TypeDependenciesWriter.class);
            planBuilder.append(FileDependenciesWriter.class);
            planBuilder.append(DexFileWriter.class);

            try {
              planBuilder.getPlan().getScheduleInstance().process(Jack.getSession());
            } catch (RuntimeException runtimeExcept) {
              throw runtimeExcept;
            } catch (Exception except) {
              throw new AssertionError(except);
            }
          } finally {
            ThreadConfig.unsetConfig();
          }

        } else {
          logger.log(Level.FINE, "No files to recompile");
        }
      } catch (WrongPermissionException e) {
        throw new AssertionError(e);
      } catch (CannotSetPermissionException e) {
        throw new AssertionError(e);
      } catch (NoSuchFileException e) {
        throw new AssertionError(e);
      } catch (NotFileOrDirectoryException e) {
        throw new AssertionError(e);
      } catch (FileAlreadyExistsException e) {
        throw new AssertionError(e);
      } catch (CannotCreateFileException e) {
        throw new AssertionError(e);
      } catch (CannotReadException e) {
        throw new IncrementalException(e);
      } finally {
        if (directVFS != null) {
          directVFS.close();
        }
      }
    } else {
      Jack.run(options);
    }
  }

  @Nonnull
  private static TypeDependencies readTypeDependencies(@Nonnull InputVDir incrementalVDir)
      throws NotFileOrDirectoryException, NoSuchFileException, CannotReadException {
    TypeDependencies typeDependencies = new TypeDependencies();
    InputVFile typeDependenciesVFile = incrementalVDir.getInputVFile(TypeDependencies.vpath);
    InputStreamReader reader = null;
    try {
      reader = new InputStreamReader(typeDependenciesVFile.openRead());
      typeDependencies.read(reader);
    } catch (IOException e) {
      throw new CannotReadException(typeDependenciesVFile.getLocation(), e);
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
        }
      }
    }
    return typeDependencies;
  }

  @Nonnull
  private static FileDependencies readFileDependencies(@Nonnull InputVDir incrementalVDir)
      throws NotFileOrDirectoryException, NoSuchFileException, CannotReadException {
    FileDependencies fileDependencies = new FileDependencies();
    InputVFile fileDependenciesVFile = incrementalVDir.getInputVFile(FileDependencies.vpath);
    InputStreamReader reader = null;
    try {
      reader = new InputStreamReader(fileDependenciesVFile.openRead());
      fileDependencies.read(reader);
    } catch (IOException e) {
      throw new CannotReadException(fileDependenciesVFile.getLocation(), e);
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
        }
      }
    }
    return fileDependencies;
  }

  /*
   * A full rebuild is needed:
   * - when a file contained inside a folder in the classpath or an archive in the classpath
   * is more recent than the generated dex file.
   * - when a file contained inside a folder in the import option or an archive in the import
   * option is more recent than the generated dex file.
   */
  private static boolean needFullRebuild(@Nonnull Options options) {
    if (!options.isAutomaticFullRebuildEnabled()) {
      return false;
    }

    File outputDexFile = new File(options.getOutputDir(), DexFileWriter.DEX_FILENAME);
    if (outputDexFile.exists()) {
      for (File lib : options.getBootclasspath()) {
        if (isModifiedLibrary(lib, outputDexFile.lastModified())) {
          return true;
        }
      }
      for (File lib : options.getClasspath()) {
        if (isModifiedLibrary(lib, outputDexFile.lastModified())) {
          return true;
        }
      }
    }

    return hasModifiedImport(options, outputDexFile.lastModified());
  }

  private static boolean isModifiedLibrary(@Nonnull File lib, long time) {
    if (lib.isFile() && (lib.lastModified() > time)) {
      return true;
    } else if (lib.isDirectory() && hasModifiedFile(lib, time)) {
      return true;
    }

    return false;
  }

  private static boolean hasModifiedFile(@Nonnull File file, long time) {
    assert file.isDirectory();

    for (File f : file.listFiles()) {
      if (f.isDirectory()) {
        if (hasModifiedFile(f, time)) {
          return true;
        }
      } else if (f.lastModified() > time) {
          return true;
      }
    }

    return false;
  }

  private static boolean hasModifiedImport(@Nonnull Options options, long time) {
    for (File importedJackFiles : options.getJayceImport()) {
      if (isModifiedLibrary(importedJackFiles, time)) {
        return true;
      }
    }
    return false;
  }

  @Nonnull
  private static String dependenciesToString(@Nonnull Map<String, Set<String>> fileDependencies) {
    StringBuilder builder = new StringBuilder();
    builder.append(TextUtils.LINE_SEPARATOR);
    builder.append("*Dependencies list*");
    builder.append(TextUtils.LINE_SEPARATOR);

    for (Map.Entry<String, Set<String>> entry : fileDependencies.entrySet()) {
      builder.append(entry.getKey());
      builder.append("->");
      builder.append(entry.getValue());
      builder.append(TextUtils.LINE_SEPARATOR);
    }

    return (builder.toString());
  }

  private static void printDependencyStat(
      @Nonnull Map<String, Set<String>> typeRecompileDependencies) {
    int dependencyNumber = 0;
    int maxDependencyNumber = -1;
    int minDependencyNumber = -1;

    for (Set<String> dependency : typeRecompileDependencies.values()) {
      int currentDepSize = dependency.size();
      dependencyNumber += currentDepSize;
      if (minDependencyNumber == -1 || minDependencyNumber > currentDepSize) {
        minDependencyNumber = currentDepSize;
      }
      if (maxDependencyNumber == -1 || maxDependencyNumber < currentDepSize) {
        maxDependencyNumber = currentDepSize;
      }
    }

    logger.log(Level.FINE,
        "There are {0} dependencies, with {1} files per dependency in average", new Object[] {
            Integer.valueOf(typeRecompileDependencies.size()),
            Double.valueOf((double) dependencyNumber / (double) typeRecompileDependencies.size())});
    logger.log(Level.FINE, "Dependencies are at minimun {0} and at maximun {1}",
        new Object[] {Integer.valueOf(minDependencyNumber), Integer.valueOf(maxDependencyNumber)});
  }

  private static void updateOptions(@Nonnull Options options,
      @Nonnull Set<String> javaFilesToRecompile) {
    List<String> newEcjArguments = new ArrayList<String>();

    for (String ecjOptions : options.getEcjArguments()) {
      if (!ecjOptions.startsWith("@") && !ecjOptions.endsWith(".java")
          && !new File(ecjOptions).isDirectory()) {
        newEcjArguments.add(ecjOptions);
      }
    }

    for (String fileToRecompile : javaFilesToRecompile) {
      newEcjArguments.add(fileToRecompile);
    }

    assert jackFilesFolder != null;
    StringBuilder newClasspath = new StringBuilder(jackFilesFolder.getPath());

    String oldClasspath = options.getClasspathAsString();
    if (oldClasspath != null) {
      newClasspath.append(File.pathSeparator);
      newClasspath.append(oldClasspath);
    }

    // Move imported jayce files from import to classpath option
    List<File> jayceImport = options.getJayceImport();
    if (!jayceImport.isEmpty()) {
      for (File importedJackFiles : jayceImport) {
        newClasspath.append(File.pathSeparator);
        newClasspath.append(importedJackFiles.getPath());
      }
      options.setJayceImports(Collections.<File>emptyList());
    }

    options.setClasspath(newClasspath.toString());

    if (!newEcjArguments.isEmpty()) {
      options.setEcjArguments(newEcjArguments);
    }
  }

  @Nonnull
  private static Set<String> getFilesToRecompile(@Nonnull FileDependencies fileDependencies,
      @Nonnull Map<String, Set<String>> typeRecompileDependencies,
      @Nonnull List<String> javaFileNames, Set<String> deletedFiles) throws JackUserException {
    Set<String> filesToRecompile = new HashSet<String>();

    filesToRecompile.addAll(getModifiedFiles(fileDependencies, typeRecompileDependencies,
        javaFileNames, deletedFiles));
    filesToRecompile.addAll(getAddedFiles(fileDependencies, javaFileNames));

    for (String deletedFile : deletedFiles) {
      deleteOldFilesFromJavaFiles(fileDependencies, deletedFile);
      addNotModifiedDependencies(fileDependencies, typeRecompileDependencies, deletedFiles,
          filesToRecompile, deletedFile);
    }

    for (String fileToRecompile : filesToRecompile) {
      deleteOldFilesFromJavaFiles(fileDependencies, fileToRecompile);
    }

    return filesToRecompile;
  }

  private static void addNotModifiedDependencies(@Nonnull FileDependencies fileDependencies,
      @Nonnull Map<String, Set<String>> typeRecompileDependencies,
      @Nonnull Set<String> deletedFiles, @Nonnull Set<String> filesToRecompile,
      @Nonnull String modifiedJavaFileName) {
    for (String modifiedTypeName : fileDependencies.getTypeNames(modifiedJavaFileName)) {
      for (String typeName : typeRecompileDependencies.get(modifiedTypeName)) {
        String dependentFileName = fileDependencies.getJavaFileName(typeName);
        if (dependentFileName != null && !deletedFiles.contains(dependentFileName)) {
          filesToRecompile.add(dependentFileName);
        }
      }
    }
  }

    @Nonnull
  private static Set<String> getDeletedFiles(@Nonnull List<String> javaFileNames,
      @Nonnull FileDependencies fileDependencies)
        throws JackUserException {
      Set<String> deletedFiles = new HashSet<String>();

      for (String javaFileName : fileDependencies.getCompiledJavaFiles()) {
        if (!javaFileNames.contains(javaFileName)) {
          logger.log(Level.FINE, "{0} was deleted", javaFileName);
          deletedFiles.add(javaFileName);
        }
      }

      return deletedFiles;
    }

  private static void deleteOldFilesFromJavaFiles(
      @Nonnull FileDependencies fileDependencies, @Nonnull String javaFileName)
      throws JackUserException {
    for (String typeNameToRemove : fileDependencies.getTypeNames(javaFileName)) {
      File jackFile = getJackFile(typeNameToRemove);
      if (jackFile.exists() && !jackFile.delete()) {
        throw new JackIOException("Failed to delete file " + jackFile.getPath());
      }
      File dexFile = getDexFile(typeNameToRemove);
      if (dexFile.exists() && !dexFile.delete()) {
        throw new JackIOException("Failed to delete file " + dexFile.getPath());
      }
    }
  }

  @Nonnull
  private static Set<String> getAddedFiles(@Nonnull FileDependencies fileDependencies,
      @Nonnull List<String> javaFileNames) {
    Set<String> addedFiles = new HashSet<String>();
    Set<String> previousFiles = fileDependencies.getCompiledJavaFiles();

    for (String javaFileName : javaFileNames) {
      if (!previousFiles.contains(javaFileName)) {
        logger.log(Level.FINE, "{0} was added", javaFileName);
        addedFiles.add(javaFileName);
      }
    }

    return addedFiles;
  }

  @Nonnull
  private static Set<String> getModifiedFiles(@Nonnull FileDependencies fileDependencies,
      @Nonnull Map<String, Set<String>> typeRecompileDependencies,
      @Nonnull List<String> javaFileNames, @Nonnull Set<String> deletedFiles)
      throws JackUserException {
    Set<String> modifiedFiles = new HashSet<String>();

    for (Map.Entry<String, Set<String>> previousFileEntry : typeRecompileDependencies.entrySet()) {
      String typeName = previousFileEntry.getKey();
      String javaFileName = fileDependencies.getJavaFileName(typeName);
      if (javaFileName != null && !deletedFiles.contains(javaFileName)) {
        File javaFile = new File(javaFileName);
        File dexFile = getDexFile(typeName);
        if (!dexFile.exists() || (javaFileNames.contains(javaFileName)
            && javaFile.lastModified() > dexFile.lastModified())) {
          logger.log(Level.FINE, "{0} was modified", new Object[] {javaFileName});
          modifiedFiles.add(javaFileName);
          addNotModifiedDependencies(fileDependencies, typeRecompileDependencies, deletedFiles,
              modifiedFiles, javaFileName);
          break;
        }
      }
    }

    return modifiedFiles;
  }

  @Nonnull
  private static List<String> getJavaFilesSpecifiedOnCommandLine(@Nonnull Options options)
      throws NothingToDoException, IllegalOptionsException {
    assert !options.getEcjArguments().isEmpty();

    org.eclipse.jdt.internal.compiler.batch.Main compiler =
        new org.eclipse.jdt.internal.compiler.batch.Main(new PrintWriter(System.out),
            new PrintWriter(System.err), false /* exit */, null /* options */
            , null /* compilationProgress */
        );

    try {
      compiler.configure(options.getEcjArguments().toArray(
          new String[options.getEcjArguments().size()]));
      if (!compiler.proceed) {
        throw new NothingToDoException();
      }
    } catch (IllegalArgumentException e) {
      throw new IllegalOptionsException(e.getMessage(), e);
    }

    ArrayList<String> javaFiles = new ArrayList<String>();
    for (String fileName : compiler.filenames) {
      File file = new File(fileName);
      assert file.exists();
      try {
        fileName = file.getCanonicalPath();
      } catch (IOException e) {
        // if we got exception keep the specified name
      }
      javaFiles.add(fileName);
    }

    return javaFiles;
  }

  private static boolean isIncrementalCompilation(@Nonnull Options options,
      @Nonnull File typeDependencies, @Nonnull File fileDependencies) {
    if (!options.getEcjArguments().isEmpty() && typeDependencies.exists()
        && fileDependencies.exists()) {
      return true;
    }

    return false;
  }

  public static TypeFormatter getFormatter() {
    return formatter;
  }

  @Nonnull
  protected static File getJackFile(@Nonnull String typeName) {
    return new File(jackFilesFolder,
        FileType.JAYCE.getPrefix()
            + File.separatorChar
            + new VPath(typeName + JayceFileImporter.JAYCE_FILE_EXTENSION, fileSeparator)
                .getPathAsString(File.separatorChar));
  }

  @Nonnull
  protected static File getDexFile(@Nonnull String typeName) {
    return new File(dexFilesFolder, new VPath(typeName + FileType.DEX.getFileExtension(),
        fileSeparator).getPathAsString(File.separatorChar));
  }
}
