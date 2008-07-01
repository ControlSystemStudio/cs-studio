package org.csstudio.nams.service.preferenceservice.definition;

import org.csstudio.nams.service.preferenceservice.Activator;
import org.csstudio.nams.service.preferenceservice.declaration.HoldsAPreferenceId;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceService;

public class PreferenceStoreServiceImpl implements PreferenceService {

	public <T extends Enum<?> & HoldsAPreferenceId> boolean getBoolean(T key) {
		return Activator.getInstanceForServiceImplementation().getEclipsePreferenceStoreWithAMSId().getBoolean(key.getPreferenceStoreId());
	}

	public <T extends Enum<?> & HoldsAPreferenceId> int getInt(T key) {
		return Activator.getInstanceForServiceImplementation().getEclipsePreferenceStoreWithAMSId().getInt(key.getPreferenceStoreId());
	}

	public <T extends Enum<?> & HoldsAPreferenceId> String getString(T key) {
		return Activator.getInstanceForServiceImplementation().getEclipsePreferenceStoreWithAMSId().getString(key.getPreferenceStoreId());
	}

}
