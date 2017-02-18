package com.devexperts.aprof.ijplugin.client;

import com.devexperts.aprof.AProfRegistry;
import com.devexperts.aprof.Configuration;
import com.devexperts.aprof.dump.DumpFormatter;
import com.devexperts.aprof.dump.SnapshotDeep;
import com.devexperts.aprof.dump.SnapshotRoot;
import com.devexperts.aprof.dump.SnapshotShallow;
import com.devexperts.aprof.ijplugin.calltree.renderer.MemoryColumnRenderer;
import com.devexperts.aprof.ijplugin.calltree.renderer.MethodColumnRenderer;
import com.devexperts.aprof.ijplugin.calltree.TreeNodeModel;
import com.devexperts.aprof.ijplugin.services.project.ConnectionState;
import com.devexperts.aprof.ijplugin.utils.Dialogs;
import com.devexperts.aprof.util.FastObjIntMap;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.ui.treeStructure.treetable.TreeTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by eugene on 2016/02/22.
 */
public class AprofObserver {

  private static final String APROF_TXT = "aprof.txt";
  private final TreeTable myTreeTable;
  private final Project myProject;
  private final SnapshotDeep locations = new SnapshotDeep(null, true, 0);

  public AprofObserver(TreeTable treeTable, Project project) {
    this.myTreeTable = treeTable;
    this.myProject = project;
  }

  public void update(@NotNull SnapshotRoot snapshot) {
    synchronized (myTreeTable) {
      snapshot.sortChildrenDeep(SnapshotShallow.COMPARATOR_SIZE);

      SwingUtilities.invokeLater(() -> {
        TreePath tPath = myTreeTable.getTree().getSelectionPath();
        ArrayList<String> path = new ArrayList<>();

        tPath = myTreeTable.getTree().getSelectionPath();
        getPathHelper(tPath, path);
        boolean expanded = myTreeTable.getTree().isExpanded(myTreeTable.getSelectedRow());

        ConnectionState connectionState = ServiceManager.getService(myProject, ConnectionState.class);
        assert connectionState != null;

        SnapshotDeep snap = snapshot;
        if (!connectionState.isReverseOrder()) {
          dumpSnapshotByLocations(snapshot);
          snap = locations;
        }

        ArrayList<Integer> newPathIdx = new ArrayList<>();
        myTreeTable.setModel(new TreeNodeModel(snap, path, newPathIdx));

        myTreeTable.setRootVisible(true);
        TableColumnModel columnModel = myTreeTable.getColumnModel();
        columnModel.getColumn(1).setCellRenderer(new MemoryColumnRenderer());
        if (connectionState.getColumnRenderer() == null) {
          connectionState.setColumnRenderer(new MethodColumnRenderer());
        }
        myTreeTable.setTreeCellRenderer(connectionState.getColumnRenderer());

        myTreeTable.getColumnModel().getColumn(0).setPreferredWidth(450);
        myTreeTable.getColumnModel().getColumn(1).setWidth(50);
        myTreeTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        myTreeTable.clearSelection();

        if (!newPathIdx.isEmpty()) {
          int sum = 0;
          for (int i = 0; i < newPathIdx.size() - 1; i++) {
            sum += newPathIdx.get(i) + 1;
            myTreeTable.getTree().expandRow(sum);
          }
          int lastSelected = sum + newPathIdx.get(newPathIdx.size() - 1) + 1;
          if (expanded) {
            myTreeTable.getTree().expandRow(lastSelected);
          }

          myTreeTable.changeSelection(lastSelected, 0, false, false);
        }
      });

      if (myProject.getBasePath() == null) {
        return;
      }

      Path pathToDump = Paths.get(myProject.getBasePath()).resolve(APROF_TXT);

      try (OutputStream os = new FileOutputStream(String.valueOf(pathToDump))) {
        DumpFormatter formatter = new DumpFormatter(new Configuration());
        PrintWriter dumpPrintWriter = new PrintWriter(os);
        formatter.dumpSnapshot(dumpPrintWriter, snapshot, "LAST");
//      dumpPrintWriter.println("::START:")
//
//      OutputStream osBase64 = Base64.getEncoder().wrap(os);
//      PrintWriter dumpPrintWriter = new PrintWriter(osBase64);

        formatter.dumpSnapshot(dumpPrintWriter, snapshot, "LAST");

        dumpPrintWriter.println();
        dumpPrintWriter.flush();
        new ObjectOutputStream(os).writeObject(snapshot);

      } catch (IOException e) {
        Dialogs.showErrorNotification("can't dump to " + APROF_TXT);
        e.printStackTrace();
      }
    }
  }

  private void getPathHelper(TreePath selectionPath, ArrayList<String> path) {
    if (selectionPath == null) return;
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();
    Pair<SnapshotDeep, SnapshotDeep> pair = (Pair<SnapshotDeep, SnapshotDeep>) node.getUserObject();
    if (pair == null)
      return;
    getPathHelper(selectionPath.getParentPath(), path);
    path.add(pair.getFirst().getName());
  }

  private final FastObjIntMap<String> locationIndex = new FastObjIntMap<>();

  private Comparator<SnapshotShallow> getOutputComparator() {
    return SnapshotShallow.COMPARATOR_SIZE;
  }

  private void dumpSnapshotByLocations(SnapshotRoot ss) {
    // rebuild locations
    locations.clearDeep();
    locations.sortChildrenDeep(SnapshotShallow.COMPARATOR_NAME);
    locationIndex.fill(-1);
    for (int i = 0; i < ss.getUsed(); i++) {
      SnapshotDeep cs = ss.getChild(i);
      String dataTypeName = cs.getName();
      findLocationsDeep(cs, dataTypeName, cs.getHistoCountsLength(), SnapshotDeep.UNKNOWN);
    }
    locations.updateSnapshotSumDeep();
    // sort them and print
    locations.sortChildrenDeep(getOutputComparator());
  }

  private void findLocationsDeep(SnapshotDeep ss, String dataTypeName, int histoCountsLength, String insideOf) {
    if (!ss.hasChildren() && insideOf.equals(SnapshotDeep.UNKNOWN)) {
      processLeafLocation(ss, AProfRegistry.getLocationNameWithoutSuffix(ss.getName()), dataTypeName, histoCountsLength);
      return;
    }
    // has children -- go recursive with a special treatment for UNKNOWN children -- attribute them this location's name
    for (int i = 0; i < ss.getUsed(); i++) {
      SnapshotDeep cs = ss.getChild(i);
      if (cs.getName().equals(insideOf)) {
        assert !cs.hasChildren() : insideOf + " location shall not have children";
        processLeafLocation(cs, AProfRegistry.getLocationNameWithoutSuffix(ss.getName()), dataTypeName, histoCountsLength);
      } else
        findLocationsDeep(cs, dataTypeName, histoCountsLength, insideOf);
    }
  }

  private void processLeafLocation(SnapshotDeep ss, String name, String dataTypeName, int histoCountsLength) {
    // use hash index to find location (fast path)
    int i = locationIndex.get(name, -1);
    if (i < 0) {
      // if that does not work, then binary-search existing node (or create new one) and remember index in hash index
      i = locations.findOrCreateChildInSorted(name);
      locationIndex.put(name, i);
    }
    SnapshotDeep cs = locations.getChild(i);
    // append data type info for this location, true to always print average size
    SnapshotDeep child = cs.getOrCreateChild(dataTypeName, true, histoCountsLength);
    child.addShallow(ss);
    // count possibly eliminated allocations separately
    if (ss.isPossiblyEliminatedAllocation())
      child.getOrCreateChild("<possibly eliminated>").addShallow(ss);
  }
}
