package org.csstudio.platform.ui.widgets;

import org.csstudio.platform.util.ITimestamp;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Demo of {@link TimestampWidget}.
 * 
 * @author Kay Kasemir
 */
public final class TimestampWidgetDemo implements TimestampWidgetListener {
	/**
	 * {@inheritDoc}
	 */
	public void updatedTimestamp(final TimestampWidget source,
			final ITimestamp stamp) {
		System.out
				.println("Time: " + stamp.format(ITimestamp.FMT_DATE_HH_MM_SS)); //$NON-NLS-1$
	}

	/**
	 * Runs the demo.
	 */
	public void run() {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());

		TimestampWidget w = new TimestampWidget(shell, 0);
		w.addListener(this);

		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		w.removeListener(this);
		display.dispose();
	}

	/**
	 * Main method to launch the demo.
	 * 
	 * @param args
	 *            command line arguments
	 */
	public static void main(final String[] args) {
		new TimestampWidgetDemo().run();
	}

}
