package io.github.whimthen.json;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInsight.daemon.impl.TrafficLightRenderer;
import com.intellij.codeInsight.intention.IntentionManager;
import com.intellij.codeInspection.ex.InspectionProfileWrapper;
import com.intellij.json.JsonLanguage;
import com.intellij.json.json5.Json5FileType;
import com.intellij.json.json5.Json5Language;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.SpellCheckingEditorCustomizationProvider;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.impl.EditorMarkupModelImpl;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.ui.Splitter;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.ui.AdditionalPageAtBottomEditorCustomization;
import com.intellij.ui.AnimatedIcon;
import com.intellij.ui.ColorUtil;
import com.intellij.ui.EditorCustomization;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.EditorTextFieldProvider;
import com.intellij.ui.ErrorStripeEditorCustomization;
import com.intellij.ui.SoftWrapsEditorCustomization;
import com.intellij.ui.components.JBPanel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import com.intellij.vcs.commit.message.CommitMessageInspectionProfile;
import io.github.whimthen.kits.JsonKit;
import io.github.whimthen.kits.UIKit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.HashSet;
import java.util.Set;

import static com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH;
import static com.intellij.util.ObjectUtils.notNull;
import static com.intellij.util.containers.ContainerUtil.addIfNotNull;
import static com.intellij.vcs.commit.message.CommitMessageInspectionProfile.getBodyLimitSettings;

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

    private static class InspectionCustomization implements EditorCustomization {
        @NotNull
        private final Project myProject;

        InspectionCustomization(@NotNull Project project) {
            myProject = project;
        }

        @Override
        public void customize(@NotNull EditorEx editor) {
            PsiFile file = PsiDocumentManager.getInstance(myProject).getPsiFile(editor.getDocument());

            if (file != null) {
                file.putUserData(InspectionProfileWrapper.CUSTOMIZATION_KEY,
                        profile -> new InspectionProfileWrapper(CommitMessageInspectionProfile.getInstance(myProject)));
            }
            editor.putUserData(IntentionManager.SHOW_INTENTION_OPTIONS_KEY, false);
            ((EditorMarkupModelImpl) editor.getMarkupModel())
                    .setErrorStripeRenderer(new ConditionalTrafficLightRenderer(myProject, editor.getDocument()));
        }
    }

    private static class ConditionalTrafficLightRenderer extends TrafficLightRenderer {
        ConditionalTrafficLightRenderer(@NotNull Project project, @NotNull Document document) {
            super(project, document);
        }

        @Override
        protected void refresh(@Nullable EditorMarkupModelImpl editorMarkupModel) {
            super.refresh(editorMarkupModel);
            if (editorMarkupModel != null) {
                editorMarkupModel.setTrafficLightIconVisible(hasHighSeverities(getErrorCount()));
            }
        }

        private boolean hasHighSeverities(@NotNull int[] errorCount) {
            HighlightSeverity minSeverity = notNull(HighlightDisplayLevel.find("TYPO"), HighlightDisplayLevel.DO_NOT_SHOW).getSeverity();

            for (int i = 0; i < errorCount.length; i++) {
                if (errorCount[i] > 0 && getSeverityRegistrar().compare(getSeverityRegistrar().getSeverityByIndex(i), minSeverity) > 0) {
                    return true;
                }
            }
            return false;
        }
    }

    private void initResourcePanel() {
        Set<EditorCustomization> features = new HashSet<>();

//        features.add(new RightMarginCustomization(project));
        features.add(SoftWrapsEditorCustomization.ENABLED);
        features.add(AdditionalPageAtBottomEditorCustomization.DISABLED);
        features.add(editor -> {
            editor.setBackgroundColor(UIUtil.getEditorPaneBackground()); // to use background from set color scheme
            boolean isLaFDark = ColorUtil.isDark(UIUtil.getPanelBackground());
            boolean isEditorDark = EditorColorsManager.getInstance().isDarkEditor();
            EditorColorsScheme scheme = isLaFDark == isEditorDark
                    ? EditorColorsManager.getInstance().getGlobalScheme()
                    : EditorColorsManager.getInstance().getSchemeForCurrentUITheme();
            editor.setColorsScheme(scheme);
        });
        features.add(ErrorStripeEditorCustomization.ENABLED);
        features.add(new InspectionCustomization(project));
        addIfNotNull(features, SpellCheckingEditorCustomizationProvider.getInstance().getEnabledCustomization());

        EditorTextField editorField = EditorTextFieldProvider.getInstance().getEditorField(JsonLanguage.INSTANCE, this.project, features);

        // Global editor color scheme is set by EditorTextField logic. We also need to use font from it and not from the current LaF.
        editorField.setFontInheritedFromLAF(false);
//        editorField.getEditor().getSettings().setWrapWhenTypingReachesRightMargin(true);


//        EditorFactory editorFactory = EditorFactory.getInstance();
////        DocumentEx document = (DocumentEx) factory.createDocument(UIKit.DEFAULT_DOCUMENT_CONTENT.replaceAll("(\n|\\s)", ""));
//
//        LightVirtualFile virtualFile = new LightVirtualFile("", Json5FileType.INSTANCE, UIKit.DEFAULT_DOCUMENT_CONTENT.replaceAll("(\n|\\s)", ""));
////        EditorHighlighterFactory highlighterFactory = EditorHighlighterFactory.getInstance();
////        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);
//        Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
//        EditorEx editor = (EditorEx) editorFactory.createEditor(document, this.project, virtualFile, false);

//        new TrafficLightRenderer(this.project, document);

//        SyntaxHighlighter syntaxHighlighter = SyntaxHighlighterFactory.getSyntaxHighlighter(Json5Language.INSTANCE, null, virtualFile);
//        EditorHighlighter editorHighlighter = highlighterFactory.createEditorHighlighter(syntaxHighlighter, editor.getColorsScheme());
//
////        Formatter.getInstance().createFormattingModelForPsiFile(virtualFile, null, editor);
////        FormatterImpl.getInstance().formatAroundRange();
//
//        editor.setHighlighter(editorHighlighter);
        editorField.setBorder(JBUI.Borders.empty());
//        editorField.setCaretEnabled(true);
        editorField.setPlaceholder("Enter the Json string to format...");
        editorField.setShowPlaceholderWhenFocused(true);
//        editor.setInsertMode(true);

//        EditorSettings settings = editorField.getSettings();
//        UIKit.settingEditor(settings);
//        settings.setLineNumbersShown(false);
//        settings.setUseSoftWraps(true);
//        settings.setGutterIconsShown(true);
//        settings.setLineMarkerAreaShown(false);
//        settings.setWrapWhenTypingReachesRightMargin(false);
//        settings.setIndentGuidesShown(false);
//        settings.setAllowSingleLogicalLineFolding(false);
//        settings.setLanguageSupplier(() -> JsonLanguage.INSTANCE);

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
        this.resourcePanel.add(editorField, UIKit.createRowFillFixedConstraints(0));
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
