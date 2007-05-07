package org.csstudio.util.time.swt;

import java.util.Calendar;

/** Listener interface for the CalendarWidget.
 *  @author Kay Kasemir
 */
public interface CalendarWidgetListener
{
    /** The user or another piece of code set the widget to a new time.
     *  @param source The affected widget.
     *  @param calendar The current date and time.
     */
    public void updatedCalendar(CalendarWidget source, Calendar calendar);
}
