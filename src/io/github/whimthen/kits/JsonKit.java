package io.github.whimthen.kits;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import io.github.whimthen.json.FormatterToolWindow;
import io.github.whimthen.json.JsonWindowPanel;
import io.github.whimthen.json.ResultToolWindowPanel;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class JsonKit {

    public static final String TOOL_WINDOW_ID = "JsonFormatter";
    public static final String TAB_NAME = "Formatter(%d)";

    public static ToolWindow getToolWindow(@NotNull Project project) {
        return ToolWindowManager.getInstance(project).getToolWindow(TOOL_WINDOW_ID);
    }

    public static @NotNull JsonWindowPanel getPanel() {
        return (JsonWindowPanel) FormatterToolWindow.selectedContent.getComponent();
    }

    public static ResultToolWindowPanel getResultPanel() {
        return getPanel().getResultPanel();
    }

    public static<T> T nullDefault(T obj, T defaultValue) {
        return Objects.isNull(obj) ? defaultValue : obj;
    }

}
