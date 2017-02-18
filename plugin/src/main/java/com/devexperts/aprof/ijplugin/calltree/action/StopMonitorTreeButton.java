package com.devexperts.aprof.ijplugin.calltree.action;

import com.devexperts.aprof.ijplugin.services.project.ConnectionState;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.treeStructure.treetable.TreeTable;

import javax.swing.*;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by eugene on 2016/05/19.
 */
public class StopMonitorTreeButton extends TreeAction {
  private boolean visible = false;

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
  protected void performAction(AnActionEvent e, TreeTable callTreeTable) {
    assert e.getProject() != null;
    ConnectionState state = ServiceManager.getService(e.getProject(), ConnectionState.class);
    ScheduledExecutorService service = state.getService();
    if (service != null) {
      service.shutdown();
      state.setService(null);
    }

    ActionManager actionManager = ActionManager.getInstance();
    RunMonitorTreeButton runButton = (RunMonitorTreeButton) actionManager.getAction("runMonitorTree");
    runButton.setVisible(true);
    setVisible(false);
  }
}
