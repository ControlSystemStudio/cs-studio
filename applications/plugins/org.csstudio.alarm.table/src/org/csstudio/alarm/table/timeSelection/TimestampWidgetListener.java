package org.csstudio.alarm.table.timeSelection;

import org.csstudio.data.Timestamp;

/** Listener interface for the TimestampWidget.
 *  @author Kay Kasemir
 */
public interface TimestampWidgetListener
{
    /** The user or another piece of code set the widget to a new timestamp.
     *  @param source The affected widget.
     *  @param stamp The current time stamp.
     */
    public void updatedTimestamp(TimestampWidget source, Timestamp stamp);
}
