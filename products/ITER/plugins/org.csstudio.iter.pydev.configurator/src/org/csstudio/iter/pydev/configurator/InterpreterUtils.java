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
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.python.pydev.core.IInterpreterInfo;
import org.python.pydev.debug.core.PydevDebugPlugin;
import org.python.pydev.debug.newconsole.PydevConsoleConstants;
import org.python.pydev.plugin.PydevPlugin;
import org.python.pydev.ui.interpreters.JythonInterpreterManager;
import org.python.pydev.ui.interpreters.PythonInterpreterManager;
import org.python.pydev.ui.pythonpathconf.InterpreterInfo;

public class InterpreterUtils {

	private static final String[] PLUGIN_TO_ADD_IN_PYTHON_PATH = new String[] {
			"org.eclipse.ui", "org.eclipse.ui.workbench", "org.eclipse.swt",
			"org.eclipse.jface", "org.csstudio.swt.xygraph",
			"org.csstudio.swt.widgets", "org.csstudio.opibuilder",
			"org.csstudio.opibuilder.widgets" };

	/**
	 * Creates a Python interpreter by attempting to read where the python
	 * command being used originates.
	 * 
	 * @param name
	 * @param monitor
	 * @throws Exception
	 */
	public static boolean createPythonInterpreter(String name,
			IProgressMonitor monitor) throws Exception {
		final String interpreterExePath = PythonUtils.getProbablePythonPath();
		if (interpreterExePath == null) {
			return false;
		}
		File scanPluginDir = BundleUtils.getBundleLocation("org.csstudio.scan");
		final File scanJythonLibDir = new File(scanPluginDir, "jython");
		String scanJythonLibPath = null;
		if (scanJythonLibDir.exists()) {
			scanJythonLibPath = scanJythonLibDir.getAbsolutePath();
		}

		final PythonInterpreterManager man = (PythonInterpreterManager) PydevPlugin
				.getPythonInterpreterManager();
		InterpreterInfo info = null;
		try {
			info = man.getInterpreterInfo(name, monitor);
		} catch (Exception ne) {
			info = null;
		}

		if (info != null
				&& interpreterExePath.equals(info.getExecutableOrJar())
				&& (scanJythonLibPath == null || info.libs
						.contains(scanJythonLibPath))) {
			// All paths are correct - Nothing to do
			return false;
		}

		// Create new interpreter
		// Horrible Hack warning: This code is copied from parts of Pydev to set
		// up the interpreter and save it.
		info = (InterpreterInfo) man.createInterpreterInfo(interpreterExePath,
				monitor, false);
		if (info == null) {
			return false;
		}
		info.setName(name);

		final Set<String> names = new HashSet<String>(1);
		names.add(name);

		// Add Scan Jython Lib dir
		if (scanJythonLibPath != null) {
			info.libs.add(scanJythonLibPath);
		}

		man.setInfos(new IInterpreterInfo[] { info }, names, monitor);

		PydevPlugin.getWorkspace().save(true, monitor);

		Activator.getLogger().info(
				"PyDev workspace saved with interpreter: " + name);

		man.clearCaches();

		return true;
	}

	/**
	 * We programmatically create a Jython Interpreter so that the user does not
	 * have to.
	 * 
	 * @throws Exception
	 */
	public static boolean createJythonInterpreter(final String name,
			final IProgressMonitor mon) throws Exception {

		final JythonInterpreterManager man = (JythonInterpreterManager) PydevPlugin
				.getJythonInterpreterManager();
		InterpreterInfo info = null;
		try {
			info = man.getInterpreterInfo(name, mon);
		} catch (Exception ne) {
			info = null;
		}
		// Code copies from Pydev when the user chooses a Jython interpreter
		// - these are the defaults.
		final File jydir = BundleUtils.getBundleLocation("org.python");
		final File exeFile = new File(jydir, "jython.jar");

		if (info != null
				&& exeFile.getAbsolutePath().equals(info.getExecutableOrJar())) {
			// All paths are correct - Nothing to do
			return false;
		}

		// Create new interpreter
		// Horrible Hack warning: This code is copied from parts of Pydev to set
		// up the interpreter and save it.
		final File script = PydevPlugin
				.getScriptWithinPySrc("interpreterInfo.py");
		if (!script.exists()) {
			throw new Exception("The file specified does not exist: " + script);
		}

		info = (InterpreterInfo) man.createInterpreterInfo(
				exeFile.getAbsolutePath(), mon, false);
		if (info == null) {
			return false;
		}

		// Add PyDev Libs dir
		File pydevdir = BundleUtils.getBundleLocation("org.python.pydev");
		final File pydevLibDir = new File(pydevdir, "libs");
		if (pydevLibDir.exists()) {
			info.libs.add(pydevLibDir.getCanonicalPath());
		}

		// Add PyDev Jython Lib dir
		File pydevjythondir = BundleUtils
				.getBundleLocation("org.python.pydev.jython");
		final File pydevjythonLibDir = new File(pydevjythondir, "Lib");
		if (pydevjythonLibDir.exists()) {
			info.libs.add(pydevjythonLibDir.getCanonicalPath());
		}

		// Add Scan Jython Lib dir
		File scanPluginDir = BundleUtils.getBundleLocation("org.csstudio.scan");
		final File scanJythonLibDir = new File(scanPluginDir, "jython");
		if (scanJythonLibDir.exists()) {
			info.libs.add(scanJythonLibDir.getCanonicalPath());
		}

		for (String pluginToAdd : PLUGIN_TO_ADD_IN_PYTHON_PATH) {
			File bundleLocation = BundleUtils.getBundleLocation(pluginToAdd);
			if (bundleLocation != null && bundleLocation.exists()) {
				info.libs.add(bundleLocation.getCanonicalPath());
			}
		}

		// java, java.lang, etc should be found now
		info.restoreCompiledLibs(mon);
		info.setName(name);

		final Set<String> names = new HashSet<String>(1);
		names.add(name);

		man.setInfos(new IInterpreterInfo[] { info }, names, mon);

		PydevPlugin.getWorkspace().save(true, mon);

		Activator.getLogger().info(
				"PyDev workspace saved with interpreter: " + name);

		man.clearCaches();
		
		updateInitialInterpreterCmds();
		
		return true;
	}

	private static void updateInitialInterpreterCmds() throws IOException {
		File scanPluginDir = BundleUtils.getBundleLocation("org.csstudio.scan");
		final File scanJythonLibDir = new File(scanPluginDir, "jython");
		if (scanJythonLibDir.exists()) {
			final ScopedPreferenceStore prefStore = new ScopedPreferenceStore(
					InstanceScope.INSTANCE, PydevDebugPlugin.getPluginID());
			prefStore.setValue(
					PydevConsoleConstants.INITIAL_INTERPRETER_CMDS,
					PydevConsoleConstants.DEFAULT_INITIAL_INTERPRETER_CMDS
							+ "sys.path.append('"
							+ scanJythonLibDir.getAbsolutePath() + "')\n"
							+ "from scan_client import *\n");
		}
	}

}