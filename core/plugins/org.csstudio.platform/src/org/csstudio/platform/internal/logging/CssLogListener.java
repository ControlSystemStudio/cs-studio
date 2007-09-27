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
package org.csstudio.platform.internal.logging;

import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;

/**
 * Log listener implementation that redirects eclipse log events to the css
 * central logging service. Thereby the severity of an eclipse
 * <code>IStatus</code> object is translated to log4j log levels according to
 * the following scheme:
 * 
 * <ul>
 * <li>IStatus.OK ==> DEBUG
 * <li>IStatus.INFO ==> INFO
 * <li>IStatus.WARNING ==> WARN
 * <li>IStatus.ERROR ==> ERROR
 * <li>IStatus.CALCEL ==> FATAL
 * <li>anything else ==> FATAL
 * </ul>
 * 
 * @author Alexander Will
 * 
 */
public class CssLogListener implements ILogListener {
	/**
	 * {@inheritDoc}
	 */
	public final void logging(final IStatus status, final String plugin) {
		Throwable throwable = status.getException();
		String message = status.getMessage();

		switch (status.getSeverity()) {
		case IStatus.OK:
			if (throwable != null) {
				CentralLogger.getInstance().debug(null, message, throwable);
			} else {
				CentralLogger.getInstance().debug(null, message);
			}
			break;
		case IStatus.INFO:
			if (throwable != null) {
				CentralLogger.getInstance().info(null, message, throwable);
			} else {
				CentralLogger.getInstance().info(null, message);
			}
			break;
		case IStatus.ERROR:
			if (throwable != null) {
				CentralLogger.getInstance().error(null, message, throwable);
			} else {
				CentralLogger.getInstance().error(null, message);
			}
			break;
		case IStatus.WARNING:
			if (throwable != null) {
				CentralLogger.getInstance().warn(null, message, throwable);
			} else {
				CentralLogger.getInstance().warn(null, message);
			}
			break;
		case IStatus.CANCEL:
			if (throwable != null) {
				CentralLogger.getInstance().fatal(null, message, throwable);
			} else {
				CentralLogger.getInstance().fatal(null, message);
			}
			break;
		default:
			if (throwable != null) {
				CentralLogger.getInstance().fatal(null, message, throwable);
			} else {
				CentralLogger.getInstance().fatal(null, message);
			}
			break;
		}
	}
}
