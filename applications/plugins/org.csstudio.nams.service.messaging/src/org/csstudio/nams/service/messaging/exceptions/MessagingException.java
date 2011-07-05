
package org.csstudio.nams.service.messaging.exceptions;

public class MessagingException extends Exception {
	private static final long serialVersionUID = -674587896666987902L;

	public MessagingException() {
		super();
	}

	public MessagingException(final String message) {
		super(message);
	}

	public MessagingException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public MessagingException(final Throwable cause) {
		super(cause);
	}
}
