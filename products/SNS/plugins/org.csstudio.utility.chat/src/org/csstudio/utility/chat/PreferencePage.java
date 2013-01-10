/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.chat;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/** Preference page
 *  @author Kay Kasemir
 */
public class PreferencePage extends FieldEditorPreferencePage
implements IWorkbenchPreferencePage
{
    /** Initialize */
	public PreferencePage()
	{
        super(FieldEditorPreferencePage.GRID);
        setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, Activator.ID));
        setMessage(Messages.PreferencesTitle);
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
        final Composite parent = getFieldEditorParent();

    	addField(new StringFieldEditor(Preferences.CHAT_SERVER, Messages.PreferencesServer, parent));
    	addField(new StringFieldEditor(Preferences.GROUP, Messages.PreferencesGroup, parent));
    }
}
