package org.csstudio.utility.logsender;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/** main() routine for testing as standalone SWT app.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class StandaloneTest
{
    /** Initial window size */
    private static final int WIDTHS = 1000, HEIGHT = 800;


    @SuppressWarnings("nls")
    public static void main(String[] args) throws Exception
    {
        final Display display = new Display();
        final Shell shell = new Shell(display);
        final Rectangle screen = display.getBounds();
        shell.setBounds((screen.width-WIDTHS)/2,
                (screen.height-HEIGHT)/2, WIDTHS, HEIGHT);
        
        new GUI(shell);

        shell.open();
        
        // Message loop left to the application
        while (!shell.isDisposed())
            if (!display.readAndDispatch())
                display.sleep();
        display.dispose(); // !
    }
}
