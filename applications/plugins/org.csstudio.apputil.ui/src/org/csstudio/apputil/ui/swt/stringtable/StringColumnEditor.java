package org.csstudio.apputil.ui.swt.stringtable;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Table;

/** Editor for table with string list
 *  @author Kay Kasemir
 *  @author Xihui Chen
 */
public class StringColumnEditor extends EditingSupport
{
	final private TableInputWrapper wrapper;

	public StringColumnEditor(final TableViewer viewer, final TableInputWrapper wrapper)
	{
		super(viewer);
		this.wrapper = wrapper;
	}

	@Override
	protected boolean canEdit(final Object element)
	{
		return true;
	}

	@Override
	protected CellEditor getCellEditor(final Object element)
	{
		final Table parent = (Table) getViewer().getControl();
		return new TextCellEditor(parent);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Object getValue(Object element)
	{
		if (element == StringTableContentProvider.ADD_ELEMENT)
			return "";
		final int index = ((Integer)element).intValue();
		return ((List<String>)wrapper.getItems()).get(index);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void setValue(Object element, Object value)
	{
		if (element == StringTableContentProvider.ADD_ELEMENT)
		{
			((List<String>)wrapper.getItems()).add(value.toString());
			getViewer().refresh();
			return;
		}
		// else
		final int index = ((Integer)element).intValue();
		((List<String>)wrapper.getItems()).set(index, value.toString());
		getViewer().refresh(element);
	}
}
