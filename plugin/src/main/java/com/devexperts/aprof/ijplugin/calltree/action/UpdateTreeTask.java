package com.devexperts.aprof.ijplugin.calltree.action;

import com.devexperts.aprof.dump.SnapshotRoot;
import com.devexperts.aprof.ijplugin.client.AprofClient;
import com.devexperts.aprof.ijplugin.client.AprofObserver;
import com.devexperts.aprof.ijplugin.services.project.ConnectionState;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.treetable.TreeTable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.TimerTask;

/**
 * Created by eugene on 2016/04/27.
 */
public class UpdateTreeTask implements Runnable {
  private int myPort;
  private String myHost;
  private TreeTable myTreeTable;
  private Project project;

  public UpdateTreeTask(int myPort, String myHost, TreeTable myTreeTable, Project project) {
    this.myPort = myPort;
    this.myHost = myHost;
    this.myTreeTable = myTreeTable;
    this.project = project;
  }

  @Override
  public void run() {
//    Logger.log.println("run");
    AprofClient client = new AprofClient(myHost, myPort);
    SnapshotRoot snapshotRoot = null;
    ConnectionState state = ServiceManager.getService(project, ConnectionState.class);
    AprofObserver observer = state.getObserver();
    if (observer == null)
      state.setObserver(observer = new AprofObserver(myTreeTable, project));

    try {
      snapshotRoot = client.getSnapshot();
    } catch (Exception e) {
      if (state.getService() != null) {
        state.getService().shutdown();
        state.setService(null);
      }

      ActionManager actionManager = ActionManager.getInstance();
      RunMonitorTreeButton runButton = (RunMonitorTreeButton) actionManager.getAction("runMonitorTree");
      StopMonitorTreeButton stopButton = (StopMonitorTreeButton) actionManager.getAction("stopMonitorTree");

      runButton.setVisible(true);
      stopButton.setVisible(false);
    }
    if (snapshotRoot != null) {
      state.setSnapshot(snapshotRoot);
      observer.update(snapshotRoot);
    }
  }
}
