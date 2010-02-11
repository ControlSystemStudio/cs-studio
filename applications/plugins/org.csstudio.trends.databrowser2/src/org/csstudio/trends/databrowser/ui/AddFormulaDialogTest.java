package org.csstudio.trends.databrowser.ui;

import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

/** [Headless] JUnit Plug-in demo of the AddPVDialog
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class AddFormulaDialogTest
{
    @Test
    public void dialogTest()
    {
        final Shell shell = new Shell();
        final String names[] = new String[] { "Fred", "Jane" };
        final AddFormulaDialog dlg = new AddFormulaDialog(shell, names);
        if (dlg.open() == AddFormulaDialog.OK)
            System.out.println("Name  : " + dlg.getName());
    }
}
