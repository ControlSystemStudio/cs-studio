package org.csstudio.platform.simpledal;

public class ConnectionException extends Exception {

	private ConnectionException() {
		super();
	}

	public ConnectionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConnectionException(String message) {
		super(message);
	}

	public ConnectionException(Throwable cause) {
		super(cause);
	}

}
