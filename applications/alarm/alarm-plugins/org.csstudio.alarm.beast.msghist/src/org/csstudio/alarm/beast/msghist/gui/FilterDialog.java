/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.msghist.gui;

import java.util.ArrayList;

import org.csstudio.alarm.beast.msghist.Messages;
import org.csstudio.alarm.beast.msghist.model.MessagePropertyFilter;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/** Dialog for configuring property filters.
 *  @author Kay Kasemir
 */
public class FilterDialog extends TitleAreaDialog
{
    /** Number of filters to offer */
    final private static int FILTER_COUNT = 5;

    /** Available properties */
    final private String[] properties;
    
    // GUI Elements
    final private Combo property_combo[];
    final private Text value_text[];
    
    /** Filter settings: Initial values; updated in <code>okPressed</code> */
    private MessagePropertyFilter filters[];

    /** Construct message filter dialog
     *  @param shell Parent shell
     *  @param properties Array of filter-able properties
     *  @param filter_count Number of filters to offer (all AND'ed together)
     *  @param current_filters Current filter settings or <code>null</code>
     */
    public FilterDialog(final Shell shell, final String[] properties,
            final MessagePropertyFilter current_filters[])
    {
        super(shell);
        this.properties = properties;
        property_combo = new Combo[FILTER_COUNT];
        value_text = new Text[FILTER_COUNT];
        filters = current_filters;
    }
    
    /** Allow resize */
    @Override
    protected boolean isResizable()
    {
	    return true;
    }

	/** {@inhericDoc} */
    @Override
    protected Control createDialogArea(final Composite parent)
    {
        final Composite area = (Composite) super.createDialogArea(parent);
        
        setMessage(Messages.Filter_Message);
        
        final Composite outer_box = new Composite(area, 0);
        outer_box.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        outer_box.setLayout(new GridLayout(1, false));
        
        final Composite box = new Composite(outer_box, SWT.BORDER);
        box.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        final GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        box.setLayout(layout);
        GridData gd;
        
        // Property: ____property____  Value: ___value___
        Label l;
        for (int i=0; i<property_combo.length; ++i)
        {
            if (i > 0)
            {   // new row
                l = new Label(box, 0);
                l.setText(Messages.Filter_and);
                gd = new GridData();
                gd.horizontalSpan = layout.numColumns;
                gd.grabExcessHorizontalSpace = true;
                gd.horizontalAlignment = SWT.CENTER;
                l.setLayoutData(gd);
            }
            l = new Label(box, 0);
            l.setText(Messages.Filter_Property);
            l.setLayoutData(new GridData());
            
            property_combo[i] = new Combo(box, SWT.DROP_DOWN | SWT.READ_ONLY);
            property_combo[i].setToolTipText(Messages.Filter_PropertyTT);
            property_combo[i].add(Messages.Filter_NoFilter);
            for (String prop : properties)
                property_combo[i].add(prop);
            property_combo[i].setLayoutData(new GridData(SWT.FILL, 0, true, false));
            property_combo[i].select(0);
            
            l = new Label(box, 0);
            l.setText(Messages.Filter_Value);
            l.setLayoutData(new GridData());
            
            value_text[i] = new Text(box, SWT.BORDER);
            value_text[i].setToolTipText(Messages.Filter_ValueTT);
            value_text[i].setLayoutData(new GridData(SWT.FILL, 0, true, false));
        }

        // -- End of 'inner' box
        l = new Label(outer_box, 0);
        l.setText(Messages.Filter_ValuePatternHelp);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.RIGHT;
        l.setLayoutData(gd);
        
        displayCurrentFilterSettings();
        
        return area;
    }

    /** Update GUI to reflect <code>filters</code> */
    private void displayCurrentFilterSettings()
    {
        if (filters == null)
            return;
        int N = filters.length;
        if (N > property_combo.length)
            N = property_combo.length;
        for (int i=0; i<N; ++i)
        {
            final MessagePropertyFilter filter = filters[i];
            // Determine index of filter property in combo box
            for (int prop_index=0; prop_index<properties.length; ++prop_index)
            {
                if (properties[prop_index].equals(filter.getProperty()))
                {   // +1 because index 0 is "no property"
                    property_combo[i].select(prop_index + 1);
                    value_text[i].setText(filter.getPattern());
                }
            }
        }
    }

    /** Memorize entered/selected data */
    @Override
    protected void okPressed()
    {
        final ArrayList<MessagePropertyFilter> valid_filters =
            new ArrayList<MessagePropertyFilter>(properties.length);
        // Collect valid entries in result arrays
        for (int i=0; i<property_combo.length; ++i)
        {   // '-1' since selection Index 0 is "no property"
            final int prop_index = property_combo[i].getSelectionIndex()-1;
            if (prop_index >= 0)
                valid_filters.add(new MessagePropertyFilter(
                    properties[prop_index], value_text[i].getText().trim()));
        }
        // Turn into array
        filters = new MessagePropertyFilter[valid_filters.size()];
        valid_filters.toArray(filters);
        
        super.okPressed();
    }
    
    /** @return Array of user-configured filters (not <code>null</code>) */
    public MessagePropertyFilter[] getFilters()
    {
        return filters;
    }
}
