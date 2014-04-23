/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.dbparser;

import java.util.logging.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements BundleActivator {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.utility.dbparser"; //$NON-NLS-1$

	public static final Logger logger = Logger.getLogger(PLUGIN_ID);

	/** @return Logger for plugin ID */
	public static Logger getLogger() {
		return logger;
	}

	@Override
	public void start(BundleContext context) throws Exception {
	}

	@Override
	public void stop(BundleContext context) throws Exception {
	}

}
