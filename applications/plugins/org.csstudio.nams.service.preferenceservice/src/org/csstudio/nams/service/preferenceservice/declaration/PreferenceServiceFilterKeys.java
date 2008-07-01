package org.csstudio.nams.service.preferenceservice.declaration;

public enum PreferenceServiceFilterKeys implements HoldsAPreferenceId {
	P_FILTER_KEYFIELDS("org.csstudio.ams.preferences.filterKeyFields");

	private String _key;
	private String _description;

	private PreferenceServiceFilterKeys(String key, String description) {
		_key = key;
		_description = description;
	}
	
	private PreferenceServiceFilterKeys(String key) {
		this(key, "Description not available");
	}
	
	public String getKey() {
		return _key;
	}
	
	public String getPreferenceStoreId() {
		return _key;
	}

	public String getDescription() {
		return _description;
	}

}
