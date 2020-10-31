package io.github.whimthen.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import io.github.whimthen.kits.JsonAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExpandToLevelGroupAction extends DefaultActionGroup implements CEInvokeAction {

    private static final AnAction[] actions;

    static {
        actions = new AnAction[5];
        actions[0] = new ExpandToLevelAction(JsonAction.InternalID.EXPAND_TO_LEVEL_1);
        actions[1] = new ExpandToLevelAction(JsonAction.InternalID.EXPAND_TO_LEVEL_2);
        actions[2] = new ExpandToLevelAction(JsonAction.InternalID.EXPAND_TO_LEVEL_3);
        actions[3] = new ExpandToLevelAction(JsonAction.InternalID.EXPAND_TO_LEVEL_4);
        actions[4] = new ExpandToLevelAction(JsonAction.InternalID.EXPAND_TO_LEVEL_5);
    }

    @NotNull
    @Override
    public AnAction[] getChildren(@Nullable AnActionEvent e) {
        return actions;
    }

}
