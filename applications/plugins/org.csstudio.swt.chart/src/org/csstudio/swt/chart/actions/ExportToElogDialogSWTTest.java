package org.csstudio.swt.chart.actions;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/** SWT app for testing the Formula dialog.
 *  @author Kay Kasemir
 */
public class ExportToElogDialogSWTTest
{
    @SuppressWarnings("nls")
    public static void main(final String[] args) throws Exception
    {
        final Display display = new Display();
        final Shell shell = new Shell(display);
        shell.setBounds(100, 100, 800, 500);
        // Without setting a layout, you see nothing at all!
        shell.setLayout(new FillLayout());

        ExportToElogInfo info = new ExportToElogInfo(
                "user", "$user", "title", "For what it's worth...");
        ExportToElogDialog dialog = new ExportToElogDialog(shell, info);
        if (dialog.open() == ExportToElogDialog.OK)
        {
            info = dialog.getInfo();
            // Run another one with entered info
            dialog = new ExportToElogDialog(shell, info);
            dialog.open();
            info = dialog.getInfo();
            System.out.println(info);
        }

        // Shut down
        display.dispose(); // !
    }
}
