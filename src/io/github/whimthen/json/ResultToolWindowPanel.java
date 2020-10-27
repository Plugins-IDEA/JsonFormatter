package io.github.whimthen.json;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.json.json5.Json5FileType;
import com.intellij.json.json5.Json5Language;
import com.intellij.json.json5.highlighting.Json5SyntaxHighlightingFactory;
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
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.ui.components.JBPanel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.util.ui.JBUI;
import io.github.whimthen.kits.JsonKit;
import io.github.whimthen.kits.UIKit;
import org.jetbrains.annotations.NotNull;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.util.Objects;

import static com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH;

public class ResultToolWindowPanel extends SimpleToolWindowPanel {

    private final JPanel layoutPanel;
    private final DefaultActionGroup actionGroup;
    private final Document document;
    private final Project project;

    public static ResultToolWindowPanel getInstance(@NotNull Project project) {
        return new ResultToolWindowPanel(project);
    }

    public ResultToolWindowPanel(@NotNull Project project) {
        super(false, true);
        this.project = project;
        this.layoutPanel = new JBPanel<>(UIKit.createGridConstraints(2, 1));
        this.actionGroup = new DefaultActionGroup();
        this.document = EditorFactory.getInstance().createDocument(UIKit.DEFAULT_DOCUMENT_CONTENT);
        this.initToolbar();
        this.initContent();
        this.setContent(this.layoutPanel);
    }

    private void initContent() {
        EditorEx editor = (EditorEx) EditorFactory.getInstance().createViewer(this.document);

        Project project = this.project;
        if (Objects.nonNull(editor.getProject())) {
            project = editor.getProject();
        }

        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (psiFile != null) {
            DaemonCodeAnalyzer.getInstance(project).setHighlightingEnabled(psiFile, false);
        }

        EditorHighlighterFactory highlighterFactory = EditorHighlighterFactory.getInstance();
        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(editor.getDocument());

        SyntaxHighlighter syntaxHighlighter = Json5SyntaxHighlightingFactory.getSyntaxHighlighter(Json5FileType.INSTANCE, project, virtualFile);
        EditorHighlighter editorHighlighter = highlighterFactory.createEditorHighlighter(syntaxHighlighter, editor.getColorsScheme());

        editor.setHighlighter(editorHighlighter);
        editor.setBorder(JBUI.Borders.empty());
        editor.setCaretEnabled(true);
        editor.setFile(virtualFile);

        EditorSettings settings = editor.getSettings();
        settings.setLanguageSupplier(() -> Json5Language.INSTANCE);
        UIKit.settingEditor(settings);
        settings.setUseSoftWraps(false);


        GridConstraints constraints = new GridConstraints();
        constraints.setRow(1);
        constraints.setFill(FILL_BOTH);
        this.layoutPanel.add(editor.getComponent(), constraints);
    }

    private void initToolbar() {
        JBPanel<SimpleToolWindowPanel> toolbar = new JBPanel<>(new BorderLayout());
        toolbar.setBorder(UIKit.bottomBorder(1));

        this.actionGroup.add(JsonKit.getConnectAction());
        this.actionGroup.addSeparator();

        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, this.actionGroup, true);
        actionToolbar.setTargetComponent(toolbar);
        toolbar.add(actionToolbar.getComponent());

        this.layoutPanel.add(toolbar, UIKit.createColumnFillFixedConstraints(0, false));
    }

}
