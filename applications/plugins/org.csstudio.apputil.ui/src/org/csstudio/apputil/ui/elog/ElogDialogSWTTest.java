package org.csstudio.apputil.ui.elog;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

/** JUnit Plug-in test for elog dialog.
 *  <p>
 *  Requires logbook implementation to get available logbooks.
 *  Will not make actual entry; just meant to check the GUI.
 *  @author Kay Kasemir
 */
public class ElogDialogSWTTest
{
    @SuppressWarnings("nls")
    @Test
    public void testElogDialog() throws Exception
    {
        final Display display = Display.getDefault();
        final Shell shell = new Shell(display);
        shell.setBounds(100, 100, 800, 500);
        // Without setting a layout, you see nothing at all!
        shell.setLayout(new FillLayout());

        ElogDialog dialog =
            new ElogDialog(shell,
                    "Create a test entry", "Test Entry", "This is a test")
        {
            @Override
            public void makeElogEntry(String logbook_name, String user, String password,
                    String title, String body) throws Exception
            {
                if ("fred".equals(user))
                    throw new Exception ("Try again");
            }
        };
        dialog.open();
    }
}
