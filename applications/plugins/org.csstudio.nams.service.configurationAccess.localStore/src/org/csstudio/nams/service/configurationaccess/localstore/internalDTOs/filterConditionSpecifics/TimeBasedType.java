
package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics;

public enum TimeBasedType {
	// bestätigungsalarm bei timeout kein alarm?
	TIMEBEHAVIOR_CONFIRMED_THEN_ALARM((short) 0),
	// aufhebungsalarm und bei timeout alarm?
	TIMEBEHAVIOR_TIMEOUT_THEN_ALARM((short) 1);

	public static TimeBasedType valueOf(final short value) {
		switch (value) {
		case 0:
			return TIMEBEHAVIOR_CONFIRMED_THEN_ALARM;
		case 1:
			return TIMEBEHAVIOR_TIMEOUT_THEN_ALARM;
		default:
			throw new IllegalArgumentException("Unknown timebehavior");
		}
	}

	private final short intId;

	private TimeBasedType(final short intID) {
		this.intId = intID;

	}

	public short asShort() {
		return this.intId;
	}
}
