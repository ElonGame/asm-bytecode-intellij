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

package org.objectweb.asm.idea.service;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.keymap.KeymapManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.codeStyle.CodeStyleManager;

import org.objectweb.asm.idea.ACodeView;

import reloc.org.objectweb.asm.ClassReader;
import reloc.org.objectweb.asm.util.ASMifier;
import reloc.org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * Created by IntelliJ IDEA.
 * User: cedric
 * Date: 07/01/11
 * Time: 17:07
 */

/**
 * ASMified code view.
 */
public class BytecodeASMified extends ACodeView {

  public BytecodeASMified(final ToolWindowManager toolWindowManager, KeymapManager keymapManager, final Project project) {
    super(toolWindowManager, keymapManager, project);
  }

  public static BytecodeASMified getInstance(Project project) {
    return ServiceManager.getService(project, BytecodeASMified.class);
  }

  public void deCompileAndSetCode(Project project, VirtualFile file, StringWriter stringWriter, ClassReader reader, int flags) {
    //第三个解析
    stringWriter.getBuffer().setLength(0);
    try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
      reader.accept(new TraceClassVisitor(null, new ASMifier(), printWriter), flags);
      PsiFile psiFile = PsiFileFactory.getInstance(project).createFileFromText("asm.java", stringWriter.toString());
      CodeStyleManager.getInstance(project).reformat(psiFile);
      this.setCode(file, psiFile.getText());
    } catch (Exception e) {
      this.setCode(file, "decompile fail" + e.getMessage());
    }
  }
}
