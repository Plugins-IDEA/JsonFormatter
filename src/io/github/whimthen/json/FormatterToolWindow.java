package io.github.whimthen.json;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import io.github.whimthen.service.JsonService;
import org.jetbrains.annotations.NotNull;

public class FormatterToolWindow implements ToolWindowFactory {

    public volatile static Content selectedContent;

    public static @NotNull
    JsonWindowPanel getPanel() {
        return (JsonWindowPanel) selectedContent.getComponent();
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        JsonService instance = JsonService.getInstance();
        selectedContent = instance.initWindow(project);
        toolWindow.getContentManager().addContent(selectedContent);
        toolWindow.getContentManager().setSelectedContent(selectedContent);
        toolWindow.addContentManagerListener(new JsonContentManagerListener(toolWindow, project));
    }

}
