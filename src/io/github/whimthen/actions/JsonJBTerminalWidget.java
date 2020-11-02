package io.github.whimthen.actions;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.terminal.JBTerminalSystemSettingsProviderBase;
import com.intellij.terminal.JBTerminalWidget;
import com.intellij.terminal.TerminalExecutionConsole;
import jdk.nashorn.internal.ir.Terminal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class JsonJBTerminalWidget extends JBTerminalWidget {

    public JsonJBTerminalWidget(@NotNull Project project, @NotNull JBTerminalSystemSettingsProviderBase settingsProvider, @NotNull Disposable parent) {
        super(project, settingsProvider, parent);
    }


    public JsonJBTerminalWidget(@NotNull Project project,
                                int columns,
                                int lines,
                                @NotNull JBTerminalSystemSettingsProviderBase settingsProvider,
                                @Nullable TerminalExecutionConsole console,
                                @NotNull Disposable parent) {
        super(project, columns, lines, settingsProvider, console, parent);
    }

//    @Override
//    public List<TerminalAction> getActions() {
//        List<com.jediterm.terminal.ui.TerminalAction> actions = super.getActions();
//
//    }
}
