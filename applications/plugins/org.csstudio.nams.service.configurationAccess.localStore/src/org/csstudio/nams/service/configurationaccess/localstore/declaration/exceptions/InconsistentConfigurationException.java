
package org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions;

/**
 * Indicates an inconsistent, not usable configuration in current database.
 */
public class InconsistentConfigurationException extends Exception {

	private static final long serialVersionUID = 6336894102492628330L;

	public InconsistentConfigurationException(final String message) {
		super(message);
	}

	public InconsistentConfigurationException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
