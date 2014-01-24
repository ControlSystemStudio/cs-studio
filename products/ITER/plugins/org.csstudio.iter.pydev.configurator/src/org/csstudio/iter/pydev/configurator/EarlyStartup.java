/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.iter.pydev.configurator;

import java.io.File;
import java.io.IOException;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class EarlyStartup implements IStartup {

	private static final String[] HIDE_MESSAGE_STARTS_WITH = new String[] {
			"Restoring info for", "Finished restoring information for",
			"Info: Rebuilding internal caches:",
			"Plug-in 'org.python.pydev' contributed an invalid Menu Extension" };

	private static Pattern SCAN_JYTHON_PLUGIN_PATH_PATTERN = Pattern
			.compile(".*/org\\.csstudio\\.scan(_[^/]*)?/jython");

	private static class HideUnWantedLogFilter implements Filter {

		private Filter previousFilter;

		public HideUnWantedLogFilter(Filter previousFilter) {
			this.previousFilter = previousFilter;
		}

		@Override
		public boolean isLoggable(LogRecord record) {
			if (record.getMessage() != null) {
				for (String hideMsgStartsWith : HIDE_MESSAGE_STARTS_WITH) {
					if (record.getMessage().startsWith(hideMsgStartsWith)) {
						return false;
					}
				}
			}
			if (previousFilter == null) {
				return true;
			}
			return previousFilter.isLoggable(record);
		}
	};

	private void removeUnWantedLog() {
		// Hide unwanted message from log
		Logger rootLogger = Logger.getLogger("");
		rootLogger.setFilter(new HideUnWantedLogFilter(rootLogger.getFilter()));
		for (Handler handler : rootLogger.getHandlers()) {
			handler.setFilter(new HideUnWantedLogFilter(handler.getFilter()));
		}
	}

	private boolean updateOpiBuilderPythonPath() throws IOException {
		File scanPluginDir = BundleUtils.getBundleLocation("org.csstudio.scan");
		final File scanJythonLibDir = new File(scanPluginDir, "jython");
		if (scanJythonLibDir.exists()) {
			final IPreferencesService prefs = Platform.getPreferencesService();

			StringBuilder newPythonPaths = new StringBuilder();
			newPythonPaths.append(scanJythonLibDir.getCanonicalPath());
			newPythonPaths.append('|');

			String oldPythonPathString = prefs.getString(
					"org.csstudio.opibuilder", "python_path", "", null);
			String[] oldPythonPaths = oldPythonPathString.split("\\|");
			for (String oldPythonPath : oldPythonPaths) {
				if (!"".equals(oldPythonPath.trim())) {
					Matcher m = SCAN_JYTHON_PLUGIN_PATH_PATTERN
							.matcher(oldPythonPath.trim());
					if (!m.matches()) {
						newPythonPaths.append(oldPythonPath);
						newPythonPaths.append('|');
					}
				}
			}
			String newPythonPathString = newPythonPaths.substring(0,
					newPythonPaths.length() - 1);
			if (!oldPythonPathString.equals(newPythonPathString)) {
				final ScopedPreferenceStore prefStore = new ScopedPreferenceStore(
						InstanceScope.INSTANCE, "org.csstudio.opibuilder");
				prefStore.setValue("python_path", newPythonPathString);
				prefStore.save();
				return true;
			}
		}
		return false;
	}

	@Override
	public void earlyStartup() {
		Job job = new Job("Configure python executable") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				removeUnWantedLog();
				boolean confChanged = false;
				try {
					boolean changed = InterpreterUtils.createPythonInterpreter(
							"default_python", monitor);
					confChanged |= changed;
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					boolean changed = InterpreterUtils.createJythonInterpreter(
							"default_jython", monitor);
					confChanged |= changed;
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					boolean changed = updateOpiBuilderPythonPath();
					confChanged |= changed;
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (confChanged) {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							MessageDialog dialog = new MessageDialog(
									null,
									"Restart required",
									null,
									"Python Configuration has been updated automatically and requires a restart.",
									MessageDialog.QUESTION, new String[] {
											"Restart Now", "Restart Later" }, 0);
							int result = dialog.open();
							if (result == 0) {
								PlatformUI.getWorkbench().restart();
							}
						}
					});
				}

				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}
}
