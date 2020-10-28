package io.github.whimthen.json;

import com.intellij.json.JsonFileType;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.ex.DocumentEx;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.ui.Splitter;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.AnimatedIcon;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.panels.FlowLayoutWrapper;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.components.BorderLayoutPanel;
import io.github.whimthen.kits.JsonKit;
import io.github.whimthen.kits.UIKit;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;

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
        Splitter splitter = new Splitter(true, UIKit.DEFAULT_SPLITTER_DIVIDER_LOCATION);
        splitter.setFirstComponent(this.resourcePanel);
        splitter.setSecondComponent(this.resultPanel);

        GridConstraints constraints = new GridConstraints();
        constraints.setColumn(1);
        constraints.setFill(FILL_BOTH);
        this.layoutPanel.add(splitter, constraints);
    }

    private void initResourcePanel() {
        DocumentEx document = (DocumentEx) EditorFactory.getInstance().createDocument(UIKit.DEFAULT_DOCUMENT_CONTENT.replaceAll("(\n|\\s)", ""));
        EditorEx editor = (EditorEx) EditorFactory.getInstance().createEditor(document);

        EditorHighlighterFactory highlighterFactory = EditorHighlighterFactory.getInstance();
        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(editor.getDocument());

        SyntaxHighlighter syntaxHighlighter = SyntaxHighlighterFactory.getSyntaxHighlighter(JsonFileType.INSTANCE, project, virtualFile);
        EditorHighlighter editorHighlighter = highlighterFactory.createEditorHighlighter(syntaxHighlighter, editor.getColorsScheme());

        editor.setHighlighter(editorHighlighter);
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

        JPanel warp = new JPanel(new GridLayoutManager(1, 1, JBUI.insets(0, 0, -5, 0), 5, 5));
        warp.setMaximumSize(new Dimension(0, 40));
        warp.setPreferredSize(new Dimension(0, 40));
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        JPanel btnPanel = new JPanel(new BorderLayout());
//        btnPanel.setPreferredSize(new Dimension(0, 80));
//        btnPanel.setMinimumSize(new Dimension(0, 80));
//        btnPanel.setMaximumSize(new Dimension(0, 80));
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
//        btnPanel.setMaximumSize(new Dimension(0, 80));
        warp.add(btnPanel, UIKit.createColumnFillFixedConstraints(0, false));

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
