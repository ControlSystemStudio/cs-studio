/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.annunciator.ui;

import org.csstudio.alarm.beast.annunciator.Activator;
import org.csstudio.alarm.beast.annunciator.Messages;
import org.csstudio.alarm.beast.annunciator.Preferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

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
            new ScopedPreferenceStore(InstanceScope.INSTANCE, Activator.PLUGIN_ID);
        setPreferenceStore(store);
        setMessage(Messages.Annunciator);
    }

    @Override
    public void init(IWorkbench workbench)
    { /* NOP */ }


    @Override
    protected void createFieldEditors()
    {
        final Composite parent = getFieldEditorParent();

        addField(new StringFieldEditor(Preferences.URL, Messages.Prefs_URL, parent));
        addField(new StringFieldEditor(Preferences.TOPICS, Messages.Prefs_Topics, parent));
        addField(new StringFieldEditor(Preferences.TRANSLATIONS_FILE, Messages.Prefs_Translations, parent));
        addField(new StringFieldEditor(Preferences.SEVERITIES, Messages.Prefs_Severities, parent));
        IntegerFieldEditor number = new IntegerFieldEditor(Preferences.THRESHOLD, Messages.Prefs_Threshold, parent);
        number.setValidRange(0, 1000);
        addField(number);
        number = new IntegerFieldEditor(Preferences.MESSAGE_BUFFER, Messages.Prefs_History, parent);
        number.setValidRange(0, 1000);
        addField(number);
    }
}
