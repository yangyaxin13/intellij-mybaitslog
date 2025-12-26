package com.plugins.mybaitslog.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.plugins.mybaitslog.console.BasicFormatter;
import com.plugins.mybaitslog.console.PrintlnUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * 恢复 SQL 的 Action
 */
public class RestoreSqlAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        final Editor editor = e.getData(CommonDataKeys.EDITOR);
        final Project project = e.getData(CommonDataKeys.PROJECT);
        if (editor == null || project == null) {
            return;
        }

        // 获取选中的文本
        String selectedText = editor.getSelectionModel().getSelectedText();
        if (StringUtils.isBlank(selectedText)) {
            Messages.showInfoMessage(project, "Please select MyBatis log text first.", "Restore SQL");
            return;
        }

        try {
            String sql = PrintlnUtil.restoreSqlFromText(selectedText);
            if (sql != null) {
                // 格式化 SQL
                String formattedSql = new BasicFormatter().format(sql);

                // 输出到 MyBatis Log 控制台
                PrintlnUtil.println(project, "--- Manual Restore SQL ---\n", com.intellij.execution.ui.ConsoleViewContentType.LOG_INFO_OUTPUT);
                PrintlnUtil.printlnSqlType(project, "", formattedSql + "\n");
                PrintlnUtil.println(project, "--------------------------\n", com.intellij.execution.ui.ConsoleViewContentType.LOG_INFO_OUTPUT);
            } else {
                Messages.showWarningDialog(project, "Could not parse SQL from selection.\nPlease ensure selection contains 'Preparing:' and 'Parameters:'", "Parse Error");
            }
        } catch (Exception ex) {
            Messages.showErrorDialog(project, "Error parsing SQL: " + ex.getMessage(), "Error");
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        // 只有在有选中文本时才启用
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        e.getPresentation().setEnabledAndVisible(editor != null && editor.getSelectionModel().hasSelection());
    }
}

