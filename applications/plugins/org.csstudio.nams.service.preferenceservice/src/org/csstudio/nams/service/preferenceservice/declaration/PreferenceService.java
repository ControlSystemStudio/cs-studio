
package org.csstudio.nams.service.preferenceservice.declaration;

/**
 * Loads a preference from a preference-repository.
 */
public interface PreferenceService {

	public static interface PreferenceChangeListener {
		public <T extends Enum<?> & HoldsAPreferenceId> void preferenceUpdated(
				T id, Object oldValue, Object newValue);
	}

	public <T extends Enum<?> & HoldsAPreferenceId> void addPreferenceChangeListenerFor(
			T[] preferenceIds, final PreferenceChangeListener changeListener);

	public <T extends Enum<?> & HoldsAPreferenceId> boolean getBoolean(T key);

	public <T extends Enum<?> & HoldsAPreferenceId> int getInt(T key);

	public <T extends Enum<?> & HoldsAPreferenceId> String getString(T key);
}
