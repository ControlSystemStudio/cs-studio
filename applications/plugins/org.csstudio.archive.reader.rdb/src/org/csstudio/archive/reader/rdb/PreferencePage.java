/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.rdb;

import org.csstudio.archive.rdb.RDBArchivePreferences;
import org.csstudio.auth.ui.security.PasswordFieldEditor;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
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
    // Most of the preferences are in the o.c.archive.rdb plugin,
	// so that is used as the overall preference store for this
	// preference GUI page.
    // The stored procedure, however, is a preference setting
	// of the rdb.reader plugin, so the preference store of that
	// one field editor is adjusted to use the 'reader' store.
	private ScopedPreferenceStore rdb_prefs;
    private ScopedPreferenceStore reader_prefs;

	/** Initialize to use 'instance' scope (install location)
     *  and not workspace!
     */
    public PreferencePage()
    {
        super(GRID);
        // TODO For Eclipse 3.7, use InstanceScope.INSTANCE
        // ITER with Eclipse 3.6 needs new InstanceScope()
        @SuppressWarnings("deprecation")
        final IScopeContext scope = new InstanceScope();
		rdb_prefs = new ScopedPreferenceStore(scope,
        		org.csstudio.archive.rdb.Activator.ID);
        reader_prefs = new ScopedPreferenceStore(scope,
        		org.csstudio.archive.reader.rdb.Activator.ID);
		setPreferenceStore(rdb_prefs);
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
        
        addField(new StringFieldEditor(RDBArchivePreferences.USER, Messages.User, parent));
        addField(new PasswordFieldEditor(RDBArchivePreferences.PASSWORD, Messages.Password, parent, Activator.ID));
        addField(new StringFieldEditor(RDBArchivePreferences.SCHEMA, Messages.Schema, parent));
        addField(new StringFieldEditor(Preferences.STORED_PROCEDURE, Messages.StoredProcedure, parent)
        {
			@Override
            public IPreferenceStore getPreferenceStore()
            {
	            return reader_prefs;
            }
        });
        addField(new BooleanFieldEditor(RDBArchivePreferences.USE_ARRAY_BLOB, Messages.UseBLOB, parent));
    }
}
