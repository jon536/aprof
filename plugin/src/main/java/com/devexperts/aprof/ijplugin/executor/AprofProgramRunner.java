package com.devexperts.aprof.ijplugin.executor;

import com.devexperts.aprof.AProfAgent;
import com.devexperts.aprof.dump.SnapshotRoot;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.*;
import com.intellij.execution.impl.DefaultJavaProgramRunner;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.RunConfigurationWithSuppressedDefaultRunAction;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.application.PathManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by eugene on 2016/02/12.
 */
public class AprofProgramRunner extends DefaultJavaProgramRunner {

  public static final String RUNNER_ID = "Aprof Runner";

  public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
    return executorId.equals(AprofExecutor.EXECUTOR_ID)
      && !(profile instanceof RunConfigurationWithSuppressedDefaultRunAction)
      && profile instanceof RunConfigurationBase;
  }

  @Override
  public void patch(JavaParameters javaParameters, RunnerSettings settings, RunProfile runProfile, boolean beforeExecution) throws ExecutionException {
    super.patch(javaParameters, settings, runProfile, beforeExecution);
    if (beforeExecution) {
      ParametersList vmParametersList = javaParameters.getVMParametersList();

      String pathToJar = PathManager.getJarPathForClass(this.getClass());
      assert pathToJar != null;

      String pathToConfig = SnapshotRoot.class.getResource("/details.config").getFile();
      String pathToAgentJar = null;
      try {
        pathToAgentJar = new URL(pathToConfig.substring(0, pathToConfig.indexOf("!/"))).getPath();
        pathToAgentJar = URLDecoder.decode(pathToAgentJar, "UTF-8");
      } catch (Exception e) {
        // never happened
        e.printStackTrace();
      }

      assert pathToAgentJar != null;

      Path pathToApof =
        Paths
          .get(pathToJar)
          .getParent()
          .resolve(pathToAgentJar);

      vmParametersList.add("-javaagent:" + pathToApof + "=file=");
    }
  }

  @Override
  protected RunContentDescriptor doExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment env) throws ExecutionException {
    return super.doExecute(state, env);
  }

  @NotNull
  public String getRunnerId() {
    return RUNNER_ID;
  }

}
