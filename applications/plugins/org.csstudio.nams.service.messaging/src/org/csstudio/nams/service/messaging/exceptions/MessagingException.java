package org.csstudio.nams.service.messaging.exceptions;


public class MessagingException extends Exception {
	private static final long serialVersionUID = -674587896666987902L;
	
	public MessagingException() {
		super();
	}

	public MessagingException(String message) {
		super(message);
	}

	public MessagingException(Throwable cause) {
		super(cause);
	}

	public MessagingException(String message, Throwable cause) {
		super(message, cause);
	}
}
