package org.csstudio.trends.databrowser.ui;

import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

/** [Headless] JUnit Plug-in demo of the AddPVDialog
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class AddPVDialogTest
{
    final String names[] = new String[] { "Fred", "Jane" };
    final String axes[] = new String[] { "Value", "Value 2" };

    @Test
    public void pvTest()
    {
        final Shell shell = new Shell();
        final AddPVDialog dlg = new AddPVDialog(shell, names, axes, false);
        if (dlg.open() == AddPVDialog.OK)
        {
            System.out.println("Name  : " + dlg.getName());
            System.out.println("Period: " + dlg.getScanPeriod());
            System.out.println("Axis  : " + dlg.getAxis());
        }
    }

    @Test
    public void formulaTest()
    {
        final Shell shell = new Shell();
        final AddPVDialog dlg = new AddPVDialog(shell, names, axes, true);
        if (dlg.open() == AddPVDialog.OK)
        {
            System.out.println("Name  : " + dlg.getName());
            System.out.println("Axis  : " + dlg.getAxis());
        }
    }
}
