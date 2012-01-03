
package org.csstudio.nams.service.preferenceservice.declaration;

public enum PreferenceServiceFilterKeys implements HoldsAPreferenceId {
	P_FILTER_KEYFIELDS("filterKeyFields");

	private String _key;
	private String _description;

	private PreferenceServiceFilterKeys(final String key) {
		this(key, "Description not available");
	}

	private PreferenceServiceFilterKeys(final String key,
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
