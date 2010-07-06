package org.csstudio.apputil.ui.formula;

import junit.framework.Assert;

import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

/**
 * JUnit test /demo of the Formula dialog.
 *
 * @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class FormulaDialogSWTTest {
    @Test
    public void dialogDemo() throws Exception {
        final Shell shell = new Shell();

        final InputItem inputs[] = new InputItem[] { new InputItem("fred", "x"),
                                                    new InputItem("freddy", "y"),
                                                    new InputItem("jane", "z"),
                                                    new InputItem("janet", "jj"), };

        final FormulaDialog dialog = new FormulaDialog(shell, "2*a + b", inputs);
        for (final InputItem input : dialog.getInputs()) {
            Assert.assertEquals(input.getVariableName(), input.getInputName());
        }

//        final Display display = shell.getDisplay();
//        display.asyncExec(new Runnable() {
//            @Override
//            public void run() {
//                if (dialog.open() == Window.OK) {
//                    for (final InputItem input : dialog.getInputs()) {
//                        Assert.assertEquals(input.getVariableName(), input.getInputName());
//                    }
//                } else {
//                    Assert.fail("Dialog cancelled?");
//                }
//            }
//        });
    }
}
