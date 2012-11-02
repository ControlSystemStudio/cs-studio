package org.csstudio.diag.pvfields.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.csstudio.diag.pvfields.PVField;
import org.csstudio.diag.pvfields.PVHelper;
import org.csstudio.diag.pvfields.model.PVModel;
import org.csstudio.diag.pvfields.model.PVModelListener;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.csstudio.ui.util.helpers.ComboHistoryHelper;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class GUI implements PVModelListener
{
	final private Composite parent;
	private PVModel model = null;
	private Combo combo;
	private TableViewer property_view;
	private TableViewer field_view;

	public GUI(final Composite parent, final IDialogSettings settings)
	{
		this.parent = parent;
		createComponents();
		new ComboHistoryHelper(settings, "pv_name", combo)
		{
			@Override
			public void newSelection(final String name)
			{
				setPVName(name);
			}
		};
	}

	private void createComponents()
	{
		final GridLayout layout = new GridLayout(2, false);
		parent.setLayout(layout);
		
		// PV: _________
		Label l = new Label(parent, 0);
		l.setText("PV Name:");
		l.setLayoutData(new GridData());
		
		combo = new Combo(parent, SWT.DROP_DOWN);
		combo.setLayoutData(new GridData(SWT.FILL, 0, true, false));
		combo.setToolTipText("Enter PV Name");
		
		
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
			}
		});
		createColumn(viewer, table_layout, "Current Value", 60, new CellLabelProvider()
        {
			@Override
			public void update(final ViewerCell cell)
			{
				final PVField field = (PVField) cell.getElement();
				cell.setText(field.getCurrentValue());
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
	
	public void setPVName(final String name)
	{
		// Stop previous model
		if (model != null)
		{
			model.stop();
			model = null;
		}
		
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
				model.stop();
				model = null;
			}
		});
	}

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

	@Override
	public void updateField(final PVField field)
	{
		field_view.update(field, null);
	}
}
