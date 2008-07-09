package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class FilterConditionsToFilterDTO_PK implements Serializable{
	private static final long serialVersionUID = 4223187179567040076L;

	@Column(name="iFilterConditionRef")
	private int iFilterConditionRef;
	
	@Column(name="iFilterRef", nullable=false)
	private int iFilterRef;

	public int getIFilterConditionRef() {
		return iFilterConditionRef;
	}

	public void setIFilterConditionRef(int filterConditionRef) {
		iFilterConditionRef = filterConditionRef;
	}

	public int getIFilterRef() {
		return iFilterRef;
	}

	public void setIFilterRef(int filterRef) {
		iFilterRef = filterRef;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + iFilterConditionRef;
		result = prime * result + iFilterRef;
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
		final FilterConditionsToFilterDTO_PK other = (FilterConditionsToFilterDTO_PK) obj;
		if (iFilterConditionRef != other.iFilterConditionRef)
			return false;
		if (iFilterRef != other.iFilterRef)
			return false;
		return true;
	}

	
}
