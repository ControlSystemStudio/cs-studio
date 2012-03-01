package org.csstudio.sns.mpsbypasses.ui;

import org.csstudio.sns.mpsbypasses.model.BypassModel;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

/** [Headless] JUnit Plug-in demo of the {@link GUI}
 *
 *  @author Kay Kasemir
 */
public class GUIHeadlessDemo
{
	@Test
	public void demoGUI() throws Exception
	{
		// SWT Setup
		final Display display = Display.getDefault();
        Shell shell = new Shell(display);
        shell.setLayout(new GridLayout(1, false));

        // Bypass model and GUI
		final BypassModel model = new BypassModel();
		final GUI gui = new GUI(shell, model, null);
        gui.selectMachineMode();

		// SWT main loop
        shell.setSize(800, 600);
        shell.open();
        while (!shell.isDisposed())
        {
	        if (!display.readAndDispatch())
	        	display.sleep();
        }
        display.dispose();
	}
}
