package org.csstudio.nams.service.regelwerkbuilder.impl.confstore;


public enum FilterConditionTypeRefToVersandRegelMapper {
	// TODO find a nice place to settle

	STRING(1), 
	TIMEBASED(2), 
	STRING_ARRAY(3), 
	PV(4), 
	JUNCTOR(5);

//	STRING(1, StringRegel.class), 
//	TIMEBASED(2, TimeBasedRegel.class), 
//	STRING_ARRAY(3, null), 
//	PV(4,ProcessVariableRegel.class), 
//	JUNCTOR(5, OderVersandRegel.class);

	
	private final int id;
//	private final Class<?> clazz;

	private FilterConditionTypeRefToVersandRegelMapper(int iD/*, Class<?> clazz*/) {
		this.id = iD;
//		this.clazz = clazz;
	}

	public static FilterConditionTypeRefToVersandRegelMapper valueOf(int iD) {
		for (FilterConditionTypeRefToVersandRegelMapper enumC : FilterConditionTypeRefToVersandRegelMapper
				.values()) {
			if (enumC.id == iD)
				return enumC;
		}
		return null;
	}
}
