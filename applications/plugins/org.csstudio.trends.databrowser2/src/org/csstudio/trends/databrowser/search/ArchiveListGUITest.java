package org.csstudio.trends.databrowser.search;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

/** JUnit Plug-In test of the ArchiveListGUI
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ArchiveListGUITest
{
    @Test
    public void testArchiveGUI() throws Exception
    {
        final Display display = Display.getDefault();

        final Shell shell = new Shell();
        shell.setSize(600, 500);
        
        new ArchiveListGUI(shell)
        {
            @Override
            protected void handleArchiveUpdate()
            {
                System.out.println("Received archive list");
            }

            @Override
            protected void handleServerError(String url, Exception ex)
            {
                ex.printStackTrace();
            }
        };
        
        shell.open();
        
        while (!shell.isDisposed())
        {
          if (!display.readAndDispatch())
            display.sleep();
        }
    }
}
