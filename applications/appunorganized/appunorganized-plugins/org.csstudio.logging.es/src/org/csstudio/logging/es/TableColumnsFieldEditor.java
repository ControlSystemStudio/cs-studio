/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logging.es;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.csstudio.ui.util.swt.stringtable.StringTableEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Preference dialog field editor for property columns
 * 
 * @author Kay Kasemir
 */
public class TableColumnsFieldEditor extends FieldEditor
{
    private List<String> columns;
    private Label label;
    private StringTableEditor editor;

    /**
     * Initialize
     * 
     * @param parent
     *            Parent Widget
     */
    public TableColumnsFieldEditor(Composite parent)
    {
        super(Preferences.COLUMNS, Messages.TableColumnsEditor_Title, parent);
    }

    /** {@inheritDoc} */
    @Override
    protected void adjustForNumColumns(int numColumns)
    {
        GridData gd = (GridData) this.label.getLayoutData();
        gd.horizontalSpan = numColumns;
        gd = (GridData) this.editor.getLayoutData();
        gd.horizontalSpan = numColumns;
    }

    /** {@inheritDoc} */
    @Override
    protected void doFillIntoGrid(Composite parent, int numColumns)
    {
        this.label = new Label(parent, 0);
        this.label.setText(Messages.TableColumnsEditor_Columns);
        GridData gd = new GridData();
        gd.horizontalSpan = numColumns;
        this.label.setLayoutData(gd);

        this.columns = new ArrayList<>();
        this.editor = new StringTableEditor(parent, this.columns);
        this.editor.setToolTipText(Messages.TableColumnsEditor_TT);
        gd = new GridData();
        gd.horizontalSpan = numColumns;
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        this.editor.setLayoutData(gd);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("nls")
    @Override
    protected void doLoad()
    {
        try
        {
            String[] column_prefs = Preferences.getColumnPreferences();
            updateColumnPrefs(column_prefs);
        }
        catch (Exception ex)
        {
            Activator.getLogger().log(Level.WARNING, "Preference Load error",
                    ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void doLoadDefault()
    {
        String pref_text = getPreferenceStore()
                .getDefaultString(getPreferenceName());
        String[] column_prefs = Preferences
                .decodeRawColumnPreferences(pref_text);
        updateColumnPrefs(column_prefs);
    }

    /** {@inheritDoc} */
    @Override
    protected void doStore()
    {
        String pref_text = Preferences.encodeRawColumnPrefs(
                this.columns.toArray(new String[this.columns.size()]));
        getPreferenceStore().setValue(getPreferenceName(), pref_text);
    }

    /** Indicate that we need at least one column */
    @Override
    public int getNumberOfControls()
    {
        return 1;
    }

    /**
     * Update the column preferences shown in the GUI
     * 
     * @param column_prefs
     *            Column prefs to show
     */
    private void updateColumnPrefs(final String[] column_prefs)
    {
        this.columns.clear();
        for (String pref : column_prefs)
            this.columns.add(pref);
        this.editor.refresh();
    }
}
