package com.devexperts.aprof.ijplugin.services.project;

import com.devexperts.aprof.dump.SnapshotDeep;
import com.devexperts.aprof.dump.SnapshotRoot;
import com.devexperts.aprof.ijplugin.calltree.renderer.MethodColumnRenderer;
import com.devexperts.aprof.ijplugin.client.AprofObserver;
import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.treetable.TreeTable;
import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by eugene on 2016/05/19.
 */
public class ConnectionState {
  private String host;
  private int port;
  private TreeTable treeTable;
  private ScheduledExecutorService service;
  private boolean reverseOrder = false;
  private SnapshotRoot snapshot;
  private AprofObserver observer;
  private MethodColumnRenderer columnRenderer;

  public MethodColumnRenderer getColumnRenderer() {
    return columnRenderer;
  }

  public void setColumnRenderer(MethodColumnRenderer columnRenderer) {
    this.columnRenderer = columnRenderer;
  }

  public AprofObserver getObserver() {
    return observer;
  }

  public void setObserver(AprofObserver observer) {
    this.observer = observer;
  }

  public SnapshotRoot getSnapshot() {
    return snapshot;
  }

  public void setSnapshot(SnapshotRoot snapshot) {
    this.snapshot = snapshot;
  }

  public boolean isReverseOrder() {
    return reverseOrder;
  }

  public void setReverseOrder(boolean reverseOrder) {
    this.reverseOrder = reverseOrder;
  }

  public TreeTable getTreeTable() {
    return treeTable;
  }

  public void setTreeTable(TreeTable treeTable) {
    this.treeTable = treeTable;
  }

  public ConnectionState(@NotNull Project project) {}

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public ScheduledExecutorService getService() {
    return service;
  }

  public void setService(ScheduledExecutorService service) {
    this.service = service;
  }
}
