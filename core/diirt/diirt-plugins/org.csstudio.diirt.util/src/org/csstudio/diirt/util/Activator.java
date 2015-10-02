package org.csstudio.diirt.util;

import java.util.logging.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    public static final String ID = "org.csstudio.diirt.util";

    private static final Logger log = Logger.getLogger(ID);

    @Override
    public void start(BundleContext context) throws Exception {
        log.info("Starting diirt.util");
    }

    @Override
    public void stop(BundleContext context) throws Exception {

    }

}
