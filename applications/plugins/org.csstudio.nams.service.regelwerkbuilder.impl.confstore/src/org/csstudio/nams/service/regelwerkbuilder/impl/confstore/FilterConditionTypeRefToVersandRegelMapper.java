package org.csstudio.nams.service.regelwerkbuilder.impl.confstore;

import org.csstudio.nams.common.material.regelwerk.OderVersandRegel;
import org.csstudio.nams.common.material.regelwerk.ProcessVariableRegel;
import org.csstudio.nams.common.material.regelwerk.StringRegel;
import org.csstudio.nams.common.material.regelwerk.TimeBasedRegel;
import org.csstudio.nams.common.material.regelwerk.UndVersandRegel;
import org.csstudio.nams.common.material.regelwerk.VersandRegel;

public enum FilterConditionTypeRefToVersandRegelMapper {
	// TODO find a nice place to settle

	UND(0, UndVersandRegel.class), STRING(1, StringRegel.class), TIMEBASED(2,
			TimeBasedRegel.class), STRING_ARRAY(3, null), PV(4,
			ProcessVariableRegel.class), ODER(5, OderVersandRegel.class);

	private final int id;
	private final Class<?> clazz;

	private FilterConditionTypeRefToVersandRegelMapper(int iD, Class<?> clazz) {
		this.id = iD;
		this.clazz = clazz;
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
