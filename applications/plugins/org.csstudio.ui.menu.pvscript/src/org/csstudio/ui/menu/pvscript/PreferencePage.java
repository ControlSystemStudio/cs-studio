/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.ui.menu.pvscript;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
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
        setPreferenceStore(new ScopedPreferenceStore(new InstanceScope(), Activator.ID));
		setDescription("Configure Process Variable context menu scripts");
	}
	
	/** {@inheritDoc} */
	public void init(final IWorkbench workbench)
	{
		// NOP
	}
	
	/** {@inheritDoc} */
	public void createFieldEditors()
	{
		// Editing a plain string preference would be easy:
		//		addField(new StringFieldEditor(Preferences.PREF_SCRIPTS,
		//					"A &text preference:", getFieldEditorParent()));
		addField(new ScriptInfoFieldEditor(getFieldEditorParent()));
	}
}