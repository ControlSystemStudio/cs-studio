
/**
 * 
 */

package org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions;

public enum AlarmbFilterActionType implements FilterActionType {
	SMS(1, "SMS an Person"), VMAIL(4, "VMail an Person"), EMAIL(7,
			"EMail an Person");

	private final int _id;
	private final String _description;

	private AlarmbFilterActionType(final int id,
			final String description) {
		this._id = id;
		this._description = description;
	}

	@Override
    public short asDatabaseId() {
		return (short) this._id;
	}

	@Override
    public String getDescription() {
		return this._description;
	}
}