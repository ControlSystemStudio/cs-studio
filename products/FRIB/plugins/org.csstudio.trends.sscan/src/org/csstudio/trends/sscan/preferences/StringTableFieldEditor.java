/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.sscan.preferences;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.trends.sscan.Messages;
import org.csstudio.ui.util.swt.stringtable.RowEditDialog;
import org.csstudio.ui.util.swt.stringtable.StringTableEditor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

/** Field Editor for preferences shown in a StringTableEditor
 *  @author Xihui Chen - Original author, org.csstudio.opibuilder.preferences.StringTableFieldEditor
 *  @author Kay Kasemir
 */
public class StringTableFieldEditor extends FieldEditor
{
    final private String headers[];
    final private boolean editable[];
    final private int columnsMinWidth[];
    final private RowEditDialog rowEditDialog;

    private StringTableEditor tableEditor;
    private List<String[]> items;

    /** Creates an editable table.  The size of headers array implies the number of columns.
     *  @param parent SWT parent
     *  @param preference Name of preference setting
     *  @param label_text Table title
     *  @param table_headers Contains the header for each column
     *  @param edit_flags Whether it is editable for each column. The size must be same as headers.
     *  @param column_widths Table column widths
     *  @param row_edit_dialog Editor for selected row
     */
    public StringTableFieldEditor(final Composite parent, final String preference,
            final String label_text, final String table_headers[],
            final boolean edit_flags[], final int column_widths[],
            final RowEditDialog row_edit_dialog)
    {
        init(preference, label_text);
        this.headers = table_headers;
        this.editable = edit_flags;
        this.columnsMinWidth = column_widths;
        this.rowEditDialog = row_edit_dialog;
        this.items = new ArrayList<String[]>();
        createControl(parent);
    }

    /** {@inheritDoc} */
    @Override
    protected void doFillIntoGrid(final Composite parent, final int numColumns)
    {
        getLabelControl(parent);
        GridData gd = new GridData();
        gd.horizontalSpan = numColumns;
        getLabelControl().setLayoutData(gd);
        tableEditor = new StringTableEditor(
                parent, headers, editable, items, rowEditDialog, columnsMinWidth);
        gd = new GridData();
        gd.horizontalSpan = numColumns;
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        tableEditor.setLayoutData(gd);
    }

    /** {@inheritDoc} */
    @Override
    protected void adjustForNumColumns(final int numColumns)
    {
        GridData gd = (GridData)tableEditor.getLayoutData();
        gd.horizontalSpan = numColumns;
        gd = (GridData)getLabelControl().getLayoutData();
        gd.horizontalSpan = numColumns;
    }


    /** {@inheritDoc} */
    @Override
    protected void doLoad()
    {
        if(tableEditor == null)
            return;
        try
        {
            items = decodeStringTable(getPreferenceStore().getString(getPreferenceName()));
            tableEditor.updateInput(items);
        }
        catch (Exception ex)
        {
            MessageDialog.openError(getPage().getShell(), Messages.Error,
                   NLS.bind(Messages.ErrorFmt, ex.getMessage()));
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void doLoadDefault()
    {
        if(tableEditor == null)
            return;
        try
        {
            items = decodeStringTable(getPreferenceStore().getDefaultString(getPreferenceName()));
            tableEditor.updateInput(items);
        }
        catch (Exception ex)
        {
            MessageDialog.openError(getPage().getShell(), Messages.Error,
                   NLS.bind(Messages.ErrorFmt, ex.getMessage()));
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void doStore()
    {
        getPreferenceStore().setValue(getPreferenceName(), flattenStringTable(items));
    }

    /** {@inheritDoc} */
    @Override
    public int getNumberOfControls()
    {
        return 1;
    }

    /**Flatten a string table to a single line string.
     * @param string_table
     * @return flattened preference value
     */
    private String flattenStringTable(List<String[]> string_table)
    {
        StringBuilder result = new StringBuilder();
        for (int i=0; i<string_table.size(); ++i)
        {
            if (i > 0)
                result.append(Preferences.ITEM_SEPARATOR);
            final String components[] = string_table.get(i);
            for (int c=0; c<components.length; ++c)
            {
                if (c > 0)
                    result.append(Preferences.COMPONENT_SEPARATOR);
                result.append(components[c]);
            }
        }
        return result.toString();
    }

    /** Decode
     *  @param flattened_preference String stored in preferences
     *  @return String table
     */
    private List<String[]> decodeStringTable(final String flattened_preference)
    {
        final List<String[]> result = new ArrayList<String[]>();
        final String rows[] = flattened_preference.split(Preferences.ITEM_SEPARATOR_RE);
        for (String rowString : rows)
        {
            // Skip empty rowString, don't split it into String[1] { "" }
            if (rowString.length() <= 0)
                continue;
            final String items[] = rowString.split(Preferences.COMPONENT_SEPARATOR_RE);
            result.add(items);
        }
        return result;
    }
}
