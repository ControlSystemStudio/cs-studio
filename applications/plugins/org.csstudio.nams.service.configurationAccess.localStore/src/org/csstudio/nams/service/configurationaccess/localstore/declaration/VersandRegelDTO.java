package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionTypeDTO;

/**
 * Dieses Daten-Transfer-Objekt stellt hält die Konfiguration einer VersandRegel
 * (ehemals: FilterCondition).
 */
@Deprecated
@Entity
public class VersandRegelDTO {
//	public enum VersandRegelArten {
//		StringRegel, StringArrayRegel, PVRegel;
//	}
	
	@OneToOne(cascade=CascadeType.ALL)
	private FilterConditionDTO filterConditionDTO;
	
	@OneToOne(cascade=CascadeType.ALL)
	private FilterConditionTypeDTO filterConditionTypeDTO;

	/**
	 * @return the filterConditionDTO
	 */
	@SuppressWarnings("unused")
	private FilterConditionDTO getFilterConditionDTO() {
		return filterConditionDTO;
	}

	/**
	 * @param filterConditionDTO the filterConditionDTO to set
	 */
	@SuppressWarnings("unused")
	private void setFilterConditionDTO(FilterConditionDTO filterConditionDTO) {
		this.filterConditionDTO = filterConditionDTO;
	}

	/**
	 * @return the filterConditionTypeDTO
	 */
	@SuppressWarnings("unused")
	private FilterConditionTypeDTO getFilterConditionTypeDTO() {
		return filterConditionTypeDTO;
	}

	/**
	 * @param filterConditionTypeDTO the filterConditionTypeDTO to set
	 */
	@SuppressWarnings("unused")
	private void setFilterConditionTypeDTO(
			FilterConditionTypeDTO filterConditionTypeDTO) {
		this.filterConditionTypeDTO = filterConditionTypeDTO;
	}
	
}
