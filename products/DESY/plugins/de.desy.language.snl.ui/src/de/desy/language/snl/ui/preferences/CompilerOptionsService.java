package de.desy.language.snl.ui.preferences;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

import de.desy.language.snl.ui.SNLUiActivator;

public class CompilerOptionsService implements ICompilerOptionsService {

	private final IPreferenceStore _preferenceStore;

	public CompilerOptionsService(IPreferenceStore preferenceStore) {
		_preferenceStore = preferenceStore;
	}

	/* (non-Javadoc)
	 * @see de.desy.language.snl.ui.preferences.ICompilerOptionsService#getSNCompilerPath()
	 */
	public File getSNCompilerPath() {
		return getFolder(PreferenceConstants.SNC_LOCATION_POST_FIX);
	}

	/* (non-Javadoc)
	 * @see de.desy.language.snl.ui.preferences.ICompilerOptionsService#getCCompilerPath()
	 */
	public File getCCompilerPath() {
		return getFolder(PreferenceConstants.C_COMPILER_LOCATION_POST_FIX);
	}
	
	/* (non-Javadoc)
	 * @see de.desy.language.snl.ui.preferences.ICompilerOptionsService#getCompilerOptions()
	 */
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

	public File getEpicsFolder() {
		return getFolder(PreferenceConstants.EPICS_BASE_LOCATION_POST_FIX);
	}

	public File getSeqFolder() {
		return getFolder(PreferenceConstants.EPICS_SEQ_LOCATION_POST_FIX);
	}
	
	private File getFolder(PreferenceConstants constantId) {
		File result = null;
		String pathToEpics = _preferenceStore
				.getString(
						SNLUiActivator.PLUGIN_ID
								+ constantId);

		if (pathToEpics != null && pathToEpics.trim().length() > 0) {
			result = new File(pathToEpics);
			if (!result.isDirectory()) {
				result = null;
			}
		}

		return result;
	}
}
