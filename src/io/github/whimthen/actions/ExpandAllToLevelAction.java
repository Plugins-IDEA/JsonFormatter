package io.github.whimthen.actions;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import io.github.whimthen.kits.JsonAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExpandAllToLevelAction extends DefaultActionGroup implements CEInvokeAction {

    @NotNull
    @Override
    public AnAction[] getChildren(@Nullable AnActionEvent e) {
        AnAction[] actions = new AnAction[5];
        actions[0] = ActionManager.getInstance().getAction(JsonAction.ID.EXPAND_ALL_TO_LEVEL_1);
        actions[1] = ActionManager.getInstance().getAction(JsonAction.ID.EXPAND_ALL_TO_LEVEL_2);
        actions[2] = ActionManager.getInstance().getAction(JsonAction.ID.EXPAND_ALL_TO_LEVEL_3);
        actions[3] = ActionManager.getInstance().getAction(JsonAction.ID.EXPAND_ALL_TO_LEVEL_4);
        actions[4] = ActionManager.getInstance().getAction(JsonAction.ID.EXPAND_ALL_TO_LEVEL_5);
        return actions;
    }

}
