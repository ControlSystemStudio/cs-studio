package org.csstudio.iter.pydev.configurator;

import java.io.File;
import java.io.IOException;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class EarlyStartup implements IStartup {

	private static final String[] HIDE_MESSAGE_STARTS_WITH = new String[] {
			"Restoring info for", "Finished restoring information for",
			"Info: Rebuilding internal caches:",
			"Plug-in 'org.python.pydev' contributed an invalid Menu Extension" };

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

	private void updateOpiBuilderPythonPath() throws IOException {
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
					if (!oldPythonPath.endsWith("org.csstudio.scan/jython")) {
					} else {
						newPythonPaths.append(oldPythonPath);
					}
					newPythonPaths.append('|');
				}
			}

			final ScopedPreferenceStore prefStore = new ScopedPreferenceStore(
					InstanceScope.INSTANCE, "org.csstudio.opibuilder");
			prefStore.setValue("python_path",
					newPythonPaths.substring(0, newPythonPaths.length() - 1));
		}
	}

	@Override
	public void earlyStartup() {
		Job job = new Job("Configure python executable") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				removeUnWantedLog();
				try {
					InterpreterUtils.createPythonInterpreter("default_python",
							monitor);
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					InterpreterUtils.createJythonInterpreter("default_jython",
							monitor);
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					updateOpiBuilderPythonPath();
				} catch (Exception e) {
					e.printStackTrace();
				}

				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}
}
