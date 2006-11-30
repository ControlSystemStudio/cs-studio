package org.csstudio.trends.databrowser.archiveview;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/** Label provider for the archive table.
 *  @author Kay Kasemir
 */
public class ArchiveTableLabelProvider extends LabelProvider
                                        implements ITableLabelProvider
{
    public static final int NAME = 0;
    public static final int KEY = 1;
    public static final int DESCRIPTION = 2;

    /** No column images ... */
    public Image getColumnImage(Object element, int columnIndex)
    {   return null;  }

    /** @return Returns the name, key or description. */
    public String getColumnText(Object element, int columnIndex)
    {
        ArchiveTableItem item = (ArchiveTableItem) element;
        switch (columnIndex)
        {
        case NAME:
            return item.getName();
        case KEY:
            return Integer.toString(item.getKey());
        case DESCRIPTION:
            return item.getDescription();
        }
        return null;
    }
}
