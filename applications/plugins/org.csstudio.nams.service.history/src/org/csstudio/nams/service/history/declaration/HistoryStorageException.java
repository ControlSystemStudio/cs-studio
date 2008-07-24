package org.csstudio.nams.service.history.declaration;

public class HistoryStorageException extends RuntimeException {
	private static final long serialVersionUID = 1835377642781124825L;

	public HistoryStorageException() {
		super();
	}

	public HistoryStorageException(String message, Throwable cause) {
		super(message, cause);
	}

	public HistoryStorageException(String message) {
		super(message);
	}

	public HistoryStorageException(Throwable cause) {
		super(cause);
	}
}
