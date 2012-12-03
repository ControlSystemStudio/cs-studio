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
import org.csstudio.display.pvtable.model.VTypeHelper;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/** PV Table GUI
 *  @author Kay Kasemir
 */
public class PVTable implements PVTableModelListener
{
	private TableViewer viewer;


	public PVTable(final Composite parent)
	{
		final TableColumnLayout layout = new TableColumnLayout();
		parent.setLayout(layout);
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.VIRTUAL);
		
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		createColumn(viewer, layout, "PV", 100, 100,
			new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				final PVTableItem item = (PVTableItem) cell.getElement();
				cell.setText(item.getName());
			}
		});

	
		createColumn(viewer, layout, "Timestamp", 100, 100,
				new CellLabelProvider()
			{
				@Override
				public void update(final ViewerCell cell)
				{
					final PVTableItem item = (PVTableItem) cell.getElement();
					cell.setText("TODO Timestamp");
				}
			});
		
		createColumn(viewer, layout, "Value", 100, 100,
				new CellLabelProvider()
			{
				@Override
				public void update(final ViewerCell cell)
				{
					final PVTableItem item = (PVTableItem) cell.getElement();
					cell.setText(VTypeHelper.toString(item.getValue()));
				}
			});

		viewer.setContentProvider(new PVTableModelContentProvider());
	}
	

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

	
	public void setModel(final PVTableModel model)
	{
		// TODO remove this as listener from previous model?!
		viewer.setInput(model);
		model.addListener(this);
	}


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


	@Override
	public void tableItemsChanged()
	{
		// TODO Auto-generated method stub
		
	}
}
