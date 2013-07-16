package org.csstudio.iter.css.product;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.iter.css.product.preferences.Preferences;
import org.csstudio.iter.css.product.util.WorkbenchUtil;
import org.eclipse.ui.IStartup;

public class EarlyStartup implements IStartup {

	@Override
	public void earlyStartup() {
		// Set upper log level on too verbose packages
		Level verboseLogLevel;
		try {
			verboseLogLevel = Preferences.getVerboseLogLevel();
		} catch (Exception e) {
			verboseLogLevel = Level.SEVERE;
		}
		Logger.getLogger("com.sun.jersey.core.spi.component").setLevel(
				verboseLogLevel);
		Logger.getLogger("com.sun.jersey.spi.service.ServiceFinder").setLevel(
				verboseLogLevel);

		// Remove Perspectives coming with XML Editor
		WorkbenchUtil.removeUnWantedPerspectives();
	}

}
