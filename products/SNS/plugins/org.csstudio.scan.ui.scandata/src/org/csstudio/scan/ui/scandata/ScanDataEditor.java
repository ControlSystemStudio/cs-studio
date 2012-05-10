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

import org.csstudio.scan.data.ScanData;
import org.csstudio.scan.data.ScanSample;
import org.csstudio.scan.data.SpreadsheetScanDataIterator;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.eclipse.core.runtime.IProgressMonitor;
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
	final public static String ID = "org.csstudio.scan.ui.scandata.display";

	private TableViewer table_viewer;
	private ScanDataModel scan_data_model;

	/** {@inheritDoc} */
	@Override
    public void init(final IEditorSite site, final IEditorInput input)
            throws PartInitException
    {
		if (! (input instanceof ScanInfoEditorInput))
			throw new PartInitException("Expecting ScanInfoEditorInput");
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

		table_viewer = new TableViewer(parent,
				SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.VIRTUAL);
		final Table table = table_viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableViewerColumn column = new TableViewerColumn(table_viewer, SWT.LEFT);
		TableColumn col = column.getColumn();
		col.setMoveable(true);
		col.setResizable(true);
		col.setText("Time");
		column.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				final ScanDataRow row = (ScanDataRow) cell.getElement();
				cell.setText(row.getTimestamp().toString());
			}
		});
		layout.setColumnData(col, new ColumnWeightData(100, 120));

		column = new TableViewerColumn(table_viewer, SWT.RIGHT);
		col = column.getColumn();
		col.setMoveable(true);
		col.setResizable(true);
		col.setText("Value 1");
		column.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				final ScanDataRow row = (ScanDataRow) cell.getElement();
				final ScanSample sample = row.getSample(cell.getColumnIndex()-1);
				cell.setText(sample.toString());
			}
		});
		layout.setColumnData(col, new ColumnWeightData(100, 60));

		table_viewer.setContentProvider(new ScanDataTableContentProvider());

		final ScanInfoEditorInput input = getScanInfoEditorInput();
		try
		{
			scan_data_model = new ScanDataModel(input.getScanID(), this);
		}
		catch (Exception ex)
		{
			ExceptionDetailsErrorDialog.openError(parent.getShell(), "Cannot obtain scan data", ex);
		}
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
        while (sheet.hasNext())
        	rows.add(new ScanDataRow(sheet.getTimestamp(), sheet.getSamples()));
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
				table_viewer.setInput(rows);
			}
		});
    }
}
