package org.csstudio.platform.libs.dal.simulator;

import org.epics.css.dal.context.AbstractApplicationContext;
import org.epics.css.dal.simulation.PropertyFactoryImpl;
import org.epics.css.dal.spi.LinkPolicy;
import org.epics.css.dal.spi.PropertyFactory;
import org.epics.css.dal.spi.PropertyFactoryService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator, PropertyFactoryService {

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
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}
	
	public PropertyFactory getPropertyFactory(AbstractApplicationContext ctx,
			LinkPolicy linkPolicy) {
		
		PropertyFactoryImpl pf= new PropertyFactoryImpl();
		pf.initialize(ctx, linkPolicy);
		return pf;
	}
	
	public PropertyFactory getPropertyFactory(AbstractApplicationContext ctx,
			LinkPolicy linkPolicy, String plugName) {
		// not required
		return null;
	}

}
