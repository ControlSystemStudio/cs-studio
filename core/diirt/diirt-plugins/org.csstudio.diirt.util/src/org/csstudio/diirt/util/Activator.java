package org.csstudio.diirt.util;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


public class Activator implements BundleActivator {

	public static final String ID = "org.csstudio.diirt.util";

	private static final Logger log = Logger.getLogger(ID);
	
	@Override
	public void start(BundleContext context) throws Exception {
		IPreferencesService prefs = Platform.getPreferencesService();
		String diirtHome = prefs.getString(ID, "diirt.home", "default", null);
		log.info("Setting diirt.home to pref:"+ diirtHome);
		System.setProperty("diirt.home", diirtHome);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		
	}

}
