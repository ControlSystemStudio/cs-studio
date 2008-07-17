package org.csstudio.nams.service.preferenceservice.definition;

import org.csstudio.nams.service.preferenceservice.declaration.HoldsAPreferenceId;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceService;
import org.eclipse.jface.preference.IPreferenceStore;

public class PreferenceStoreServiceImpl implements PreferenceService {

	private static IPreferenceStore preferenceStore;

	public <T extends Enum<?> & HoldsAPreferenceId> boolean getBoolean(T key) {
		return PreferenceStoreServiceImpl.preferenceStore.getBoolean(key.getPreferenceStoreId());
	}

	public <T extends Enum<?> & HoldsAPreferenceId> int getInt(T key) {
		return PreferenceStoreServiceImpl.preferenceStore.getInt(key.getPreferenceStoreId());
	}

	public <T extends Enum<?> & HoldsAPreferenceId> String getString(T key) {
		return PreferenceStoreServiceImpl.preferenceStore.getString(key.getPreferenceStoreId());
	}

	public static void staticInject(IPreferenceStore preferenceStore) {
		PreferenceStoreServiceImpl.preferenceStore = preferenceStore;
	}

}
