package org.csstudio.apputil.ui.swt.stringtable;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Table;

/** Editor for table with string list
 *  @author Kay Kasemir
 */
public class StringColumnEditor extends EditingSupport
{
	final private List<String> items;

	public StringColumnEditor(final TableViewer viewer, final List<String> items)
	{
		super(viewer);
		this.items = items;
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

	@Override
	protected Object getValue(Object element)
	{
		if (element == StringTableContentProvider.ADD_ELEMENT)
			return "";
		final int index = ((Integer)element).intValue();
		return items.get(index);
	}

	@Override
	protected void setValue(Object element, Object value)
	{
		if (element == StringTableContentProvider.ADD_ELEMENT)
		{
			items.add(value.toString());
			getViewer().refresh();
			return;
		}
		// else
		final int index = ((Integer)element).intValue();
		items.set(index, value.toString());
		getViewer().refresh(element);
	}
}
