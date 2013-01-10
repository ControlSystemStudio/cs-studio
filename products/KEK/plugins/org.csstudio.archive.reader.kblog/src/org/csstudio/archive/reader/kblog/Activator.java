package org.csstudio.archive.reader.kblog;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Plugin activator
 * 
 * @author Takashi Nakamoto
  */
public class Activator implements BundleActivator {
	
	/** Plugin ID defined in MANIFEST.MF */
	final public static String ID = "org.csstudio.archive.reader.kblog";
	
	private static Activator instance;
	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		Activator.instance = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

	/**
	 * @return Singleton instance
	 */
	public static Activator getInstance()
	{
		return Activator.instance;
	}
}
