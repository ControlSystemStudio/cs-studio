package org.csstudio.platforms.libs.channelfinderapi;

import java.io.InputStream;
import java.util.Arrays;

import gov.bnl.channelfinder.api.ChannelFinderClient;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.platforms.libs.channelfinderAPI";

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		System.out.println("start");
//		System.out.println(ChannelFinderClient.class.getResource(
//				"/config/channelfinder.properties").getPath());
//		InputStream stream = ChannelFinderClient.class.getResource(
//				"/config/truststore.jks").openConnection().getInputStream();
//		byte[] buffer = new byte[1024];
//		stream.read(buffer);
//		System.out.println(Arrays.toString(buffer));
//		try {
//			System.out.println(ChannelFinderClient.getInstance()
//					.retrieveChannels().getChannels().size());
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
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
	public static Activator getDefault() {
		return plugin;
	}

}
