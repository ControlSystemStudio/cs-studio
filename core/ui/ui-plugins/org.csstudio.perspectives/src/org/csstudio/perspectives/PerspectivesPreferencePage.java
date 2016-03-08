/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.perspectives;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * The preference page for org.csstudio.perspectives.
 */
public class PerspectivesPreferencePage extends FieldEditorPreferencePage
        implements IWorkbenchPreferencePage {

    public static final String ID = "org.csstudio.perspectives.preferences";
    public static final String PERSPECTIVE_LOAD_DIRECTORY = "perspective_load_dir";
    public static final String PERSPECTIVE_SAVE_DIRECTORY = "perspective_save_dir";
    public static final String FILE_PREFIX = "file:";

    private IFileUtils fileUtils = new FileUtils();

    private ScopedPreferenceStore store;

    public PerspectivesPreferencePage() {
        super(FieldEditorPreferencePage.GRID);
        setPreferenceStore(new Plugin().getPreferenceStore());
        setMessage(Messages.PerspectivesPreferencePage_pageMessage);
    }

    @Override
    protected void createFieldEditors() {
        final Composite parent = getFieldEditorParent();
        String lastLoadPath = store.getString(PERSPECTIVE_LOAD_DIRECTORY);
        SelectDirectoryFieldEditor perspectiveLoadDirEditor = new
                SelectDirectoryFieldEditor(PERSPECTIVE_LOAD_DIRECTORY,
                        Messages.PerspectivesPreferencePage_loadText, parent, lastLoadPath, fileUtils);
        perspectiveLoadDirEditor.getTextControl(parent).setToolTipText(Messages.PerspectivesPreferencePage_loadTooltip);
        addField(perspectiveLoadDirEditor);
        String lastSavePath = store.getString(PERSPECTIVE_SAVE_DIRECTORY);
        SelectDirectoryFieldEditor perspectiveSaveDirEditor = new
                SelectDirectoryFieldEditor(PERSPECTIVE_SAVE_DIRECTORY,
                        Messages.PerspectivesPreferencePage_saveText, parent, lastSavePath, fileUtils);
        perspectiveSaveDirEditor.getTextControl(parent).setToolTipText(Messages.PerspectivesPreferencePage_saveTooltip);
        addField(perspectiveSaveDirEditor);
    }

    @Override
    public void init(IWorkbench workbench) {
        store = new ScopedPreferenceStore(InstanceScope.INSTANCE, ID);
        setPreferenceStore(store);
        setDescription(Messages.PerspectivesPreferencePage_pageDescription);
    }

    @Override
    public boolean performOk() {
        return super.performOk();
    }

}
