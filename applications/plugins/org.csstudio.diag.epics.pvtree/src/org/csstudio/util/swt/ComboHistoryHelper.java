package org.csstudio.util.swt;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;

/** Maintains a 'history' Combo box.
 *  <p>
 *  Newly entered items are added to the end of the combo list,
 *  dropping last items off the list when reaching a comfigurable maximum
 *  list size.
 *  <p>
 *  @see #newSelection(String)
 *  @author Kay Kasemir
 */
public abstract class ComboHistoryHelper
{
    private static final String TAG = "values"; //$NON-NLS-1$
    private static final int DEFAULT_MAX = 10;
    private final IDialogSettings settings;
    private final String tag;
    private final Combo combo;
    private final int max;
    
    /** Attach helper to given combo box, using default list length. */
    public ComboHistoryHelper(IDialogSettings settings, String tag, Combo combo)
    {
        this(settings, tag, combo, DEFAULT_MAX);
    }
    
    /** Attach helper to given combo box, using max list length.
     *  @param settings Where to persist the combo box list
     *  @param tag      Tag used for persistence
     *  @param combo    The combo box
     *  @param max      Max list length
     */
    public ComboHistoryHelper(IDialogSettings settings, String tag,
                              Combo combo, int max)
    {
        this.settings = settings;
        this.tag = tag;
        this.combo = combo;
        this.max = max;
    
        // React whenever an existing entry is selected,
        // or a new name is entered.
        // New names are also added to the list.
        combo.addSelectionListener(new SelectionListener()
        {
            // Called after <Return> was pressed
            public void widgetDefaultSelected(SelectionEvent e)
            {
                String new_entry = ComboHistoryHelper.this.combo.getText();
                addEntry(new_entry);
                newSelection(new_entry);
            }
    
            // Called after existing entry was picked from list
            public void widgetSelected(SelectionEvent e)
            {
                String name = ComboHistoryHelper.this.combo.getText();
                newSelection(name);
            }
        });
    }

    /** Add entry to top of list. */
    public void addEntry(String new_entry)
    {
        // Avoid empty entries
        if (new_entry.length() < 1)
            return;
        // Avoid duplicates
        for (int i=0; i<combo.getItemCount(); ++i)
            if (combo.getItem(i).equals(new_entry))
                return;
        // Maybe remove oldest, i.e. top-most, entry
        if (combo.getItemCount() >= max)
            combo.remove(0);
        // Add at end
        combo.add(new_entry);
    }
    
    /** Invoked whenever a new entry was entered or selected. */
    public abstract void newSelection(String entry);
    
    /** Load persisted list values. */
    public void loadSettings()
    {
        IDialogSettings pvs = settings.getSection(tag);
        if (pvs == null)
            return;
        String values[] = pvs.getArray(TAG);
        if (values != null)
            for (int i = 0; i < values.length; i++)
                if (values[i].length() > 0)
                    combo.add(values[i]);
    }

    /** Save list values to persistent storage. */
    public void saveSettings()
    {
        IDialogSettings values = settings.addNewSection(tag);
        values.put(TAG, combo.getItems());
    }
}
