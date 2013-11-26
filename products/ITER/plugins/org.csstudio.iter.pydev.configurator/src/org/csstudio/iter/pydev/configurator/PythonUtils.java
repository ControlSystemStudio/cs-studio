/*******************************************************************************
 * Copyright (c) 2010-2013 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.iter.pydev.configurator;

import java.io.File;
import java.util.Collection;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.Version;

public class PythonUtils {

	/**
	 * Attempts to determine probable python interpreter path to be used with
	 * edna. The pydev extensions also attempt to use this interpreter for the
	 * default pydev interpreter when running python written in the workbench.
	 * 
	 * 
	 * @return
	 */
	public static String getProbablePythonPath() {

		final String pythonDefinition = System.getenv("PYTHON");
		if (pythonDefinition == null) {

			String path = System.getenv("PATH");
			if (path == null)
				path = System.getenv("Path"); // Windows...

			if (path != null) {
				final String pythonPath = searchPath(path, "python");
				if (pythonPath != null)
					return pythonPath;
			}
		}

		// Sometimes can find old default interpreter on linux
		if (PythonUtils.isLinuxOS()) {
			final File python = new File("/usr/bin/python");
			if (python.exists())
				return "/usr/bin/python";
		}

		// Some kind of default which we hope works.
		return null;
	}

	/**
	 * Could not find Java method to search path, so since it is not hard, will
	 * write a simple algorithm.
	 * 
	 * @param path
	 * @param execName
	 * @return
	 */
	private static String searchPath(final String path, final String execName) {

		String pathSep = ":";
		if (PythonUtils.isWindowsOS())
			pathSep = ";";
		final StringTokenizer toker = new StringTokenizer(path, pathSep);
		while (toker.hasMoreTokens()) {
			final String execPath = getExecutable(toker.nextToken(), execName);
			if (execPath != null)
				return execPath;
		}
		return null;
	}

	private static String getExecutable(final String dirPath,
			final String execName) {

		final File dir = new File(dirPath);
		if (!dir.exists())
			return null;
		if (!dir.isDirectory())
			return null;

		final Pattern pattern = Pattern.compile(execName + "(\\d+\\.?\\d*)");

		String found = null;
		Version version = new Version("0.0.0");
		for (File file : dir.listFiles()) {
			if (!file.isFile())
				continue;
			// if (!file.canExecute()) continue;

			// If exact match return it
			if (file.getName().equals(execName)) {
				return file.getAbsolutePath();
			}

			// Looking for latest version
			final Matcher matcher = pattern.matcher(file.getName());
			if (matcher.matches()) {
				final Version v = new Version(matcher.group(1));
				if (v.compareTo(version) > 0) {
					version = v;
					found = file.getAbsolutePath();
				}
			}
		}

		return found;

	}

	/**
	 * Attempts to generate legal variable name. Does not take into account key
	 * words.
	 * 
	 * @param setName
	 * @return
	 * @throws Exception
	 */
	public static String getLegalVarName(String setName,
			final Collection<String> names) throws Exception {

		if (setName.endsWith("/"))
			setName = setName.substring(0, setName.length() - 1);
		if (setName.indexOf('/') > -1)
			setName = setName.substring(setName.lastIndexOf('/'));

		setName = setName.replaceAll(" ", "_");
		setName = setName.replaceAll("[^a-zA-Z0-9_]", "");
		final Matcher matcher = Pattern.compile("(\\d+)(.+)").matcher(setName);
		if (matcher.matches()) {
			setName = matcher.group(2);
		}

		if (Pattern.compile("(\\d+)").matcher(setName).matches()) {
			throw new Exception("Variable name of numbers only not possible!");
		}

		if (names != null)
			if (names.contains(setName)) {
				int i = 1;
				while (names.contains(setName + i))
					i++;
				setName = setName + i;
			}

		return setName;
	}

	/**
	 * Does not fix key words
	 * 
	 * @param name
	 * @return
	 */
	public static boolean isLegalName(final String name) {
		return !name.matches(".*[^a-zA-Z0-9_]+.*");
	}

	/**
	 * @return true if linux
	 */
	private static boolean isLinuxOS() {
		String os = System.getProperty("os.name");
		return os != null && os.startsWith("Linux");
	}

	/**
	 * @return true if windows
	 */
	static private boolean isWindowsOS() {
		return (System.getProperty("os.name").indexOf("Windows") == 0);
	}

	/**
	 * Attempts to guess a python interpreter command to use when linking to
	 * python with RPC.
	 * 
	 * @return
	 */
	public static String getPythonInterpreterCommand() {

		if (System
				.getProperty("org.dawb.passerelle.actors.scripts.python.command") != null) {
			return System
					.getProperty("org.dawb.passerelle.actors.scripts.python.command");
		}

		// ESRF path
		final String esrfPath;
		if ("64".equals(System.getProperty("sun.arch.data.model"))) {
			esrfPath = "python";
		} else {
			esrfPath = "python";
		}

		final File esrfPy = new File(esrfPath);
		if (esrfPy.exists()) {
			return esrfPy.getAbsolutePath();
		}

		final File diamondPy = new File(
				"/dls_sw/prod/tools/RHEL5/bin/python2.6");
		if (diamondPy.exists()) {
			return diamondPy.getAbsolutePath();
		}

		return "python"; // make sure python is on the path and has numpy
							// please.
	}

}