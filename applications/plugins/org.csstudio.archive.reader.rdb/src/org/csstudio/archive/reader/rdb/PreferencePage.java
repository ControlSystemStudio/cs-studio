/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.rdb;

import org.csstudio.auth.ui.security.PasswordFieldEditor;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/** Preference Page for the RDB Archive's user and password.
 *  @author Kay Kasemir
 */
public class PreferencePage extends FieldEditorPreferencePage
        implements IWorkbenchPreferencePage
{
    /** Initialize to use 'instance' scope (install location)
     *  and not workspace!
     */
    public PreferencePage()
    {
        super(GRID);
        setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE,
                Activator.ID));
    }

    @Override
    public void init(IWorkbench workbench)
    {
        // NOP
    }

    @Override
    protected void createFieldEditors()
    {
        setMessage(Messages.PreferenceTitle);
        final Composite parent = getFieldEditorParent();
        addField(new StringFieldEditor(Preferences.USER, Messages.User, parent));
        addField(new PasswordFieldEditor(Preferences.PASSWORD, Messages.Password, parent, Activator.ID));
        addField(new StringFieldEditor(Preferences.SCHEMA, Messages.Schema, parent));
        addField(new StringFieldEditor(Preferences.STORED_PROCEDURE, Messages.StoredProcedure, parent));
    }
}
