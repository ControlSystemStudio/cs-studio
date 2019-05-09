/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logging.es.gui;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.logging.es.Helpers;
import org.csstudio.logging.es.Messages;
import org.csstudio.logging.es.archivedjmslog.PropertyFilter;
import org.csstudio.logging.es.archivedjmslog.StringPropertyFilter;
import org.csstudio.logging.es.model.MessageSeverityPropertyFilter;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog for configuring property filters.
 *
 * @author Kay Kasemir
 */
public class FilterDialog extends TitleAreaDialog
{
    /** Number of filters to offer */
    private static final int FILTER_COUNT = 5;

    /** Available properties */
    private final String[] properties;

    // GUI Elements
    private final Button inverted_box[];
    private final Combo property_combo[];
    private final Text value_text[];
    private Combo severity_combo;

    /** Filter settings: Initial values; updated in <code>okPressed</code> */
    private PropertyFilter filters[];

    /**
     * Construct message filter dialog
     *
     * @param shell
     *            Parent shell
     * @param properties
     *            Array of filter-able properties
     * @param filter_count
     *            Number of filters to offer (all AND'ed together)
     * @param current_filters
     *            Current filter settings or <code>null</code>
     */
    public FilterDialog(Shell shell, String[] properties,
            PropertyFilter current_filters[])
    {
        super(shell);
        this.properties = properties;
        this.inverted_box = new Button[FILTER_COUNT];
        this.property_combo = new Combo[FILTER_COUNT];
        this.value_text = new Text[FILTER_COUNT];
        this.filters = current_filters;
    }

    /** {@inheritDoc} */
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
        layout.numColumns = 5;
        box.setLayout(layout);
        GridData gd;
        Label l;

        l = new Label(box, 0);
        l.setText(Messages.Filter_MinSeverity);
        l.setLayoutData(new GridData());
        this.severity_combo = new Combo(box, SWT.DROP_DOWN | SWT.READ_ONLY);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 4;
        this.severity_combo.setLayoutData(gd);
        this.severity_combo.setItems(Helpers.LOG_LEVELS);

        // Not: __not__ Property: ____property____ Value: ___value___
        for (int i = 0; i < this.property_combo.length; ++i)
        {
            if (i > 0)
            { // new row
                l = new Label(box, 0);
                l.setText(Messages.Filter_and);
                gd = new GridData();
                gd.horizontalSpan = layout.numColumns;
                gd.grabExcessHorizontalSpace = true;
                gd.horizontalAlignment = SWT.CENTER;
                l.setLayoutData(gd);
            }

            this.inverted_box[i] = new Button(box, SWT.CHECK);
            this.inverted_box[i]
                    .setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            this.inverted_box[i].setText(Messages.Filter_not);

            l = new Label(box, 0);
            l.setText(Messages.Filter_Property);
            l.setLayoutData(new GridData());

            this.property_combo[i] = new Combo(box,
                    SWT.DROP_DOWN | SWT.READ_ONLY);
            this.property_combo[i].setToolTipText(Messages.Filter_PropertyTT);
            this.property_combo[i].add(Messages.Filter_NoFilter);
            for (String prop : this.properties)
            {
                this.property_combo[i].add(prop);
            }
            this.property_combo[i]
                    .setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            this.property_combo[i].select(0);

            l = new Label(box, 0);
            l.setText(Messages.Filter_Value);
            l.setLayoutData(new GridData());

            this.value_text[i] = new Text(box, SWT.BORDER);
            this.value_text[i].setToolTipText(Messages.Filter_ValueTT);
            this.value_text[i]
                    .setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
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
        if (this.filters == null)
        {
            return;
        }
        int N = this.filters.length;
        if (N > this.property_combo.length)
        {
            N = this.property_combo.length;
        }
        for (int i = 0; i < N; ++i)
        {
            final PropertyFilter f = this.filters[i];
            if (f instanceof StringPropertyFilter)
            {
                StringPropertyFilter filter = (StringPropertyFilter) f;
                // Determine index of filter property in combo box
                for (int prop_index = 0; prop_index < this.properties.length; ++prop_index)
                {
                    if (this.properties[prop_index]
                            .equals(filter.getProperty()))
                    { // +1 because index 0 is "no property"
                        this.property_combo[i].select(prop_index + 1);
                        this.value_text[i].setText(filter.getPattern());
                        this.inverted_box[i].setSelection(filter.isInverted());
                    }
                }
            }
            else if (f instanceof MessageSeverityPropertyFilter)
            {
                MessageSeverityPropertyFilter filter = (MessageSeverityPropertyFilter) f;
                this.severity_combo.select(filter.getMinLevel());
            }
        }
    }

    /** @return Array of user-configured filters (not <code>null</code>) */
    public PropertyFilter[] getFilters()
    {
        return this.filters;
    }

    /** Allow resize */
    @Override
    protected boolean isResizable()
    {
        return true;
    }

    /** Memorize entered/selected data */
    @Override
    protected void okPressed()
    {
        List<PropertyFilter> valid_filters = new ArrayList<>(
                this.properties.length);
        // Collect valid entries in result arrays
        for (int i = 0; i < this.property_combo.length; ++i)
        { // '-1' since selection Index 0 is "no property"
            final int prop_index = this.property_combo[i].getSelectionIndex()
                    - 1;
            if (prop_index >= 0)
            {
                valid_filters.add(
                        new StringPropertyFilter(this.properties[prop_index],
                                this.value_text[i].getText().trim(),
                                this.inverted_box[i].getSelection()));
            }
        }

        final String min_level = this.severity_combo.getText();
        if (!min_level.isEmpty())
        {
            // start at 0: no need to set a filter >= FINEST.
            for (int i = 1; i < Helpers.LOG_LEVELS.length; ++i)
            {
                if (Helpers.LOG_LEVELS[i].equals(min_level))
                {
                    valid_filters.add(new MessageSeverityPropertyFilter(i));
                    break;
                }
            }
        }

        // Turn into array
        this.filters = new PropertyFilter[valid_filters.size()];
        valid_filters.toArray(this.filters);

        super.okPressed();
    }
}
