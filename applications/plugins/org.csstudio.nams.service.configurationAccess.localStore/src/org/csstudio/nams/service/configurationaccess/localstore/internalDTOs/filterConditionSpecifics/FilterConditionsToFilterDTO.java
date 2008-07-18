package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.NewAMSConfigurationElementDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;

/**
 * Enthält die Daten der Aggregationstabelle von {@link FilterConditionDTO} zu
 * {@link FilterDTO}s.
 * 
 * <pre>
 * create table AMS_Filter_FilterCondition
 *  (
 *  iFilterRef		INT,
 *  iFilterConditionRef	INT,
 *  iPos			INT,
 *  PRIMARY KEY (iFilterRef,iFilterConditionRef)
 *  );
 * </pre>
 */
@Entity
@Table(name = "AMS_Filter_FilterCondition")
public class FilterConditionsToFilterDTO implements NewAMSConfigurationElementDTO {
	@EmbeddedId
	private FilterConditionsToFilterDTO_PK filterCTFPK;

	public FilterConditionsToFilterDTO() {
		filterCTFPK = new FilterConditionsToFilterDTO_PK();
	}
	
	/**
	 * This column is no more in use.
	 */
	@Deprecated
	@Column(name = "iPos")
	private int iPos = 0; // INT

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(this.getClass().getSimpleName());
		builder.append(": ");
		builder.append("iFilterRef: ");
		builder.append(filterCTFPK.getIFilterRef());
		builder.append(", iFilterConditionRef: ");
		builder.append(filterCTFPK.getIFilterConditionRef());
		builder.append(", iPos: ");
		builder.append(iPos);
		return builder.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + filterCTFPK.getIFilterConditionRef();
		result = prime * result + filterCTFPK.getIFilterRef();
		result = prime * result + iPos;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof FilterConditionsToFilterDTO))
			return false;
		final FilterConditionsToFilterDTO other = (FilterConditionsToFilterDTO) obj;
		if (filterCTFPK.getIFilterRef() != other.getIFilterRef())
			return false;
		if (filterCTFPK.getIFilterRef() != other.getIFilterRef())
			return false;
		if (iPos != other.iPos)
			return false;
		return true;
	}

	/**
	 * @return the iFilterRef
	 */
	public int getIFilterRef() {
		return filterCTFPK.getIFilterRef();
	}

	/**
	 * @param filterRef the iFilterRef to set
	 */
	@SuppressWarnings("unused")
	public void setIFilterRef(int filterRef) {
		filterCTFPK.setIFilterRef(filterRef);
	}

	/**
	 * @return the iFilterConditionRef
	 */
	public int getIFilterConditionRef() {
		return filterCTFPK.getIFilterConditionRef();
	}

	/**
	 * @param filterConditionRef the iFilterConditionRef to set
	 */
	@SuppressWarnings("unused")
	public void setIFilterConditionRef(int filterConditionRef) {
		filterCTFPK.setIFilterConditionRef(filterConditionRef);
	}

	/**
	 * @return the iPos
	 */
	@SuppressWarnings("unused")
	private int getIPos() {
		return iPos;
	}

	/**
	 * @param pos the iPos to set
	 */
	@SuppressWarnings("unused")
	private void setIPos(int pos) {
		iPos = pos;
	}
	public void setFilterConditionsToFilterDTO_PK(FilterConditionsToFilterDTO_PK key) {
		filterCTFPK = key;
	}

	public String getUniqueHumanReadableName() {
		return toString();
	}

	public boolean isInCategory(int categoryDBId) {
		return false;
	}
	
}
