package org.csstudio.trends.databrowser.archiveview;

import org.csstudio.platform.data.ITimestamp;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/** A table label provider for NameTableItem-type data
 *  @author Kay Kasemir
 */
public class NameTableItemLabelProvider extends LabelProvider
                                        implements ITableLabelProvider
{
    final public static int NAME = 0;
    final public static int ARCHIVE = 1;
    final public static int START_OR_END = 2;
    final public static int END = 3;
    final private boolean show_start_times;

    /** Initialize
     *  @param show_start_times <code>true</code> for table with 'start' column
     */
    public NameTableItemLabelProvider(final boolean show_start_times)
    {
        this.show_start_times = show_start_times;
    }

    /** No column images ... */
    public Image getColumnImage(Object element, int column)
    {
        return null;
    }

    /** @return Returns the name, key, .... */
    public String getColumnText(final Object element, final int column)
    {
        final NameTableItem item = (NameTableItem) element;
        switch (column)
        {
        case NAME:
            return item.getName();
        case ARCHIVE:
            return item.getArchiveName();
        case START_OR_END:
            return show_start_times
                ? getTimeInfo(item.getStart()) : getTimeInfo(item.getEnd());
        case END:
            return getTimeInfo(item.getEnd());
        }
        return null;
    }

    /** @return String for time, also handling <code>null</code> time */
    private String getTimeInfo(final ITimestamp time)
    {
        if (time == null)
            return ""; //$NON-NLS-1$
        return time.format(ITimestamp.Format.DateTimeSeconds);
    }
}
