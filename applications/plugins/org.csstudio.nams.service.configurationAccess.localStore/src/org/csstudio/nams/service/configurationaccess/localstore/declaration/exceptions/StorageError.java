
package org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions;

/**
 * Indicates an access-configuration-error or missing access-configuration or an
 * incomplete mapping configuration.
 */
public class StorageError extends Error {

	private static final long serialVersionUID = -8247564317748776482L;

	public StorageError(final String message) {
		super(message);
	}

	public StorageError(final String message, final Throwable cause) {
		super(message, cause);
	}
}
