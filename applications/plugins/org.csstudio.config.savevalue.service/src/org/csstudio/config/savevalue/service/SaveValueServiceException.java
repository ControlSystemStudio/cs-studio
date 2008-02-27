package org.csstudio.config.savevalue.service;

/**
 * Thrown when a save value service call fails.
 * 
 * @author Joerg Rathlev
 */
public class SaveValueServiceException extends Exception {
	
	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -9045328319220622957L;

	/**
	 * Creates a new save value service exception.
	 * @param message error message.
	 * @param cause the cause.
	 */
	public SaveValueServiceException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
