package org.csstudio.workspace.ui;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/** Demo of TimestampWidget.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ChooseWorkspaceDialogTest
{
    public void run()
    {
        final Display display = new Display();
        final Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());
        

        shell.pack();
        shell.open();

        final String root = System.getProperty("user.home");
        final ChooseWorkspaceDialog dlg =
            new ChooseWorkspaceDialog(shell, "SNS CSS", root);
        dlg.open();

        display.dispose();
    }
    
    public static void main(String[] args)
    {
        new ChooseWorkspaceDialogTest().run();
    }
}
