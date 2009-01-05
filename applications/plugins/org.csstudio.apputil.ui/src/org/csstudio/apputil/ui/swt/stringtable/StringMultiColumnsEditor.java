package org.csstudio.apputil.ui.swt.stringtable;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Table;

/** Editor for table with multiple columns
 *  @author Xihui Chen
 */
public class StringMultiColumnsEditor extends EditingSupport {

	final private List<String[]> items;
	final private int columnNo;
	final private int numOfColumns;
	
	public StringMultiColumnsEditor(ColumnViewer viewer, List<String[]> items, int numOfColumns, int columnNo) {
		super(viewer);
		this.items = items;
		this.columnNo = columnNo;
		this.numOfColumns = numOfColumns;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		final Table parent = (Table) getViewer().getControl();
		return new TextCellEditor(parent);
	}

	@Override
	protected Object getValue(Object element) {
		
		if (element == StringTableContentProvider.ADD_ELEMENT)
			return "";
		final int index = ((Integer)element).intValue();
		return items.get(index)[columnNo];		
	}

	@Override
	protected void setValue(Object element, Object value) {
		String[] rowData;
		if (element == StringTableContentProvider.ADD_ELEMENT)
		{
			rowData = new String[numOfColumns];
			Arrays.fill(rowData, "");
			rowData[columnNo] = value.toString();
			items.add(rowData);
			getViewer().refresh();
			return;
		}
		// else
		final int index = ((Integer)element).intValue();
		rowData = items.get(index);
		rowData[columnNo] = value.toString();
		items.set(index, rowData);
		getViewer().refresh(element);
	}

}
