package org.csstudio.platform.utility.rdb;

import org.apache.log4j.Logger;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/** The activator class controls the plug-in life cycle */
public class Activator extends Plugin
{
	/** The plug-in ID */
	public static final String ID = "org.csstudio.utility.rdb"; //$NON-NLS-1$

    /** Lazily initialized Log4j Logger */
    private static Logger log = null;

	/** The shared instance */
	private static Activator plugin;
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	@Override
    public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
    public void stop(BundleContext context) throws Exception
	{
		plugin = null;
		super.stop(context);
	}

	/** @return the shared instance */
	public static Activator getDefault()
	{
		return plugin;
	}
	
    /** @return Log4j Logger */
    public static Logger getLogger()
    {
        if (log == null) // Also works with plugin==null during unit tests
            log = CentralLogger.getInstance().getLogger(plugin);
        return log;
    }
}
