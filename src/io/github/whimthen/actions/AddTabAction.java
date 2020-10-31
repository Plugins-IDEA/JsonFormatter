package io.github.whimthen.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import io.github.whimthen.service.JsonService;
import org.jetbrains.annotations.NotNull;

public class AddTabAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        JsonService.getInstance().createTab(event.getProject());
    }

}
