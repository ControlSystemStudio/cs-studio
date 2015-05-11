/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.sysmon;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/** Sysmon prefs.
 *  @author Kay Kasemir
 */
public class PreferencePage extends FieldEditorPreferencePage
                implements IWorkbenchPreferencePage
{
    // Min..Max for the pref values.
    final private static int MIN_HOURS = 1;
    final private static int MAX_HOURS = 24;
    final private static int MIN_DELAY = 1;
    final private static int MAX_DELAY = 120;

    /** Preference ID (also used in preferences.ini) */
    final private static String P_HISTORY_HOURS = "history_hours"; //$NON-NLS-1$

    /** Preference ID (also used in preferences.ini) */
    final private static String P_SCAN_DELAY_SECS = "scan_delay_secs"; //$NON-NLS-1$

    public PreferencePage()
    {
        super(GRID);
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
        setDescription(Messages.PreferencePage_Title);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    @Override
    public void init(IWorkbench workbench)
    { /* NOP */ }

    /** Creates the field editors.
     *  Each one knows how to save and restore itself.
     */
    @Override
    public void createFieldEditors()
    {
        final Composite parent = getFieldEditorParent();

        final IntegerFieldEditor size_editor =
            new IntegerFieldEditor(P_HISTORY_HOURS, Messages.PreferencePage_HistSize, parent);
        size_editor.setValidRange(MIN_HOURS, MAX_HOURS);
        size_editor.setErrorMessage(NLS.bind(Messages.PreferencePage_ValidHistSize,
                MIN_HOURS, MAX_HOURS));
        addField(size_editor);

        final IntegerFieldEditor delay_editor =
            new IntegerFieldEditor(P_SCAN_DELAY_SECS, Messages.PreferencePage_ScanDelay, parent);
        delay_editor.setValidRange(MIN_DELAY, MAX_DELAY);
        delay_editor.setErrorMessage(NLS.bind(Messages.PreferencePage_ValidScanDelay,
                                              MIN_DELAY, MAX_DELAY));
        addField(delay_editor);
    }

    /** @return History size [number of samples]. */
    static public int getHistorySize()
    {
        final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        int hours = store.getInt(P_HISTORY_HOURS);
        if (hours < MIN_HOURS)
            hours = MIN_HOURS;
        if (hours > MAX_HOURS)
            hours = MAX_HOURS;
        // Convert history size in hours to size in # of samples
        final double scan_period_hours =
            getScanDelaySecs() / 60.0 / 60.0;
        return (int) (hours / scan_period_hours + 0.5);
    }

    /** @return Scan delay [secs]. */
    static public int getScanDelaySecs()
    {
        final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        int secs = store.getInt(P_SCAN_DELAY_SECS);
        if (secs < MIN_DELAY)
            secs = MIN_DELAY;
        if (secs > MAX_DELAY)
            secs = MAX_DELAY;
        return secs;
    }

    /** {@inheritDoc} */
    @Override
    public final void propertyChange(final PropertyChangeEvent event)
    {
        setMessage(Messages.PreferencePage_Restart,
                   IMessageProvider.INFORMATION);
        super.propertyChange(event);
    }
}
