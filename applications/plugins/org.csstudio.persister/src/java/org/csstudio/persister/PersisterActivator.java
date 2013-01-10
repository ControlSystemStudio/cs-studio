package org.csstudio.persister;

import javax.annotation.Nonnull;

import org.csstudio.persister.declaration.IPersistenceService;
import org.csstudio.persister.internal.PersistenceService;
import org.csstudio.servicelocator.ServiceLocatorFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class PersisterActivator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	@Override
    public void start(@Nonnull final BundleContext bundleContext) throws Exception {
		PersisterActivator.context = bundleContext;
		
        ServiceLocatorFactory.registerServiceWithTracker("", bundleContext, IPersistenceService.class, new PersistenceService());
	}

	@Override
	public void stop(@Nonnull final BundleContext bundleContext) throws Exception {
		PersisterActivator.context = null;
	}

}
