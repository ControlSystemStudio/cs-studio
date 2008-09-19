package org.csstudio.archive.engine;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/** Plugin Activator
 *  @author Kay Kasemir
 */
public class Activator extends Plugin
{
	/** Plug-in ID defined in MANIFEST.MF */
	public static final String ID = "org.csstudio.archive.engine"; //$NON-NLS-1$

	/** The shared instance */
	private static Activator plugin;
	
	/** {@inheritDoc} */
	@Override
    public void start(BundleContext context) throws Exception
	{
		super.start(context);		
		plugin = this;
	}

	/** @return the shared instance */
	public static Activator getDefault()
	{
		return plugin;
	}
}
