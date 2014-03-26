/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
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

import org.csstudio.autocomplete.ui.util.UIHelper;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class AutoCompleteUIPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.csstudio.autocomplete.ui"; //$NON-NLS-1$
	public static final String HISTORY_TAG = "auto_complete_history"; //$NON-NLS-1$

	/** Extension point ID for providing the helpers */
	public static final String EXT_ID = "org.csstudio.autocomplete.ui.helpers";
	private static boolean isRAP = SWT.getPlatform().startsWith("rap"); //$NON-NLS-1$;

	/** The shared instance */
	private static AutoCompleteUIPlugin plugin;

	private static final Logger logger = Logger.getLogger(PLUGIN_ID);

	private static Map<String, LinkedList<String>> fifos = null;
	private static IDialogSettings settings;

	private ImageRegistry imageRegistry;

	private static UIHelper ui;

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		fifos = new HashMap<String, LinkedList<String>>();
		readExtensionRegistry();
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

	private void readExtensionRegistry() throws Exception {
		// Registry lookup
		final IExtensionRegistry registry = RegistryFactory.getRegistry();
		final IConfigurationElement[] configs = registry
				.getConfigurationElementsFor(EXT_ID);
		if (configs.length > 1)
			throw new Exception("Found " + configs.length
							+ " Auto-Complete UI Helper implementations, expecting at most one");
		if (configs.length == 1) { // Use implementations from extension point
			Logger.getLogger(getClass().getName()).config(
					"UI Helper provided by " + configs[0].getContributor().getName());
			AutoCompleteUIPlugin.ui = (UIHelper) configs[0]
					.createExecutableExtension("ui");
		} else { // Use default implementations
			AutoCompleteUIPlugin.ui = new UIHelper();
		}
	}

	/** @return the shared instance */
	public static AutoCompleteUIPlugin getDefault() {
		return plugin;
	}

	/** @return Logger for plugin ID */
	public static Logger getLogger() {
		return logger;
	}

	/** Load persisted list values. */
	public synchronized void loadSettings() {
		if (plugin == null)
			return;
		IDialogSettings ds = plugin.getDialogSettings();
		if (ds != null) {
			settings = ds.getSection(HISTORY_TAG);
			if (settings == null)
				settings = ds.addNewSection(HISTORY_TAG);
		}
	}

	/** Save list values to persistent storage. */
	public synchronized void saveSettings() {
		IDialogSettings ds = plugin.getDialogSettings();
		if (ds != null) {
			for (Entry<String, LinkedList<String>> entry : fifos.entrySet()) {
				final String value_tag = entry.getKey();
				final LinkedList<String> fifo = entry.getValue();
				if (fifo != null && !fifo.isEmpty())
					settings.put(value_tag,
							fifo.toArray(new String[fifo.size()]));
			}
		}
	}

	/** Clear list values from persistent storage. */
	public synchronized void clearSettings() {
		IDialogSettings ds = plugin.getDialogSettings();
		if (ds != null)
			settings = ds.addNewSection(HISTORY_TAG);
		fifos.clear();
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

	/**
	 * Load the <code>Image</code> from the given path in the given plugin.
	 * 
	 * @param pluginId The id of the plugin that contains the requested image.
	 * @param relativePath The resource path of the requested image.
	 * @return The <code>Image</code> from the given path in the given plugin.
	 */
	public Image getImageFromPlugin(final String pluginId,
			final String relativePath) {
		if (imageRegistry == null) {
			imageRegistry = new ImageRegistry(Display.getDefault());
		}
		String key = pluginId + "." + relativePath; //$NON-NLS-1$
		// does image exist
		if (imageRegistry.get(key) == null) {
			ImageDescriptor descr = AbstractUIPlugin.imageDescriptorFromPlugin(
					pluginId, relativePath);
			imageRegistry.put(key, descr);
		}
		return imageRegistry.get(key);
	}

	/** @return {@link UIHelper} */
	public static UIHelper getUIHelper() {
		return AutoCompleteUIPlugin.ui;
	}

	/** @return true if this is running in RAP */
	public static boolean isRAP() {
		return isRAP;
	}

}
