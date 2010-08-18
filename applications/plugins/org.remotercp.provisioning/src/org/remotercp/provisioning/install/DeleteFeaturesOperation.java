package org.remotercp.provisioning.install;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.update.configuration.IConfiguredSite;
import org.eclipse.update.core.IFeature;
import org.eclipse.update.core.IPluginEntry;

/**
 * This class does delete features and plugins from disk. As Eclipse usually
 * does not do this we will handle it by our own. This class is run in a
 * separate thread in case that this class does couse some errors the usual
 * uninstall operation won't throw any exceptions.
 * 
 * @author Eugen Reiswich
 * 
 */
public class DeleteFeaturesOperation extends Thread {

	private final Map<IConfiguredSite, IFeature> featuresToUninstall;

	private final static Logger logger = Logger
			.getLogger(DeleteFeaturesOperation.class.getName());

	public DeleteFeaturesOperation(
			Map<IConfiguredSite, IFeature> featuresToDeleteFromDisk) {
		this.featuresToUninstall = featuresToDeleteFromDisk;
	}

	@Override
	public void run() {

		for (IConfiguredSite configuredSite : this.featuresToUninstall.keySet()) {
			IFeature feature = this.featuresToUninstall.get(configuredSite);

			// get root dir of application
			URL url = configuredSite.getSite().getURL();

			// delete all plugins in a feature
			deletePlugins(url, feature);

			// delete the feature itself
			deleteFeature(url, feature);

		}
	}

	private void deleteFeature(URL url, IFeature feature) {
		String featureVersion = feature.getVersionedIdentifier().toString();
		// delete feature
		File featureToDelete = new File(url.getPath() + File.separator
				+ "features" + File.separator + featureVersion);
		String featureName = featureToDelete.getPath();

		/*
		 * featureToDele is a folder. Folders can onyl be deleted if empty.
		 * Therefore delete all files in folder as well
		 */
		logger.info("Trying to delete feature : " + featureName);
		boolean featureExists = featureToDelete.exists();
		logger.info("Feature found on disk: " + featureExists);

		if (featureExists) {
			boolean delete = this.deleteFile(featureToDelete);

			logger.info("Feature " + featureName + " deleted: " + delete);
		} else {
			logger.info("Feature: " + featureName + " does not exist");
		}
	}

	private boolean deleteFile(File file) {
		logger.info("Trying to delete all files in features folder");

		if (file.exists()) {

			File[] listFiles = file.listFiles();
			for (File fileToDelete : listFiles) {
				if (fileToDelete.isDirectory()) {
					// check if directory is empty
					deleteFile(fileToDelete);
				} else {
					String fileName = fileToDelete.getName();
					// try to delete file
					boolean delete = fileToDelete.delete();

					logger.info("File " + fileName + " deleted: " + delete);
					if (!delete) {
						// try to delete on exit
						fileToDelete.deleteOnExit();
					}
				}
			}
		}

		// delete root at the end
		boolean delete = file.delete();
		if (!delete) {
			file.deleteOnExit();
		}
		return delete;
	}

	private void deletePlugins(URL url, IFeature feature) {
		IPluginEntry[] pluginEntries = feature.getPluginEntries();
		logger.info("Plugins for feature: " + feature.getLabel() + " found: "
				+ pluginEntries.length);

		for (IPluginEntry pluginEntry : pluginEntries) {
			String pluginId = pluginEntry.getVersionedIdentifier().toString();

			// Plugin to delete
			File pluginToDelete = new File(url.getPath() + File.separator
					+ "plugins" + File.separator + pluginId + ".jar");

			String pluginName = pluginToDelete.getPath();

			logger.info("Trying to delete plugin: " + pluginName);
			boolean pluginExist = pluginToDelete.exists();
			logger.info("Plugin found on disk: " + pluginExist);

			if (pluginExist) {
				boolean delete = pluginToDelete.delete();
				logger.info("Plugin " + pluginName + " deleted: " + delete);
				if (!delete) {
					// try to delete plugin on exit
					pluginToDelete.deleteOnExit();
				}
			} else {
				logger.info("Unable to delete plugn: " + pluginName);
			}
		}

	}

}
