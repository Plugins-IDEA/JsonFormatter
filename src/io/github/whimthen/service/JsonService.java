package io.github.whimthen.service;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.content.Content;

public interface JsonService {

    static JsonService getInstance() {
        return ServiceManager.getService(JsonService.class);
    }

    Content initWindow(Project project);

    void createTab(Project project);

    Content newContent(Project project);

}
