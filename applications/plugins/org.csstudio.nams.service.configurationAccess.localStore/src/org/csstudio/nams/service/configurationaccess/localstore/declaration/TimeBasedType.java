package org.csstudio.nams.service.configurationaccess.localstore.declaration;

public enum TimeBasedType {
	// bestätigungsalarm bei timeout kein alarm?
	TIMEBEHAVIOR_CONFIRMED_THEN_ALARM,
	// aufhebungsalarm und bei timeout alarm?
	TIMEBEHAVIOR_TIMEOUT_THEN_ALARM;

	public TimeBasedType valueOf(int value) {
		switch (value) {
		case 0:
			return TIMEBEHAVIOR_CONFIRMED_THEN_ALARM;
		case 1:
			return TIMEBEHAVIOR_TIMEOUT_THEN_ALARM;
		default:
			throw new IllegalArgumentException("Unknown timebehavior");
		}
	}
}
