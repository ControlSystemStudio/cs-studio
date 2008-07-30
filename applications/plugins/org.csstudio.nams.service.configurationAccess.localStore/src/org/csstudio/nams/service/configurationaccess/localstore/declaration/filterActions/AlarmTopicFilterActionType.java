/**
 * 
 */
package org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions;

public enum AlarmTopicFilterActionType implements FilterActionType {
	TOPIC(10, "Nachricht an Topic");

	private final int id;
	private final String description;

	private AlarmTopicFilterActionType(final int id, final String description) {
		this.id = id;
		this.description = description;
	}

	public short asDatabaseId() {
		return (short) this.id;
	}

	public String getDescription() {
		return this.description;
	}

}