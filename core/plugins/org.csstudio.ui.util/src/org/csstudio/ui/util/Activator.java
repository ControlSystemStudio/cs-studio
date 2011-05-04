package org.csstudio.ui.util;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator extends Plugin implements BundleActivator {

	// The shared instance
	private static Activator plugin;
	
	private BundleContext context;
    
	/**
	 * The constructor
	 */
	public Activator() {
		plugin = this;
	}
	
	@Override
    public void start(BundleContext context) throws Exception
    {
	    super.start(context);
		setPlugin(this);
		this.context = context;
    }

	@Override
    public void stop(BundleContext context) throws Exception
    {
		setPlugin(this);	
	    super.stop(context);
    }
	
	public BundleContext getContext() {
		return context;
	}

	/** Static setter to avoid FindBugs warning */
	private static void setPlugin(final Activator the_plugin)
	{
		plugin = the_plugin;
	}

	/** @eturn The shared instance. */
	public static Activator getDefault()
    {
		return plugin;
	}
}
