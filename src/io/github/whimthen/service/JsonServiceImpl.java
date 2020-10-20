package io.github.whimthen.service;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import io.github.whimthen.json.JsonWindowPanel;
import io.github.whimthen.kits.JsonKit;

public class JsonServiceImpl implements JsonService {

    public static int tabIndex = 1;

    @Override
    public Content initWindow(Project project) {
        Content content = this.newContent(project);
        content.setCloseable(false);
        return content;
    }

    @Override
    public void createTab(Project project) {
        ToolWindow toolWindow = JsonKit.getToolWindow(project);
        toolWindow.getContentManager().addContent(this.newContent(project));
    }

    @Override
    public Content newContent(Project project) {
        ContentFactory factory = ContentFactory.SERVICE.getInstance();
        Content content = factory.createContent(JsonWindowPanel.getInstance(project), String.format("Parser(%d)", tabIndex), false);
        content.putUserData(ToolWindow.SHOW_CONTENT_ICON, Boolean.TRUE);
        content.setIcon(AllIcons.Toolwindows.ToolWindowModuleDependencies);
        tabIndex++;
        return content;
    }

}
