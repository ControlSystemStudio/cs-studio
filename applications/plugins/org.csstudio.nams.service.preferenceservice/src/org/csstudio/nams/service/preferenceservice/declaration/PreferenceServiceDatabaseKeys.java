package org.csstudio.nams.service.preferenceservice.declaration;

public enum PreferenceServiceDatabaseKeys implements HoldsAPreferenceId {
	P_CONFIG_DATABASE_CONNECTION("org.csstudio.ams.preferences.configDatabaseConnection", "URL to the Derby-Database"),
	P_CONFIG_DATABASE_USER("org.csstudio.ams.preferences.configDatabaseUser","The name of the user for the Derby-DB"),
	P_CONFIG_DATABASE_PASSWORD("org.csstudio.ams.preferences.configDatabasePassword","The password for the Derby-DB"),
	
	P_APP_DATABASE_CONNECTION("org.csstudio.ams.preferences.appDatabaseConnection"),
	P_APP_DATABASE_USER("org.csstudio.ams.preferences.appDatabaseUser"),
	P_APP_DATABASE_PASSWORD("org.csstudio.ams.preferences.appDatabasePassword");

	private String _key;
	private String _description;

	private PreferenceServiceDatabaseKeys(String key, String description) {
		_key = key;
		_description = description; 
	}
	
	private PreferenceServiceDatabaseKeys(String key) {
		_key = key;
		_description = "No description available";
	}
	
	public String getKey() {
		return _key;
	}
	
	public String getDescription() {
		return _description;
	}

	public String getPreferenceStoreId() {
		return _key;
	}

}
