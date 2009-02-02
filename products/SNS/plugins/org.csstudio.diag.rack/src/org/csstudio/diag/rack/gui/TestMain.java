package org.csstudio.diag.rack.gui;

import org.csstudio.diag.rack.model.RackControl;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

/** Standalone "main" test of the Model and the GUI */
public class TestMain
{
    //private static final String URL = "jdbc:oracle:thin:sns_reports/sns@//snsdev3.sns.ornl.gov:1521/devl";
    private static final String URL = "jdbc:oracle:thin:sns_reports/sns@//snsdb1.sns.ornl.gov/prod";

    @Test
    public void test() throws Exception
    {
        // Initialize SWT
        Display display = Display.getCurrent();
        Shell shell = new Shell(display);
        shell.setText("Real Rack Profile Utility");

        
        RackControl control = new RackControl();
        new GUI(shell, control);

        shell.pack();
        shell.open();
        
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }
}
