/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.scan.ui.scandata;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.scan.data.DataFormatter;
import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.data.SpreadsheetScanDataIterator;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

/** Eclipse "Editor" to display scan data
 *
 *  <p>Input of editor is {@link ScanInfoEditorInput}.
 *
 *  @author Kay Kasemir
 */
public class ScanDataEditor extends EditorPart implements ScanDataModelListener
{
	/** Editor ID defined in plugin.xml */
	final public static String ID = "org.csstudio.scan.ui.scandata.display"; //$NON-NLS-1$

	private TableViewer table_viewer;
	private ScanDataModel scan_data_model;

	/** {@inheritDoc} */
	@Override
    public void init(final IEditorSite site, final IEditorInput input)
            throws PartInitException
    {
		if (! (input instanceof ScanInfoEditorInput))
			throw new PartInitException("Expecting ScanInfoEditorInput"); //$NON-NLS-1$
    	setInput(input);
    	setSite(site);

    	// Editor title: scan name
    	setPartName(input.getName());
    }

	private ScanInfoEditorInput getScanInfoEditorInput()
	{
		return (ScanInfoEditorInput) getEditorInput();
	}

	/** Create GUI elements.
	 * {@inheritDoc}
	 */
	@Override
    public void createPartControl(final Composite parent)
    {
		// TableColumnLayout requires the table to be the only
		// child of it's parent.
		final TableColumnLayout layout = new TableColumnLayout();
		parent.setLayout(layout);

		// Create table
		table_viewer = new TableViewer(parent,
				SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.VIRTUAL);
		final Table table = table_viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		// Add timestamp column
		final TableViewerColumn column = new TableViewerColumn(table_viewer, SWT.LEFT);
		final TableColumn col = column.getColumn();
		col.setMoveable(true);
		col.setResizable(true);
		col.setText(Messages.Timestamp);
		column.setLabelProvider(new CellLabelProvider()
		{
			@Override
		    public String getToolTipText(Object element)
		    {
			    final ScanDataRow row = (ScanDataRow) element;
			    return DataFormatter.format(row.getTimestamp());
		    }

			@Override
			public void update(final ViewerCell cell)
			{
				cell.setText(getToolTipText((ScanDataRow) cell.getElement()));
			}
		});
		layout.setColumnData(col, new ColumnWeightData(10, 155));

		// Enable tool tips
		ColumnViewerToolTipSupport.enableFor(table_viewer);

		// Device columns will be added in updateTableColumns
		table_viewer.setContentProvider(new ScanDataTableContentProvider());

		// Create data model that will provide updates
		final ScanInfoEditorInput input = getScanInfoEditorInput();
		try
		{
			scan_data_model = new ScanDataModel(input.getScanID(), this);
		}
		catch (Exception ex)
		{
			ExceptionDetailsErrorDialog.openError(parent.getShell(), "Cannot obtain scan data", ex); //$NON-NLS-1$
		}

		// Release model when editor is closed
		parent.addDisposeListener(new DisposeListener()
		{
			@Override
			public void widgetDisposed(final DisposeEvent e)
			{
				scan_data_model.release();
				scan_data_model = null;
			}
		});
    }

	/** Create table columns
	 *  @param devices Devices that each need a table columns
	 */
	private void updateTableColumns(final String[] devices)
	{
		final Table table = table_viewer.getTable();
		final Composite parent = table.getParent();

		// Avoid flicker while the table is fundamentally updated
		parent.setRedraw(false);

		// Remove all existing device columns, leaving the timestamp column
		for (int i=table.getColumnCount()-1;  i>0;  --i)
			table.getColumn(i).dispose();

		// Add device columns
		final TableColumnLayout layout = (TableColumnLayout) parent.getLayout();

		for (int i=0; i<devices.length; ++i)
		{
			final String device = devices[i];
			final TableViewerColumn column = new TableViewerColumn(table_viewer, SWT.CENTER);
			final TableColumn col = column.getColumn();
			col.setMoveable(true);
			col.setResizable(true);
			col.setText(device);
			column.setLabelProvider(new ScanDataLabelProvider(i));
			layout.setColumnData(col, new ColumnWeightData(100, 35));
		}

		// Trigger re-layout
		parent.layout();

		// Update GUI
		parent.setRedraw(true);
	}

	/** {@inheritDoc} */
	@Override
    public void setFocus()
    {
		table_viewer.getTable().setFocus();
    }

	/** {@inheritDoc} */
	@Override
    public boolean isDirty()
    {	// Read-only, never gets 'Dirty'
        return false;
    }

	/** {@inheritDoc} */
	@Override
    public boolean isSaveAsAllowed()
    {	// Read-only, cannot save
        return false;
    }

	/** {@inheritDoc} */
	@Override
    public void doSave(final IProgressMonitor monitor)
    {
        // Should not be called
    }

	/** {@inheritDoc} */
	@Override
    public void doSaveAs()
    {
	    // Should not be called
    }

	/** Update display with newly received scan data
	 *  @see ScanDataModelListener
	 */
	@Override
    public void updateScanData(final ScanData data)
    {
		// Transform data in update thread
		final List<ScanDataRow> rows = new ArrayList<ScanDataRow>();
		SpreadsheetScanDataIterator sheet = new SpreadsheetScanDataIterator(data);
		final String[] devices = sheet.getDevices();
        while (sheet.hasNext())
        {
        	final ScanDataRow row = new ScanDataRow(sheet.getTimestamp(), sheet.getSamples());
			rows.add(row);
        }
        sheet = null;

		// Update display in UI thread
		final Table table = table_viewer.getTable();
		if (table.isDisposed())
			return;
		table.getDisplay().asyncExec(new Runnable()
		{
			@Override
            public void run()
			{
				if (table.isDisposed())
					return;

				// Has the data changed?
				if (table.getColumnCount() != devices.length + 1)
					updateTableColumns(devices);

				table_viewer.setInput(rows);
			}
		});
    }
}
