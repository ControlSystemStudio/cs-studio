package de.desy.language.snl.ui.editor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

import de.desy.language.snl.configurationservice.CompilerOptionPreferenceConstants;
import de.desy.language.snl.configurationservice.ICompilerOptionsService;
import de.desy.language.snl.configurationservice.PreferenceConstants;
import de.desy.language.snl.ui.SNLUiActivator;

/**
 * Service to get the compiler options from the preference store.
 * 
 * @author Kai Meyer (C1 WPS)
 * 
 */
public class CompilerOptionsService implements ICompilerOptionsService {

	/**
	 * The store of the preferences.
	 */
	private final IPreferenceStore _preferenceStore;

	/**
	 * Constructor.
	 * 
	 * @param preferenceStore
	 *            The store of the preferences.
	 */
	public CompilerOptionsService(final IPreferenceStore preferenceStore) {
		_preferenceStore = preferenceStore;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getSNCompilerPath() {
		return getFolder(PreferenceConstants.SNC_LOCATION_POST_FIX);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getCCompilerPath() {
		return getFolder(PreferenceConstants.C_COMPILER_LOCATION_POST_FIX);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getPreCompilerPath() {
		return getFolder(PreferenceConstants.PRE_COMPILER_LOCATION_POST_FIX);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getApplicationCompilerPath() {
		return getFolder(PreferenceConstants.APPLICATION_COMPILER_POST_FIX);
	}

	/**
	 * {@inheritDoc}
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

	/**
	 * {@inheritDoc}
	 */
	public String getEpicsFolder() {
		return getFolder(PreferenceConstants.EPICS_BASE_LOCATION_POST_FIX);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getSeqFolder() {
		return getFolder(PreferenceConstants.EPICS_SEQ_LOCATION_POST_FIX);
	}

	/**
	 * {@inheritDoc}
	 */
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

	public boolean getKeepGeneratedFiles() {
		return _preferenceStore.getBoolean(SNLUiActivator.PLUGIN_ID
				+ PreferenceConstants.KEEP_GENERATED_FILES_POST_FIX);
	}

	public boolean getSaveAndCompile() {
		return _preferenceStore.getBoolean(SNLUiActivator.PLUGIN_ID
				+ PreferenceConstants.SAVE_AND_COMPILE_POST_FIX);
	}
}
