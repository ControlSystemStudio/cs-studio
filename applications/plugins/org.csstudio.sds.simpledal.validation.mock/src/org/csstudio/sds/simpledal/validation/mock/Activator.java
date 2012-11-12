package org.csstudio.sds.simpledal.validation.mock;

import org.csstudio.platform.simpledal.IProcessVariableAddressValidationService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		bundleContext.registerService(IProcessVariableAddressValidationService.class, new ProcessVariableAddressValidationMock("Configuration (Mock)"), null);
		bundleContext.registerService(IProcessVariableAddressValidationService.class, new ProcessVariableAddressValidationMock("Connection (Mock)"), null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
