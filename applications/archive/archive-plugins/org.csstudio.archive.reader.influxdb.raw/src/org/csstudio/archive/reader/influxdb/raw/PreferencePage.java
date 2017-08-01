/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.influxdb.raw;

import java.io.IOException;

import org.csstudio.archive.influxdb.InfluxDBArchivePreferences;
import org.csstudio.security.ui.PasswordFieldEditor;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * Preference Page for influxdb Archive settings
 *
 * <p>
 * This combines all settings that an influxdb client uses: Basic Archive
 * influxdb settings from org.csstudio.archive.influxdb plus specifics of
 * org.csstudio.archive.reader.influxdb.raw
 *
 * @author Kay Kasemir
 */
public class PreferencePage extends FieldEditorPreferencePage
implements IWorkbenchPreferencePage
{
    private ScopedPreferenceStore reader_prefs;

    /** Initialize to use 'instance' scope (install location)
     *  and not workspace!
     */
    public PreferencePage()
    {
        super(GRID);

        final IScopeContext scope = InstanceScope.INSTANCE;
        // 'main' pref. store for most of the settings
        setPreferenceStore(new ScopedPreferenceStore(scope,
                org.csstudio.archive.influxdb.Activator.ID));

        // Separate store for archive.reader.influxdb
        reader_prefs = new ScopedPreferenceStore(scope,
                org.csstudio.archive.reader.influxdb.raw.Activator.ID);
    }

    /** {@inheritDoc} */
    @Override
    public void init(IWorkbench workbench)
    {
        // NOP
    }

    /** {@inheritDoc} */
    @Override
    protected void createFieldEditors()
    {
        setMessage(Messages.PreferenceTitle);
        final Composite parent = getFieldEditorParent();

        addField(new StringFieldEditor(InfluxDBArchivePreferences.URL, Messages.URL, parent));
        addField(new StringFieldEditor(InfluxDBArchivePreferences.USER, Messages.User, parent));
        addField(new PasswordFieldEditor(org.csstudio.archive.influxdb.Activator.ID,
                InfluxDBArchivePreferences.PASSWORD, Messages.Password, parent));

        addField(new StringFieldEditor(InfluxDBArchivePreferences.DB_PREFIX, Messages.DBPrefix, parent));
        addField(new StringFieldEditor(InfluxDBArchivePreferences.DFLT_META_DB, Messages.DBData, parent));
        addField(new StringFieldEditor(InfluxDBArchivePreferences.DFLT_DB, Messages.DBMeta, parent));

        // FieldEditorPreferencePage will set all its
        // editors to the 'main' pref. store.
        // Hack around that by replacing setPreferenceStore
        final IntegerFieldEditor fetch_size = new IntegerFieldEditor(Preferences.CHUNK_SIZE, Messages.FetchSize, parent)
        {
            @Override
            public void setPreferenceStore(final IPreferenceStore ignored)
            {
                super.setPreferenceStore(reader_prefs);
            }
        };
        addField(fetch_size);
        // final StringFieldEditor editor =
        // new StringFieldEditor(Preferences.STORED_PROCEDURE,
        // Messages.StoredProcedure, parent)
        // {
        // @Override
        // public void setPreferenceStore(final IPreferenceStore ignored)
        // {
        // super.setPreferenceStore(reader_prefs);
        // }
        // };
        // addField(editor);

        // addField(new
        // BooleanFieldEditor(InfluxDBArchivePreferences.USE_ARRAY_BLOB,
        // Messages.UseBLOB, parent));
    }

    /** FieldEditorPreferencePage will save settings
     *  of its 'main' store.
     *  Override to also save <code>reader_prefs</code>
     */
    @SuppressWarnings("nls")
    @Override
    public boolean performOk()
    {
        if (! super.performOk())
            return false;

        if (reader_prefs.needsSaving())
        {
            try
            {
                reader_prefs.save();
            }
            catch (IOException ex)
            {
                ExceptionDetailsErrorDialog.openError(getShell(), "Error",
                        "Cannot save settings for InfluxDB Reader", ex);
            }
        }
        return true;
    }
}
