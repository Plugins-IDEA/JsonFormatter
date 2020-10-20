package io.github.whimthen.json;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManagerEvent;
import com.intellij.ui.content.ContentManagerListener;
import io.github.whimthen.kits.JsonKit;
import io.github.whimthen.service.JsonServiceImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class JsonContentManagerListener implements ContentManagerListener {

    private final ToolWindow toolWindow;
    private final Project project;

    public JsonContentManagerListener(@NotNull ToolWindow toolWindow, @NotNull Project project) {
        this.toolWindow = toolWindow;
        this.project = project;
    }

    @Override
    public void contentAdded(@NotNull ContentManagerEvent event) {
        PrettyToolWindow.selectedContent.setCloseable(true);
        this.toolWindow.getContentManager().setSelectedContent(event.getContent());
    }

    @Override
    public void contentRemoved(@NotNull ContentManagerEvent event) {
        if (this.toolWindow.getContentManager().getContentCount() == 1) {
            Objects.requireNonNull(this.toolWindow.getContentManager().getContent(0)).setCloseable(false);
        }
        event.getContent().release();
    }

    @Override
    public void contentRemoveQuery(@NotNull ContentManagerEvent event) {
//        int yesNo = Messages.showYesNoDialog("The connection is about to be closed. Are you sure you want to close the tab?",
//                "Close Confirm",
//                Messages.getQuestionIcon());
//        if (yesNo != 0) {
//            event.consume();
//        }

        int currentIndex = event.getIndex();
        JsonServiceImpl.tabIndex--;
        Content[] contents = JsonKit.getToolWindow(this.project).getContentManager().getContents();
        for (int i = 0; i < contents.length; i++) {
            if (i <= currentIndex) {
                continue;
            }
            String tabName = String.format("Parser(%d)", i);
            contents[i].setDisplayName(tabName);
            contents[i].setTabName(tabName);
        }
    }

    @Override
    public void selectionChanged(@NotNull ContentManagerEvent event) {
        PrettyToolWindow.selectedContent = event.getContent();
    }
}
