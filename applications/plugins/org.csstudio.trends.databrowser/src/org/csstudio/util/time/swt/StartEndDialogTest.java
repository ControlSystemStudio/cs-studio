package org.csstudio.util.time.swt;

import org.csstudio.platform.data.ITimestamp;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.apputil.time.RelativeTime;
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
        System.out.println("Time: " + time);
    }

    public void run()
    {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());
        

        shell.pack();
        shell.open();

        StartEndDialog dlg = new StartEndDialog(shell);
        if (dlg.open() == StartEndDialog.OK)
        {
            System.out.println("Start: '" + dlg.getStartSpecification() + "'");
            System.out.println("End: '" + dlg.getEndSpecification() + "'");
            ITimestamp start = TimestampFactory.fromCalendar(dlg.getStartCalendar());
            ITimestamp end = TimestampFactory.fromCalendar(dlg.getEndCalendar());
            System.out.println(start + " ... " + end);
        }

        display.dispose();
    }
    
    public static void main(String[] args)
    {
        new StartEndDialogTest().run();
    }
}
