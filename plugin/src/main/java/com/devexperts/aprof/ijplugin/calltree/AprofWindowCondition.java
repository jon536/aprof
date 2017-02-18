package com.devexperts.aprof.ijplugin.calltree;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;

/**
 * Created by eugene on 2016/05/19.
 */
public class AprofWindowCondition implements Condition<Project> {
  @Override
  public boolean value(Project project) {
    return false;
  }
}
