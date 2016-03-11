package org.csstudio.perspectives;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;

public class SelectDirectoryFieldEditor extends StringButtonFieldEditor {

    private String lastPath;
    private IFileUtils fileUtils;

    public SelectDirectoryFieldEditor(String name, String labelText, Composite parent, String lastPath, IFileUtils fileUtils) {
        super(name, labelText, parent);
        this.lastPath = lastPath;
        this.fileUtils = fileUtils;
        setErrorMessage(Messages.PerspectivesPreferencePage_dirNotSelected);
    }

    @Override
    protected String changePressed() {
        DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.SHEET);
        if (lastPath != null) {
            try {
                if (new File(lastPath).exists()) {
                    File lastDir = new File(lastPath);
                    dialog.setFilterPath(lastDir.getCanonicalPath());
                }
            } catch (IOException e) {
                dialog.setFilterPath(lastPath);
            }
        }
        String dir = dialog.open();
        if (dir != null) {
            String dirUri = fileUtils.stringPathToUriFileString(dir);
            dirUri = dirUri.trim();
            if (dirUri.length() == 0) {
                return null;
            }
            lastPath = dirUri;
        }
        return dir;
    }

    @Override
    public boolean doCheckState() {
        try {
            return fileUtils.isDirectory(getTextControl().getText());
        } catch (IOException e) {
            return false;
        }
    }

}
