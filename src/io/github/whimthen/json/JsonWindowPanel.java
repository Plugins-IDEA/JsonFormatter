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
import com.intellij.openapi.ui.panel.ComponentPanelBuilder;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.ui.components.JBPanel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UI;
import io.github.whimthen.kits.JsonKit;
import org.jetbrains.annotations.NotNull;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import java.util.Objects;

import static com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH;
import static com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED;

public class JsonWindowPanel extends SimpleToolWindowPanel {

    private final Project project;
    private final JPanel layoutPanel;
    private final DefaultActionGroup actionGroup;
    private JPanel resourcePanel;
    private JPanel resultPanel;
    private JSplitPane contentPanel;
    private Document resourceDocument;
    private Document resultDocument;

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
//        this.contentPanel.setDividerSize(2);
        this.contentPanel.setBorder(JBUI.Borders.empty());

        GridConstraints constraints = new GridConstraints();
        constraints.setColumn(1);
        constraints.setFill(FILL_BOTH);
        this.layoutPanel.add(this.contentPanel, constraints);
    }

    private void initResourcePanel() {
        this.resourceDocument = EditorFactory.getInstance().createDocument("");
        EditorEx editor = (EditorEx) EditorFactory.getInstance().createEditor(this.resourceDocument);
//        EditorSettings settings = editor.getSettings();
//        settingEditor(settings);
//        editor.setBorder(JBUI.Borders.empty());

        PsiFile psiFile = PsiDocumentManager.getInstance(this.project).getPsiFile(editor.getDocument());
        if (psiFile != null) {
            DaemonCodeAnalyzer.getInstance(this.project).setHighlightingEnabled(psiFile, false);
        }

        EditorHighlighterFactory highlighterFactory = EditorHighlighterFactory.getInstance();
        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(editor.getDocument());

        SyntaxHighlighter syntaxHighlighter = Json5SyntaxHighlightingFactory.getSyntaxHighlighter(Json5FileType.INSTANCE, editor.getProject(), virtualFile);
        EditorHighlighter editorHighlighter = highlighterFactory.createEditorHighlighter(syntaxHighlighter, editor.getColorsScheme());

        editor.setHighlighter(editorHighlighter);
        editor.setBorder(JBUI.Borders.empty());
        editor.setCaretEnabled(true);
        editor.setFile(virtualFile);

        EditorSettings settings = editor.getSettings();
        settings.setLanguageSupplier(() -> Json5Language.INSTANCE);
        settingEditor(settings);
        settings.setUseSoftWraps(false);

        ComponentPanelBuilder btnPanelBuilder = UI.PanelFactory.panel(new JButton("Parse"));

        this.resourcePanel = UI.PanelFactory.grid()
                .add(UI.PanelFactory.panel(editor.getComponent()))
                .add(btnPanelBuilder)
                .createPanel();
        this.resourcePanel.setLayout(new BoxLayout(this.resourcePanel, BoxLayout.Y_AXIS));
        this.resourcePanel.setBorder(JBUI.Borders.empty());
    }

    private void initResultPanel() {
        this.resultDocument = EditorFactory.getInstance().createDocument("{\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"cycleEnName\": \"180 day free\",\n" +
                "      \"cycleId\": 2,\n" +
                "      \"cycleName\": \"按天计息（最长180天）\",\n" +
                "      \"id\": 2,\n" +
                "      \"interestOneDayRateShow\": \"0.547945%\",\n" +
                "      \"interestRate\": 2.000000,\n" +
                "      \"interestRateShow\": \"200%\",\n" +
                "      \"type\": 1\n" +
                "    },\n" +
                "    {\n" +
                "      \"cycleEnName\": \"180 day free\",\n" +
                "      \"cycleId\": 2,\n" +
                "      \"cycleName\": \"按天计息（最长180天）\",\n" +
                "      \"id\": 6,\n" +
                "      \"interestOneDayRateShow\": \"0.002191%\",\n" +
                "      \"interestRate\": 0.008000,\n" +
                "      \"interestRateShow\": \"0.8%\",\n" +
                "      \"type\": 1\n" +
                "    },\n" +
                "    {\n" +
                "      \"cycleEnName\": \"30 days\",\n" +
                "      \"cycleId\": 1,\n" +
                "      \"cycleName\": \"1个月\",\n" +
                "      \"id\": 7,\n" +
                "      \"interestOneDayRateShow\": \"0.002191%\",\n" +
                "      \"interestRate\": 0.008000,\n" +
                "      \"interestRateShow\": \"0.8%\",\n" +
                "      \"type\": 1\n" +
                "    },\n" +
                "    {\n" +
                "      \"cycleEnName\": \"30 days\",\n" +
                "      \"cycleId\": 1,\n" +
                "      \"cycleName\": \"1个月\",\n" +
                "      \"id\": 1,\n" +
                "      \"interestOneDayRateShow\": \"0.013698%\",\n" +
                "      \"interestRate\": 0.050000,\n" +
                "      \"interestRateShow\": \"5%\",\n" +
                "      \"type\": 1\n" +
                "    }\n" +
                "  ]\n" +
                "}");
        EditorEx editor = (EditorEx) EditorFactory.getInstance().createViewer(this.resultDocument);

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
        settingEditor(settings);
        settings.setUseSoftWraps(false);

        this.resultPanel = new JPanel();
        this.resultPanel.setLayout(new BoxLayout(this.resultPanel, BoxLayout.Y_AXIS));
        this.resultPanel.setBorder(JBUI.Borders.empty());
        this.resultPanel.add(editor.getComponent());
    }

    private void settingEditor(EditorSettings settings) {
        // 是否自动换行
        settings.setUseSoftWraps(true);
        // 编辑器右侧的竖线是否显示
        settings.setRightMarginShown(false);
        settings.setWhitespacesShown(true);
        settings.setLeadingWhitespaceShown(true);
        settings.setInnerWhitespaceShown(true);
        settings.setTrailingWhitespaceShown(true);
        settings.setWrapWhenTypingReachesRightMargin(true);
        settings.setLineNumbersShown(true);
        settings.setLineMarkerAreaShown(true);
        settings.setGutterIconsShown(true);
        settings.setFoldingOutlineShown(true);
        settings.setCaretInsideTabs(true);
        settings.setCaretRowShown(false);
        settings.setAnimatedScrolling(true);
        settings.setAdditionalPageAtBottom(false);
        settings.setShowingSpecialChars(true);

        // 右边距
        settings.setRightMargin(-1);
        // 设置软边距
//        settings.setSoftMargins(Lists.newArrayList());
        // 其他行数
//        settings.setAdditionalLinesCount(0);
        // 其他列数
//        settings.setAdditionalColumnsCount(0);
        // 启用自动代码折叠
        settings.setAutoCodeFoldingEnabled(true);
//        settings.setUseTabCharacter(false);
//        settings.setTabSize(0);
//        settings.setSmartHome(false);
//        settings.setVirtualSpace(false);
        // 光标
//        settings.setBlinkCaret(true);
        // 光标闪动时间
//        settings.setCaretBlinkPeriod(5);
        // 块光标
//        settings.setBlockCursor(false);
        // 块光标宽度
//        settings.setLineCursorWidth(0);
        // 驼峰
//        settings.setCamelWords(false);
//        settings.setDndEnabled(false);
        // 鼠标滚动改变字体大小
//        settings.setWheelFontChangeEnabled(false);
        // 鼠标打击选中单词
//        settings.setMouseClickSelectionHonorsCamelWords(false);
        // 启用变量就地重命名
        settings.setVariableInplaceRenameEnabled(true);
        // 避免滚动
        settings.setRefrainFromScrolling(false);
        // 显示缩进指南
        settings.setIndentGuidesShown(true);
        // 自定义缩进
//        settings.setUseCustomSoftWrapIndent(false);
//        settings.setCustomSoftWrapIndent(0);
        // 允许单个逻辑行折叠
//        settings.setAllowSingleLogicalLineFolding(true);
        // 预选重命名
//        settings.setPreselectRename(false);
        // 显示意图灯泡
        settings.setShowIntentionBulb(true);
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
