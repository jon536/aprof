package com.devexperts.aprof.ijplugin.calltree;

import com.intellij.ui.treeStructure.treetable.ListTreeTableModel;
import com.intellij.ui.treeStructure.treetable.TreeColumnInfo;
import com.intellij.util.ui.ColumnInfo;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by eugene on 2016/03/07.
 */
public class EmptyTreeModel extends ListTreeTableModel {

  private static final ColumnInfo[] COLUMNS = new ColumnInfo[]{
    new TreeColumnInfo("")
  };

  public EmptyTreeModel() {
    super(new DefaultMutableTreeNode("No monitoring data to display (hit 'Refresh' button)"), COLUMNS);
  }
}

