package org.csstudio.nams.common.activatorUtils;

public class InitialisationFailedError extends Error {

	private static final long serialVersionUID = 9086473996492452347L;

	public InitialisationFailedError() {
		super();
	}

	public InitialisationFailedError(String message, Throwable t) {
		super(message, t);
	}

	public InitialisationFailedError(String message) {
		super(message);
	}

	public InitialisationFailedError(Throwable t) {
		super(t);
	}

}
