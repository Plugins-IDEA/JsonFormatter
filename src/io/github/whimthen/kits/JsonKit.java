package io.github.whimthen.kits;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import io.github.whimthen.actions.AddTabAction;
import org.jetbrains.annotations.NotNull;

public class JsonKit {

    public static final String TOOL_WINDOW_ID = "PrettyJson";
    public static final String TAB_NAME = "Formatter(%d)";

    public static ToolWindow getToolWindow(@NotNull Project project) {
        return ToolWindowManager.getInstance(project).getToolWindow(TOOL_WINDOW_ID);
    }

    public static AnAction getConnectAction() {
        return ActionManager.getInstance().getAction(AddTabAction.ID);
    }

}
