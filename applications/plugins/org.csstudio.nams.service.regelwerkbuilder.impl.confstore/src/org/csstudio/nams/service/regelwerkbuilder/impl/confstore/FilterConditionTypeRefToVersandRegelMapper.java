package org.csstudio.nams.service.regelwerkbuilder.impl.confstore;

import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorConditionForFilterTreeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.NegationConditionForFilterTreeDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.ProcessVariableFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringArrayFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.StringFilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.TimeBasedFilterConditionDTO;

public enum FilterConditionTypeRefToVersandRegelMapper {
	// TODO find a nice place to settle

	// STRING(1),
	// TIMEBASED(2),
	// STRING_ARRAY(3),
	// PV(4),
	// JUNCTOR(5);

	STRING(StringFilterConditionDTO.class), TIMEBASED(
			TimeBasedFilterConditionDTO.class), STRING_ARRAY(
			StringArrayFilterConditionDTO.class), PV(
			ProcessVariableFilterConditionDTO.class), JUNCTOR(
			JunctorConditionDTO.class), JUNCTOR_FOR_TREE(
			JunctorConditionForFilterTreeDTO.class), NEGATION(
			NegationConditionForFilterTreeDTO.class);

	public static FilterConditionTypeRefToVersandRegelMapper valueOf(
			final Class<? extends FilterConditionDTO> clazz) {
		for (final FilterConditionTypeRefToVersandRegelMapper enumC : FilterConditionTypeRefToVersandRegelMapper
				.values()) {
			if (enumC.clazz == clazz) {
				return enumC;
			}
		}
		return null;
	}

	// private final int id;
	private final Class<? extends FilterConditionDTO> clazz;

	private FilterConditionTypeRefToVersandRegelMapper(
			final Class<? extends FilterConditionDTO> clazz) {
		// this.id = iD;
		this.clazz = clazz;
	}
}
