package org.csstudio.display.pace.gui;

import java.io.FileInputStream;

import org.csstudio.display.pace.model.Model;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

/** Standalone GUI test, run as headless JUnit plugin test.
 *  Runs as headless application.
 *  For PV connections to work, use junit_customization.ini
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class GUITest
{
    @Test
    public void testGUI() throws Exception
    {
        final Display display = Display.getDefault();
        final Shell shell = new Shell(display);
        shell.setBounds(10, 100, 800, 600);
   
        final Model model =
            new Model(new FileInputStream("configFiles/rf_admin.pace"));
        final GUI gui = new GUI(shell, model, null);
        model.start();
        
        shell.open();
        while (!shell.isDisposed())
            if (!display.readAndDispatch())
                display.sleep();
        model.stop();
    }
}
