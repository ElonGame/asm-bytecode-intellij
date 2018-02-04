package org.objectweb.asm.idea.service;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.keymap.KeymapManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindowManager;

import org.objectweb.asm.idea.ACodeView;
import org.objectweb.asm.idea.util.CfrPluginRunner;
import org.objectweb.asm.idea.util.Settings;

import java.io.StringWriter;

/**
 * @author Quding Ding
 * @since 2018/1/31
 */
public class CfrDecompile extends ACodeView {

  private static Settings settings;

  static {
    settings = Settings.getInstance();
  }

  public CfrDecompile(ToolWindowManager toolWindowManager, KeymapManager keymapManager, Project project) {
    super(toolWindowManager, keymapManager, project);
  }

  public static CfrDecompile getInstance(Project project) {
    return ServiceManager.getService(project, CfrDecompile.class);
  }

  public void deCompileAndSetCode(Project project, VirtualFile file, StringWriter stringWriter) {
    // 第四个解析,内部实现有缓存,所以直接new一个
    stringWriter.getBuffer().setLength(0);
    CfrPluginRunner.compile((file.getPath() + " " + settings.getCfrParams()).split(" "), stringWriter);
    this.setCode(file, stringWriter.toString());
  }

}
