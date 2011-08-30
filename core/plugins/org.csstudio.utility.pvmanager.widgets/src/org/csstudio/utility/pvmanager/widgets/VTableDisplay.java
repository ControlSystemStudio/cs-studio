package org.csstudio.utility.pvmanager.widgets;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.epics.pvmanager.data.VTable;
import org.eclipse.swt.layout.FillLayout;

/**
 * Basic ui component that can display a VTable on screen.
 * 
 * @author carcassi
 */
public class VTableDisplay extends Composite {
	TableViewer tableViewer;
	private Table table;
	private Composite tableContainer;
	private VTableCellLabelProvider cellLabelProvider;

	/**
	 * Creates a new display.
	 * 
	 * @param parent
	 */
	public VTableDisplay(Composite parent) {
		// Use no background so that image does not flicker
		super(parent, SWT.NONE);
		tableContainer = this; //new Composite(parent, SWT.NONE);
		tableContainer.setLayout(new FillLayout(SWT.HORIZONTAL));
		//container.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		tableViewer = new TableViewer(tableContainer, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		tableViewer.setContentProvider(new VTableContentProvider());
		tableViewer.setLabelProvider(getCellLabelProvider());
        VTableToolTipSupport.enableFor(tableViewer,ToolTip.NO_RECREATE);
	}
	
	// The current table being displayed
	private VTable vTable;
	
	/**
	 * Changes the current table being displayed.
	 * 
	 * @param vTable the new table
	 */
	public void setVTable(VTable vTable) {
		if (!isDisposed()) {
			this.vTable = vTable;
			refreshColumns();
			tableViewer.setInput(vTable);
		}
	}
	
	public VTableCellLabelProvider getCellLabelProvider() {
		if (cellLabelProvider == null)
			cellLabelProvider = new VTableCellLabelProvider();
		return cellLabelProvider;
	}
	
	public void setCellLabelProvider(VTableCellLabelProvider cellLabelProvider) {
		this.cellLabelProvider = cellLabelProvider;
		for (TableColumn column: table.getColumns()) {
			column.dispose();
		}
		tableViewer.setLabelProvider(cellLabelProvider);
		refreshColumns();
	}
	
	private void refreshColumns() {
		int requiredCount = 0;
		if (vTable != null)
			requiredCount = vTable.getColumnCount();
		
		if (table.getColumnCount() == requiredCount)
			return;
		
		while (table.getColumnCount() > requiredCount) {
			table.getColumn(table.getColumnCount() - 1).dispose();
		}
		
		while (table.getColumnCount() < requiredCount) {
			TableViewerColumn newViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
			newViewerColumn.setLabelProvider(getCellLabelProvider());
		}
		
		TableColumnLayout layout = new TableColumnLayout();
		tableContainer.setLayout(layout);
		for (int i = 0; i < requiredCount; i++) {
			table.getColumn(i).setText(vTable.getColumnName(i));
			layout.setColumnData(table.getColumn(i), new ColumnWeightData(1, 30));
		}
		tableContainer.layout();
		
	}

	/**
	 * Returns the current image being displayed.
	 * 
	 * @return the current table
	 */
	public VTable getVTable() {
		return vTable;
	}

}
