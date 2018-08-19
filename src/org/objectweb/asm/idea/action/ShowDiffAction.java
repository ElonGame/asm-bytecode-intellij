package org.objectweb.asm.idea.action;

import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.contents.DocumentContentImpl;
import com.intellij.diff.contents.EmptyContent;
import com.intellij.diff.requests.ContentDiffRequest;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Quding Ding
 * @since 2018/1/31
 */
public class ShowDiffAction extends AnAction {

    private Project project;

    private String previousCode;
    private String currentCode;

    private VirtualFile previousFile;
    private Document currentDocument;

    private static final String DIFF_WINDOW_TITLE = "Show differences from previous class contents";
    private static final List<String> DIFF_TITLES = new ArrayList<>(2);

    static {
        DIFF_TITLES.add("Current version");
        DIFF_TITLES.add("Previous version");
    }

    public ShowDiffAction(Project project,Document currentDocument) {
        super("Show differences",
            "Shows differences from the previous version of bytecode for this file",
            IconLoader.getIcon("/actions/diffWithCurrent.png"));
        this.project = project;
        this.currentDocument = currentDocument;
    }

    @Override
    public void update(final AnActionEvent e) {
        e.getPresentation().setEnabled(!"".equals(currentDocument.getText()) && (previousFile != null));
    }

    @Override
    public boolean displayTextInToolbar() {
        return true;
    }

    @Override
    public void actionPerformed(final AnActionEvent e) {
        com.intellij.diff.DiffManager.getInstance().showDiff(project, new ContentDiffRequest() {
            @Override
            public String getTitle() {
                return DIFF_WINDOW_TITLE;
            }

            @NotNull
            @Override
            public List<DiffContent> getContents() {
                DiffContent currentContent = new DocumentContentImpl(currentDocument);
                DiffContent oldContent = previousCode == null ? new EmptyContent() : new DocumentContentImpl(new DocumentImpl(previousCode));
                List<DiffContent> contents = new ArrayList<>(2);
                contents.add(currentContent);
                contents.add(oldContent);
                return contents;
            }

            @NotNull
            @Override
            public List<String> getContentTitles() {
                return DIFF_TITLES;
            }

        });
    }

    /**
     * 判断两者是否是相同的文件
     */
    public boolean isSameFile(VirtualFile file) {
        if (null == file || null == this.previousFile) {
            return false;
        }
        return this.previousFile.getPath().equals(file.getPath());
    }

    /**
     * 接收新的文件
     */
    public void acceptNewFile(VirtualFile file,String code) {
        if (!isSameFile(file)) {
            this.previousCode = "";
        } else {
            this.previousCode = this.currentCode;
        }
        if (file != null) {
            this.previousFile = file;
            this.currentCode = code;
        }
    }
}
