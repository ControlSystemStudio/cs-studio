package org.csstudio.apputil.ui.dialog;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

/** JUnit demo of the Error Dialog
 *  @author Kay Kasemir
 */
public class ErrorDialogTest
{
    @Test
    @SuppressWarnings("nls")
    public void demoErrorDialog()
    {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setBounds(400, 100, 300, 350);
        GridLayout gl = new GridLayout();
        gl.numColumns = 1;
        shell.setLayout(gl);

        String title = "Error";
        String message = "There was an error:\nIt's a bad one.\nFirst, this happened.\n Then this: Some very long detail about the actual error with all types of stuff included";
//        MessageDialog.openError(shell, title, message);
        ErrorDialog.open(shell, title, message);
        
//        shell.open();
        // Message loop left to the application
//        while (!shell.isDisposed())
//            if (!display.readAndDispatch())
//                display.sleep();
        display.dispose(); // !
    }
}
