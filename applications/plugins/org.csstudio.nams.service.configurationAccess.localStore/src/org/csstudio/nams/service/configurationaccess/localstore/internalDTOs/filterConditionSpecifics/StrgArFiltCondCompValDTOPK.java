
package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class StrgArFiltCondCompValDTOPK implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7501395016232068023L;

	public static long getSerialVersionUID() {
		return StrgArFiltCondCompValDTOPK.serialVersionUID;
	}

	@Column(name = "iFilterConditionRef", nullable = false, insertable = false, updatable = false)
	private int filterConditionRef;

	@Column(name = "cCompValue", length = 128)
	private String compValue;

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
		final StrgArFiltCondCompValDTOPK other = (StrgArFiltCondCompValDTOPK) obj;
		if (this.compValue == null) {
			if (other.compValue != null) {
				return false;
			}
		} else if (!this.compValue.equals(other.compValue)) {
			return false;
		}
		if (this.filterConditionRef != other.filterConditionRef) {
			return false;
		}
		return true;
	}

	public String getCompValue() {
		return this.compValue;
	}

	public int getFilterConditionRef() {
		return this.filterConditionRef;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.compValue == null) ? 0 : this.compValue.hashCode());
		result = prime * result + this.filterConditionRef;
		return result;
	}

	public void setCompValue(final String compValue) {
		this.compValue = compValue;
	}

	public void setFilterConditionRef(final int filterConditionRef) {
		this.filterConditionRef = filterConditionRef;
	}
}
