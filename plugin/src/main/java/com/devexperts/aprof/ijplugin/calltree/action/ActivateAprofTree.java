package com.devexperts.aprof.ijplugin.calltree.action;

import com.devexperts.aprof.ijplugin.calltree.AprofWindowFactory;
import com.devexperts.aprof.ijplugin.services.project.ConnectionState;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowEP;
import com.intellij.openapi.wm.ex.ToolWindowManagerEx;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by eugene on 2016/02/22.
 */
public class ActivateAprofTree extends AnAction {
  public ActivateAprofTree() {
    super();
  }

  private InputValidator addressValidator = new InputValidator() {
    @Override
    public boolean checkInput(String address) {
      try {
        URI uri = new URI("my://" + address);

        if (uri.getHost() == null || uri.getPort() == -1) {
          return false;
        }
      } catch (URISyntaxException ex) {
        return false;
      }

      return true;
    }

    @Override
    public boolean canClose(String s) {
      return true;
    }
  };

  @Override
  public void actionPerformed(AnActionEvent e) {
    Project project = e.getProject();
    assert project != null;

    ConnectionState connectionState = ServiceManager.getService(project, ConnectionState.class);
    assert connectionState != null;

    String previousAddress = "";
    if (connectionState.getHost() != null) {
      previousAddress = connectionState.getHost() + ":" + connectionState.getPort();
    }

    String address = Messages.showInputDialog(project, "address(host:port)", "Activate Aprof", null,
      previousAddress, addressValidator);


    assert address != null;
    connectionState.setHost(address.substring(0, address.indexOf(':')));
    connectionState.setPort(Integer.parseInt(address.substring(address.lastIndexOf(':') + 1)));
//    Logger.log.println("connect to: " + ourHost + ":" + ourPort);

    registerAprofToolWindow(project);
  }

  private void registerAprofToolWindow(Project project) {
    final ToolWindowManagerEx twm = ToolWindowManagerEx.getInstanceEx(project);
    final ToolWindow aprofToolWindow = twm.getToolWindow(AprofWindowFactory.APROF_TOOL_WINDOW_ID);
    if (aprofToolWindow == null) {
      ToolWindowEP[] toolWindows = Extensions.getExtensions(ToolWindowEP.EP_NAME);
      for (ToolWindowEP toolWindow : toolWindows) {
        if (toolWindow.id.equals(AprofWindowFactory.APROF_TOOL_WINDOW_ID)) {
          twm.initToolWindow(toolWindow);
          twm.getToolWindow(AprofWindowFactory.APROF_TOOL_WINDOW_ID).show(null);
        }
      }
    } else {
      aprofToolWindow.show(null);
    }
  }

}
