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

	final private TableInputWrapper wrapper;
	final private int columnNo;
	final private int numOfColumns;
	
	public StringMultiColumnsEditor(final ColumnViewer viewer, final TableInputWrapper wrapper,
			final int numOfColumns, final int columnNo) {
		super(viewer);
		this.wrapper = wrapper;
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

	@SuppressWarnings("unchecked")
	@Override
	protected Object getValue(Object element) {
		
		if (element == StringTableContentProvider.ADD_ELEMENT)
			return "";
		final int index = ((Integer)element).intValue();
		return ((List<String[]>)wrapper.getItems()).get(index)[columnNo];		
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void setValue(Object element, Object value) {
		String[] rowData;
		if (element == StringTableContentProvider.ADD_ELEMENT)
		{
			rowData = new String[numOfColumns];
			Arrays.fill(rowData, "");
			rowData[columnNo] = value.toString();
			((List<String[]>)wrapper.getItems()).add(rowData);
			getViewer().refresh();
			return;
		}
		// else
		final int index = ((Integer)element).intValue();
		rowData = ((List<String[]>)wrapper.getItems()).get(index);
		rowData[columnNo] = value.toString();
		((List<String[]>)wrapper.getItems()).set(index, rowData);
		getViewer().refresh(element);
	}

}
