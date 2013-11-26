/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.iter.pydev.configurator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

public class BundleUtils {

	public static File getBundleLocation(final String bundleName)
			throws IOException {
		final Bundle bundle = Platform.getBundle(bundleName);
		return BundleUtils.getBundleLocation(bundle);
	}

	/**
	 * Get the java.io.File location of a bundle.
	 * 
	 * @param bundleName
	 * @return
	 * @throws Exception
	 */
	public static File getBundleLocation(final Bundle bundle)
			throws IOException {

		String dirPath = BundleUtils.cleanPath(bundle.getLocation());
		final File dir = new File(dirPath);
		if (dir.exists())
			return dir;

		// Just in case...
		final String eclipseDir = BundleUtils.getEclipseHome();
		final File bundDir = new File(eclipseDir + "/" + dirPath);
		if (bundDir.exists())
			return bundDir;

		final File plugins = new File(eclipseDir + "/plugins/");
		if (plugins.exists()) {
			final File[] fa = plugins.listFiles();
			for (int i = 0; i < fa.length; i++) {
				final File file = fa[i];
				if (file.getName().equals(bundle.getSymbolicName()))
					return file;
				if (file.getName().startsWith(bundle.getSymbolicName() + "_"))
					return file;
			}
		}
		throw new IOException("Cannot locate bundle "
				+ bundle.getSymbolicName());
	}

	private static String cleanPath(String loc) {

		// Remove reference:file: from the start. TODO find a better way,
		// and test that this works on windows (it might have ///)
		if (loc.startsWith("reference:file:")) {
			loc = loc.substring(15);
		} else if (loc.startsWith("file:")) {
			loc = loc.substring(5);
		} else {
			return loc;
		}

		loc = loc.replace("//", "/");
		loc = loc.replace("\\\\", "\\");

		return loc;
	}

	/**
	 * Get the bundle path using eclipse.home.location not loading the bundle.
	 * 
	 * @param bundleName
	 * @return
	 */
	public static File getBundlePathNoLoading(String bundleName) {

		return new File(getEclipseHome() + "/plugins/" + bundleName);
	}

	/**
	 * Gets eclipse home in debug and in deployed application mode.
	 * 
	 * @return
	 */
	public static String getEclipseHome() {

		String home = System.getProperty("eclipse.home.location");
		if (home.startsWith("file:"))
			home = home.substring("file:".length());

		final String path;
		if (home.endsWith("/plugins/") || home.endsWith("/bundles/")) {
			path = ((new File(home))).getParentFile().getParentFile()
					.getAbsolutePath();
		} else {
			path = home;
		}
		return path;
	}
}
