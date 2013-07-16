package org.csstudio.askap.jms2email;

import java.util.logging.Logger;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class Activator extends Plugin {
    /** The plug-in ID defined in MANIFEST.MF */
    final public static String ID = "org.csstudio.askap.jms2email";

    final private static Logger logger = Logger.getLogger(ID);
    
    /** Singleton instance */
    private static Activator instance;

    /** {@inheritDoc} */
    @Override
	public void start(BundleContext bundleContext) throws Exception {
        super.start(bundleContext);
        instance = this;
	}

    /** {@inheritDoc} */
    @Override
	public void stop(BundleContext bundleContext) throws Exception {
        instance = null;
        super.stop(bundleContext);
	}

	public static Logger getLogger() {
		return logger;
	}

	public static Plugin getInstance() {
		return instance;
	}

}
