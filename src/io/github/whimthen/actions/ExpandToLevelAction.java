package io.github.whimthen.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import org.jetbrains.annotations.NotNull;

public class ExpandToLevelAction extends DefaultActionGroup implements CEInvokeAction {

    private final String internalId;

    public ExpandToLevelAction(String internalId) {
        this.internalId = internalId;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        this.invoke(this.internalId, e);
    }

}
