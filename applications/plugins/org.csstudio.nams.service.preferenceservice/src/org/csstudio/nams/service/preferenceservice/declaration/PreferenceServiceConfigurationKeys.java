
package org.csstudio.nams.service.preferenceservice.declaration;

/**
 * @since 15.11.2011
 */
public enum PreferenceServiceConfigurationKeys implements HoldsAPreferenceId {

    FILTER_THREAD_COUNT("filterThreadCount",
	                             "Number of threads used to process alarm messages");
    
	private String _key;
	private String _description;

	private PreferenceServiceConfigurationKeys(final String key,
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
