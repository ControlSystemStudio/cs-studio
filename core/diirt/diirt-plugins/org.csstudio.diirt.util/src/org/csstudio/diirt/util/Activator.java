package org.csstudio.diirt.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.osgi.service.datalocation.Location;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    public static final String ID = "org.csstudio.diirt.util";

    private static final String PLATFORM_URI_PREFIX = "platform:";

    private static final Logger log = Logger.getLogger(ID);

    @Override
    public void start(BundleContext context) throws Exception {
        log.info("Starting diirt.util");
    }

    @Override
    public void stop(BundleContext context) throws Exception {

    }

}
