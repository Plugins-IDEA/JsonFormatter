package io.github.whimthen.json;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextArea;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.ui.EditorAdapter;
import com.intellij.util.ui.JBUI;
import io.github.whimthen.kits.JsonKit;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

import static com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH;
import static com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED;

public class JsonWindowPanel extends SimpleToolWindowPanel {

    private final Project project;
    private final JPanel contentPanel;
    private final DefaultActionGroup actionGroup;

    public JsonWindowPanel(@NotNull Project project) {
        super(false, true);
        this.project = project;
        this.actionGroup = new DefaultActionGroup();
        this.contentPanel = new JPanel(new GridLayoutManager(
                2, 1, JBUI.emptyInsets(), 0, 0
        ));
        this.setContent(createContentPanel());
    }

    public static JsonWindowPanel getInstance(@NotNull Project project) {
        return new JsonWindowPanel(project);
    }

    private JPanel createContentPanel() {
        JBPanel<SimpleToolWindowPanel> panel = new JBPanel<>(new GridLayoutManager(
                1, 2, JBUI.emptyInsets(), 0, 0
        ));

        initToolbar(panel);
        initContentPanel(panel);
        return panel;
    }

    private void initContentPanel(JBPanel<SimpleToolWindowPanel> panel) {
        this.contentPanel.setBackground(JBColor.WHITE);
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(JBUI.Borders.empty());

        Document document = EditorFactory.getInstance().createDocument("");
//        Editor editor = EditorFactory.getInstance().createEditor(document);
//        EditorSettings settings = editor.getSettings();
//        settings.setLineNumbersShown(true);
//        settings.setDndEnabled(true);
        EditorTextField textField = new EditorTextField(document, this.project, FileTypes.PLAIN_TEXT, false, false);
        textField.setPreferredSize(new Dimension(0, 100));
        textField.setBorder(JBUI.Borders.empty());
        textField.addSettingsProvider(provider -> {
            provider.getSettings().setUseSoftWraps(true);
        });
        textField.setPlaceholder("Please input json text to parse...");
        inputPanel.add(textField);

        GridConstraints gridConstraints = new GridConstraints();
        gridConstraints.setRow(0);
        gridConstraints.setFill(FILL_BOTH);
        gridConstraints.setVSizePolicy(SIZEPOLICY_FIXED);
        gridConstraints.setHSizePolicy(SIZEPOLICY_FIXED);
        contentPanel.add(inputPanel, gridConstraints);

        GridConstraints constraints = new GridConstraints();
        constraints.setColumn(1);
        constraints.setFill(FILL_BOTH);
        panel.add(this.contentPanel, constraints);
    }

    private void initToolbar(JBPanel<SimpleToolWindowPanel> panel) {
        JBPanel<SimpleToolWindowPanel> toolbar = new JBPanel<>(new BorderLayout());
        toolbar.setBorder(JBUI.Borders.customLine(JBUI.CurrentTheme.DefaultTabs.borderColor(),
                0, 0, 0, 1));
        this.actionGroup.add(JsonKit.getConnectAction());
//        actionGroup.add(ComponentUtil.getDisConnectAction());
//        actionGroup.add(ComponentUtil.getPauseAction());
        this.actionGroup.addSeparator();
//        actionGroup.add(ComponentUtil.getClearAction());
//        actionGroup.add(ComponentUtil.getAddTabAction());
//        actionGroup.addSeparator();
//        actionGroup.add(ComponentUtil.getExpandAction());
//        actionGroup.add(ComponentUtil.getCollapseAction());

        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLWINDOW_TITLE, this.actionGroup, false);
        actionToolbar.setTargetComponent(toolbar);
        toolbar.add(actionToolbar.getComponent());

        GridConstraints constraints = new GridConstraints();
        constraints.setColumn(0);
        constraints.setFill(FILL_BOTH);
        constraints.setHSizePolicy(SIZEPOLICY_FIXED);
        panel.add(toolbar, constraints);
    }

}
