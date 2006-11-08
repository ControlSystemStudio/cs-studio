package org.csstudio.platform.ui;

import org.csstudio.platform.CSSPlatformPlugin;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.BundleContext;

/**
 * The activator for the CSS platform UI plugin.
 * 
 * @author awill
 */
public class CSSPlatformUiPlugin extends AbstractUIPlugin {
	/**
	 * This _plugin's ID.
	 */
	public static final String ID = "org.csstudio.platform.ui"; //$NON-NLS-1$

	/**
	 * The shared instance of this plugin class.
	 */
	private static CSSPlatformUiPlugin _plugin;

	/**
	 * Standard constructor.
	 */
	public CSSPlatformUiPlugin() {
		_plugin = this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void start(final BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void stop(final BundleContext context) throws Exception {
		_plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return Return the shared instance.
	 */
	public static CSSPlatformUiPlugin getDefault() {
		return _plugin;
	}

	/**
	 * Return the preference store of the css core plugin.
	 * 
	 * @return The preference store of the css core plugin.
	 */
	public static IPreferenceStore getCorePreferenceStore() {
		return new ScopedPreferenceStore(new InstanceScope(), CSSPlatformPlugin
				.getDefault().getBundle().getSymbolicName());

	}
}
