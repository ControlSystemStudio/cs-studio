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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.desy.language.snl.ui.preferences.ICompilerOptionsService#getSNCompilerPath
	 * ()
	 */
	public String getSNCompilerPath() {
		return getFolder(PreferenceConstants.SNC_LOCATION_POST_FIX);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.desy.language.snl.ui.preferences.ICompilerOptionsService#getCCompilerPath
	 * ()
	 */
	public String getCCompilerPath() {
		return getFolder(PreferenceConstants.C_COMPILER_LOCATION_POST_FIX);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.desy.language.snl.ui.preferences.ICompilerOptionsService#
	 * getCompilerOptions()
	 */
	public List<String> getCCompilerOptions() {
		List<String> result = new ArrayList<String>();

		for (CompilerOptionPreferenceConstants copc : CompilerOptionPreferenceConstants
				.values()) {
			boolean checked = _preferenceStore
					.getBoolean(SNLUiActivator.PLUGIN_ID
							+ copc.getPreferenceStoreId());
			if (checked) {
				result.add(copc.getOption());
			}
		}

		return result;
	}

	public String getEpicsFolder() {
		return getFolder(PreferenceConstants.EPICS_BASE_LOCATION_POST_FIX);
	}

	public String getSeqFolder() {
		return getFolder(PreferenceConstants.EPICS_SEQ_LOCATION_POST_FIX);
	}

	private String getFolder(PreferenceConstants constantId) {
		String result = _preferenceStore.getString(SNLUiActivator.PLUGIN_ID
				+ constantId);

		if (result != null && result.trim().length() > 0) {
			File file = new File(result);
			if (!file.exists()) {
				result = null;
			}
		} else {
			result = null;
		}

		return result;
	}
}
