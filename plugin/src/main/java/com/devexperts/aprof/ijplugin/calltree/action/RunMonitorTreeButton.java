package com.devexperts.aprof.ijplugin.calltree.action;

import com.devexperts.aprof.ijplugin.services.project.ConnectionState;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.treeStructure.treetable.TreeTable;
import com.intellij.util.Time;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

/**
 * Created by eugene on 2016/03/09.
 */
public class RunMonitorTreeButton extends TreeAction {
  private boolean visible = true;

  @Override
  public void setVisible(boolean visible) {
    super.setVisible(visible);
    this.visible = visible;
  }

  @Override
  public boolean isVisible() {
    return visible;
  }

  @Override
  protected void performAction(AnActionEvent evt, final TreeTable treeTable) {
//    Logger.log.println("perform action button");
    Project project = evt.getProject();
    assert project != null;

    ConnectionState connectionState = ServiceManager.getService(project, ConnectionState.class);
    assert connectionState != null;

    ScheduledExecutorService service = connectionState.getService();
    if (service == null) {
      Runnable task = new UpdateTreeTask(connectionState.getPort(), connectionState.getHost(), treeTable, evt.getProject());
      service = Executors.newSingleThreadScheduledExecutor();
      connectionState.setService(service);
      service.scheduleWithFixedDelay(task, 0, 1, TimeUnit.SECONDS);
    }

    ActionManager actionManager = ActionManager.getInstance();
    StopMonitorTreeButton stopButton = (StopMonitorTreeButton) actionManager.getAction("stopMonitorTree");
    stopButton.setVisible(true);
    setVisible(false);
  }

}

