package org.csstudio.archive;

import org.csstudio.platform.AbstractCssPlugin;
import org.osgi.framework.BundleContext;

/** The activator class controls the plug-in life cycle
 *  @author Jan Hatje
 *  @author Albert Kagarmanov
 *  @author Kay Kasemir
 */
public class Activator extends AbstractCssPlugin
{
	/** The plug-in ID */
	public static final String ID = "org.csstudio.archive"; //$NON-NLS-1$

	/** The shared instance */
	private static Activator plugin;
	
	/** The constructor */
	public Activator()
    {	plugin = this;	}

    /* @see org.csstudio.platform.AbstractCssPlugin#getPluginId() */
    @Override
    public String getPluginId()
    {   return ID;  }

	/* @see org.csstudio.platform.AbstractCssPlugin#doStart(org.osgi.framework.BundleContext)
     */
    @Override
    protected void doStart(BundleContext context) throws Exception
    {}

    /* @see org.csstudio.platform.AbstractCssPlugin#doStop(org.osgi.framework.BundleContext)
     */
    @Override
    protected void doStop(BundleContext context) throws Exception
    {
        plugin = null;
        super.stop(context);
    }

	/** @return shared instance */
	public static Activator getDefault()
    {	return plugin;	}
}
