package org.objectweb.asm.idea.service;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.keymap.KeymapManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;

import org.objectweb.asm.idea.ACodeView;

/**
 * @author Quding Ding
 * @since 2018/1/31
 */
public class CfrDecompile extends ACodeView {

  public CfrDecompile(ToolWindowManager toolWindowManager, KeymapManager keymapManager, Project project) {
    super(toolWindowManager, keymapManager, project);
  }

  public static CfrDecompile getInstance(Project project) {
    return ServiceManager.getService(project, CfrDecompile.class);
  }

}
