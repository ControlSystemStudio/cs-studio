/*******************************************************************************
* Copyright (c) 2010-2013 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.autocomplete.ui;

import java.util.LinkedList;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.autocomplete.ui"; //$NON-NLS-1$

	public static final Logger logger = Logger.getLogger(PLUGIN_ID);
	
	/** The shared instance */
	private static Activator plugin;
	
	public static final String VALUE_TAG = "values"; //$NON-NLS-1$
	public static final String HISTORY_TAG = "pv_names"; //$NON-NLS-1$
	private static LinkedList<String> fifo = null;
	
	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		fifo = new LinkedList<String>();
		loadSettings();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		saveSettings();
		fifo.clear();
		fifo = null;
		plugin = null;
		super.stop(context);
	}

	/** @return the shared instance */
	public static Activator getDefault() {
		return plugin;
	}

	/** @return Logger for plugin ID */
	public static Logger getLogger() {
		return logger;
	}
	
	public static void activatePlugin() {
		Bundle bundle = Platform.getBundle(PLUGIN_ID);
		try {
			bundle.start();
		} catch (BundleException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** Load persisted list values. */
	public synchronized void loadSettings() {
		if (plugin == null) return;
		IDialogSettings settings = plugin.getDialogSettings();
		if (settings != null) {
			IDialogSettings pvs = settings.getSection(HISTORY_TAG);
			if (pvs == null) return;
			String values[] = pvs.getArray(VALUE_TAG);
			if (values != null)
				for (int i = values.length - 1; i >= 0; i--)
					// Load as if they were entered, i.e. skip duplicates
					fifo.addFirst(values[i]);
		}
	}

	/** Save list values to persistent storage. */
	public synchronized void saveSettings() {
		IDialogSettings settings = plugin.getDialogSettings();
		if (settings != null) {
			IDialogSettings values = settings.addNewSection(HISTORY_TAG);
			values.put(VALUE_TAG, fifo.toArray(new String[fifo.size()]));
		}
	}
	
	/** Save list values to persistent storage. */
	public synchronized void clearSettings() {
		IDialogSettings settings = plugin.getDialogSettings();
		if (settings != null) {
			IDialogSettings values = settings.addNewSection(HISTORY_TAG);
			values.put(VALUE_TAG, new String[] {});
		}
	}
	
	public synchronized LinkedList<String> getHistory() {
		return fifo;
	}

}
