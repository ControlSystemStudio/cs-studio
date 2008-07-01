package org.csstudio.nams.service.preferenceservice.declaration;

/**
 * Loads a preference from a preference-repository.
 */
public interface PreferenceService {
	/**
	 * Gets a preference with the given key as {@link String}.
	 * @param <T>
	 * @param key
	 * @return
	 */
	public <T extends Enum<?> & HoldsAPreferenceId> String getString(T key);
	public <T extends Enum<?> & HoldsAPreferenceId> boolean getBoolean(T key);
	public <T extends Enum<?> & HoldsAPreferenceId> int getInt(T key);

}
