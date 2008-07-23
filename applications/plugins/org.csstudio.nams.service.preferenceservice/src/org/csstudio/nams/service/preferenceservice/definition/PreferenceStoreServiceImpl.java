package org.csstudio.nams.service.preferenceservice.definition;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.nams.service.preferenceservice.declaration.HoldsAPreferenceId;
import org.csstudio.nams.service.preferenceservice.declaration.PreferenceService;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

public class PreferenceStoreServiceImpl implements PreferenceService {

	private static IPreferenceStore preferenceStore;

	public <T extends Enum<?> & HoldsAPreferenceId> boolean getBoolean(T key) {
		return PreferenceStoreServiceImpl.preferenceStore.getBoolean(key
				.getPreferenceStoreId());
	}

	public <T extends Enum<?> & HoldsAPreferenceId> int getInt(T key) {
		return PreferenceStoreServiceImpl.preferenceStore.getInt(key
				.getPreferenceStoreId());
	}

	public <T extends Enum<?> & HoldsAPreferenceId> String getString(T key) {
		return PreferenceStoreServiceImpl.preferenceStore.getString(key
				.getPreferenceStoreId());
	}

	public static void staticInject(IPreferenceStore preferenceStore) {
		PreferenceStoreServiceImpl.preferenceStore = preferenceStore;
	}

//	public interface PreferenceChangeListener {
//		public <T extends Enum<?> & HoldsAPreferenceId> void preferenceUpdated(
//				T id, Object oldValue, Object newValue);
//	}

	public <T extends Enum<?> & HoldsAPreferenceId> void addPreferenceChangeListenerFor(
			T[] preferenceIds, final PreferenceChangeListener changeListener) {

		final Map<String, T> ids = new HashMap<String, T>();
		for (T id : preferenceIds) {
			ids.put(id.getPreferenceStoreId(), id);
		}

		preferenceStore
				.addPropertyChangeListener(new IPropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent event) {
						if (ids.keySet().contains(event.getProperty())) {
							changeListener.preferenceUpdated(ids.get(event
									.getProperty()), event.getOldValue(), event
									.getNewValue());
						}
					}
				});
	}
}
