package org.csstudio.platform.simpledal;

import java.util.Arrays;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class ProcessVariableAddressValidationServiceTracker  {

	private ServiceTracker<IProcessVariableAddressValidationService, IProcessVariableAddressValidationService> serviceTracker;
	
	public ProcessVariableAddressValidationServiceTracker(BundleContext context) {
		serviceTracker = new ServiceTracker<IProcessVariableAddressValidationService, IProcessVariableAddressValidationService>(context, IProcessVariableAddressValidationService.class, null);
		serviceTracker.open();
	}
	
	public List<IProcessVariableAddressValidationService> getServices() {
		return Arrays.asList(serviceTracker.getServices(new IProcessVariableAddressValidationService[0]));
	}
	
	public void close() {
		serviceTracker.close();
	}
}
