package com.devexperts.aprof.ijplugin.calltree.renderer;

import com.devexperts.aprof.dump.SnapshotDeep;
import com.devexperts.aprof.dump.SnapshotRoot;
import com.intellij.openapi.util.Pair;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

/**
 * Created by eugene on 2016/03/07.
 */
public class MemoryColumnRenderer extends DefaultTableCellRenderer {

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    if (value instanceof DefaultMutableTreeNode) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
      Pair<SnapshotDeep, SnapshotDeep> pair = (Pair<SnapshotDeep, SnapshotDeep>) node.getUserObject();

      if (pair != null) {
        return createProgressBar(pair.first.getSize(), pair.second.getSize());
      }
    }

    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
  }

  private Component createProgressBar(long curSize, long totalSize) {
    JProgressBar progressBar = new JProgressBar();
    progressBar.setStringPainted(true);
    double percentage = (100.0 * curSize) / totalSize;
    progressBar.setString(String.valueOf(curSize));
//    progressBar.setString(String.format("%.2f", percentage) + "% (" + curSize + " bt)");
//    progressBar.setString(String.format("%.2f", percentage) + "%");
    progressBar.setValue((int) Math.round(percentage));
    progressBar.setToolTipText("self: " + curSize + "bt");
    return progressBar;
  }
}
