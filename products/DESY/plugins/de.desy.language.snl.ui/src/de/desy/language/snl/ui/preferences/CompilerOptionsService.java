package de.desy.language.snl.ui.preferences;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

import de.desy.language.snl.ui.SNLUiActivator;

public class CompilerOptionsService {

	private final IPreferenceStore _preferenceStore;

	public CompilerOptionsService(IPreferenceStore preferenceStore) {
		_preferenceStore = preferenceStore;
	}

	/**
	 * Returns the compiler path (directory) stored in the preference store if
	 * exists, null otherwise.
	 * 
	 * @return The Path as a File-instance or null if no valid path is avail.
	 */
	public File getSNCompilerPath() {
		File result = null;
		String pathToSNC = _preferenceStore
				.getString(
						SNLUiActivator.PLUGIN_ID
								+ PreferenceConstants.SNC_LOCATION_POST_FIX);

		if (pathToSNC != null && pathToSNC.trim().length() > 0) {
			result = new File(pathToSNC);
			if (!result.isDirectory()) {
				result = null;
			}
		}

		return result;
	}

	public File getCCompilerPath() {
		File result = null;
		String pathToSNC = _preferenceStore
				.getString(
						SNLUiActivator.PLUGIN_ID
								+ PreferenceConstants.C_COMPILER_LOCATION_POST_FIX);

		if (pathToSNC != null && pathToSNC.trim().length() > 0) {
			result = new File(pathToSNC);
			if (!result.isDirectory()) {
				result = null;
			}
		}

		return result;
	}
	
	public List<String> getCompilerOptions() {
		List<String> result = new ArrayList<String>();
		
		
		for (CompilerOptionPreferenceConstants copc : CompilerOptionPreferenceConstants.values()) {
			boolean checked = _preferenceStore.getBoolean(SNLUiActivator.PLUGIN_ID + copc.getPreferenceStoreId());
			if (checked) {
				result.add(copc.getOption());
			}
		}
		
		return result;
	}
}
