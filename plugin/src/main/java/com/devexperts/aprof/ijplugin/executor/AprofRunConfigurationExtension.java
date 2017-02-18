package com.devexperts.aprof.ijplugin.executor;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.RunConfigurationExtension;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.util.InvalidDataException;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by eugene on 2016/02/15.
 */
public class AprofRunConfigurationExtension extends RunConfigurationExtension {
  @Override
  public <T extends RunConfigurationBase> void updateJavaParameters(T configuration,
                                                                    JavaParameters params,
                                                                    RunnerSettings runnerSettings) throws ExecutionException {

  }

  @Override
  protected void readExternal(@NotNull RunConfigurationBase runConfiguration, @NotNull Element element) throws InvalidDataException {

  }

  @Nullable
  @Override
  protected String getEditorTitle() {
    return "Aprof";
  }

  @Override
  protected boolean isApplicableFor(@NotNull RunConfigurationBase configuration) {
    return true;
  }


  @Nullable
  @Override
  protected <P extends RunConfigurationBase> SettingsEditor<P> createEditor(@NotNull P configuration) {
    return new AprofSettingsEditor<>(configuration.getProject());
  }
}
