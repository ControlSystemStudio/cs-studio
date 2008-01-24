package org.csstudio.sds.util;

/**
 * Exception which might be thrown during the validation of channel names.
 * 
 * @author swende
 *
 */
public final class ChannelReferenceValidationException extends Exception {
	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -2945242750122658230L;

	/**
	 * Constructor.
	 * @param message the error message
	 */
	public ChannelReferenceValidationException(final String message) {
		super(message);
	}
	
}
