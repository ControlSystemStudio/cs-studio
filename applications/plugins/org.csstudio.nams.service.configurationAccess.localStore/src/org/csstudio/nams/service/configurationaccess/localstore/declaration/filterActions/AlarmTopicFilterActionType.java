/**
 * 
 */
package org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions;

public enum AlarmTopicFilterActionType implements FilterActionType {
	TOPIC(10, "Nachricht an Topic");

	private final int id;
	private final String description;

	private AlarmTopicFilterActionType(int id, String description) {
		this.id = id;
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}

	public short asDatabaseId() {
		return (short) id;
	}
	
}