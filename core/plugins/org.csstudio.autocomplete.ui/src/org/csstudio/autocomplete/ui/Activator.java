/*******************************************************************************
* Copyright (c) 2010-2013 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.autocomplete.ui;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
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
public class Activator extends AbstractUIPlugin  {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.autocomplete.ui"; //$NON-NLS-1$

	public static final Logger logger = Logger.getLogger(PLUGIN_ID);
	
	/** The shared instance */
	private static Activator plugin;
	
	public static final String VALUE_TAG = "values"; //$NON-NLS-1$
	public static final String HISTORY_TAG = "auto_complete_history"; //$NON-NLS-1$
	private static Map<String, LinkedList<String>> fifos = null;
	private static IDialogSettings settings;
	

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		fifos = new HashMap<String, LinkedList<String>>();
		loadSettings();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		saveSettings();
		fifos.clear();
		fifos = null;
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
		if (plugin == null)
			return;
		IDialogSettings ds = plugin.getDialogSettings();
		if (ds != null) {
			settings = ds.getSection(HISTORY_TAG);
			if (settings == null) {
				ds.addNewSection(HISTORY_TAG);
			}
		}
	}

	/** Save list values to persistent storage. */
	public synchronized void saveSettings() {
		IDialogSettings ds = plugin.getDialogSettings();
		if (ds != null) {
			IDialogSettings settings = ds.getSection(HISTORY_TAG);
			if (settings == null) {
				ds.addNewSection(HISTORY_TAG);
			}
			for (Entry<String, LinkedList<String>> entry : fifos.entrySet()) {
				final String value_tag = entry.getKey();
				final LinkedList<String> fifo = entry.getValue();
				if (fifo != null && !fifo.isEmpty())
					settings.put(value_tag,
							fifo.toArray(new String[fifo.size()]));
			}
		}
	}
	
	/** Save list values to persistent storage. */
	public synchronized void clearSettings() {
		IDialogSettings ds = plugin.getDialogSettings();
		if (ds != null) {
			IDialogSettings settings = ds.addNewSection(HISTORY_TAG);
			settings.put(VALUE_TAG, new String[] {});
		}
	}
	
	public synchronized LinkedList<String> getHistory(final String type) {
		if (fifos.get(type) == null) {
			final LinkedList<String> fifo = new LinkedList<String>();
			if (settings != null) {
				String values[] = settings.getArray(type);
				if (values != null)
					for (int i = values.length - 1; i >= 0; i--)
						// Load as if they were entered, i.e. skip duplicates
						fifo.addFirst(values[i]);
			}
			fifos.put(type, fifo);
		}
		return fifos.get(type);
	}

}
