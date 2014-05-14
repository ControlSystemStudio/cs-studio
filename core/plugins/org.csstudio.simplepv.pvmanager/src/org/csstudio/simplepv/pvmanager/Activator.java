/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.csstudio.simplepv.pvmanager;

import java.util.logging.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author Xihui Chen
 *
 */
public class Activator implements BundleActivator {

	private static BundleContext context;
	
	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.simplepv.pvmanager"; //$NON-NLS-1$
	
	final private static Logger logger = Logger.getLogger(PLUGIN_ID);

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		
		// This plugin depends on the PVManager plugin, and its Activator
		// should configure the PVManager.
		// After the actual PVManager code was extracted to third party bundles,
		// org.csstudio.utility.pvmanager only re-exports those classes,
		// and access to org.epics.pvmanager.* would no longer cause activation.
		// By explicitly accessing a class in the org.csstudio.utility.pvmanager plugin,
		// it gets activated:
		org.csstudio.utility.pvmanager.Activator dummy = new org.csstudio.utility.pvmanager.Activator();
		@SuppressWarnings("static-access")
		final String pvm_id = dummy.ID;
		if (! pvm_id.endsWith("pvmanager"))
			throw new Exception("Cannot access PVManager plugin");
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}
	
	public static Logger getLogger() {
		return logger;
	}

}
