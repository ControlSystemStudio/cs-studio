package org.csstudio.util.time.swt;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/** Demo of TimestampWidget.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class CalendarWidgetTest implements CalendarWidgetListener
{
    private static DateFormat format =
        new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public void updatedCalendar(CalendarWidget source, Calendar calendar)
    {
        JmsLogsPlugin.logInfo("Time: " + format.format(calendar.getTime()));
    }

    public void run()
    {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());
        
        CalendarWidget w = new CalendarWidget(shell, 0);
        w.addListener(this);

        shell.pack();
        shell.open();
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch())
                display.sleep();
        }
        w.removeListener(this);
        display.dispose();
    }
    
    public static void main(String[] args)
    {
        new CalendarWidgetTest().run();
    }
}
