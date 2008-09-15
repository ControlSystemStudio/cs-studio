package org.csstudio.swt.chart.actions;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/** SWT app for testing the elog dialog.
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

        final String logbooks[] = new String[]
        { "Main", "Some Logbook", "other" };
        ExportToElogDialog dialog = new ExportToElogDialog(shell, "Test", logbooks, "Main")
        {
            @Override
            void makeElogEntry(String logbook_name, String user, String password,
                    String title, String body) throws Exception
            {
                if ("fred".equals(user))
                    throw new Exception ("Try again");
            }
        };
        dialog.open();

        // Shut down
        display.dispose(); // !
    }
}
