package org.csstudio.platform.simpledal;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;

public interface IProcessVariableAddressValidationCallback {
	
	public enum ValidationResult {
		VALID(4), INVALID(2), ARCHIVED(3), VALIDATION_ERROR(1);
		
		private final int comparisonIndex;
		
		private ValidationResult(int comparisonIndex) {
			this.comparisonIndex = comparisonIndex;
			
		}
		public int compare(ValidationResult other) {
			return this.comparisonIndex - other.comparisonIndex;
		}
	}

	/**
	 * Called after validating an {@link IProcessVariableAddress}
	 * @param pvAddress the validated {@link IProcessVariableAddress}
	 * @param validationResult the {@link ValidationResult}
	 * @param comment a comment describing the {@link ValidationResult}, or <code>null</code>
	 */
	public void onValidate(IProcessVariableAddress pvAddress, ValidationResult validationResult, String comment);
	
}
