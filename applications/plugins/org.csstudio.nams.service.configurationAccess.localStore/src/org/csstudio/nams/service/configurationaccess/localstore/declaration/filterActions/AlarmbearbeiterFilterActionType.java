/**
 * 
 */
package org.csstudio.nams.service.configurationaccess.localstore.declaration.filterActions;

public enum AlarmbearbeiterFilterActionType implements FilterActionType {
	SMS(1, "SMS an Person"), VMAIL(4, "VMail an Person"), EMAIL(7,
			"EMail an Person");

	private final int id;
	private final String description;

	private AlarmbearbeiterFilterActionType(final int id,
			final String description) {
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