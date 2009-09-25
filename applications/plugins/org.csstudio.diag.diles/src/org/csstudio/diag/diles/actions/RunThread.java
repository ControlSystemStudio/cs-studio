package org.csstudio.diag.diles.actions;

import org.eclipse.swt.widgets.Shell;

public class RunThread extends Thread {

	private long lastCheck;
	private static int miliseconds;

	private boolean wait = false;

	public static int getMiliseconds() {
		return miliseconds;
	}

	public static void setMiliseconds(int miliseconds) {
		RunThread.miliseconds = miliseconds;
	}

	private int defaultMiliseconds;
	private Shell shell;

	public RunThread(Shell s, long lfc, int ms) {
		shell = s;
		lastCheck = lfc;
		miliseconds = ms;
		defaultMiliseconds = ms;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		long now = System.currentTimeMillis();

		if (now - lastCheck < miliseconds) {
			// sleep a little while to avoid being a CPU hog
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// re-schedule
			if (!shell.getDisplay().isDisposed()) {
				shell.getDisplay().asyncExec(this);
			}
			return;
		}
		try {
			OneStepAction.nextStep();
		} finally {
			// update the timestamp variable to delay
			lastCheck = System.currentTimeMillis();

			// Checks, if user didn't click STOP button.
			if (AutoRunAction.getRun()) {
				shell.getDisplay().asyncExec(this);
			}

			if (miliseconds != defaultMiliseconds) {
				if (wait) {
					miliseconds = defaultMiliseconds;
					wait = false;
				}
				wait = true;
			}
		}

	}
}
