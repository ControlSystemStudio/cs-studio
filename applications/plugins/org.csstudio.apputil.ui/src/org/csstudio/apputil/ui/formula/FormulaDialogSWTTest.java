package org.csstudio.apputil.ui.formula;

import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

/** JUnit test /demo of the Formula dialog.
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class FormulaDialogSWTTest
{
    @Test
    public void dialogDemo() throws Exception
    {
        final Shell shell = new Shell();

        final InputItem inputs[] = new InputItem[]
        {
            new InputItem("fred", "x"),
            new InputItem("freddy", "y"),
            new InputItem("jane", "z"),
            new InputItem("janet", "jj"),
        };

        final FormulaDialog dialog = new FormulaDialog(shell, inputs, "2*a + b");
        if (dialog.open() == FormulaDialog.OK)
        {
            System.out.println("Formula : " + dialog.getFormula());
            for (InputItem input : dialog.getInputs())
                System.out.println(input.getVariableName() + " = " + input.getInputName());
        }
        else
            System.out.println("Canceled");
    }
}
