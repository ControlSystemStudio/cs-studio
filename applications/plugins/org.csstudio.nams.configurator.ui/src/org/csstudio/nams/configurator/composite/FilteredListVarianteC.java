package org.csstudio.nams.configurator.composite;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

public abstract class FilteredListVarianteC {

	public FilteredListVarianteC(Composite parent, int style) {
		this.createPartControl(parent, style);
	}

	private void createPartControl(Composite parent, int style) {
		Composite main = new Composite(parent, style);
		main.setLayout(new GridLayout(1, false));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(main);

		{
			Composite compDown = new Composite(main, SWT.None);
			compDown.setLayout(new GridLayout(4, false));
			GridDataFactory.fillDefaults().grab(true, false).applyTo(compDown);

			{
				new Label(compDown, SWT.READ_ONLY).setText("Suche Name");

				Text filter = new Text(compDown, SWT.BORDER);
				GridDataFactory.fillDefaults().grab(true, false)
						.applyTo(filter);
			}

			{
				new Label(compDown, SWT.READ_ONLY).setText("Suche Rubrik");

				Text filter = new Text(compDown, SWT.BORDER);
				GridDataFactory.fillDefaults().grab(true, false)
						.applyTo(filter);
			}
		}

		{
			TableViewer table = new TableViewer(main);
			table.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
			// GridDataFactory.fillDefaults().grab(true, true).applyTo(
			// table.getControl());
			table.getTable().setHeaderVisible(true);

			// table.setColumnProperties(new String[] { "Name", "Rubrik" });
			TableColumn column1 = new TableColumn(table.getTable(), SWT.LEFT);
			column1.setText("Name");
			column1.setWidth(100);
			TableColumn column2 = new TableColumn(table.getTable(), SWT.RIGHT);
			column2.setText("Rubrik");
			column2.setWidth(100);

			table.setContentProvider(new ArrayContentProvider());
			table.setLabelProvider(new TableLabelProvider());
			table.setInput(this.getTableInput());
		}
	}

	protected abstract Object[] getTableInput();

	private class TableInput {
		String name;
		String rubrik;

		TableInput(String name, String rubrik) {
			this.name = name;
			this.rubrik = rubrik;
		}
	}

	private class TableLabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {

			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			switch (columnIndex) {
			case 0:
				return ((TableInput) element).name;
			case 1:
				return ((TableInput) element).rubrik;
			}
			return element.toString();
		}

		public void addListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub

		}

		public void dispose() {
			// TODO Auto-generated method stub

		}

		public boolean isLabelProperty(Object element, String property) {
			// TODO Auto-generated method stub
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub

		}

	}
}
