package org.csstudio.trends.databrowser.model.formula_gui;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/** SWT app for testing the Formula dialog.
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

        FormulaDialog dialog = new FormulaDialog(shell);
        int result = dialog.open();
        System.out.println("Result: " + result); //$NON-NLS-1$
        
        // Display & Run
//        shell.open();
//        while (!shell.isDisposed())
//            if (!display.readAndDispatch())
//                display.sleep();
        // Shut down
        display.dispose(); // !
    }
}
