package org.csstudio.trends.databrowser.archiveview;

import org.csstudio.platform.util.ITimestamp;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/** A table label provider for NameTableItem-type data
 *  @author Kay Kasemir
 */
public class NameTableItemLabelProvider extends LabelProvider
                                        implements ITableLabelProvider
{
    public static final int NAME = 0;
    public static final int ARCHIVE = 1;
    public static final int START = 2;
    public static final int END = 3;

    /** No column images ... */
    public Image getColumnImage(Object element, int column)
    {
        return null;
    }

    /** @return Returns the name, key, .... */
    public String getColumnText(Object element, int column)
    {
        NameTableItem item = (NameTableItem) element;
        switch (column)
        {
        case NAME:
            return item.getName();
        case ARCHIVE:
            return item.getArchiveName();
        case START:
            return item.getStart().format(ITimestamp.FMT_DATE_HH_MM_SS);
        case END:
            return item.getEnd().format(ITimestamp.FMT_DATE_HH_MM_SS);
        }
        return null;
    }
}
