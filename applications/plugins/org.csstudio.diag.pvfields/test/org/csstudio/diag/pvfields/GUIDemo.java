package org.csstudio.diag.pvfields;

import java.util.concurrent.Executor;

import org.csstudio.diag.pvfields.gui.GUI;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.epics.pvmanager.PVManager;
import org.junit.Before;
import org.junit.Test;

public class GUIDemo
{  
	@Before
    public void setup() throws Exception
    {
        TestSetup.setup();
    }

	@Test
	public void demoGUI()
	{
		final Display display = Display.getDefault();
		
		// In plugin environment, PVManager defaults to SWT thread.
		// Emulate that in demo
		PVManager.setDefaultNotificationExecutor(new Executor()
		{
			@Override
			public void execute(final Runnable runnable)
			{
				display.asyncExec(runnable);
			}
		});
		
		final Shell shell = new Shell(display);
		shell.setSize(600, 400);
		
		final GUI gui = new GUI(shell, null, null);
		gui.setFocus();
		gui.setPVName(TestSetup.CHANNEL_NAME);

		shell.open();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
}
