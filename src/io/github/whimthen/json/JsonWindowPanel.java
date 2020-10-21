package io.github.whimthen.json;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.components.JBPanel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.ui.JBUI;
import io.github.whimthen.kits.JsonKit;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH;
import static com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED;

public class JsonWindowPanel extends SimpleToolWindowPanel {

    private final Project project;
    private final JPanel layoutPanel;
    private final DefaultActionGroup actionGroup;
    private JPanel resourcePanel;
    private JPanel resultPanel;
    private JSplitPane contentPanel;

    public JsonWindowPanel(@NotNull Project project) {
        super(false, true);
        this.project = project;
        this.actionGroup = new DefaultActionGroup();
        this.layoutPanel = new JBPanel<>(new GridLayoutManager(
                1, 2, JBUI.emptyInsets(), 0, 0
        ));
        initToolbar();
        initResourcePanel();
        initResultPanel();
        initContentPanel();
        this.setContent(this.layoutPanel);
    }

    public static JsonWindowPanel getInstance(@NotNull Project project) {
        return new JsonWindowPanel(project);
    }

    private void initContentPanel() {
        this.contentPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, this.resourcePanel, this.resultPanel);
        this.contentPanel.setContinuousLayout(true);
        this.contentPanel.setDividerSize(1);
        this.contentPanel.setBorder(JBUI.Borders.empty());

        GridConstraints constraints = new GridConstraints();
        constraints.setColumn(1);
        constraints.setFill(FILL_BOTH);
        this.layoutPanel.add(this.contentPanel, constraints);
    }

    private void initResourcePanel() {
        Document document = EditorFactory.getInstance().createDocument("");
        EditorTextField textField = new EditorTextField(document, this.project, FileTypes.PLAIN_TEXT, false, false);
        textField.setPreferredSize(new Dimension(0, 100));
        textField.setBorder(JBUI.Borders.empty());
        textField.addSettingsProvider(provider -> provider.getSettings().setUseSoftWraps(true));
        textField.setPlaceholder("Please input json text to parse...");
        textField.setBackground(JBUI.Panels.simplePanel().getBackground());

//        JPanel inputPanel = UI.PanelFactory.panel(textField).createPanel();
//        JPanel bottomPanel = UI.PanelFactory.panel(new JButton("Parse")).createPanel();
//        JPanel panel = UI.PanelFactory.grid().splitColumns()
//                .add(UI.PanelFactory.panel(inputPanel))
//                .add(UI.PanelFactory.panel(bottomPanel))
//                .createPanel();

        this.resourcePanel = new JPanel();
        this.resourcePanel.setLayout(new BoxLayout(this.resourcePanel, BoxLayout.Y_AXIS));
        this.resourcePanel.setBorder(JBUI.Borders.empty());
        this.resourcePanel.add(textField);
    }

    private void initResultPanel() {
        this.resultPanel = new JPanel();
        TextArea textArea = new TextArea();
        textArea.setBackground(this.resultPanel.getBackground());
        this.resultPanel.add(textArea);
    }

    private void initToolbar() {
        JBPanel<SimpleToolWindowPanel> toolbar = new JBPanel<>(new BorderLayout());
        toolbar.setBorder(JBUI.Borders.customLine(JBUI.CurrentTheme.DefaultTabs.borderColor(),
                0, 0, 0, 1));

        this.actionGroup.add(JsonKit.getConnectAction());
        this.actionGroup.addSeparator();

        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLWINDOW_TITLE, this.actionGroup, false);
        actionToolbar.setTargetComponent(toolbar);
        toolbar.add(actionToolbar.getComponent());

        GridConstraints constraints = new GridConstraints();
        constraints.setColumn(0);
        constraints.setFill(FILL_BOTH);
        constraints.setHSizePolicy(SIZEPOLICY_FIXED);
        this.layoutPanel.add(toolbar, constraints);
    }

}
