package org.csstudio.diag.pvfields.gui;

import org.csstudio.diag.pvfields.PVField;
import org.csstudio.diag.pvfields.model.PVModel;
import org.csstudio.ui.util.helpers.ComboHistoryHelper;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class GUI
{
	public GUI(final Composite parent, IDialogSettings settings)
	{
		createComponents(parent, settings);
	}

	private void createComponents(final Composite parent, IDialogSettings settings)
	{
		final GridLayout layout = new GridLayout(2, false);
		parent.setLayout(layout);
		
		// PV: _________
		Label l = new Label(parent, 0);
		l.setText("PV Name:");
		l.setLayoutData(new GridData());
		
		final Combo combo = new Combo(parent, SWT.DROP_DOWN);
		combo.setLayoutData(new GridData(SWT.FILL, 0, true, false));
		combo.setToolTipText("Enter PV Name");
		
		new ComboHistoryHelper(settings, "pv_name", combo)
		{
			@Override
			public void newSelection(final String name)
			{
				// TODO Auto-generated method stub
				System.out.println("PV Name: " + name);
			}
		};
		
		// Sash for property and field tables
		final SashForm sashes = new SashForm(parent, SWT.VERTICAL);
		sashes.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, layout.numColumns, 1));
		
		// TableColumnLayout requires table to be in its own container
		final Composite box = new Composite(sashes, 0);
		box.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, layout.numColumns, 1));
		final TableViewer properties = createPropertyTable(box);

		final Composite box2 = new Composite(sashes, 0);
		box2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, layout.numColumns, 1));
		final TableViewer fields = createFieldTable(box2);
		
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
		
		createColumn(viewer, table_layout, "Property", 100, new CellLabelProvider()
        {
			@Override
			public void update(ViewerCell cell) {
				// TODO Auto-generated method stub
				
			}
		});
		createColumn(viewer, table_layout, "Value", 161, new CellLabelProvider()
        {
			@Override
			public void update(ViewerCell cell) {
				// TODO Auto-generated method stub
				
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
		
		createColumn(viewer, table_layout, "Field", 40, new CellLabelProvider()
        {
			@Override
			public void update(ViewerCell cell) {
				// TODO Auto-generated method stub
				
			}
		});
		createColumn(viewer, table_layout, "Original Value", 60, new CellLabelProvider()
        {
			@Override
			public void update(ViewerCell cell) {
				// TODO Auto-generated method stub
				
			}
		});
		createColumn(viewer, table_layout, "Current Value", 60, new CellLabelProvider()
        {
			@Override
			public void update(ViewerCell cell) {
				// TODO Auto-generated method stub
				
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
}
