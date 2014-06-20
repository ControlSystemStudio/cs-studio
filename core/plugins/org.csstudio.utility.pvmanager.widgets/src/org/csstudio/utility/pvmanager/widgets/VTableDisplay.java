package org.csstudio.utility.pvmanager.widgets;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.ui.util.composites.BeanComposite;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.epics.vtype.VTable;

/**
 * Basic ui component that can display a VTable on screen.
 * 
 * @author carcassi
 */
public class VTableDisplay extends BeanComposite implements ISelectionProvider {
	TableViewer tableViewer;
	private Table table;
	private Composite tableContainer;
	private VTableCellLabelProvider cellLabelProvider;
	
	@Override
	public void setMenu(Menu menu) {
		super.setMenu(menu);
		table.setMenu(menu);
	}

	/**
	 * Creates a new display.
	 * 
	 * @param parent
	 */
	public VTableDisplay(Composite parent) {
		super(parent, SWT.NONE);
		tableContainer = this;
		tableContainer.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		tableViewer = new TableViewer(tableContainer, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.addListener(SWT.MenuDetect, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				Point pt = table.toControl(new Point(event.x, event.y));
				Rectangle clientArea = table.getClientArea();
				boolean header = clientArea.y <= pt.y && pt.y < (clientArea.y + table.getHeaderHeight());
				ViewerCell cell = tableViewer.getCell(new Point(pt.x, Math.max(pt.y, clientArea.y + table.getHeaderHeight())));
				if (cell == null) {
					setSelection(null);
				} else {
					int row = ((VTableContentProvider.VTableRow) cell.getElement()).getRow();
					if (header)
						row = -1;
					int column = cell.getColumnIndex();
					setSelection(new StructuredSelection(new VTableDisplayCell(VTableDisplay.this, row, column)));
				}
			}
		});
		table.addListener(SWT.MouseUp, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				ViewerCell cell = tableViewer.getCell(new Point(event.x, event.y));
				if (cell == null) {
					setSelection(null);
				} else {
					int row = ((VTableContentProvider.VTableRow) cell.getElement()).getRow();
					int column = cell.getColumnIndex();
					setSelection(new StructuredSelection(new VTableDisplayCell(VTableDisplay.this, row, column)));
				}
			}
		});
		tableViewer.setContentProvider(new VTableContentProvider());
		tableViewer.setLabelProvider(getCellLabelProvider());
        VTableToolTipSupport.enableFor(tableViewer,ToolTip.NO_RECREATE);
	}
	
	@Override
    public void setFont(final Font font)
	{
	    super.setFont(font);
	    table.setFont(font);
	}
	
	public void addSelectionListener(SelectionListener listener) {
		table.addSelectionListener(listener);
	}
	
	public void removeSelecctionListener(SelectionListener listener) {
		table.removeSelectionListener(listener);
	}
	
	// The current table being displayed
	private VTable vTable;
	
	/**
	 * Changes the current table being displayed.
	 * 
	 * @param vTable the new table
	 */
	public void setVTable(VTable vTable) {
		VTable oldVTable = this.vTable;
		if (!isDisposed()) {
			this.vTable = vTable;
			refreshColumns();
			tableViewer.setInput(vTable);
		}
		changeSupport.firePropertyChange("vTable", oldVTable, vTable);
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
	
	private List<ISelectionChangedListener> selectionChangedListeners = new ArrayList<ISelectionChangedListener>();

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.add(listener);
	}
	
	private ISelection selection = new StructuredSelection();

	@Override
	public ISelection getSelection() {
		return selection;
	}

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		selectionChangedListeners.remove(listener);
	}

	@Override
	public void setSelection(ISelection selection) {
		this.selection = selection;
		if (selection == null)
			this.selection = new StructuredSelection();
		fireSelectionChangedListener();
	}
	
	private void fireSelectionChangedListener() {
		for (ISelectionChangedListener listener : selectionChangedListeners) {
			listener.selectionChanged(new SelectionChangedEvent(this, getSelection()));
		}
	}

}
