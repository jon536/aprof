package com.devexperts.aprof.ijplugin.calltree.renderer;

import com.devexperts.aprof.dump.SnapshotDeep;
import com.devexperts.aprof.dump.SnapshotRoot;
import com.intellij.ide.util.treeView.NodeRenderer;
import com.intellij.openapi.util.Pair;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by eugene on 2016/03/07.
 */
public class MethodColumnRenderer extends NodeRenderer {
  private boolean classToAllocatedOrder = true;

  public void changeClassToAllocatedOrder() {
    this.classToAllocatedOrder ^= true;
  }

  @Override
  public void customizeCellRenderer(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
    Pair<SnapshotDeep, SnapshotDeep> pair = (Pair<SnapshotDeep, SnapshotDeep>) node.getUserObject();

    String label;
    if (pair == null) {
      if (classToAllocatedOrder) {
        label = "Classes (From class to allocated location)";
      } else {
        label = "Classes (From allocated location to class)";
      }
    } else {
      label = pair.first.getName();
    }

    super.customizeCellRenderer(tree, label, selected, expanded, leaf, row, hasFocus);
  }

}

