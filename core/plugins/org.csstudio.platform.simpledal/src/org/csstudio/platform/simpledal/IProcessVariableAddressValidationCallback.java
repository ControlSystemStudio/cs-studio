package org.csstudio.platform.simpledal;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;

public interface IProcessVariableAddressValidationCallback {
	
	public enum ValidationResult {
		VALID, INVALID, ARCHIVED, VALIDATION_ERROR;
	}

	/**
	 * Called after validating an {@link IProcessVariableAddress}
	 * @param pvAddress the validated {@link IProcessVariableAddress}
	 * @param validationResult the {@link ValidationResult}
	 * @param comment a comment describing the {@link ValidationResult}, or <code>null</code>
	 */
	public void onValidate(IProcessVariableAddress pvAddress, ValidationResult validationResult, String comment);
	
}
