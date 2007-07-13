package org.csstudio.platform;

/**
 * An entry of the system property preferences.
 * 
 * @author Joerg Rathlev
 */
public final class SystemPropertyPreferenceEntry {
	/**
	 * The preference key.
	 */
	private String _key;
	
	/**
	 * The preference value.
	 */
	private String _value;
	
	/**
	 * Creates a new system property preference entry.
	 * @param key the property key.
	 * @param value the value.
	 */
	public SystemPropertyPreferenceEntry(final String key, final String value) {
		_key = key;
		_value = value;
	}
	
	/**
	 * Returns the preference key.
	 * @return the preference key.
	 */
	public String getKey() {
		return _key;
	}
	
	/**
	 * Returns the preference value.
	 * @return the preference value.
	 */
	public String getValue() {
		return _value;
	}

	/**
	 * Sets the key.
	 * @param key the key.
	 */
	public void setKey(final String key) {
		_key = key;
	}
	
	/**
	 * Sets the value.
	 * @param value the value.
	 */
	public void setValue(final String value) {
		_value = value;
	}
}
