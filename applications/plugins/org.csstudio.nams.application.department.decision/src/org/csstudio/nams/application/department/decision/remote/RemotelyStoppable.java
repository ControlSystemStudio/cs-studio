package org.csstudio.nams.application.department.decision.remote;

import org.csstudio.nams.service.logging.declaration.Logger;

/**
 * Something that runs and can be stopped by a remote call.
 */
public interface RemotelyStoppable {
	/**
	 * Stops this, whatever this is.
	 * 
	 * @param logger
	 *            The logger to log infos and errors to, not null.
	 */
	public void stopRemotely(Logger logger);
}
