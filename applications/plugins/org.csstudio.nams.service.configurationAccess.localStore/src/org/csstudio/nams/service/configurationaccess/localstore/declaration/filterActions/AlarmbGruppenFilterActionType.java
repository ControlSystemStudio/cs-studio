
/**
 * 
 */

package org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions;

public enum AlarmbGruppenFilterActionType implements FilterActionType {
	SMS(2, "SMS an Gruppe"), SMS_Best(3, "SMS an Gruppe Best."), VMAIL(5,
			"VMail an Gruppe"), VMAIL_Best(6, "VMail an Gruppe Best."), EMAIL(
			8, "EMail an Gruppe"), EMAIL_Best(9, "EMail an Gruppe Best.");

	private final int _key;
	private final String _description;

	private AlarmbGruppenFilterActionType(final int key,
			final String description) {
		this._key = key;
		this._description = description;
	}

	@Override
    public short asDatabaseId() {
		return (short) this._key;
	}

	@Override
    public String getDescription() {
		return this._description;
	}
}