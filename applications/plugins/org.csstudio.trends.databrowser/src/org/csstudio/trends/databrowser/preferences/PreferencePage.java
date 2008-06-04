package org.csstudio.trends.databrowser.preferences;

import org.csstudio.trends.databrowser.Plugin;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/** Preference page for <code>Prederences</code>.
 *  <p>
 *  Mostly created by the Eclipse wizard:
 *  <p> 
 *  "This class represents a preference page that is contributed to the
 *   Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>,
 *   we can use the field support built into JFace that allows us to create a page
 *   that is small and knows how to save, restore and apply itself.
 *   <p>
 *   This page is used to modify preferences only. They are stored in the
 *   preference store that belongs to the main plug-in class. That way,
 *   preferences can be accessed directly via the preference store."
 *   @author Kay Kasemir
 */
public class PreferencePage extends FieldEditorPreferencePage implements
                IWorkbenchPreferencePage
{
    private static final int MAX_BINS = 10000;
    private static final int MIN_BINS = 100;

    public PreferencePage()
    {
        super(GRID);
        setPreferenceStore(Plugin.getDefault().getPreferenceStore());
        setDescription(Messages.PageTitle);
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

        addField(new StringFieldEditor(Preferences.START_TIME_SPEC,
                Messages.StartTime, parent));
        addField(new StringFieldEditor(Preferences.END_TIME_SPEC,
                Messages.EndTime, parent));

        addField(new PeriodFieldEditor(Preferences.SCAN_PERIOD,
                Messages.ScanPeriod, parent, Preferences.MIN_SCAN_PERIOD));
        addField(new PeriodFieldEditor(Preferences.UPDATE_PERIOD,
                Messages.UpdatePeriod, parent, Preferences.MIN_UPDATE_PERIOD));

        final IntegerFieldEditor live_buffer = new IntegerFieldEditor(
                Preferences.LIVE_BUFFER_SIZE, Messages.LiveBufferSize, parent);
        live_buffer.setValidRange(Preferences.MIN_LIVE_BUFFER_SIZE, Integer.MAX_VALUE);
        live_buffer.setErrorMessage(
                NLS.bind(Messages.LiveBufferMsg, Preferences.MIN_LIVE_BUFFER_SIZE));
        addField(live_buffer);

        final IntegerFieldEditor plot_bins = new IntegerFieldEditor(
                Preferences.PLOT_BINS, Messages.PlotBins, parent);
        plot_bins.setValidRange(MIN_BINS, MAX_BINS);
        plot_bins.setErrorMessage(NLS.bind(Messages.PlotBinsError, MIN_BINS,
                MAX_BINS));
        addField(plot_bins);

        addField(new BooleanFieldEditor(Preferences.AUTOSCALE,
                Messages.Label_Autoscale, parent));
        addField(new BooleanFieldEditor(Preferences.SHOW_REQUEST_TYPES,
                        Messages.Label_Show_Request_Types, parent));
        
        addField(new URLListEditor(Preferences.URLS, parent));
        addField(new ArchiveListEditor(Preferences.ARCHIVES, parent));
    }
}