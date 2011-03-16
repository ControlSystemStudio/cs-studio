/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.converter;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.opibuilder.converter.ui.PreferencesHelper;
import org.csstudio.opibuilder.converter.writer.OpiWriter;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class EDM2OPIConverterPlugin extends AbstractUIPlugin {
	/** Plug-in ID registered in MANIFEST.MF */
	public static final String PLUGIN_ID = "org.csstudio.opibuilder.converter"; //$NON-NLS-1$

	private IPropertyChangeListener preferenceLisener;

	private IResource opiColorFolder;


	// The shared instance
	private static EDM2OPIConverterPlugin plugin;

	public EDM2OPIConverterPlugin() {
		plugin = this;
	}


	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static EDM2OPIConverterPlugin getDefault() {
		return plugin;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		setEDMColorListFile();

		setOPIColorFile();

		setRobustParsing();
		convertColorFile();
		preferenceLisener = new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if(event.getProperty().equals(PreferencesHelper.EDM_COLORLIST_FILE)){
					setEDMColorListFile();
					convertColorFile();
				} else if(event.getProperty().equals(PreferencesHelper.OUTPUT_OPICOLOR_FILE)){
					setOPIColorFile();
					convertColorFile();
				}else if(event.getProperty().equals(PreferencesHelper.FAIL_FAST))
					setRobustParsing();
			}
		};

		getPluginPreferences().addPropertyChangeListener(preferenceLisener);

	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		getPluginPreferences().removePropertyChangeListener(preferenceLisener);
		super.stop(context);
	}


	private void convertColorFile(){
		try {
			if(opiColorFolder == null)
				return;
			OpiWriter.getInstance().writeColorDef(System.getProperty("edm2xml.colorsOutput"));
			opiColorFolder.refreshLocal(IResource.DEPTH_ONE, null);
		} catch (Exception e) {
			final String message = "Failed to convert color file. ";
            EDM2OPIConverterPlugin.getLogger().log(Level.WARNING, message, e);
			ConsoleService.getInstance().writeError(message + e.getMessage());
		}
	}


	/**
	* Enable fail-fast mode for stricter tests.
	* Set this to true for the partial conversion in case of exceptions.
	*/
	private void setRobustParsing() {
		System.setProperty("edm2xml.robustParsing",
		        Boolean.toString(PreferencesHelper.isRobustParsing()));
	}


	private void setOPIColorFile() {
		IPath opiColorPath = PreferencesHelper.getOutputOPIColorFilePath();
		if(opiColorPath == null || opiColorPath.isEmpty())
			return;
		opiColorFolder = ResourcesPlugin.getWorkspace().getRoot().findMember(
				opiColorPath.removeLastSegments(1));
		if(opiColorFolder == null)
			return;
		System.setProperty("edm2xml.colorsOutput",
				opiColorFolder.getLocation().append(
						opiColorPath.lastSegment()).toOSString());
	}


	private void setEDMColorListFile() {
		IFile colorsListfile = ResourceUtil.getIFileFromIPath(
				PreferencesHelper.getEDMColorListFilePath());
		if(colorsListfile != null)
			System.setProperty("edm2xml.colorsFile", colorsListfile.getLocation().toOSString());
	}

	/** @return Logger for plugin ID */
	public static Logger getLogger()
	{
	    return Logger.getLogger(PLUGIN_ID);
	}
}
