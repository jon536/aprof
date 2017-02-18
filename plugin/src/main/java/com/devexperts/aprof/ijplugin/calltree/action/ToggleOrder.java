package com.devexperts.aprof.ijplugin.calltree.action;

import com.devexperts.aprof.ijplugin.calltree.renderer.MemoryColumnRenderer;
import com.devexperts.aprof.ijplugin.calltree.renderer.MethodColumnRenderer;
import com.devexperts.aprof.ijplugin.services.project.ConnectionState;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;

/**
 * Created by eugene on 2016/05/31.
 */
public class ToggleOrder extends AnAction {
  @Override
  public void actionPerformed(AnActionEvent e) {
    Project project = e.getProject();
    assert project != null;

    ConnectionState state = ServiceManager.getService(project, ConnectionState.class);
    assert state != null;

    synchronized (state.getTreeTable()) {
      if (state.getObserver() == null) return;

      state.setReverseOrder(!state.isReverseOrder());
      state.getColumnRenderer().changeClassToAllocatedOrder();
    }
    state.getObserver().update(state.getSnapshot());
  }
}
