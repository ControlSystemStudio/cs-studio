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
 * @author awill
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
				CentralLogger.getInstance().debug(null, message);
			} else {
				CentralLogger.getInstance().debug(null, message, throwable);
			}
			break;
		case IStatus.INFO:
			if (throwable != null) {
				CentralLogger.getInstance().info(null, message);
			} else {
				CentralLogger.getInstance().info(null, message, throwable);
			}
			break;
		case IStatus.ERROR:
			if (throwable != null) {
				CentralLogger.getInstance().error(null, message);
			} else {
				CentralLogger.getInstance().error(null, message, throwable);
			}
			break;
		case IStatus.WARNING:
			if (throwable != null) {
				CentralLogger.getInstance().warn(null, message);
			} else {
				CentralLogger.getInstance().warn(null, message, throwable);
			}
			break;
		case IStatus.CANCEL:
			if (throwable != null) {
				CentralLogger.getInstance().fatal(null, message);
			} else {
				CentralLogger.getInstance().fatal(null, message, throwable);
			}
			break;
		default:
			if (throwable != null) {
				CentralLogger.getInstance().fatal(null, message);
			} else {
				CentralLogger.getInstance().fatal(null, message, throwable);
			}
			break;
		}
	}
}
