package de.desy.language.snl.ui.preferences;

import java.io.File;

import de.desy.language.snl.ui.SNLUiActivator;

public abstract class CompilerOptionsService {

	public static CompilerOptionsService defaultInstance() {
		return new CompilerOptionsService() {
		};
	}

	/**
	 * Returns the compiler path (directory) stored in the preference store if
	 * exists, null otherwise.
	 * 
	 * @return The Path as a File-instance or null if no valid path is avail.
	 */
	public File getCompilerPath() {
		String pathToSNC = SNLUiActivator.getDefault().getPreferenceStore()
				.getString(
						SNLUiActivator.PLUGIN_ID
								+ PreferenceConstants.SNC_LOCATION_POST_FIX);

		if (pathToSNC == null || pathToSNC.trim().length() < 1) {
			return null;
		}

		File result = new File(pathToSNC);
		if (!result.isDirectory()) {
			return null;
		}

		return result;
	}
}
