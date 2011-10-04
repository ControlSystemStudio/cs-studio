
package org.csstudio.nams.service.preferenceservice.declaration;

/**
 * TODO (C1-WPS) :
 *
 * @author bknerr
 * @author $Author: bknerr $
 * @version $Revision: 1.7 $
 * @since 29.07.2010
 */
public enum PreferenceServiceDatabaseKeys implements HoldsAPreferenceId {

    P_CONFIG_DATABASE_CONNECTION("configDatabaseConnection",
	                             "Configuration databases jdbc-url"),
	P_CONFIG_DATABASE_TYPE("configDatabaseType",
                           "Type of config database"),
	P_CONFIG_DATABASE_USER("configDatabaseUser",
			               "Configuration databases user"),
	P_CONFIG_DATABASE_PASSWORD("configDatabasePassword",
			                   "Configuration databases password"),
	P_APP_DATABASE_CONNECTION("appDatabaseConnection",
			                  "Application databases jdbc-url"),
	P_APP_DATABASE_USER("appDatabaseUser",
			            "Application databases user"),
	P_APP_DATABASE_PASSWORD("appDatabasePassword",
			                "Application databases password"),
	P_APP_DATABASE_TYPE("appDatabaseType",
			            "Type of application database");


	private String _key;
	private String _description;

	private PreferenceServiceDatabaseKeys(final String key,
			final String description) {
		this._key = key;
		this._description = description;
	}

	@Override
    public String getDescription() {
		return this._description;
	}

	public String getKey() {
		return this._key;
	}

	@Override
    public String getPreferenceStoreId() {
		return this._key;
	}
}
