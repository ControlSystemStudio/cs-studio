/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.ui.swt;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;

/** Maintains a 'history' Combo box.
 *  <p>
 *  Newly entered items are added to the top of the combo list,
 *  dropping last items off the list when reaching a configurable maximum
 *  list size.
 *  <p>
 *  You must
 *  <ul>
 *  <li>implement newSelection() to handle entered/selected values
 *  <li>decide if you want to call loadSettings() to restore the saved
 *      values
 *  <li>save values via saveSettings, or use the save_on_dispose
 *      option of the constructor.
 *  </ul>
 *  @see #newSelection(String)
 *  @author Kay Kasemir
 */
public abstract class ComboHistoryHelper
{
    private static final String TAG = "values"; //$NON-NLS-1$
    private static final int DEFAULT_HISTORY_SIZE = 10;
    private final IDialogSettings settings;
    private final String tag;
    private final Combo combo;
    private final int max;

    /** Attach helper to given combo box, using max list length.
     *  @param settings         Where to persist the combo box list
     *  @param tag              Tag used for persistence
     *  @param combo            The combo box
     */
    public ComboHistoryHelper(IDialogSettings settings, String tag,
                    Combo combo)
    {
        this(settings, tag, combo, DEFAULT_HISTORY_SIZE, true);
    }

    /** Attach helper to given combo box, using max list length.
     *  @param settings         Where to persist the combo box list
     *  @param tag              Tag used for persistence
     *  @param combo            The combo box
     *  @param max              Number of elements to keep in history
     *  @param save_on_dispose  Set <code>true</code> if you want
     *                          to save current values on widget disposal
     */
    public ComboHistoryHelper(IDialogSettings settings, String tag,
                              Combo combo, int max, boolean save_on_dispose)
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
            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
                String new_entry = ComboHistoryHelper.this.combo.getText();
                addEntry(new_entry);
                newSelection(new_entry);
            }

            // Called after existing entry was picked from list
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                String name = ComboHistoryHelper.this.combo.getText();
                newSelection(name);
            }
        });

        if (save_on_dispose)
            combo.addDisposeListener(new DisposeListener()
        {
            @Override
            public void widgetDisposed(DisposeEvent e)
            {   saveSettings();  }
        });
    }

    /** Add entry to list. */
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
                // Load as if they were entered, i.e. skip duplicates
                addEntry(values[i]);
    }

    /** Save list values to persistent storage. */
    public void saveSettings()
    {
        IDialogSettings values = settings.addNewSection(tag);
        values.put(TAG, combo.getItems());
    }
}
