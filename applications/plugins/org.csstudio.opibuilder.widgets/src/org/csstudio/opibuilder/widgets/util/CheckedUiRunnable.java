package org.csstudio.opibuilder.widgets.util;

import org.csstudio.opibuilder.util.UIBundlingThread;


/**The runnable that can start its running during constructing.
 * @author Xihui Chen
 *
 */
public abstract class CheckedUiRunnable implements Runnable {
	public CheckedUiRunnable() {
		UIBundlingThread.getInstance().addRunnable(this);
	}

	public void run() {
		doRunInUi();
	}

	protected abstract void doRunInUi();
}
