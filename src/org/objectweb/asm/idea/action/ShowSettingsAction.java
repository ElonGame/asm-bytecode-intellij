package org.objectweb.asm.idea.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;

import org.objectweb.asm.idea.ui.ASMPluginComponent;

/**
 * @author Quding Ding
 * @since 2018/1/31
 */
public class ShowSettingsAction extends AnAction {

    private Project project;

    public ShowSettingsAction(Project project) {
        super("Settings", "Show settings for ASM plugin", IconLoader.getIcon("/general/projectSettings.png"));
        this.project = project;
    }

    @Override
    public boolean displayTextInToolbar() {
        return true;
    }

    @Override
    public void actionPerformed(final AnActionEvent e) {
        ShowSettingsUtil.getInstance().showSettingsDialog(this.project, this.project.getComponent(ASMPluginComponent.class));
    }
}
