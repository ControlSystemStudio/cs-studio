package org.csstudio.util.swt;

import org.csstudio.platform.util.ITimestamp;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/** Demo of TimestampWidget.
 *  @author Kay Kasemir
 */
public class TimestampWidgetTest implements TimestampWidgetListener
{
    public void updatedTimestamp(TimestampWidget source, ITimestamp stamp)
    {
        System.out.println("Time: " + stamp.format(ITimestamp.FMT_DATE_HH_MM_SS)); //$NON-NLS-1$
    }

    public void run()
    {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());
        
        TimestampWidget w = new TimestampWidget(shell, 0);
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
        new TimestampWidgetTest().run();
    }

}
