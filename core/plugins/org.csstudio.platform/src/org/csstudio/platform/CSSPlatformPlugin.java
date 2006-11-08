package org.csstudio.platform;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * The activator for the platform plugin.
 * 
 * @author awill, swende
 */
public class CSSPlatformPlugin extends AbstractCssPlugin {
	/**
	 * The shared instance of this _plugin class.
	 */
	private static CSSPlatformPlugin _plugin;

	/**
	 * This _plugin's ID.
	 */
	public static final String ID = "org.csstudio.platform"; //$NON-NLS-1$

	/**
	 * Standard constructor.
	 */
	public CSSPlatformPlugin() {
		_plugin = this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void doStart(final BundleContext context) throws Exception {
		// generate a sample eclipse log message...
		getLog()
				.log(
						new Status(IStatus.INFO, ID, 0,
								"CSS core plugin started", null)); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void doStop(final BundleContext context) throws Exception {
		// generate a sample eclipse log message...
		getLog()
				.log(
						new Status(IStatus.INFO, ID, 0,
								"CSS core plugin stopped", null)); //$NON-NLS-1$
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return Return the shared instance.
	 */
	public static CSSPlatformPlugin getDefault() {
		return _plugin;
	}

}
