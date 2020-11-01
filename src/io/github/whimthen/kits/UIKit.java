package io.github.whimthen.kits;

import com.intellij.openapi.editor.EditorSettings;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.ui.JBUI;

import javax.swing.border.Border;

import static com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH;
import static com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED;

public class UIKit {

    public static final float DEFAULT_SPLITTER_DIVIDER_LOCATION = .3F;
    public static final String DEFAULT_DOCUMENT_CONTENT = "[{\"cycleEnName\":\"180dayfree\",\"cycleId\":2,\"cycleName\":\"按天计息（最长180天）\",\"id\":2,\"interestOneDayRateShow\":\"0.547945%\",\"interestRate\":2.000000,\"interestRateShow\":\"200%\",\"type\":1},{\"cycleEnName\":\"180dayfree\",\"cycleId\":2,\"cycleName\":\"按天计息（最长180天）\",\"id\":6,\"interestOneDayRateShow\":\"0.002191%\",\"interestRate\":0.008000,\"interestRateShow\":\"0.8%\",\"type\":1},{\"cycleEnName\":\"30days\",\"cycleId\":1,\"cycleName\":\"1个月\",\"id\":7,\"interestOneDayRateShow\":\"0.002191%\",\"interestRate\":0.008000,\"interestRateShow\":\"0.8%\",\"type\":1},{\"cycleEnName\":\"30days\",\"cycleId\":1,\"cycleName\":\"1个月\",\"id\":1,\"interestOneDayRateShow\":\"0.013698%\",\"interestRate\":0.050000,\"interestRateShow\":\"5%\",\"type\":1}]".replaceAll("(\n|\\s)", "");

    public static Border topBorder(int offset) {
        return JBUI.Borders.customLine(JBUI.CurrentTheme.ToolWindow.borderColor(), offset, 0, 0, 0);
    }

    public static Border leftBorder(int offset) {
        return JBUI.Borders.customLine(JBUI.CurrentTheme.ToolWindow.borderColor(), 0, offset, 0, 0);
    }

    public static Border rightBorder(int offset) {
        return JBUI.Borders.customLine(JBUI.CurrentTheme.ToolWindow.borderColor(), 0, 0, 0, offset);
    }

    public static Border bottomBorder(int offset) {
        return JBUI.Borders.customLine(JBUI.CurrentTheme.ToolWindow.borderColor(), 0, 0, offset, 0);
    }

    public static GridLayoutManager createGridConstraints(int row, int column) {
        return new GridLayoutManager(
                row, column, JBUI.emptyInsets(), 0, 0
        );
    }

    public static GridConstraints createRowFillFixedConstraints(int row) {
        GridConstraints constraints = new GridConstraints();
        constraints.setRow(row);
        constraints.setFill(FILL_BOTH);
        constraints.setHSizePolicy(SIZEPOLICY_FIXED);
        return constraints;
    }

    public static GridConstraints createColumnFillFixedConstraints(int column, boolean horizontal) {
        GridConstraints constraints = new GridConstraints();
        constraints.setColumn(column);
        constraints.setFill(FILL_BOTH);
        if (horizontal) {
            constraints.setHSizePolicy(SIZEPOLICY_FIXED);
        } else {
            constraints.setVSizePolicy(SIZEPOLICY_FIXED);
        }
        return constraints;
    }

    public static void settingEditor(EditorSettings settings) {
        // 是否自动换行
        settings.setUseSoftWraps(true);
        // 编辑器右侧的竖线是否显示
        settings.setRightMarginShown(false);
//        settings.setWhitespacesShown(true);
//        settings.setLeadingWhitespaceShown(true);
//        settings.setInnerWhitespaceShown(true);
//        settings.setTrailingWhitespaceShown(true);
        settings.setWrapWhenTypingReachesRightMargin(true);
        settings.setLineNumbersShown(true);
        settings.setLineMarkerAreaShown(true);
        settings.setGutterIconsShown(true);
        settings.setFoldingOutlineShown(true);
//        settings.setCaretInsideTabs(true);
        settings.setCaretRowShown(false);
        settings.setAnimatedScrolling(true);
        settings.setAdditionalPageAtBottom(false);
        settings.setShowingSpecialChars(true);

        // 右边距
        settings.setRightMargin(-1);
        // 设置软边距
//        settings.setSoftMargins(Lists.newArrayList());
        // 其他行数, 如果不设置值则会在编辑器下方有一块空白可滚动
        settings.setAdditionalLinesCount(0);
        // 其他列数
        settings.setAdditionalColumnsCount(0);
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
        // 鼠标单击选中单词
//        settings.setMouseClickSelectionHonorsCamelWords(false);
        // 启用变量就地重命名
        settings.setVariableInplaceRenameEnabled(true);
        // 避免滚动
        settings.setRefrainFromScrolling(false);
        // 显示缩进指南
//        settings.setIndentGuidesShown(true);
        // 自定义缩进
//        settings.setUseCustomSoftWrapIndent(false);
//        settings.setCustomSoftWrapIndent(0);
        // 允许单个逻辑行折叠
//        settings.setAllowSingleLogicalLineFolding(true);
        // 预选重命名
//        settings.setPreselectRename(false);
        // 显示意图灯泡
        settings.setShowIntentionBulb(false);
    }

}
