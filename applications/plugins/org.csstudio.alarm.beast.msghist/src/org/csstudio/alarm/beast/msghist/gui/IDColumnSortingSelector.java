package org.csstudio.alarm.beast.msghist.gui;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.TableColumn;

/** Listener to table column selections, sorting the table on message ID.
 *  @author Kay Kasemir
 */
public class IDColumnSortingSelector extends SortingColumnSelector
{
    public IDColumnSortingSelector(final TableViewer table_viewer,
            final TableColumn column)
    {
        super(table_viewer, column);
    }

    @Override
    protected void sort(boolean up)
    {
        table_viewer.setComparator(new MessageIDComparator(up));
    }
}
