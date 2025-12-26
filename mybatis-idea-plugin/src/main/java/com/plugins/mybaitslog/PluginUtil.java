package com.plugins.mybaitslog;

import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.extensions.PluginId;
import com.plugins.mybaitslog.gui.FilterSetting;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.HyperlinkEvent;
import java.io.File;
import java.text.MessageFormat;
import java.util.ResourceBundle;

public class PluginUtil {

    private static final ResourceBundle bundle = ResourceBundle.getBundle("messages.MyBatisLogBundle");

    /**
     * 获取核心Jar路径
     *
     * @return String
     */
    public static String getAgentCoreJarPath() {
        return getJarPathByStartWith();
    }

    /**
     * 根据jar包的前缀名称获路径
     *
     * @return String
     */
    private static String getJarPathByStartWith() {
        PluginId pluginId = PluginId.getId("com.linkkou.plugin.intellij.assistant.mybaitslog");
        final File filePlugin = PluginManagerCore.getPlugin(pluginId).getPluginPath().toFile();
        final File[] fileslibs = filePlugin.listFiles();
        for (File listFile : fileslibs) {
            if ("lib".equals(listFile.getName())) {
                final File[] fileslib = listFile.listFiles();
                for (File file : fileslib) {
                    //优化非写死
                    if (file.getName().contains("mybatis-agent")) {
                        return file.toPath().toString();
                    }
                }
            }
        }
        return null;
    }

    public static void Notificat_AddConfiguration() {
        NotificationListener.Adapter notificationListener = new NotificationListener.Adapter() {
            @Override
            protected void hyperlinkActivated(@NotNull Notification notification, @NotNull HyperlinkEvent e) {
                // e.getDescription() 的值就是标签 a 中的 href 属性值
                //启动filter配置
                FilterSetting dialog = new FilterSetting();
                dialog.show();
            }
        };
        String content = bundle.getString("notification.content.unknown.actuator");
        NotificationGroupManager.getInstance()
                .getNotificationGroup("MyBatisLog.Notification")
                .createNotification(bundle.getString("notification.title"), content, NotificationType.WARNING)
                .setListener(notificationListener)
                .notify(null);
    }

    public static void Notificat_Success() {
        String content = bundle.getString("notification.content.success");
        NotificationGroupManager.getInstance()
                .getNotificationGroup("MyBatisLog.Notification")
                .createNotification(bundle.getString("notification.title"), content, NotificationType.INFORMATION)
                .notify(null);
    }

    public static void Notificat_Error(String error) {
        String content = MessageFormat.format(bundle.getString("notification.content.error"), error);
        NotificationGroupManager.getInstance()
                .getNotificationGroup("MyBatisLog.Notification")
                .createNotification(bundle.getString("notification.title"), content, NotificationType.ERROR)
                .notify(null);
    }

}
