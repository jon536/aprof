package com.devexperts.aprof.ijplugin.calltree.action;

import com.devexperts.aprof.ijplugin.services.project.ConnectionState;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.treeStructure.treetable.TreeTable;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by eugene on 2016/03/09.
 */
public class RefreshMonitorTreeButton extends TreeAction {
  @Override
  protected void performAction(AnActionEvent evt, final TreeTable treeTable) {
    Project project = evt.getProject();
    assert project != null;

    ConnectionState connectionState = ServiceManager.getService(project, ConnectionState.class);
    assert connectionState != null;

    new UpdateTreeTask(connectionState.getPort(), connectionState.getHost(), treeTable, evt.getProject()).run();
  }

}

