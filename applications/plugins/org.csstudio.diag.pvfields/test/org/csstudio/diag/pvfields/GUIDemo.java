package org.csstudio.diag.pvfields;

import org.csstudio.diag.pvfields.gui.GUI;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
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
		final Shell shell = new Shell(display);
		shell.setSize(600, 400);
		
		new GUI(shell, null);

		shell.open();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
	}
}
