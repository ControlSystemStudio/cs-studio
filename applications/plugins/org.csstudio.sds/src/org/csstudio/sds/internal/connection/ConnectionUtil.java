/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.internal.connection;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * Utility class that holds the availabe connection services.
 * 
 * @author Sven Wende
 * @version $Revision$
 */
public final class ConnectionUtil {
	/**
	 * The singleton instance.
	 */
	private static ConnectionUtil _instance;

	/**
	 * A display instance.
	 */
	private Display _display;

	/**
	 * Private constructor, to prevent instantiation.
	 */
	private ConnectionUtil() {
		_display = Display.getCurrent();
	}

	/**
	 * Sets the current display instance.
	 * @param display the display
	 */
	public void setDisplay(final Display display) {
		_display = display;
	}

	
	/**
	 * @return The singleton instance.
	 */
	public static ConnectionUtil getInstance() {
		if (_instance == null) {
			_instance = new ConnectionUtil();
		}

		return _instance;
	}

	/**
	 * Executes the specified runnable synchroneously in the UI thread.
	 * 
	 * @param runnable
	 *            a runnable
	 * @return true, if the sync excec could be performed successfully.
	 */
	public boolean syncExec(final Runnable runnable) {
		boolean result = true;

		if (_display != null && !_display.isDisposed()
				&& !PlatformUI.getWorkbench().isClosing()) {
			_display.syncExec(runnable);
		} else {
			result = false;
		}

		return result;
	}
}
