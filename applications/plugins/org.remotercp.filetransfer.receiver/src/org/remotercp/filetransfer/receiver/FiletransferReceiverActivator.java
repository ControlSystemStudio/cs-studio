package org.remotercp.filetransfer.receiver;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ecf.filetransfer.ISendFileTransferContainerAdapter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.remotercp.ecf.session.ISessionService;
import org.remotercp.util.osgi.OsgiServiceLocatorUtil;

/**
 * The activator class controls the plug-in life cycle
 */
public class FiletransferReceiverActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.remotercp.filetransfer.receiver";

	// The shared instance
	private static FiletransferReceiverActivator plugin;

	private static BundleContext bundlecontext;

	/**
	 * The constructor
	 */
	public FiletransferReceiverActivator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		bundlecontext = context;

		this.registerFiletransferListener();
	}

	private void registerFiletransferListener() {
//		ISessionService service = OsgiServiceLocatorUtil.getOSGiService(
//				getBundleContext(), ISessionService.class);
//		Assert.isNotNull(service);
//
//		ISendFileTransferContainerAdapter adapter = (ISendFileTransferContainerAdapter) service
//				.getContainer().getAdapter(
//						ISendFileTransferContainerAdapter.class);
//		Assert.isNotNull(adapter);
//
//		adapter.addListener(new IncomingFiletransferReceiver());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static FiletransferReceiverActivator getDefault() {
		return plugin;
	}

	public static BundleContext getBundleContext() {
		return bundlecontext;
	}

	public static ImageDescriptor getImageDescriptor(String imageFilePath) {
		return imageDescriptorFromPlugin(PLUGIN_ID, imageFilePath);
	}
}
