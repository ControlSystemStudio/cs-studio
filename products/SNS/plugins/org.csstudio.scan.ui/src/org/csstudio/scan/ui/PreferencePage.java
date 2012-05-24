/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui;

import org.csstudio.scan.Activator;
import org.csstudio.scan.ScanSystemPreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/** Preference page for scan system settings
 *
 *  <p>Settings are defined in plugin org.csstudio.scan
 *  and some of them need to be copied to system properties.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PreferencePage extends FieldEditorPreferencePage
        implements IWorkbenchPreferencePage
{
    /** Initialize */
    public PreferencePage()
    {
        super(FieldEditorPreferencePage.GRID);
        setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, Activator.ID));
        setMessage(Messages.ScanPrefsMessage);
    }

    /** {@inheritDoc} */
    @Override
    public void init(final IWorkbench workbench)
    {
        // NOP

    }

    /** {@inheritDoc} */
    @Override
    protected void createFieldEditors()
    {
        final Composite parent = getFieldEditorParent();

        addField(new StringFieldEditor("server_host", Messages.ServerHost, parent));
        addField(new IntegerFieldEditor("server_port", Messages.ServerPort, parent));
    }

    /** {@inheritDoc} */
    @Override
    public boolean performOk()
    {
        ScanSystemPreferences.setSystemPropertiesFromPreferences();
        return super.performOk();
    }
}
