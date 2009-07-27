package org.csstudio.opibuilder.widgets.util;

import org.csstudio.opibuilder.util.UIBundlingThread;


public abstract class CheckedUiRunnable implements Runnable {
	public CheckedUiRunnable() {
		UIBundlingThread.getInstance().addRunnable(this);
	}

	public void run() {
		doRunInUi();
	}

	protected abstract void doRunInUi();
}
