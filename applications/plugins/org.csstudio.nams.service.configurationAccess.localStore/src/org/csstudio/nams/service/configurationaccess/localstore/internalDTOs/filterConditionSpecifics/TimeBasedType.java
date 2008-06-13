package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics;

public enum TimeBasedType {
	// bestätigungsalarm bei timeout kein alarm?
	TIMEBEHAVIOR_CONFIRMED_THEN_ALARM((short) 0),
	// aufhebungsalarm und bei timeout alarm?
	TIMEBEHAVIOR_TIMEOUT_THEN_ALARM((short) 1);
	
	private final short intId;

	private TimeBasedType(short intID){
		this.intId = intID;
		
	}

	public static TimeBasedType valueOf(short value) {
		switch (value) {
		case 0:
			return TIMEBEHAVIOR_CONFIRMED_THEN_ALARM;
		case 1:
			return TIMEBEHAVIOR_TIMEOUT_THEN_ALARM;
		default:
			throw new IllegalArgumentException("Unknown timebehavior");
		}
	}
	
	public short asShort(){
		return intId;
	}
}
