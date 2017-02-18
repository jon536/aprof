package com.devexperts.aprof.ijplugin.calltree.action;

import com.devexperts.aprof.ijplugin.calltree.EmptyTreeModel;
import com.devexperts.aprof.ijplugin.services.project.ConnectionState;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.treeStructure.treetable.TreeTable;

import javax.swing.*;

/**
 * Created by eugene on 2016/03/09.
 */
public abstract class TreeAction extends AnActionButton {

  public TreeAction() {}

  public TreeAction(String text, Icon icon) {
    super(text, icon);
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
    assert e.getProject() != null;
    ConnectionState connectionState = ServiceManager.getService(e.getProject(), ConnectionState.class);
    performAction(e, connectionState.getTreeTable());
  }

  protected abstract void performAction(AnActionEvent e, TreeTable callTreeTable);

  protected void drawEmptyCallTree(final TreeTable treeTable) {
    SwingUtilities.invokeLater(() -> {
      treeTable.setModel(new EmptyTreeModel());
      treeTable.setRootVisible(true);
    });
  }

}

