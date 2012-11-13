package org.csstudio.sds.service.pvvalidation.mkk;

import org.csstudio.platform.simpledal.IProcessVariableAddressValidationService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	private ServiceRegistration<IProcessVariableAddressValidationService> _mkkPvServiceRegistration;

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		MkkPvValidationService mkkPvService = new MkkPvValidationService();
		_mkkPvServiceRegistration = bundleContext.registerService(IProcessVariableAddressValidationService.class, mkkPvService, null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		_mkkPvServiceRegistration.unregister();
		Activator.context = null;
	}

}
