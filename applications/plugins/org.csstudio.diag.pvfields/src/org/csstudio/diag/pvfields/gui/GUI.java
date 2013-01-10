/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.diag.pvfields.gui;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.diag.pvfields.PVField;
import org.csstudio.diag.pvfields.PVHelper;
import org.csstudio.diag.pvfields.model.PVModel;
import org.csstudio.diag.pvfields.model.PVModelExport;
import org.csstudio.diag.pvfields.model.PVModelListener;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.csstudio.ui.util.dnd.ControlSystemDropTarget;
import org.csstudio.ui.util.helpers.ComboHistoryHelper;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartSite;

/** GUI for the Model
 * 
 *  <p>Allow entering PV name,
 *  set name in model, display data from model.
 * 
 * @author Kay Kasemir
 */
public class GUI implements PVModelListener
{
	final private Composite parent;
	private PVModel model = null;
	private Combo combo;
	private ComboHistoryHelper combo_history;
	private TableViewer property_view;
	private TableViewer field_view;
	private Button export;

	/** Initialize
	 *  @param parent Parent widget
	 *  @param settings Saved settings or <code>null</code>
	 *  @param site Part site or <code>null</code>
	 */
	public GUI(final Composite parent, final IDialogSettings settings, final IWorkbenchPartSite site)
	{
		this.parent = parent;
		createComponents();
		
		combo_history = new ComboHistoryHelper(settings, "pv_name", combo)
		{
			@Override
			public void newSelection(final String name)
			{
				setPVName(name);
			}
		};
		combo_history.loadSettings();
		
    	// Enable 'Drop' on to combo box (entry box) and tables
		hookDrop(combo);
		hookDrop(property_view.getControl());
		hookDrop(field_view.getControl());
		
		if (site != null)
		{
	        // Add empty context menu so that other CSS apps can
	        // add themselves to it
	        final MenuManager menuMgr = new MenuManager("");
	        menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	        final Menu menu = menuMgr.createContextMenu(field_view.getControl());
	        field_view.getControl().setMenu(menu);
	        site.registerContextMenu(menuMgr, field_view);
		}
	}

	/** Create GUI components */
	private void createComponents()
	{
		final GridLayout layout = new GridLayout(3, false);
		parent.setLayout(layout);
		
		// PV: _________ [Export]
		Label l = new Label(parent, 0);
		l.setText("PV Name:");
		l.setLayoutData(new GridData());
		
		combo = new Combo(parent, SWT.DROP_DOWN);
		combo.setLayoutData(new GridData(SWT.FILL, 0, true, false));
		combo.setToolTipText("Enter PV Name");
		
		export = new Button(parent, SWT.PUSH);
		export.setText("Export");
		export.setToolTipText("Export displayed values to file");
		export.setLayoutData(new GridData());
		export.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				if (model != null)
					PVModelExport.export(model, parent.getShell());
			}
		});
		export.setEnabled(false);
		
		// Sash for property and field tables
		final SashForm sashes = new SashForm(parent, SWT.VERTICAL);
		sashes.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, layout.numColumns, 1));
		
		// TableColumnLayout requires table to be in its own container
		final Composite box = new Composite(sashes, 0);
		box.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, layout.numColumns, 1));
		property_view = createPropertyTable(box);

		final Composite box2 = new Composite(sashes, 0);
		box2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, layout.numColumns, 1));
		field_view = createFieldTable(box2);
		
		sashes.setWeights(new int[] { 100, 161 });
	}
	
	/** Create table for Properties
	 *  @param box Container
	 *  @return {@link TableViewer}
	 */
	private static TableViewer createPropertyTable(final Composite box)
	{
		final TableColumnLayout table_layout = new TableColumnLayout();
		box.setLayout(table_layout);

		final TableViewer viewer = new TableViewer(box, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		ColumnViewerToolTipSupport.enableFor(viewer);
		viewer.setContentProvider(new ArrayContentProvider());
		
		createColumn(viewer, table_layout, "Property", 100, new CellLabelProvider()
        {
			@Override
			public void update(final ViewerCell cell)
			{
				final String[] values = (String[]) cell.getElement();
				cell.setText(values[0]);
			}
		});
		createColumn(viewer, table_layout, "Value", 161, new CellLabelProvider()
        {
			@Override
			public void update(final ViewerCell cell)
			{
				final String[] values = (String[]) cell.getElement();
				cell.setText(values[1]);
			}
		});
		
		return viewer;
	}

	/** Create table for {@link PVField}s
	 *  @param box Container
	 *  @return {@link TableViewer}
	 */
	private static TableViewer createFieldTable(final Composite box)
	{
		final TableColumnLayout table_layout = new TableColumnLayout();
		box.setLayout(table_layout);

		final TableViewer viewer = new TableViewer(box, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		ColumnViewerToolTipSupport.enableFor(viewer);
		viewer.setContentProvider(new ArrayContentProvider());
		
		final Display display = viewer.getTable().getDisplay();
		createColumn(viewer, table_layout, "Field", 40, new CellLabelProvider()
        {
			@Override
			public void update(final ViewerCell cell)
			{
				final PVField field = (PVField) cell.getElement();
				cell.setText(PVHelper.getField(field.getName()));
			}
		});
		createColumn(viewer, table_layout, "Original Value", 60, new CellLabelProvider()
        {
			@Override
			public void update(final ViewerCell cell)
			{
				final PVField field = (PVField) cell.getElement();
				cell.setText(field.getOriginalValue());
				if (field.isChanged())
					cell.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
				else
					cell.setBackground(null);
			}
		});
		createColumn(viewer, table_layout, "Current Value", 60, new CellLabelProvider()
        {
			@Override
			public void update(final ViewerCell cell)
			{
				final PVField field = (PVField) cell.getElement();
				cell.setText(field.getCurrentValue());
				if (field.isChanged())
					cell.setBackground(display.getSystemColor(SWT.COLOR_GRAY));
				else
					cell.setBackground(null);
			}
		});
		return viewer;
	}

	/** Helper for creating table columns
	 *  @param viewer
	 *  @param table_layout
	 *  @param header
	 *  @param weight
	 *  @param provider
	 */
	private static void createColumn(final TableViewer viewer, final TableColumnLayout table_layout,
			final String header, final int weight, final CellLabelProvider provider)
	{
		final TableViewerColumn view_col = new TableViewerColumn(viewer, 0);
        final TableColumn table_col = view_col.getColumn();
        table_layout.setColumnData(table_col, new ColumnWeightData(weight));
        table_col.setText(header);
        view_col.setLabelProvider(provider);
	}
	
	/** Allow dropping PV names
	 *  @param control Control that should allow dropping the names
	 */
    private void hookDrop(final Control control)
    {
        new ControlSystemDropTarget(control, ProcessVariable.class, String.class)
        {
            @Override
            public void handleDrop(final Object item)
            {
            	GUI.this.handleDrop(item);
            }
        };
	}

    /** Use dropped PV name
     *  @param item PV or string
     */
	private void handleDrop(final Object item)
    {
        if (item instanceof ProcessVariable)
            setPVName(((ProcessVariable)item).getName());
        else if (item instanceof String)
            setPVName(item.toString().trim());
    }

	/** Set focus */
    public void setFocus()
    {
    	combo.setFocus();
    }
	
    /** @return Currently entered PV name */
    public String getPVName()
    {
    	return combo.getText();
    }
    
    /** Set or update the PV name
     *  @param name Name of PV for which to get data
     */
	public void setPVName(String name)
	{
		name = PVHelper.getPV(name);
		
		if (! combo.getText().equals(name))
		{
			combo.setText(name);
			combo_history.addEntry(name);
		}
		// Stop previous model
		if (model != null)
		{
			model.stop();
			model = null;
		}
		
		if (name.isEmpty())
		{
			showMessage("Enter PV name", "");
			export.setEnabled(false);
		}
		
		showMessage("Please wait...", "Getting data for " + name);
		export.setEnabled(true);
		
		// Create model for PV name
		try
		{
			model = new PVModel(name, this);
		}
		catch (Exception ex)
		{
			ExceptionDetailsErrorDialog.openError(parent.getShell(), "Error", ex);
		}
		
		// Stop when GUI is disposed
		parent.addDisposeListener(new DisposeListener()
		{
			@Override
			public void widgetDisposed(DisposeEvent e)
			{
				if (model != null)
				{
					model.stop();
					model = null;
				}
			}
		});
	}

	/** Display a message by (mis-) using the property table
	 *  @param text1 Short text to display
	 *  @param text2 ..
	 */
	private void showMessage(final String text1, final String text2)
	{
		property_view.setInput(new String[][] { { text1, text2 }});
		field_view.setInput(new PVField[0]);
	}

	/** {@inheritDoc} */
	@Override
	public void updateProperties(final Map<String, String> properties)
	{
		Set<String> keys = properties.keySet();
		
		final String[] names = keys.toArray(new String[keys.size()]);
		Arrays.sort(names);
		
		final String[][] values = new String[names.length][2];
		for (int i=0; i<values.length; ++i)
		{
			values[i][0] = names[i];
			values[i][1] = properties.get(names[i]);
		}
		parent.getDisplay().asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				property_view.setInput(values);
			}
		});
	}

	/** {@inheritDoc} */
	@Override
	public void updateFields(final List<PVField> fields)
	{
		parent.getDisplay().asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				field_view.setInput(fields);
			}
		});
	}

	/** {@inheritDoc} */
	@Override
	public void updateField(final PVField field)
	{
		parent.getDisplay().asyncExec(new Runnable()
		{
			@Override
			public void run()
			{
				field_view.update(field, null);
			}
		});
	}
}
