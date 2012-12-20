package org.csstudio.swt.widgets.util;

import java.io.InputStream;

/**A runnable that able to inject inputstream to the task,
 * so the method {@link #setInputStream(InputStream)} must be called before
 * scheduling this task. Subclass should only implement {@link #runWithInputStream(InputStream)}.
 * @author Xihui Chen
 *
 */
public abstract class AbstractInputStreamRunnable implements Runnable {
	
	private InputStream inputStream;
	
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	/**The task to be executed.
	 * @param inputStream the injected inputstream.
	 */
	public abstract void runWithInputStream(final InputStream inputStream); 

	public void run() {
		runWithInputStream(inputStream);
	}

}
