package org.csstudio.remote.internal;

import javax.annotation.Nonnull;

import org.csstudio.remote.jms.command.IRemoteCommandService;
import org.csstudio.remote.jms.command.JmsRemoteCommandService;
import org.csstudio.servicelocator.ServiceLocatorFactory;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class RemoteActivator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.remote"; //$NON-NLS-1$

	// The shared instance
	private static RemoteActivator plugin;

	/**
	 * The constructor
	 */
	public RemoteActivator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
        registerRemoteCommandService(context);
        
        // TODO (jp, 2012-10-09) remote uses o.c.utility.jms but does not initialize it properly
        // you will notice this when o.c.remote is used without starting o.c.alarm.service - you will get NPE in o.c.utility.jms
        // see activator of o.c.alarm.service for details
    }
    
    private void registerRemoteCommandService(@Nonnull final BundleContext bundleContext) {
        ServiceLocatorFactory.registerServiceWithTracker("Remote command service implementation",
                                                         bundleContext,
                                                         IRemoteCommandService.class,
                                                         new JmsRemoteCommandService());
    }

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static RemoteActivator getDefault() {
		return plugin;
	}

}
