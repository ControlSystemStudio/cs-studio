package org.csstudio.nams.service.configurationaccess.localstore.declaration;

/**
 * Indicates an unknown or missing configuration to load from or store to
 * database.
 */
public class UnknownConfigurationElementError extends Exception {

	private static final long serialVersionUID = 1296085075276490743L;

	public UnknownConfigurationElementError(String message) {
		super(message);
	}

	public UnknownConfigurationElementError(String message, Throwable cause) {
		super(message, cause);
	}
}
