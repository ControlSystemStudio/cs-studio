package org.csstudio.platform.simpledal;

public interface IValidationProcess {

	/**
	 * Cancels the validation process if it is still running (see {@link IValidationProcess#isDone()})
	 */
	public void cancel();

}
