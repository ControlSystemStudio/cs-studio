
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

	public static void staticInject(final IPreferenceStore preferenceStore) {
		PreferenceStoreServiceImpl.preferenceStore = preferenceStore;
	}

	@Override
    public <T extends Enum<?> & HoldsAPreferenceId> void addPreferenceChangeListenerFor(
			final T[] preferenceIds,
			final PreferenceChangeListener changeListener) {

		final Map<String, T> ids = new HashMap<String, T>();
		for (final T id : preferenceIds) {
			ids.put(id.getPreferenceStoreId(), id);
		}

		PreferenceStoreServiceImpl.preferenceStore
				.addPropertyChangeListener(new IPropertyChangeListener() {
					@Override
                    public void propertyChange(final PropertyChangeEvent event) {
						if (ids.keySet().contains(event.getProperty())) {
							changeListener.preferenceUpdated(ids.get(event
									.getProperty()), event.getOldValue(), event
									.getNewValue());
						}
					}
				});
	}

	@Override
    public <T extends Enum<?> & HoldsAPreferenceId> boolean getBoolean(
			final T key) {
		return PreferenceStoreServiceImpl.preferenceStore.getBoolean(key
				.getPreferenceStoreId());
	}

	@Override
    public <T extends Enum<?> & HoldsAPreferenceId> int getInt(final T key) {
		return PreferenceStoreServiceImpl.preferenceStore.getInt(key
				.getPreferenceStoreId());
	}

	// public interface PreferenceChangeListener {
	// public <T extends Enum<?> & HoldsAPreferenceId> void preferenceUpdated(
	// T id, Object oldValue, Object newValue);
	// }

	@Override
    public <T extends Enum<?> & HoldsAPreferenceId> String getString(final T key) {
		return PreferenceStoreServiceImpl.preferenceStore.getString(key
				.getPreferenceStoreId());
	}
}
