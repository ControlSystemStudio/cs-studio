package org.csstudio.util.time.swt;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.util.time.RelativeTime;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/** Demo of TimestampWidget.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class StartEndDialogTest
{
    public void updatedTime(RelativeTimeWidget source, RelativeTime time)
    {
        JmsLogsPlugin.logInfo("Time: " + time);
    }

    public void run()
    {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());
        

        shell.pack();
        shell.open();

        StartEndDialog dlg = new StartEndDialog(shell);
        dlg.open();
        
        JmsLogsPlugin.logInfo("Start: '" + dlg.getStartSpecification() + "'");
        JmsLogsPlugin.logInfo("End: '" + dlg.getEndSpecification() + "'");
        ITimestamp start = TimestampFactory.fromCalendar(dlg.getStartCalendar());
        ITimestamp end = TimestampFactory.fromCalendar(dlg.getEndCalendar());
        JmsLogsPlugin.logInfo(start + " ... " + end);

        display.dispose();
    }
    
    public static void main(String[] args)
    {
        new StartEndDialogTest().run();
    }
}
