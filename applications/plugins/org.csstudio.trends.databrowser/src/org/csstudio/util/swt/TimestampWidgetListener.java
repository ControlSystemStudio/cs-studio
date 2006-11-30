package org.csstudio.util.swt;

import org.csstudio.platform.util.ITimestamp;

/** Listener interface for the TimestampWidget.
 *  @author Kay Kasemir
 */
public interface TimestampWidgetListener
{
    /** The user or another piece of code set the widget to a new timestamp.
     *  @param source The affected widget.
     *  @param stamp The current time stamp.
     */
    public void updatedTimestamp(TimestampWidget source, ITimestamp stamp);
}
