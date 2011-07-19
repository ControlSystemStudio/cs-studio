
package org.csstudio.nams.common.activatorUtils;

public class InitialisationFailedError extends Error {

	private static final long serialVersionUID = 9086473996492452347L;

	public InitialisationFailedError() {
		super();
	}

	public InitialisationFailedError(final String message) {
		super(message);
	}

	public InitialisationFailedError(final String message, final Throwable t) {
		super(message, t);
	}

	public InitialisationFailedError(final Throwable t) {
		super(t);
	}
}
