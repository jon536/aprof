package com.devexperts.aprof.ijplugin.executor;

import com.intellij.execution.Executor;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Created by eugene on 2016/02/12.
 */
public class AprofExecutor extends Executor {

    public static final String EXECUTOR_ID = "Aprof Executor";

    public AprofExecutor() {
    }

    public String getToolWindowId() {
        return this.getId();
    }

    public Icon getToolWindowIcon() {
        return AllIcons.Toolwindows.ToolWindowRun;
    }

    @NotNull
    public Icon getIcon() {
        return IconLoader.getIcon("/icons/aprof.png", this.getClass());
    }

    public Icon getDisabledIcon() {
        return AllIcons.Process.DisabledRun;
    }

    public String getDescription() {
        return "Run application with Aprof agent";
    }

    @NotNull
    public String getActionName() {
        return "Run with Aprof";
    }

    @NotNull
    public String getId() {
        return EXECUTOR_ID;
    }

    @NotNull
    public String getStartActionText() {
        return "Run with Aprof";
    }

    public String getContextActionId() {
        return this.getId() + " context-action-does-not-exist";
    }

    public String getHelpId() {
        return null;
    }
}
