/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.ui;

import org.csstudio.display.pvtable.model.PVTableItem;
import org.csstudio.display.pvtable.model.PVTableModel;
import org.csstudio.display.pvtable.model.PVTableModelListener;
import org.csstudio.display.pvtable.model.TimestampHelper;
import org.csstudio.display.pvtable.model.VTypeHelper;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.epics.pvmanager.data.VType;

/** PV Table GUI
 *  @author Kay Kasemir
 */
public class PVTable implements PVTableModelListener
{
	private TableViewer viewer;
	private Color changed_background;

	/** Initialize
	 *  @param parent Parent widget
	 */
	public PVTable(final Composite parent)
	{
		createComponents(parent);
		createContextMenu(viewer);
		
		viewer.getTable().addListener(SWT.Selection, new Listener()
		{
			@Override
			public void handleEvent(final Event event)
			{
				if (event.detail != SWT.CHECK)
					return;
				final TableItem tab_item = (TableItem) event.item;
				final PVTableItem item = (PVTableItem) tab_item.getData();
				item.setSelected(tab_item.getChecked());
			}
		});
	}

	/** Create GUI components
	 *  @param parent
	 */
	private void createComponents(final Composite parent)
	{
		// TableColumnLayout requires table to be only child of parent. 
		// To assert that'll always be the case, create box.
		final Composite table_box = new Composite(parent, 0);
		parent.setLayout(new FillLayout());
		final TableColumnLayout layout = new TableColumnLayout();
		table_box.setLayout(layout);
		
		// Tried CheckboxTableViewer, but it lead to inconsistent refreshes:
		// Rows would appear blank. Didn't investigate further, stuck with TableViewer.
		viewer = new TableViewer(table_box, SWT.CHECK | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.VIRTUAL);
		
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		changed_background = table.getDisplay().getSystemColor(SWT.COLOR_CYAN);
		
		createColumn(viewer, layout, "PV", 75, 100,
			new CellLabelProvider()
			{
				@Override
				public void update(final ViewerCell cell)
				{
					final TableItem tab_item = (TableItem) cell.getItem();
					final PVTableItem item = (PVTableItem) cell.getElement();
					tab_item.setChecked(item.isSelected());
					cell.setText(item.getName());
					updateCommonCellSettings(cell, item);
				}
			});
		createColumn(viewer, layout, "Timestamp", 50, 100,
			new CellLabelProvider()
			{
				@Override
				public void update(final ViewerCell cell)
				{
					final PVTableItem item = (PVTableItem) cell.getElement();
					final VType value = item.getValue();
					if (value == null)
						cell.setText("");
					else
						cell.setText(TimestampHelper.format(VTypeHelper.getTimestamp(value)));
					updateCommonCellSettings(cell, item);
				}
			});
		createColumn(viewer, layout, "Value", 100, 50,
			new CellLabelProvider()
			{
				@Override
				public void update(final ViewerCell cell)
				{
					final PVTableItem item = (PVTableItem) cell.getElement();
					final VType value = item.getValue();
					if (value == null)
						cell.setText("");
					else
						cell.setText(VTypeHelper.toString(value));
					updateCommonCellSettings(cell, item);
				}
			});
		createColumn(viewer, layout, "Alarm", 100, 50,
			new CellLabelProvider()
			{
				@Override
				public void update(final ViewerCell cell)
				{
					final PVTableItem item = (PVTableItem) cell.getElement();
					final VType value = item.getValue();
					if (value == null)
						cell.setText("");
					else
						cell.setText(VTypeHelper.formatAlarm(value));
					updateCommonCellSettings(cell, item);
				}
			});
		createColumn(viewer, layout, "Saved Value", 100, 50,
			new CellLabelProvider()
			{
				@Override
				public void update(final ViewerCell cell)
				{
					final PVTableItem item = (PVTableItem) cell.getElement();
					final VType value = item.getSavedValue();
					if (value == null)
						cell.setText("");
					else
						cell.setText(VTypeHelper.toString(value));
					updateCommonCellSettings(cell, item);
				}
			});

		viewer.setContentProvider(new PVTableModelContentProvider());
	}
	
	/** Update common cell features (background, ...)
	 *  @param cell Cell to update
	 *  @param item Item to display in cell
	 */
	final protected void updateCommonCellSettings(final ViewerCell cell, final PVTableItem item)
	{
		if (item.isChanged())
			cell.setBackground(changed_background);
		else
			cell.setBackground(null);
	}

	/** Helper for creating table column
	 *  @param viewer
	 *  @param layout
	 *  @param header
	 *  @param weight
	 *  @param min_width
	 *  @param label_provider
	 */
	private void createColumn(final TableViewer viewer,
			final TableColumnLayout layout,
			final String header,
			final int weight,
			final int min_width,
			final CellLabelProvider label_provider)
	{
		final TableViewerColumn view_col = new TableViewerColumn(viewer, 0);
		final TableColumn col = view_col.getColumn();
		col.setText(header);
		col.setResizable(true);
		layout.setColumnData(col, new ColumnWeightData(weight, min_width));
		view_col.setLabelProvider(label_provider);		
	}
	
	/** Helper for creating context menu
	 *  @param viewer
	 */
	private void createContextMenu(final TableViewer viewer)
	{
		final MenuManager manager = new MenuManager();
		manager.add(new SnapshotAction(viewer));
		manager.add(new RestoreAction(viewer));
		final Control control = viewer.getControl();
		final Menu menu = manager.createContextMenu(control);
		control.setMenu(menu);
	}
	
	/** @param model Model to display in table */
	public void setModel(final PVTableModel model)
	{
		// Remove this as listener from previous model
		final PVTableModel previous = (PVTableModel) viewer.getInput();
		if (previous != null)
			previous.removeListener(this);
		viewer.setInput(model);
		model.addListener(this);
	}

	/** {@inheritDoc} */
	@Override
	public void tableItemChanged(final PVTableItem item)
	{
		final Table table = viewer.getTable();
		if (table.isDisposed())
			return;
		table.getDisplay().asyncExec(new Runnable()
		{
			public void run()
			{
				if (!table.isDisposed())
					viewer.refresh(item);
			}
		});
	}

	/** {@inheritDoc} */
	@Override
	public void tableItemsChanged()
	{
		viewer.refresh();
	}
}
