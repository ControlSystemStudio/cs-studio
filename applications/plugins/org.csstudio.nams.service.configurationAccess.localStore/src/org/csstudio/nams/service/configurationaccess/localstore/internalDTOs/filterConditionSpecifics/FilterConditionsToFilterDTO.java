package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterDTO;
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
public class FilterConditionsToFilterDTO {
	
	@Id
	@Column(name = "iFilterRef")
	private int iFilterRef; // INT,
	@Id
	@Column(name = "iFilterConditionRef")
	private int iFilterConditionRef; // INT,

	@Column(name = "iPos")
	private int iPos; // INT

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(this.getClass().getSimpleName());
		builder.append(": ");
		builder.append("iFilterRef: ");
		builder.append(iFilterRef);
		builder.append(", iFilterConditionRef: ");
		builder.append(iFilterConditionRef);
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
		result = prime * result + iFilterConditionRef;
		result = prime * result + iFilterRef;
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
		if (iFilterConditionRef != other.iFilterConditionRef)
			return false;
		if (iFilterRef != other.iFilterRef)
			return false;
		if (iPos != other.iPos)
			return false;
		return true;
	}

	/**
	 * @return the iFilterRef
	 */
	private int getIFilterRef() {
		return iFilterRef;
	}

	/**
	 * @param filterRef the iFilterRef to set
	 */
	private void setIFilterRef(int filterRef) {
		iFilterRef = filterRef;
	}

	/**
	 * @return the iFilterConditionRef
	 */
	private int getIFilterConditionRef() {
		return iFilterConditionRef;
	}

	/**
	 * @param filterConditionRef the iFilterConditionRef to set
	 */
	private void setIFilterConditionRef(int filterConditionRef) {
		iFilterConditionRef = filterConditionRef;
	}

	/**
	 * @return the iPos
	 */
	private int getIPos() {
		return iPos;
	}

	/**
	 * @param pos the iPos to set
	 */
	private void setIPos(int pos) {
		iPos = pos;
	}
	
	
}
