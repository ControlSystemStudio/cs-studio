package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class StringArrayFilterConditionCompareValuesDTO_PK implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7501395016232068023L;

	@Column(name = "iFilterConditionRef", nullable = false, insertable=false, updatable=false)
	private int filterConditionRef;

	@Column(name = "cCompValue", length = 128)
	private String compValue;

	public int getFilterConditionRef() {
		return filterConditionRef;
	}

	public void setFilterConditionRef(int filterConditionRef) {
		this.filterConditionRef = filterConditionRef;
	}

	public String getCompValue() {
		return compValue;
	}

	public void setCompValue(String compValue) {
		this.compValue = compValue;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((compValue == null) ? 0 : compValue.hashCode());
		result = prime * result + filterConditionRef;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final StringArrayFilterConditionCompareValuesDTO_PK other = (StringArrayFilterConditionCompareValuesDTO_PK) obj;
		if (compValue == null) {
			if (other.compValue != null)
				return false;
		} else if (!compValue.equals(other.compValue))
			return false;
		if (filterConditionRef != other.filterConditionRef)
			return false;
		return true;
	}
	
}
