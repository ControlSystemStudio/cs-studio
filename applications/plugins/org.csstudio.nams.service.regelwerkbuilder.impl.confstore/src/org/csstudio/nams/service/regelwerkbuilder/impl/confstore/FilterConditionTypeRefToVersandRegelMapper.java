package org.csstudio.nams.service.regelwerkbuilder.impl.confstore;

import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.ProcessVariableFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringArrayFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.TimeBasedFilterConditionDTO;


public enum FilterConditionTypeRefToVersandRegelMapper {
	// TODO find a nice place to settle

//	STRING(1), 
//	TIMEBASED(2), 
//	STRING_ARRAY(3), 
//	PV(4), 
//	JUNCTOR(5);

	STRING(StringFilterConditionDTO.class), 
	TIMEBASED(TimeBasedFilterConditionDTO.class),
	STRING_ARRAY(StringArrayFilterConditionDTO.class), 
	PV(ProcessVariableFilterConditionDTO.class), 
	JUNCTOR(JunctorConditionDTO.class);

	
//	private final int id;
	private final Class<? extends FilterConditionDTO> clazz;

	private FilterConditionTypeRefToVersandRegelMapper(Class<? extends FilterConditionDTO> clazz) {
//		this.id = iD;
		this.clazz = clazz;
	}

	public static FilterConditionTypeRefToVersandRegelMapper valueOf(Class<? extends FilterConditionDTO> clazz) {
		for (FilterConditionTypeRefToVersandRegelMapper enumC : FilterConditionTypeRefToVersandRegelMapper
				.values()) {
			if (enumC.clazz == clazz)
				return enumC;
		}
		return null;
	}
}
