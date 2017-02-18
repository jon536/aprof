package com.devexperts.aprof.ijplugin.calltree;

import com.devexperts.aprof.dump.SnapshotDeep;
import com.devexperts.aprof.dump.SnapshotRoot;
import com.devexperts.aprof.ijplugin.services.project.ConnectionState;
import com.intellij.execution.JavaExecutionUtil;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.TreeTableSpeedSearch;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.treeStructure.treetable.TreeTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Created by eugene on 2016/02/23.
 */
public class AprofWindowFactory implements ToolWindowFactory {
  public static final String APROF_TOOL_WINDOW_ID = "Aprof";

  public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
    JPanel toolWindowContent = new JPanel(new BorderLayout());

    TreeTable treeTable = new TreeTable(new EmptyTreeModel());
    treeTable.getTree().getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "toggle");

    ConnectionState connectionState = ServiceManager.getService(project, ConnectionState.class);
    assert connectionState != null;
    connectionState.setTreeTable(treeTable);

    JBScrollPane scrollPane = new JBScrollPane(treeTable);
    toolWindowContent.add(scrollPane, BorderLayout.CENTER);

    ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
    Content content = contentFactory.createContent(toolWindowContent, "", false);
    toolWindow.getContentManager().addContent(content);

    registerToolbar(toolWindowContent, treeTable);
    addKeyListener(project, treeTable);
    addMouseListener(project, treeTable);
    addSpeedSearch(treeTable);
  }

  // TODO: create ExpandedTreeTableSpeedSearch
  private void addSpeedSearch(TreeTable treeTable) {
    new TreeTableSpeedSearch(treeTable, path -> {
      final DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
      final Object value = node.getUserObject();
      if (value == null) {
        return String.valueOf((char) 0);
      }
      Pair<SnapshotDeep, SnapshotDeep> pair = (Pair<SnapshotDeep, SnapshotDeep>) value;
      return pair.first.getName();
    });
  }

  // add listener to enter for toggle row state
  private void addKeyListener(@NotNull Project project, TreeTable treeTable) {
    treeTable.addKeyListener(new KeyListener() {
      @Override
      public void keyTyped(KeyEvent e) {

      }

      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
          if (treeTable.getTree().isExpanded(treeTable.getSelectedRow())) {
            treeTable.getTree().collapseRow(treeTable.getSelectedRow());
          } else {
            treeTable.getTree().expandRow(treeTable.getSelectedRow());
          }
        }
      }

      @Override
      public void keyReleased(KeyEvent e) {

      }
    });
  }

  private void addMouseListener(@NotNull Project project, TreeTable treeTable) {
    treeTable.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        if (e.getClickCount() == 2) {
          TreeTable target = (TreeTable) e.getSource();
          int row = target.getSelectedRow();
          int column = target.getSelectedColumn();

          DefaultMutableTreeNode node = (DefaultMutableTreeNode) target.getValueAt(row, column);
          if (node.getUserObject() != null && !(node.getUserObject() instanceof String)) {
            Pair<SnapshotDeep, SnapshotDeep> pair = (Pair<SnapshotDeep, SnapshotDeep>) node.getUserObject();

            final GlobalSearchScope scope = GlobalSearchScope.allScope(project);
            PsiClass psiClass = JavaExecutionUtil.findMainClass(project, pair.first.getName(), scope);

            // if clicked node is class
            if (psiClass != null) {
              psiClass.navigate(true);
            } else {
              // if clicked node is method
              String splittedName[] = pair.first.getName().split("\\.");
              String mainClassName = StringUtil.join(splittedName, 0, splittedName.length - 1, ".");
              String methodName = splittedName[splittedName.length - 1];

              psiClass = JavaExecutionUtil.findMainClass(project, mainClassName, scope);
              if (psiClass != null) {
                PsiMethod[] methods = psiClass.findMethodsByName(methodName, false);
                if (methods.length > 0) {
                  methods[0].navigate(true);
                }
              }
            }
          }
        }
      }
    });
  }

  private void registerToolbar(JPanel toolWindowContent, TreeTable treeTable) {
    ActionManager actionManager = ActionManager.getInstance();
    ActionGroup actionGroup = (ActionGroup) actionManager.getAction("TreeToolWindowGroup");

    ActionToolbar toolbar = actionManager.createActionToolbar("", actionGroup, true);

    List<AnAction> actions = toolbar.getActions(true);
    for (AnAction action : actions) {
      if (action instanceof AnActionButton) {
        ((AnActionButton) action).setContextComponent(treeTable);
      }
    }

    toolbar.setTargetComponent(toolWindowContent);
    toolWindowContent.add(toolbar.getComponent(), BorderLayout.PAGE_START);
  }
}

