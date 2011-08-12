
package org.csstudio.nams.service.history.declaration;

public class HistoryStorageException extends RuntimeException {
	private static final long serialVersionUID = 1835377642781124825L;

	public HistoryStorageException() {
		super();
	}

	public HistoryStorageException(final String message) {
		super(message);
	}

	public HistoryStorageException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public HistoryStorageException(final Throwable cause) {
		super(cause);
	}
}
