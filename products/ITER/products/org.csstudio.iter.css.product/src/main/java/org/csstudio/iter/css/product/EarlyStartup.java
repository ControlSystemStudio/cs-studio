/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.iter.css.product;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.iter.css.product.preferences.Preferences;
import org.csstudio.iter.css.product.util.WorkbenchUtil;
import org.eclipse.ui.IStartup;

public class EarlyStartup implements IStartup {

	private static final String[] VERBOSE_PACKAGES = new String[] {
			"com.sun.jersey.core.spi.component",
			"com.sun.jersey.spi.service.ServiceFinder" };

	@Override
	public void earlyStartup() {
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
		}

		// Remove Perspectives coming with XML Editor
		WorkbenchUtil.removeUnWantedPerspectives();

		WorkbenchUtil.removeUnWantedLog();
	}

}
