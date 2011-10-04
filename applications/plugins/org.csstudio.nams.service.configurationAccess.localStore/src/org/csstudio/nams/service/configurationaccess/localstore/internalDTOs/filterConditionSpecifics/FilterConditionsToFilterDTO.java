
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
public class FilterConditionsToFilterDTO implements
		NewAMSConfigurationElementDTO {
	@EmbeddedId
	private FilterCondToFilterDTOPK filterCTFPK;

	/**
	 * This column is no more in use.
	 */
	@Deprecated
	@Column(name = "iPos")
	private int iPos = 0; // INT

	public FilterConditionsToFilterDTO() {
		this.filterCTFPK = new FilterCondToFilterDTOPK();
	}

	public FilterConditionsToFilterDTO(final int iFilterRef,
			final int iFilterConditionRef) {
		this.filterCTFPK = new FilterCondToFilterDTOPK(iFilterRef,
				iFilterConditionRef);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof FilterConditionsToFilterDTO)) {
			return false;
		}
		final FilterConditionsToFilterDTO other = (FilterConditionsToFilterDTO) obj;
		if (this.filterCTFPK.getIFilterRef() != other.getIFilterRef()) {
			return false;
		}
		if (this.filterCTFPK.getIFilterRef() != other.getIFilterRef()) {
			return false;
		}
		// if (iPos != other.iPos)
		// return false;
		return true;
	}

	/**
	 * @return the iFilterConditionRef
	 */
	public int getIFilterConditionRef() {
		return this.filterCTFPK.getIFilterConditionRef();
	}

	/**
	 * @return the iFilterRef
	 */
	public int getIFilterRef() {
		return this.filterCTFPK.getIFilterRef();
	}

	public String getUniqueHumanReadableName() {
		return this.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.filterCTFPK.getIFilterConditionRef();
		result = prime * result + this.filterCTFPK.getIFilterRef();
		// result = prime * result + iPos;
		return result;
	}

	public boolean isInCategory(final int categoryDBId) {
		return false;
	}

	public void setFilterConditionsToFilterDTO_PK(
			final FilterCondToFilterDTOPK key) {
		this.filterCTFPK = key;
	}

	/**
	 * @param filterConditionRef
	 *            the iFilterConditionRef to set
	 */
	@SuppressWarnings("unused")
	public void setIFilterConditionRef(final int filterConditionRef) {
		this.filterCTFPK.setIFilterConditionRef(filterConditionRef);
	}

	/**
	 * @param filterRef
	 *            the iFilterRef to set
	 */
	@SuppressWarnings("unused")
	public void setIFilterRef(final int filterRef) {
		this.filterCTFPK.setIFilterRef(filterRef);
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder(this.getClass()
				.getSimpleName());
		builder.append(": ");
		builder.append("iFilterRef: ");
		builder.append(this.filterCTFPK.getIFilterRef());
		builder.append(", iFilterConditionRef: ");
		builder.append(this.filterCTFPK.getIFilterConditionRef());
		builder.append(", iPos: ");
		builder.append(this.iPos);
		return builder.toString();
	}

	/**
	 * @return the iPos
	 */
	@SuppressWarnings("unused")
	private int getIPos() {
		return this.iPos;
	}

	/**
	 * @param pos
	 *            the iPos to set
	 */
	@SuppressWarnings("unused")
	private void setIPos(final int pos) {
		this.iPos = pos;
	}

}
