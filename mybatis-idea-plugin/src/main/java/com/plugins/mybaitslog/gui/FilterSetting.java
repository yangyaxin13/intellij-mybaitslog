package com.plugins.mybaitslog.gui;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.ColorChooser;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.FormBuilder;
import com.plugins.mybaitslog.Config;
import com.plugins.mybaitslog.gui.compone.MyColorButton;
import com.plugins.mybaitslog.gui.compone.MyTableModel;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * 过滤设置 窗口
 * 重构为使用 DialogWrapper 和 FormBuilder
 */
public class FilterSetting extends DialogWrapper {

    private final ResourceBundle bundle;

    private JBTextField preparingTextField;
    private JBCheckBox startupCheckBox;
    private JBCheckBox checkBox_sql;
    private JBCheckBox checkBox_notification;
    private JBCheckBox checkBox_fold;
    private JBCheckBox checkBox_welcome;
    private JBCheckBox checkBox_rmi;

    private MyColorButton btnSelect;
    private MyColorButton btnUpdate;
    private MyColorButton btnDelete;
    private MyColorButton btnInsert;
    private MyColorButton btnOther;

    private JBTextArea addOpensTextArea;
    private JBTable excludeTable;
    private MyTableModel myTableModel;

    public FilterSetting() {
        super(true); // use current window as parent
        // 加载资源包，默认中文
        bundle = ResourceBundle.getBundle("messages.MyBatisLogBundle", java.util.Locale.CHINA);

        init();
        setTitle(bundle.getString("title"));
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        // 初始化组件
        preparingTextField = new JBTextField(Config.Idea.getParameters());
        startupCheckBox = new JBCheckBox(bundle.getString("startup"), Config.Idea.getStartup());
        checkBox_sql = new JBCheckBox(bundle.getString("formatSql"), Config.Idea.getFormatSql());
        checkBox_notification = new JBCheckBox(bundle.getString("notification"), Config.Idea.getRunNotification());
        checkBox_fold = new JBCheckBox(bundle.getString("fold"), Config.Idea.getWhetherfold());
        checkBox_welcome = new JBCheckBox(bundle.getString("welcomeMessage"), Config.Idea.getWelcomeMessage());
        checkBox_rmi = new JBCheckBox(bundle.getString("rmi"), Config.Idea.getRunRmi());

        // 颜色按钮
        btnSelect = createColorButton("select");
        btnUpdate = createColorButton("update");
        btnDelete = createColorButton("delete");
        btnInsert = createColorButton("insert");
        btnOther = createColorButton("other");

        // 表格
        myTableModel = new MyTableModel();
        Map<String, Boolean> perRunMap = Config.Idea.getPerRunMap();
        for (Map.Entry<String, Boolean> next : perRunMap.entrySet()) {
            myTableModel.addRow(new Object[]{next.getKey(), next.getValue()});
        }
        excludeTable = new JBTable(myTableModel);
        JBScrollPane tableScroll = new JBScrollPane(excludeTable);
        tableScroll.setPreferredSize(new Dimension(400, 100));

        // Add Opens
        addOpensTextArea = new JBTextArea(String.join("\n", Config.Idea.getAddOpens()));
        JBScrollPane addOpensScroll = new JBScrollPane(addOpensTextArea);
        addOpensScroll.setPreferredSize(new Dimension(400, 60));

        // 构建布局
        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        colorPanel.add(new JLabel(bundle.getString("select"))); colorPanel.add(btnSelect);
        colorPanel.add(new JLabel(bundle.getString("update"))); colorPanel.add(btnUpdate);
        colorPanel.add(new JLabel(bundle.getString("delete"))); colorPanel.add(btnDelete);
        colorPanel.add(new JLabel(bundle.getString("insert"))); colorPanel.add(btnInsert);
        colorPanel.add(new JLabel(bundle.getString("other"))); colorPanel.add(btnOther);

        JPanel checkBoxPanel = new JPanel(new GridLayout(2, 3));
        checkBoxPanel.add(startupCheckBox);
        checkBoxPanel.add(checkBox_sql);
        checkBoxPanel.add(checkBox_notification);
        checkBoxPanel.add(checkBox_fold);
        checkBoxPanel.add(checkBox_welcome);
        checkBoxPanel.add(checkBox_rmi);

        return FormBuilder.createFormBuilder()
                .addLabeledComponent(bundle.getString("preparing"), preparingTextField)
                .addComponent(checkBoxPanel)
                .addComponent(colorPanel)
                .addLabeledComponent(bundle.getString("exclude"), tableScroll)
                .addLabeledComponent(bundle.getString("addOpens"), addOpensScroll)
                .getPanel();
    }

    private MyColorButton createColorButton(String type) {
        MyColorButton btn = new MyColorButton(Config.Idea.getColor(type));
        btn.addActionListener(e -> {
            Color newColor = ColorChooser.chooseColor(this.getRootPane(), "Choose Color", Color.white, true);
            if (newColor != null) {
                btn.setColor(newColor);
                Config.Idea.setColor(type, newColor);
            }
        });
        return btn;
    }

    @Override
    protected void doOKAction() {
        // 保存设置
        String preparing = preparingTextField.getText();
        String addOpens = addOpensTextArea.getText();

        final int rowCount = myTableModel.getRowCount();
        for (int r = 0; r < rowCount; r++) {
            final Object key = myTableModel.getValueAt(r, 0);
            final Object value = myTableModel.getValueAt(r, 1);
            Config.Idea.setPerRunMap((String) key, (Boolean) value, true);
        }

        Config.Idea.setAddOpens(addOpens);
        Config.Idea.setParameters(preparing, Config.Idea.PARAMETERS);
        Config.Idea.setStartup(startupCheckBox.isSelected() ? 1 : 0);
        Config.Idea.setFormatSql(checkBox_sql.isSelected() ? 1 : 0);
        Config.Idea.setRunNotification(checkBox_notification.isSelected() ? 1 : 0);
        Config.Idea.setWelcomeMessage(checkBox_welcome.isSelected() ? 1 : 0);
        Config.Idea.setWhetherfold(checkBox_fold.isSelected() ? 1 : 0);
        Config.Idea.setRunRmi(checkBox_rmi.isSelected() ? 1 : 0);

        super.doOKAction();
    }
}
