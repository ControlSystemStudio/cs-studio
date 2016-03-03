/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.perspectives;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * The preference page for org.csstudio.perspectives.
 *
 */
public class PerspectivesPreferencePage extends FieldEditorPreferencePage
        implements IWorkbenchPreferencePage {

    public static final String PERSPECTIVE_LOAD_DIRECTORY = "perspective_load_dir";
    public static final String MESSAGE = "Perspectives preferences";
    public static final String LOAD_DIR_TITLE = "Perspective directory";
    public static final String PAGE_TITLE = "Perspectives preference page";
    public static final String LOAD_DIR_TOOLTIP = "Directory from which to load perspectives at startup.";
    public static final String ID = "org.csstudio.perspectives.preferences";
    public static final String FILE_PREFIX = "file:";

    private IFileUtils fileUtils = new FileUtils();

    private StringButtonFieldEditor perspectivesDirEditor;
    private ScopedPreferenceStore store;

    public PerspectivesPreferencePage() {
        super(FieldEditorPreferencePage.GRID);
        setPreferenceStore(new Plugin().getPreferenceStore());
        setMessage(MESSAGE);
    }

    @Override
    protected void createFieldEditors() {
        final Composite parent = getFieldEditorParent();
        perspectivesDirEditor = new StringButtonFieldEditor(PERSPECTIVE_LOAD_DIRECTORY, LOAD_DIR_TITLE, parent) {
            private String lastPath = store.getString(PERSPECTIVE_LOAD_DIRECTORY);

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
                String dirUri = fileUtils.stringPathToUriFileString(dir);
                if (dirUri != null) {
                    dirUri = dirUri.trim();
                    if (dirUri.length() == 0) {
                        return null;
                    }
                    lastPath = dirUri;
                }
                return dir;
            }
        };
        perspectivesDirEditor.getTextControl(parent).setToolTipText(LOAD_DIR_TOOLTIP);
        addField(perspectivesDirEditor);
    }

    @Override
    public void init(IWorkbench workbench) {
        store = new ScopedPreferenceStore(InstanceScope.INSTANCE, ID);
        setPreferenceStore(store);
        setDescription(PAGE_TITLE);
    }

    @Override
    public boolean performOk() {
        return super.performOk();
    }

}
