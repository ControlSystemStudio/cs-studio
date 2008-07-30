/**
 * 
 */
package org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions;

public enum AlarmbearbeitergruppenFilterActionType implements FilterActionType {
	SMS(2, "SMS an Gruppe"), SMS_Best(3, "SMS an Gruppe Best."), VMAIL(5,
			"VMail an Gruppe"), VMAIL_Best(6, "VMail an Gruppe Best."), EMAIL(
			8, "EMail an Gruppe"), EMAIL_Best(9, "EMail an Gruppe Best.");

	private final int key;
	private final String description;

	private AlarmbearbeitergruppenFilterActionType(final int key,
			final String description) {
		this.key = key;
		this.description = description;
	}

	public short asDatabaseId() {
		return (short) this.key;
	}

	public String getDescription() {
		return this.description;
	}

}