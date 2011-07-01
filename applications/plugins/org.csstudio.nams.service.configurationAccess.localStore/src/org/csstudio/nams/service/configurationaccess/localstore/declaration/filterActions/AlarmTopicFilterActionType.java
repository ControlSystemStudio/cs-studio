
/**
 * 
 */

package org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions;

public enum AlarmTopicFilterActionType implements FilterActionType {
	TOPIC(10, "Nachricht an Topic");

	private final int _id;
	private final String _description;

	private AlarmTopicFilterActionType(final int id, final String description) {
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