package org.csstudio.apputil.ui.time;

import org.csstudio.apputil.time.RelativeTime;

/** Listener interface for the RelativeTimeWidget.
 *  @author Kay Kasemir
 */
public interface RelativeTimeWidgetListener
{
    /** The user or another piece of code set the widget to a new time.
     *  @param source The affected widget.
     *  @param time The current relative time specification.
     */
    public void updatedTime(RelativeTimeWidget source, RelativeTime time);
}
