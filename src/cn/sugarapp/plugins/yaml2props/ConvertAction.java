package cn.sugarapp.plugins.yaml2props;

import java.io.IOException;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;

/**
 * @author zm9913@sina.cn
 */
public class ConvertAction extends AnAction {

    private static final String GROUP_DISPLAY_ID = "cn.sugarapp.plugins.yaml2props";

    @Override
    public void update(AnActionEvent anActionEvent) {
        PsiFile selectedFile = getSelectedPropertiesFile(anActionEvent, false);
        anActionEvent.getPresentation().setEnabledAndVisible(selectedFile != null);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        PsiFile selectedFile = getSelectedPropertiesFile(anActionEvent, true);
        if (selectedFile == null) {
            return;
        }

        VirtualFile propertiesFile = selectedFile.getVirtualFile();
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                String content=new String(propertiesFile.contentsToByteArray());
                System.out.println(content);
                String yamlContent = new YamlToProperties(content).convert();
                propertiesFile.rename(this, propertiesFile.getNameWithoutExtension() + ".properties");
                propertiesFile.setBinaryContent(yamlContent.getBytes());
            } catch (IOException e) {
                Notifications.Bus.notify(new Notification(GROUP_DISPLAY_ID, "Cannot rename file", e.getMessage(), NotificationType.ERROR));
            }
        });

        Notifications.Bus.notify(new Notification(GROUP_DISPLAY_ID, "File converted", "File converted successfully", NotificationType.INFORMATION));
    }

    @Nullable
    private PsiFile getSelectedPropertiesFile(AnActionEvent anActionEvent, boolean showNotifications) {
        PsiFile selectedFile = anActionEvent.getData(LangDataKeys.PSI_FILE);
        if (selectedFile == null) {
            if (showNotifications) {
                Notifications.Bus.notify(new Notification(GROUP_DISPLAY_ID, "No file selected", "Please select properties file first", NotificationType.ERROR));
            }
            return null;
        }
        LanguageFileType YAML = (LanguageFileType) FileTypeManager.getInstance().getStdFileType("YAML");
        if (!YAML.equals(selectedFile.getFileType())) {
            if (showNotifications) {
                Notifications.Bus.notify(new Notification(GROUP_DISPLAY_ID, "Incorrect file selected", "Please select properties file first", NotificationType.ERROR));
            }
            return null;
        }
        return selectedFile;
    }

}
