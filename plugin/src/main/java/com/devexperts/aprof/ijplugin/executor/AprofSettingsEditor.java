package com.devexperts.aprof.ijplugin.executor;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.classFilter.ClassFilterEditor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * Created by eugene on 2016/02/15.
 */
public class AprofSettingsEditor<P> extends SettingsEditor<P> {
  private ClassFilterEditor monitoredClassesEditor;
  private final JPanel mainPanel;

  public AprofSettingsEditor(Project project) {
    mainPanel = new JPanel(new GridBagLayout());
    monitoredClassesEditor = new ClassFilterEditor(project);
    monitoredClassesEditor.setClassDelimiter(".");
    monitoredClassesEditor.setBorder(IdeBorderFactory.createTitledBorder("Monitored classes", false));
    GridBagConstraints bagConstraints = new GridBagConstraints(0, 0, 1, 1, 1.0D, 1.0D, 10, 1, new Insets(0, 0, 0, 0), 0, 0);
    mainPanel.add(this.monitoredClassesEditor, bagConstraints);
  }

  @Override
  protected void resetEditorFrom(P config) {
  }

  @Override
  protected void applyEditorTo(P config) throws ConfigurationException {
  }

  @NotNull
  @Override
  protected JComponent createEditor() {
    return mainPanel;
  }

}

