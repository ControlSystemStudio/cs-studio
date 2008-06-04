package org.csstudio.nams.service.configurationaccess.localstore.declaration;

/**
 * Indicates an inconsistent, not usable configuration in current database.
 */
public class InconsistentConfiguration extends Exception {
	
	private static final long serialVersionUID = 6336894102492628330L;

	public InconsistentConfiguration(String message) {
		super(message);
	}

	public InconsistentConfiguration(String message, Throwable cause) {
		super(message, cause);
	}
}
