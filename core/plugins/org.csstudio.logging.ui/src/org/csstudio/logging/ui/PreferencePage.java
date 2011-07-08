/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logging.ui;

import java.util.logging.Level;

import org.csstudio.logging.LogConfigurator;
import org.csstudio.logging.LogFormatDetail;
import org.csstudio.logging.Preferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/** Preference page for Logging
 *  @author Kay Kasemir
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
            new ScopedPreferenceStore(new InstanceScope(),
                                      org.csstudio.logging.Activator.ID);
        setPreferenceStore(store);
        setMessage(Messages.PrefPageTitle);
    }

    /** {@inheritDoc */
    @Override
    public void init(IWorkbench workbench)
    { /* NOP */ }

    /** Creates the field editors.
     *  Each one knows how to save and restore itself.
     */
    @Override
    protected void createFieldEditors()
    {
        final Composite parent = getFieldEditorParent();

        addField(new LabelFieldEditor(Messages.GlobalSettings, false, parent));

        final String levels[][] = new String[][]
        {
            { Level.OFF.getLocalizedName(), Level.OFF.getName() },
            { Level.SEVERE.getLocalizedName(), Level.SEVERE.getName() },
            { Level.WARNING.getLocalizedName(), Level.WARNING.getName() },
            { Level.INFO.getLocalizedName(), Level.INFO.getName() },
            { Level.CONFIG.getLocalizedName(), Level.CONFIG.getName() },
            { Level.FINE.getLocalizedName(), Level.FINE.getName() },
            { Level.FINER.getLocalizedName(), Level.FINER.getName() },
            { Level.FINEST.getLocalizedName(), Level.FINEST.getName() },
            { Level.ALL.getLocalizedName(), Level.ALL.getName() },
        };
        addField(new ComboFieldEditor(Preferences.CONSOLE_LEVEL, Messages.GlobalLevel, levels, parent));

        int size = LogFormatDetail.values().length;
        final String detail[][] = new String[size][2];
        for (int i=0; i<size; ++i)
        {
            detail[i][0] = LogFormatDetail.values()[i].toString();
            detail[i][1] = LogFormatDetail.values()[i].name();
        }
        addField(new ComboFieldEditor(Preferences.DETAIL, Messages.MessageDetail, detail, parent));

        addField(new LabelFieldEditor(Messages.FileLogSettings, true, parent));
        addField(new ComboFieldEditor(Preferences.FILE_LEVEL, Messages.FileLevel, levels, parent));
        addField(new StringFieldEditor(Preferences.FILE_PATTERN, Messages.FilePathPattern, parent));
        addField(new IntegerFieldEditor(Preferences.FILE_BYTES, Messages.FileSize, parent));
        addField(new IntegerFieldEditor(Preferences.FILE_COUNT, Messages.FileCount, parent));

        addField(new LabelFieldEditor(Messages.JMSLogSettings, true, parent));
        addField(new ComboFieldEditor(Preferences.JMS_LEVEL, Messages.JMSLevel, levels, parent));
        addField(new StringFieldEditor(Preferences.JMS_URL, Messages.JMSURL, parent));
        addField(new StringFieldEditor(Preferences.JMS_TOPIC, Messages.JMSTopic, parent));
    }

    /** {@inheritDoc} */
    @Override
    public boolean performOk()
    {
        return super.performOk()  &&  performLogConfigUpdate();
    }

    /** {@inheritDoc} */
    @Override
    protected void performApply()
    {
        super.performApply();
        performLogConfigUpdate();
    }

    /** Attempt to apply configuration changes
     *  @return <code>true</code> if successful
     */
    private boolean performLogConfigUpdate()
    {
        try
        {
            LogConfigurator.configureFromPreferences();
        }
        catch (Throwable ex)
        {
            setErrorMessage(NLS.bind(Messages.LogConfigError, ex.getMessage()));
            return false;
        }
        return true;
    }
}
