/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.debugging.jmsmonitor;

import org.csstudio.auth.ui.security.PasswordFieldEditor;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/** Preference GUI for JMS Monitor.
 *  <p>
 *  plugin.xml registers this with the Eclipse preference GUI
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
        setMessage(Messages.JMSMonitorPrefs);
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
        addField(new StringFieldEditor(Preferences.JMS_URL, Messages.Preferences_JMS_URL, parent));
        addField(new PasswordFieldEditor(Preferences.JMS_USER, Messages.Preferences_JMS_USER, parent, Activator.ID, false));
        addField(new PasswordFieldEditor(Preferences.JMS_PASSWORD, Messages.Preferences_JMS_PASSWORD, parent, Activator.ID));
        final IntegerFieldEditor max_messages = new IntegerFieldEditor(Preferences.MAX_MESSAGES, Messages.Preferences_MAX_MESSAGES, parent);
		max_messages.setValidRange(1, Integer.MAX_VALUE);
        addField(max_messages);
    }
}
