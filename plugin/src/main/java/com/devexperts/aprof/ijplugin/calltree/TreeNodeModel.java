package com.devexperts.aprof.ijplugin.calltree;

import com.devexperts.aprof.dump.SnapshotDeep;
import com.devexperts.aprof.dump.SnapshotRoot;
import com.intellij.openapi.util.Pair;
import com.intellij.ui.treeStructure.treetable.ListTreeTableModel;
import com.intellij.ui.treeStructure.treetable.TreeColumnInfo;
import com.intellij.util.ui.ColumnInfo;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import java.util.ArrayList;

/**
 * Created by eugene on 2016/03/07.
 */
public class TreeNodeModel extends ListTreeTableModel {
  private static final ColumnInfo[] COLUMNS = new ColumnInfo[]{
    new TreeColumnInfo("Method"),
    new TreeColumnInfo("Size of allocated memory")
  };

  public TreeNodeModel(SnapshotDeep root, ArrayList<String> path, ArrayList<Integer> newPathIdx) {
    super(createRootNode(root, path, newPathIdx), COLUMNS);
  }

  private static DefaultMutableTreeNode createRootNode(SnapshotDeep snapshot, ArrayList<String> path, ArrayList<Integer> newPathIdx) {
    DefaultMutableTreeNode root = new DefaultMutableTreeNode(null);
    for (int i = 0; i < snapshot.getUsed(); i++) {
      SnapshotDeep child = snapshot.getChild(i);
      if (!path.isEmpty() && child.getName().equals(path.get(0))) {
        newPathIdx.add(i);
        root.add(createTreeNode(child, snapshot, path, newPathIdx, 1));
      } else{
        root.add(createTreeNode(child, snapshot, path, newPathIdx, -1));
      }
    }

    return root;
  }

  private static MutableTreeNode createTreeNode(SnapshotDeep snapshot, SnapshotDeep root,
                                                ArrayList<String> path, ArrayList<Integer> newPathIdx, int deep) {
    DefaultMutableTreeNode result = new DefaultMutableTreeNode(Pair.createNonNull(snapshot, root));
    SnapshotDeep[] children = snapshot.getChildren();
    for (int i = 0; i < snapshot.getUsed(); i++) {
      SnapshotDeep cur = children[i];
      if (deep != -1 && deep < path.size() && path.get(deep).equals(cur.getName())) {
        newPathIdx.add(i);
        result.add(createTreeNode(cur, root, path, newPathIdx, deep + 1));
      } else {
        result.add(createTreeNode(cur, root, path, newPathIdx, -1));
      }
    }

    return result;
  }

  @Override
  public DefaultMutableTreeNode getRoot() {
    return (DefaultMutableTreeNode) super.getRoot();
  }
}

