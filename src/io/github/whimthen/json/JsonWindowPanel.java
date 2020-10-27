package io.github.whimthen.json;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.AnimatedIcon;
import com.intellij.ui.components.JBPanel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.util.ui.JBUI;
import io.github.whimthen.kits.JsonKit;
import io.github.whimthen.kits.UIKit;
import org.jetbrains.annotations.NotNull;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Objects;

import static com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH;

public class JsonWindowPanel extends SimpleToolWindowPanel {

    private final Project project;
    private final JPanel layoutPanel;
    private final DefaultActionGroup actionGroup;
    private JPanel resourcePanel;
    private JPanel resultPanel;

    public JsonWindowPanel(@NotNull Project project) {
        super(false, true);
        this.project = project;
        this.actionGroup = new DefaultActionGroup();
        this.layoutPanel = new JBPanel<>(UIKit.createGridConstraints(1, 2));
        this.initToolbar();
        this.initResourcePanel();
        this.initResultPanel();
        this.initContentPanel();
        this.setContent(this.layoutPanel);
    }

    public static JsonWindowPanel getInstance(@NotNull Project project) {
        return new JsonWindowPanel(project);
    }

    private void initContentPanel() {
        JSplitPane contentPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, this.resourcePanel, this.resultPanel);
        contentPanel.setDividerSize(3);
        contentPanel.setBorder(JBUI.Borders.empty());
        contentPanel.setDividerLocation(UIKit.DEFAULT_JSPLIT_DIVIDER_LOCATION);

        GridConstraints constraints = new GridConstraints();
        constraints.setColumn(1);
        constraints.setFill(FILL_BOTH);
        this.layoutPanel.add(contentPanel, constraints);
    }

    private void initResourcePanel() {
        Document document = EditorFactory.getInstance().createDocument(UIKit.DEFAULT_DOCUMENT_CONTENT);
        EditorEx editor = (EditorEx) EditorFactory.getInstance().createEditor(document);

        EditorHighlighterFactory highlighterFactory = EditorHighlighterFactory.getInstance();
        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(editor.getDocument());

        if (Objects.nonNull(virtualFile)) {
            SyntaxHighlighter syntaxHighlighter = SyntaxHighlighterFactory.getSyntaxHighlighter(virtualFile.getFileType(), project, virtualFile);
            EditorHighlighter editorHighlighter = highlighterFactory.createEditorHighlighter(syntaxHighlighter, editor.getColorsScheme());

            editor.setHighlighter(editorHighlighter);
        }
        editor.setBorder(JBUI.Borders.empty());
        editor.setCaretEnabled(true);
        editor.setPlaceholder("Please enter the Json string to format...");
        editor.setShowPlaceholderWhenFocused(true);
        editor.setInsertMode(true);

        EditorSettings settings = editor.getSettings();
        UIKit.settingEditor(settings);
        settings.setLineNumbersShown(false);
        settings.setUseSoftWraps(true);
        settings.setGutterIconsShown(true);
        settings.setLineMarkerAreaShown(false);
        settings.setWrapWhenTypingReachesRightMargin(false);
        settings.setIndentGuidesShown(false);
        settings.setAllowSingleLogicalLineFolding(false);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton button = new JButton("Format");
//        button.setBorder(JBUI.Borders.empty());
//        btnPanel.add(CommonUI.createButton("Format", e -> {
//
//        }));
        button.addActionListener(e -> {
            if (button.getIcon() != null) {
                button.setIcon(null);
            } else {
                button.setIcon(new AnimatedIcon.Default());
            }
        });
        btnPanel.add(button);

        this.resourcePanel = new JPanel(UIKit.createGridConstraints(2, 1));
        this.resourcePanel.add(editor.getComponent(), UIKit.createRowFillFixedConstraints(0));
        this.resourcePanel.add(btnPanel, UIKit.createRowFillFixedConstraints(1));
        JPanel editorPanelWarp = (JPanel) this.resourcePanel.getComponent(0);
        editorPanelWarp.setBorder(UIKit.bottomBorder(1));
        this.resourcePanel.setLayout(new BoxLayout(this.resourcePanel, BoxLayout.Y_AXIS));
        this.resourcePanel.setBorder(JBUI.Borders.empty());
    }

    private void initResultPanel() {
        this.resultPanel = ResultToolWindowPanel.getInstance(this.project);
        this.resultPanel.setLayout(new BoxLayout(this.resultPanel, BoxLayout.Y_AXIS));
        this.resultPanel.setBorder(UIKit.topBorder(1));
    }

    private void initToolbar() {
        JBPanel<SimpleToolWindowPanel> toolbar = new JBPanel<>(new BorderLayout());
        toolbar.setBorder(JBUI.Borders.customLine(JBUI.CurrentTheme.DefaultTabs.borderColor(), 0, 0, 0, 1));

        this.actionGroup.add(JsonKit.getConnectAction());
        this.actionGroup.addSeparator();

        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLWINDOW_TITLE, this.actionGroup, false);
        actionToolbar.setTargetComponent(toolbar);
        toolbar.add(actionToolbar.getComponent());

        this.layoutPanel.add(toolbar, UIKit.createColumnFillFixedConstraints(0, true));
    }

}
