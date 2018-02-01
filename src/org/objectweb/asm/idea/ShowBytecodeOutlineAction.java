/*
 *
 *  Copyright 2011 Cédric Champeau
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * /
 */

package org.objectweb.asm.idea;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.impl.text.PsiAwareTextEditorImpl;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassOwner;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.codeStyle.CodeStyleManager;

import org.objectweb.asm.idea.cfr.CfrPluginRunner;
import org.objectweb.asm.idea.constant.Constants;
import org.objectweb.asm.idea.service.BytecodeASMified;
import org.objectweb.asm.idea.service.BytecodeOutline;
import org.objectweb.asm.idea.service.CfrDecompile;
import org.objectweb.asm.idea.service.GroovifiedView;
import org.objectweb.asm.idea.ui.GroovyCodeStyle;
import org.objectweb.asm.idea.util.Settings;

import reloc.org.objectweb.asm.ClassReader;
import reloc.org.objectweb.asm.ClassVisitor;
import reloc.org.objectweb.asm.util.ASMifier;
import reloc.org.objectweb.asm.util.TraceClassVisitor;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.Semaphore;


/**
 * Given a java file (or any file which generates classes), tries to locate a .class file. If the compilation state is
 * not up to date, performs an automatic compilation of the class. If the .class file can be located, generates bytecode
 * instructions for the class and ASMified code, and displays them into a tool window.
 *
 * @author Cédric Champeau
 */
public class ShowBytecodeOutlineAction extends AnAction {

  private static Settings settings;

  static {
    settings = Settings.getInstance();
  }

  @Override
  public void update(final AnActionEvent e) {
    final VirtualFile virtualFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
    final Project project = e.getData(PlatformDataKeys.PROJECT);
    final Presentation presentation = e.getPresentation();
    if (project == null || virtualFile == null) {
      presentation.setEnabled(false);
      return;
    }
    final PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
    presentation.setEnabled(psiFile instanceof PsiClassOwner);
  }

  public void actionPerformed(AnActionEvent e) {
    final VirtualFile virtualFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
    final Project project = e.getData(PlatformDataKeys.PROJECT);
    if (project == null || virtualFile == null) return;
    final PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
    if (psiFile instanceof PsiClassOwner) {
      // 定位到所在的模块
      final Module module = ModuleUtil.findModuleForPsiElement(psiFile);
      // 得到编译所在目录信息
      final CompilerModuleExtension cme = CompilerModuleExtension.getInstance(module);
      final CompilerManager compilerManager = CompilerManager.getInstance(project);

      final VirtualFile[] files = {virtualFile};
      // 处理class文件
      if ("class".equals(virtualFile.getExtension())) {
        updateToolWindowContents(project, virtualFile);
        // 本地文件
      } else if (!virtualFile.isInLocalFileSystem() && !virtualFile.isWritable()) {
        // probably a source file in a library
        final PsiClass[] psiClasses = ((PsiClassOwner) psiFile).getClasses();
        if (psiClasses.length > 0) {
          updateToolWindowContents(project, psiClasses[0].getOriginalElement().getContainingFile().getVirtualFile());
        }
        //默认处理
      } else {
        final Application application = ApplicationManager.getApplication();
        application.runWriteAction(() -> FileDocumentManager.getInstance().saveAllDocuments());
        application.executeOnPooledThread(() -> {
          final CompileScope compileScope = compilerManager.createFilesCompileScope(files);
          final VirtualFile[] result = {null};
          final VirtualFile[] outputDirectories = cme == null ? null : cme.getOutputRoots(true);
          final Semaphore semaphore = new Semaphore(1);
          try {
            semaphore.acquire();
          } catch (InterruptedException e1) {
            result[0] = null;
          }
          if (outputDirectories != null && compilerManager.isUpToDate(compileScope)) {
            application.invokeLater(() -> {
              result[0] = findClassFile(outputDirectories, psiFile);
              semaphore.release();
            });
          } else {
            application.invokeLater(() -> compilerManager.compile(files,
                (aborted, errors, warnings, compileContext) -> {
                  if (errors == 0) {
                    VirtualFile[] outputDirectories1 = cme.getOutputRoots(true);
                    if (outputDirectories1 != null) {
                      result[0] = findClassFile(outputDirectories1, psiFile);
                    }
                  }
                  semaphore.release();
                }));
            try {
              semaphore.acquire();
            } catch (InterruptedException e1) {
              result[0] = null;
            }
          }
          application.invokeLater(() -> updateToolWindowContents(project, result[0]));
        });
      }
    }
  }

  private VirtualFile findClassFile(final VirtualFile[] outputDirectories, final PsiFile psiFile) {
    return ApplicationManager.getApplication().runReadAction(new Computable<VirtualFile>() {
      public VirtualFile compute() {
        if (outputDirectories != null && psiFile instanceof PsiClassOwner) {
          FileEditor editor = FileEditorManager.getInstance(psiFile.getProject()).getSelectedEditor(psiFile.getVirtualFile());
          int caretOffset = editor == null ? -1 : ((PsiAwareTextEditorImpl) editor).getEditor().getCaretModel().getOffset();
          if (caretOffset >= 0) {
            PsiClass psiClass = findClassAtCaret(psiFile, caretOffset);
            if (psiClass != null) {
              return getClassFile(psiClass);
            }
          }
          PsiClassOwner psiJavaFile = (PsiClassOwner) psiFile;
          for (PsiClass psiClass : psiJavaFile.getClasses()) {
            final VirtualFile file = getClassFile(psiClass);
            if (file != null) {
              return file;
            }
          }
        }
        return null;
      }

      private VirtualFile getClassFile(PsiClass psiClass) {
        StringBuilder sb = new StringBuilder(psiClass.getQualifiedName());
        while (psiClass.getContainingClass() != null) {
          sb.setCharAt(sb.lastIndexOf("."), '$');
          psiClass = psiClass.getContainingClass();
        }
        String classFileName = sb.toString().replace('.', '/') + ".class";
        for (VirtualFile outputDirectory : outputDirectories) {
          final VirtualFile file = outputDirectory.findFileByRelativePath(classFileName);
          if (file != null && file.exists()) {
            return file;
          }
        }
        return null;
      }

      private PsiClass findClassAtCaret(PsiFile psiFile, int caretOffset) {
        PsiElement elem = psiFile.findElementAt(caretOffset);
        while (elem != null) {
          if (elem instanceof PsiClass) {
            return (PsiClass) elem;
          }
          elem = elem.getParent();
        }
        return null;
      }
    });
  }


  /**
   * Reads the .class file, processes it through the ASM TraceVisitor and ASMifier to update the contents of the two
   * tabs of the tool window.
   *
   * @param project the project instance
   * @param file    the class file
   */
  private void updateToolWindowContents(final Project project, final VirtualFile file) {
    ApplicationManager.getApplication().runWriteAction(() -> {
      if (file == null) {
        BytecodeOutline.getInstance(project).setCode(null, Constants.NO_CLASS_FOUND);
        BytecodeASMified.getInstance(project).setCode(null, Constants.NO_CLASS_FOUND);
        GroovifiedView.getInstance(project).setCode(null, Constants.NO_CLASS_FOUND);
        CfrDecompile.getInstance(project).setCode(null, Constants.NO_CLASS_FOUND);
        ToolWindowManager.getInstance(project).getToolWindow("ASM").activate(null);
        return;
      }
      StringWriter stringWriter = new StringWriter();
      ClassVisitor visitor = new TraceClassVisitor(new PrintWriter(stringWriter));
      ClassReader reader = null;
      try {
        file.refresh(false, false);
        reader = new ClassReader(file.contentsToByteArray());
      } catch (IOException e) {
        return;
      }
      int flags = 0;
      if (settings.isSkipDebug()) flags = flags | ClassReader.SKIP_DEBUG;
      if (settings.isSkipFrames()) flags = flags | ClassReader.SKIP_FRAMES;
      if (settings.isExpandFrames()) flags = flags | ClassReader.EXPAND_FRAMES;
      if (settings.isSkipCode()) flags = flags | ClassReader.SKIP_CODE;

      reader.accept(visitor, flags);
      //第一个解析
      BytecodeOutline.getInstance(project).setCode(file, stringWriter.toString());
      stringWriter.getBuffer().setLength(0);
      // 第二个解析
      reader.accept(new TraceClassVisitor(null, new GroovifiedTextifier(GroovyCodeStyle.valueOf(settings.getCodeStyle())),
          new PrintWriter(stringWriter)), ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
      GroovifiedView.getInstance(project).setCode(file, stringWriter.toString());
      //第三个解析
      stringWriter.getBuffer().setLength(0);
      reader.accept(new TraceClassVisitor(null,
          new ASMifier(), new PrintWriter(stringWriter)), flags);
      PsiFile psiFile = PsiFileFactory.getInstance(project).createFileFromText("asm.java", stringWriter.toString());
      CodeStyleManager.getInstance(project).reformat(psiFile);
      BytecodeASMified.getInstance(project).setCode(file, psiFile.getText());

      // 第四个解析,内部实现有缓存,所以直接new一个
      final String decompilation = CfrPluginRunner.compile((file.getPath() + " " + settings.getCfrParams()).split(" "));
      CfrDecompile.getInstance(project).setCode(file, decompilation);
      // 激活窗口
      ToolWindowManager.getInstance(project).getToolWindow("ASM").activate(null);
    });
  }
}
