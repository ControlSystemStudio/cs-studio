package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class FilterCondToFilterDTOPK implements Serializable {
	private static final long serialVersionUID = 4223187179567040076L;

	@Column(name = "iFilterConditionRef")
	private int iFilterConditionRef;

	@Column(name = "iFilterRef", nullable = false)
	private int iFilterRef;

	public FilterCondToFilterDTOPK() {
	    // Not used
	}

	public FilterCondToFilterDTOPK(final int iFilterRef,
			final int iFilterConditionRef) {
		this.iFilterRef = iFilterRef;
		this.iFilterConditionRef = iFilterConditionRef;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final FilterCondToFilterDTOPK other = (FilterCondToFilterDTOPK) obj;
		if (this.iFilterConditionRef != other.iFilterConditionRef) {
			return false;
		}
		if (this.iFilterRef != other.iFilterRef) {
			return false;
		}
		return true;
	}

	public int getIFilterConditionRef() {
		return this.iFilterConditionRef;
	}

	public int getIFilterRef() {
		return this.iFilterRef;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.iFilterConditionRef;
		result = prime * result + this.iFilterRef;
		return result;
	}

	public void setIFilterConditionRef(final int filterConditionRef) {
		this.iFilterConditionRef = filterConditionRef;
	}

	public void setIFilterRef(final int filterRef) {
		this.iFilterRef = filterRef;
	}
}
