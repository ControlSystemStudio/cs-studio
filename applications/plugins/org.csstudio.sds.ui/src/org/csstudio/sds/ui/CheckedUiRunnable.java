package org.csstudio.sds.ui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public abstract class CheckedUiRunnable {
	public CheckedUiRunnable() {
		run();
	}

	public void run() {
		Display display = Display.getCurrent();

		if (display == null) {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					doRunInUi();
				}
			});
		} else {
			doRunInUi();
		}
	}

	protected abstract void doRunInUi();
}
