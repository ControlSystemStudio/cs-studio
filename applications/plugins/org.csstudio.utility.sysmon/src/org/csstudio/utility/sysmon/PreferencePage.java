package org.csstudio.utility.sysmon;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
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
    final private static int MIN_SIZE = 50;
    final private static int MAX_SIZE = 500;
    final private static int MIN_DELAY = 1000;
    final private static int MAX_DELAY = 10000;
    
    /** Preference ID (also used in preferences.ini) */
    final private static String P_HISTORY_SIZE = "history_size"; //$NON-NLS-1$
    
    /** Preference ID (also used in preferences.ini) */
    final private static String P_SCAN_DELAY_MILLI = "scan_delay_milli"; //$NON-NLS-1$

    public PreferencePage()
    {
        super(GRID);
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
        setDescription(Messages.PreferencePage_Title);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
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
            new IntegerFieldEditor(P_HISTORY_SIZE, Messages.PreferencePage_HistSize, parent);
        size_editor.setValidRange(MIN_SIZE, MAX_SIZE);
        size_editor.setErrorMessage(NLS.bind(Messages.PreferencePage_ValidHistSize,
                                             MIN_SIZE, MAX_SIZE));
        addField(size_editor);
        
        final IntegerFieldEditor delay_editor =
            new IntegerFieldEditor(P_SCAN_DELAY_MILLI, Messages.PreferencePage_ScanDelay, parent);
        delay_editor.setValidRange(MIN_DELAY, MAX_DELAY);
        delay_editor.setErrorMessage(NLS.bind(Messages.PreferencePage_ValidScanDelay,
                                              MIN_DELAY, MAX_DELAY));
        addField(delay_editor);
    }
    
    /** @return History size. */
    static public int getHistorySize()
    {
        final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        final int result = store.getInt(P_HISTORY_SIZE);
        if (result < MIN_SIZE)
            return MIN_SIZE;
        if (result > MAX_SIZE)
            return MAX_SIZE;
        return result;
    }

    /** @return History size. */
    static public int getScanDelayMillis()
    {
        final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        final int result = store.getInt(P_SCAN_DELAY_MILLI);
        if (result < MIN_DELAY)
            return MIN_DELAY;
        if (result > MAX_DELAY)
            return MAX_DELAY;
        return result;
    }
}
