
package org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions;

/**
 * Indicates an access exception to configuration-database or missing
 * mapping-configuration.
 */
public class StorageException extends Exception {

	private static final long serialVersionUID = -1541451576222179297L;

	public StorageException(final String message) {
		super(message);
	}

	public StorageException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
