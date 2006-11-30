package org.csstudio.trends.databrowser;

import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.trends.databrowser.ploteditor.BrowserUI;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/** SWT app for testing the controller.
 *  @author Kay Kasemir
 */
public class SWTTest
{
    public static void main(String[] args)
    {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setBounds(100, 100, 800, 500);
        // Without setting a layout, you see nothing at all!
        shell.setLayout(new FillLayout());

        Model model = new Model();
        BrowserUI ui = new BrowserUI(shell, 0);
        Controller controller = new Controller(model, ui);
        
        // Display & Run
        shell.open();
        while (!shell.isDisposed())
            if (!display.readAndDispatch())
                display.sleep();
        // Shut down
        controller.dispose();
        display.dispose(); // !
    }
}
