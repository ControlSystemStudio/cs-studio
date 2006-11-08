package org.csstudio.platform;

import org.csstudio.platform.internal.logging.CssLogListener;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * Common super class for all CSS plugin classes.
 * 
 * @author awill
 * 
 */
public abstract class AbstractCssPlugin extends Plugin {
	/**
	 * Log listener that catches log events and redirects them to the central
	 * log service.
	 */
	private ILogListener _logListener;

	/**
	 * Standard constructor.
	 */
	public AbstractCssPlugin() {
		_logListener = new CssLogListener();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void start(final BundleContext context) throws Exception {
		super.start(context);
		getLog().addLogListener(_logListener);
		doStart(context);
	}

	/**
	 * Hook method that is called from
	 * {@link AbstractCssPlugin#start(BundleContext)}.
	 * 
	 * @param context
	 *            the bundle context for this plug-in
	 * @exception Exception
	 *                if this plug-in did not start up properly
	 */
	protected abstract void doStart(final BundleContext context)
			throws Exception;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void stop(final BundleContext context) throws Exception {
		super.stop(context);
		savePluginPreferences();
		doStop(context);
		getLog().removeLogListener(_logListener);
	}

	/**
	 * Hook method that is called from
	 * {@link AbstractCssPlugin#stop(BundleContext)}.
	 * 
	 * @param context
	 *            the bundle context for this plug-in
	 * @exception Exception
	 *                if this method fails to shut down this plug-in
	 */
	protected abstract void doStop(final BundleContext context)
			throws Exception;
}
