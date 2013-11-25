/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist;

import org.csstudio.security.ui.PasswordFieldEditor;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/** Preference page.
 *  Connected to GUI in plugin.xml
 *  @author Kay Kasemir
 *  @author Xihui Chen
 */
public class PreferencePage extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage
{
    /** Constructor */
	public PreferencePage()
	{
        // This way, preference changes in the GUI end up in a file under
        // {workspace}/.metadata/.plugins/org.eclipse.core.runtime/.settings/,
        // i.e. they are specific to the workspace instance.
        final IPreferenceStore store =
            new ScopedPreferenceStore(InstanceScope.INSTANCE, Activator.ID);
        setPreferenceStore(store);
        setMessage(Messages.MessageHistory);
	}

    /** {@inheritDoc */
    @Override
    public void init(IWorkbench workbench)
    { /* NOP */ }

    /** {@inheritDoc */
    @Override
    protected void createFieldEditors()
    {
        final Composite parent = getFieldEditorParent();
        addField(new StringFieldEditor(Preferences.RDB_URL, Messages.Pref_URL, parent));
        addField(new StringFieldEditor(Preferences.RDB_USER, Messages.Pref_User, parent));
        addField(new PasswordFieldEditor(Activator.ID, Preferences.RDB_PASSWORD, Messages.Pref_Password, parent));
        addField(new StringFieldEditor(Preferences.RDB_SCHEMA, Messages.Pref_Schema, parent));
        addField(new StringFieldEditor(Preferences.START, Messages.Pref_Starttime, parent));
        addField(new StringFieldEditor(Preferences.AUTO_REFRESH_PERIOD, Messages.Pref_AutoRefreshPeriod, parent));
        final IntegerFieldEditor max_properties =
        	new IntegerFieldEditor(Preferences.MAX_PROPERTIES, Messages.Pref_MaxProperties, parent);
        max_properties.setValidRange(0, Integer.MAX_VALUE);
        addField(max_properties);
        addField(new TableColumnsFieldEditor(parent));
    }
}
