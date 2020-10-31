package io.github.whimthen.actions;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import io.github.whimthen.json.ResultToolWindowPanel;
import io.github.whimthen.kits.JsonKit;

public interface CEInvokeAction {

    default void invoke(String internalId, AnActionEvent event) {
        EditorAction action = (EditorAction) ActionManager.getInstance().getAction(internalId);
        ResultToolWindowPanel resultPanel = JsonKit.getResultPanel();
        action.actionPerformed(resultPanel.getEditor(), event.getDataContext());
    }

}
