package org.csstudio.trends.databrowser.model.formula_gui;

import org.csstudio.swt.chart.TraceType;
import org.csstudio.trends.databrowser.model.FormulaModelItem;
import org.csstudio.trends.databrowser.model.Model;
import org.csstudio.trends.databrowser.model.ModelItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/** SWT app for testing the Formula dialog.
 *  @author Kay Kasemir
 */
public class FormulaDialogSWTTest
{
    @SuppressWarnings("nls")
    public static void main(String[] args) throws Exception
    {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setBounds(100, 100, 800, 500);
        // Without setting a layout, you see nothing at all!
        shell.setLayout(new FillLayout());

        
        ModelItem.test_mode = true;

        ModelItem.test_mode = true;
        
        Model model = new Model();
        
        model.add("fred");
        model.add("freddy");
        model.add("jane");
        model.add("janet");
        

        FormulaModelItem formula = new FormulaModelItem(model, "calc",
                        0, 0, 0, true, false, 0, 0, 0, 0,
                        TraceType.Lines, false);
        formula.addInput(model.getItem(0), "x");
        formula.addInput(model.getItem(3), "jj");
        formula.setFormula("1000*x + jj");
        
        FormulaDialog dialog = new FormulaDialog(shell, formula);
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
