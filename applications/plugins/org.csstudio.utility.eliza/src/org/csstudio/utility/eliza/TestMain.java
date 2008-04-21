package org.csstudio.utility.eliza;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/** Standalone SWT Test of ElizaGUI
 *  @author Kay Kasemir
 */
public class TestMain
{
    public static void main(String[] args)
    {
        final Display display = new Display();
        final Shell shell = new Shell(display);
        shell.setBounds(400, 100, 300, 350);
        
        new ElizaGUI(shell);

        shell.open();
        // Message loop left to the application
        while (!shell.isDisposed())
            if (!display.readAndDispatch())
                display.sleep();
        display.dispose(); // !
    }
}
