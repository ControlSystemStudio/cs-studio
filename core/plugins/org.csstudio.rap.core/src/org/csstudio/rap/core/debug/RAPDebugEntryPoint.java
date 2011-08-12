package org.csstudio.rap.core.debug;

import org.csstudio.rap.core.DisplayManager;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Then entry point for debug info display.
 * 
 * @author Xihui Chen
 * 
 */
public class RAPDebugEntryPoint implements IEntryPoint {

	@Override
	public int createUI() {
		final Display display = new Display();
		try {
			Shell shell = new Shell(display, SWT.TITLE | SWT.MAX | SWT.RESIZE
					| SWT.NO_TRIM);
			DisplayManager.getInstance().registerDisplay(display, true);
			shell.setMaximized(true);

			shell.setText("CSS RAP Debug");
			shell.setLayout(new FillLayout());
			final Text text = new Text(shell, SWT.READ_ONLY | SWT.MULTI);
			text.setText(DisplayManager.getInstance().getDebugInfo());
			shell.layout();
			shell.open();
			display.timerExec(1000, new Runnable() {

				@Override
				public void run() {
					text.setText(DisplayManager.getInstance().getDebugInfo());
					display.timerExec(1000, this);
				}
			});

			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} finally {
			display.dispose();
		}
		return 0;
	}
}
