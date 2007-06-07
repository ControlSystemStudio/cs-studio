package org.csstudio.alarm.treeView;

import org.csstudio.platform.ui.AbstractCssUiPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class of the LdapTree-Plug-In. This manages the plug-in's
 * lifecycle.
 */
public class AlarmTreePlugin extends AbstractCssUiPlugin {

	/**
	 * The plug-in id.
	 */
	public static final String PLUGIN_ID = "org.csstudio.alarm.treeView";
	
	/**
	 * Shared instance of this class.
	 */
	private static AlarmTreePlugin plugin;
	
	/**
	 * Returns the shared instance.
	 */
	public static AlarmTreePlugin getDefault() {
		return plugin;
	}

	/**
	 * The constructor.
	 */
	public AlarmTreePlugin() {
		plugin = this;
	}
	
	@Override
	protected void doStart(BundleContext context) throws Exception {
		// do nothing
	}

	@Override
	protected void doStop(BundleContext context) throws Exception {
		plugin = null;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	/**
	 * Return this plug-in's id.
	 */
	@Override
	public String getPluginId() {
		return PLUGIN_ID;
	}
	
}
