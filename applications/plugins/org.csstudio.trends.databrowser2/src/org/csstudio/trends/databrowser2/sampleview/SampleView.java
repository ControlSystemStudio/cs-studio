/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.sampleview;

import org.csstudio.data.values.IDoubleValue;
import org.csstudio.data.values.IMinMaxDoubleValue;
import org.csstudio.data.values.ISeverity;
import org.csstudio.data.values.IValue;
import org.csstudio.trends.databrowser2.Messages;
import org.csstudio.trends.databrowser2.editor.DataBrowserAwareView;
import org.csstudio.trends.databrowser2.model.AxisConfig;
import org.csstudio.trends.databrowser2.model.Model;
import org.csstudio.trends.databrowser2.model.ModelItem;
import org.csstudio.trends.databrowser2.model.ModelListener;
import org.csstudio.trends.databrowser2.model.PVItem;
import org.csstudio.trends.databrowser2.model.PlotSample;
import org.csstudio.trends.databrowser2.ui.TableHelper;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;

/** A View that shows all the current Model Samples in a list.
 *
 *  @author Kay Kasemir
 *  @author Helge Rickens contributed to the previous Data Browser SampleView
 *  @author Albert Kagarmanov changed the previous Data Browser's
 *              SampleTableLabelProvider to show numbers with 4 trailing digits.
 *              This implementation uses tooltips to show the Double.toString(number)
 *  @author Takashi Nakamoto changed SampleView to handle multiple items with the
 *                           same name correctly. 
 */
public class SampleView extends DataBrowserAwareView
	implements ModelListener
{
    /** View ID registered in plugin.xml */
    final public static String ID = "org.csstudio.trends.databrowser.sample_view"; //$NON-NLS-1$

    /** Model of the currently active Data Browser plot or <code>null</code> */
    private Model model;
    
    /** GUI elements */
    private Combo items;
    private TableViewer sample_table;

    /** {@inheritDoc} */
    @Override
    protected void doCreatePartControl(final Composite parent)
    {
        final GridLayout layout = new GridLayout(3, false);
        parent.setLayout(layout);

        // Item: pvs [Refresh]
        Label l = new Label(parent, 0);
        l.setText(Messages.SampleView_Item);
        l.setLayoutData(new GridData());

        items = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        items.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        items.addSelectionListener(new SelectionListener()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {
                widgetDefaultSelected(e);
            }

            @Override
            public void widgetDefaultSelected(final SelectionEvent e)
            {   // Configure table to display samples of the selected model item
                if (items.getSelectionIndex() == 0)
                {
                    sample_table.setInput(null);
                    return;
                }
                final ModelItem item = model.getItem(items.getSelectionIndex() - 1);
                if (item == null)
                    return;
                sample_table.setInput(item);
            }
        });

        final Button refresh = new Button(parent, SWT.PUSH);
        refresh.setText(Messages.SampleView_Refresh);
        refresh.setToolTipText(Messages.SampleView_RefreshTT);
        refresh.setLayoutData(new GridData());
        refresh.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(final SelectionEvent e)
            {   // Trigger GUI update
            	update(false);
            }
        });

        // Sample Table
        // TableColumnLayout requires this to be in its own container
        final Composite table_parent = new Composite(parent, 0);
        table_parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, layout.numColumns, 1));
        final TableColumnLayout table_layout = new TableColumnLayout();
        table_parent.setLayout(table_layout);

        sample_table = new TableViewer(table_parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
        sample_table.setContentProvider(new SampleTableContentProvider());
        final Table table = sample_table.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        // Time column
        TableViewerColumn col =
            TableHelper.createColumn(table_layout, sample_table, Messages.TimeColumn, 90, 100);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final PlotSample sample = (PlotSample) cell.getElement();
                cell.setText(sample.getTime().toString());
            }
        });
        // Value column
        col = TableHelper.createColumn(table_layout, sample_table, Messages.ValueColumn, 50, 100);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final PlotSample sample = (PlotSample) cell.getElement();
                cell.setText(sample.getValue().format());
            }

            @Override
            public String getToolTipText(Object element)
            {
                final PlotSample sample = (PlotSample) element;
                final IValue value = sample.getValue();
                if (value instanceof IMinMaxDoubleValue)
                {
                    final IMinMaxDoubleValue mmd = (IMinMaxDoubleValue) value;
                    return NLS.bind(Messages.SampleView_MinMaxValueTT,
                        new String[]
                        {
                            Double.toString(mmd.getValue()),
                            Double.toString(mmd.getMinimum()),
                            Double.toString(mmd.getMaximum())
                        });
                }
                else if (value instanceof IDoubleValue)
                {
                    final IDoubleValue dbl = (IDoubleValue) value;
                    return Double.toString(dbl.getValue());
                }
                else
                    return value.toString();
            }
        });
        // Severity column
        col = TableHelper.createColumn(table_layout, sample_table, Messages.SeverityColumn, 90, 50);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final PlotSample sample = (PlotSample) cell.getElement();
                final IValue value = sample.getValue();
                final ISeverity severity = value.getSeverity();
                cell.setText(severity.toString());
                if (severity.isOK())
                {
                    cell.setBackground(null);
                    return;
                }
                final Display display = cell.getControl().getDisplay();
                if (severity.isMajor())
                    cell.setBackground(display.getSystemColor(SWT.COLOR_RED));
                else if (severity.isMinor())
                    cell.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
                else
                    cell.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
            }
        });
        // Status column
        col = TableHelper.createColumn(table_layout, sample_table, Messages.StatusColumn, 90, 50);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final PlotSample sample = (PlotSample) cell.getElement();
                final IValue value = sample.getValue();
                cell.setText(value.getStatus());
            }
        });
        // Sample Source column
        col = TableHelper.createColumn(table_layout, sample_table, Messages.SampleView_Source, 90, 10);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final PlotSample sample = (PlotSample) cell.getElement();
                cell.setText(sample.getSource());
            }
        });
        // Data Quality column
        col = TableHelper.createColumn(table_layout, sample_table, Messages.SampleView_Quality, 90, 10);
        col.setLabelProvider(new CellLabelProvider()
        {
            @Override
            public void update(final ViewerCell cell)
            {
                final PlotSample sample = (PlotSample) cell.getElement();
                cell.setText(sample.getValue().getQuality().toString());
            }
        });
        ColumnViewerToolTipSupport.enableFor(sample_table);
        
        // Be ignorant of any change of the current model after this view
        // is disposed.
        parent.addDisposeListener(new DisposeListener()
        {
            @Override
            public void widgetDisposed(DisposeEvent e)
            {
            	if (model != null)
            		model.removeListener(SampleView.this);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    protected void updateModel(final Model old_model, final Model model)
    {
    	this.model = model;
    	if (old_model != model) {
    		if (old_model != null)
    			old_model.removeListener(this);
    		
    		if (model != null)
    			model.addListener(this);
    	}
    	update(old_model != model);
    }
    
    /** Update combo box of this view.
     * @param model_changed set true if the model was changed.
     */
    private void update(final boolean model_changed)
    {
        if (model == null)
        {   // Clear/disable GUI
            items.setItems(new String[] { Messages.SampleView_NoPlot});
            items.select(0);
            items.setEnabled(false);
            sample_table.setInput(null);
            return;
        }

        // Show PV names
        final String names[] = new String[model.getItemCount()+1];
        names[0] = Messages.SampleView_SelectItem;
        for (int i=1; i<names.length; ++i)
            names[i] = model.getItem(i-1).getName();
        if (!model_changed  &&  items.getSelectionIndex() > 0)
        {
            // Is the previously selected item still valid?
        	if (sample_table.getInput() instanceof ModelItem)
        	{
        		final ModelItem selected_item = (ModelItem) sample_table.getInput();
        		if (model.indexOf(selected_item) != -1)
        		{   // Show same PV name again in combo box
        			items.setItems(names);
        			items.select(model.indexOf(selected_item) + 1);
        			items.setEnabled(true);
        			// Update sample table size
        			sample_table.setItemCount(selected_item.getSamples().getSize());
        			sample_table.refresh();
        			return;
        		}
        	}
        }
        // Previously selected item no longer valid.
        // Show new items, clear rest
        items.setItems(names);
        items.select(0);
        items.setEnabled(true);
        sample_table.setInput(null);
    }

    /** {@inheritDoc} */
    @Override
    public void setFocus()
    {
        items.setFocus();
    }
    
    /** {@inheritDoc} */
	@Override
	public void itemAdded(ModelItem item) {
	    // Be aware of the addition of a new item to update combo box.
		update(false);
	}

	/** {@inheritDoc} */
	@Override
	public void itemRemoved(ModelItem item) {
	    // Be aware of the addition of a new item to update combo box.
		update(false);
	}
	
	/** {@inheritDoc} */
	@Override
	public void changedItemLook(ModelItem item) {
		// Be aware of the change of the item name.
		update(false);
	}

	// Following methods are defined as they are mandatory to fulfill
	// ModelListener interface, but they are not used at all to update
	// this sample view.
	@Override
	public void changedUpdatePeriod() {}

	@Override
	public void changedArchiveRescale() {}

	@Override
	public void changedColors() {}

	@Override
	public void changedTimerange() {}

	@Override
	public void changedAxis(AxisConfig axis) {}

	@Override
	public void changedItemVisibility(ModelItem item) {}

	@Override
	public void changedItemDataConfig(PVItem item) {}

	@Override
	public void scrollEnabled(boolean scroll_enabled) {}

	@Override
	public void changedAnnotations() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changedXYGraphConfig() {
		// TODO Auto-generated method stub
		
	}
}
