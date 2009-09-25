package org.csstudio.diag.diles;

import java.util.List;

import org.csstudio.diag.diles.model.Activity;
import org.csstudio.diag.diles.providers.ModelContentProvider;
import org.csstudio.diag.diles.providers.ModelLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.part.ViewPart;

public class InOutView extends ViewPart {

	public static final String ID = "org.csstudio.diag.diles.inoutview";

	private TableViewer viewer;

	public InOutView() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * This will create the columns for the table
	 * 
	 * @param viewer
	 */
	private void createColumns(TableViewer viewer) {

		String[] titles = { "ID", "Command", "Status", "Hardware Input",
				"Hardware Output" };
		int[] bounds = { 30, 200, 200, 200, 200 };

		for (int i = 0; i < titles.length; i++) {
			TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
			column.getColumn().setText(titles[i]);
			column.getColumn().setWidth(bounds[i]);
			column.getColumn().setResizable(true);
			column.getColumn().setMoveable(true);
		}
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

	public void setTableInput(List<Activity> children) {
		viewer.setInput(children);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		createViewer(parent);
		// Get the content for the viewer, setInput will call getElements in the
		// contentProvider
	}

	/**
	 * @param parent
	 */
	private void createViewer(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION);
		createColumns(viewer);
		viewer.setContentProvider(new ModelContentProvider());
		viewer.setLabelProvider(new ModelLabelProvider());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public void updateTable() {
		viewer.refresh();
	}
	
}
