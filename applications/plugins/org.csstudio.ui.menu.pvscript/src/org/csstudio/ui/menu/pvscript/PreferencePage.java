/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.ui.menu.pvscript;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/** Preference Page, registered in plugin.xml
 *  @author Kay Kasemir
 */
public class PreferencePage	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage
{
	/** Initialize */
	public PreferencePage()
	{
        super(FieldEditorPreferencePage.GRID);
        setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, Activator.ID));
		setDescription(Messages.PreferencePageMessage);
	}

	/** {@inheritDoc} */
	@Override
    public void init(final IWorkbench workbench)
	{
		// NOP
	}

	/** {@inheritDoc} */
	@Override
    public void createFieldEditors()
	{
		final Composite parent = getFieldEditorParent();

		// Editing a plain string preference is easy:
		addField(new BooleanFieldEditor(Preferences.PREV_INDIVIDUAL,
				Messages.PrefEdit_IndividualScripts, parent));

		// A preference string that's really an encoded list of
		// complex objects needs a custom editor
		addField(new ScriptInfoFieldEditor(parent));
	}
}