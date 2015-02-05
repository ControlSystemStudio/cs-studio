package org.csstudio.platform.simpledal;

import java.util.List;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;

public interface IProcessVariableAddressValidationService {

	/**
	 * @return the name of the validation service implementation
	 */
	public String getServiceName();

	/**
	 * @return a description of the validation service
	 */
	public String getServiceDescription();
	
	/**
	 * Validates the given IProcessVariableAddresses
	 * @param pvAddresses the address objects to validate
	 * @param callback gets called for each validated pvAddress
	 * @return an IValidationProcess object that can be used to cancel the validation
	 */
	public IValidationProcess validateProcessVariableAddresses(List<IProcessVariableAddress> pvAddresses, IProcessVariableAddressValidationCallback callback);
	
}