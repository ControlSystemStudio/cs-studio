package org.csstudio.platform.ui.swt.stringtable;

import java.util.List;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerCell;

/** Label provider that transforms Integer index into list
 *  into the string to display.
 *  @author Kay Kasemir
 */
class StringColumnLabelProvider extends CellLabelProvider
{
	final private TableViewer viewer;

	/** Initialize
	 *  @param items It
	 */
	public StringColumnLabelProvider(final TableViewer viewer)
	{
		this.viewer = viewer;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update(final ViewerCell cell)
	{
		final List<String> items = (List<String>)viewer.getInput();
		final int index = ((Integer)cell.getElement()).intValue();
		if (index < 0)
			cell.setText("<Add>");
		else
			cell.setText(items.get(index));
	}
}
