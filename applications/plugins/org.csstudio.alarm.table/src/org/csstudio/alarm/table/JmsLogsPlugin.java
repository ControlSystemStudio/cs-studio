package org.csstudio.alarm.table;



import org.eclipse.ui.plugin.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.BundleContext;



/**
 * The main plugin class to be used in the desktop.
 */
public class JmsLogsPlugin extends AbstractUIPlugin {

	//The shared instance.
	private static JmsLogsPlugin plugin;
	
//	private JMSMessageAdapterFactory jmsmAdapterFactory; 
	
	/**
	 * The constructor.
	 */
	public JmsLogsPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		System.out.println("log plugin");
//		this.getWorkbench().getViewRegistry();
//		jmsmAdapterFactory = new JMSMessageAdapterFactory();
//		IAdapterManager mgr = Platform.getAdapterManager();
//		mgr.registerAdapters(jmsmAdapterFactory, adaptable)
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static JmsLogsPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.csstudio.alarm.table", path);
	}
}
