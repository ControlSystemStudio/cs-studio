package org.csstudio.iter.css.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.iter.css.product.preferences.Preferences;
import org.csstudio.utility.product.Workbench;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;

public class ITERWorkbench extends Workbench {

	private static final String[] VERBOSE_PACKAGES = new String[] {
		"com.sun.jersey.core.spi.component",
		"com.sun.jersey.core.spi.component.ProviderServices",
		"com.sun.jersey.spi.service.ServiceFinder" };
	private final List<Logger> strongRefLoggers = new ArrayList<>();

	@Override
	public Object beforeWorkbenchCreation(Display display,
			IApplicationContext context, Map<String, Object> parameters) {
		
		// Set upper log level on too verbose packages
		Level verboseLogLevel;
		try {
			verboseLogLevel = Preferences.getVerboseLogLevel();
		} catch (Exception e) {
			verboseLogLevel = Level.SEVERE;
		}
		for (String verbosePackage : VERBOSE_PACKAGES) {
			Logger logger = Logger.getLogger(verbosePackage);
			logger.setLevel(verboseLogLevel);
			for (Handler handler : logger.getHandlers()) {
				handler.setLevel(verboseLogLevel);
			}
			//keep strong references to all loggers. Otherwise the LogMaager will flush them out
			strongRefLoggers.add(logger);
		}
		
		return super.beforeWorkbenchCreation(display, context, parameters);
	}
}
