package io.github.whimthen.json;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInsight.daemon.impl.TrafficLightRenderer;
import com.intellij.codeInsight.intention.IntentionManager;
import com.intellij.codeInspection.ex.InspectionProfileImpl;
import com.intellij.codeInspection.ex.InspectionProfileWrapper;
import com.intellij.json.json5.Json5Language;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.SpellCheckingEditorCustomizationProvider;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.impl.EditorMarkupModelImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.ui.Splitter;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.ui.AdditionalPageAtBottomEditorCustomization;
import com.intellij.ui.AnimatedIcon;
import com.intellij.ui.ColorUtil;
import com.intellij.ui.EditorCustomization;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.EditorTextFieldProvider;
import com.intellij.ui.ErrorStripeEditorCustomization;
import com.intellij.ui.SoftWrapsEditorCustomization;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.panels.HorizontalLayout;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UI;
import com.intellij.util.ui.UIUtil;
import io.github.whimthen.kits.JsonAction;
import io.github.whimthen.kits.UIKit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import static com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH;
import static com.intellij.util.containers.ContainerUtil.addIfNotNull;

public class JsonWindowPanel extends SimpleToolWindowPanel {

    private final Project project;
    private final JPanel layoutPanel;
    private final DefaultActionGroup actionGroup;
    private JPanel resourcePanel;
    private ResultToolWindowPanel resultPanel;
    private JButton formatterBtn;

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
//                        profile -> new InspectionProfileWrapper(CommitMessageInspectionProfile.getInstance(myProject)));
                        profile -> new InspectionProfileWrapper(new InspectionProfileImpl("JsonFormatter")));
            }
            editor.putUserData(IntentionManager.SHOW_INTENTION_OPTIONS_KEY, true);
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
                editorMarkupModel.setErrorStripeVisible(true);
            }
        }

        private boolean hasHighSeverities(@NotNull int[] errorCount) {
            HighlightSeverity minSeverity = HighlightDisplayLevel.ERROR.getSeverity();

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
        features.add(SoftWrapsEditorCustomization.ENABLED);
        features.add(AdditionalPageAtBottomEditorCustomization.DISABLED);
        features.add(editor -> {
            editor.setBackgroundColor(UIUtil.getEditorPaneBackground()); // to use background from set color scheme
            editor.setBorder(JBUI.Borders.empty());
            boolean isLaFDark = ColorUtil.isDark(UIUtil.getPanelBackground());
            boolean isEditorDark = EditorColorsManager.getInstance().isDarkEditor();
            EditorColorsScheme scheme = isLaFDark == isEditorDark
                    ? EditorColorsManager.getInstance().getGlobalScheme()
                    : EditorColorsManager.getInstance().getSchemeForCurrentUITheme();
            editor.setColorsScheme(scheme);
            EditorSettings settings = editor.getSettings();
            settings.setLineNumbersShown(false);
            settings.setUseSoftWraps(true);
            settings.setShowIntentionBulb(false);
            settings.setWrapWhenTypingReachesRightMargin(false);
            settings.setAllowSingleLogicalLineFolding(false);
        });
        features.add(ErrorStripeEditorCustomization.ENABLED);
        features.add(new InspectionCustomization(this.project));
        addIfNotNull(features, SpellCheckingEditorCustomizationProvider.getInstance().getEnabledCustomization());

        EditorTextField editorField = EditorTextFieldProvider.getInstance().getEditorField(Json5Language.INSTANCE, this.project, features);
        editorField.setText(UIKit.DEFAULT_DOCUMENT_CONTENT);
        editorField.setFontInheritedFromLAF(false);
        editorField.setPlaceholder("Enter the Json string to format...");
        editorField.setShowPlaceholderWhenFocused(true);

//        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        this.formatterBtn = new JButton("Format");
        this.formatterBtn.addActionListener(formatterBtnListener());
//        btnPanel.add(this.formatterBtn);
//
//        this.resourcePanel = new JPanel(UIKit.createGridConstraints(2, 1));
//        this.resourcePanel.add(editorField, UIKit.createRowFillFixedConstraints(0));
//        this.resourcePanel.add(btnPanel, UIKit.createRowFillFixedConstraints(1));
//        JPanel editorPanelWarp = (JPanel) this.resourcePanel.getComponent(0);
//        editorPanelWarp.setBorder(UIKit.bottomBorder(1));
//        this.resourcePanel.setLayout(new BoxLayout(this.resourcePanel, BoxLayout.Y_AXIS));
//        this.resourcePanel.setBorder(JBUI.Borders.empty());

        this.resourcePanel = UI.PanelFactory.grid().splitColumns()
                .add(UI.PanelFactory.panel(editorField).resizeX(true).resizeY(true))
                .add(UI.PanelFactory.panel(this.formatterBtn).resizeX(true).resizeY(false))
                .createPanel();
    }

    private void initResultPanel() {
        this.resultPanel = ResultToolWindowPanel.getInstance(this.project);
        this.resultPanel.setLayout(new BoxLayout(this.resultPanel, BoxLayout.Y_AXIS));
        this.resultPanel.setBorder(UIKit.topBorder(1));
    }

    private ActionListener formatterBtnListener() {
        return e -> {
            if (this.formatterBtn.getIcon() != null) {
                this.formatterBtn.setIcon(null);
            } else {
                this.formatterBtn.setIcon(new AnimatedIcon.Default());
            }
        };
    }

    private void initToolbar() {
        JBPanel<SimpleToolWindowPanel> toolbar = new JBPanel<>(new BorderLayout());
        toolbar.setBorder(JBUI.Borders.customLine(JBUI.CurrentTheme.DefaultTabs.borderColor(), 0, 0, 0, 1));

        this.actionGroup.add(ActionManager.getInstance().getAction(JsonAction.ID.ADD_TAB));
        this.actionGroup.addSeparator();

        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLWINDOW_TITLE, this.actionGroup, false);
        actionToolbar.setTargetComponent(toolbar);
        toolbar.add(actionToolbar.getComponent());

        this.layoutPanel.add(toolbar, UIKit.createColumnFillFixedConstraints(0, true));
    }

    public ResultToolWindowPanel getResultPanel() {
        return resultPanel;
    }

    public @NotNull Project getProject() {
        return this.project;
    }

}
