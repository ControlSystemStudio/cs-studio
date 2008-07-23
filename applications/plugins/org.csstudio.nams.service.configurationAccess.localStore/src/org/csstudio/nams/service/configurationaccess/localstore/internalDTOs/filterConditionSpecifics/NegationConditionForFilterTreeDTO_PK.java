package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class NegationConditionForFilterTreeDTO_PK implements Serializable {

	private static final long serialVersionUID = 3738876076150454709L;

	@SuppressWarnings("unused")
	@Column(name = "iFilterConditionRef", nullable = false, updatable = false, insertable = false)
	private int iFilterConditionRef;

	@SuppressWarnings("unused")
	@Column(name = "iNegatedFCRef", nullable = false)
	private int iNegatedFCRef;

	public int getINegatedFCRef() {
		return iNegatedFCRef;
	}

	public void setINegatedFCRef(int negatedFCRef) {
		iNegatedFCRef = negatedFCRef;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + iFilterConditionRef;
		result = prime * result + iNegatedFCRef;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof NegationConditionForFilterTreeDTO_PK))
			return false;
		final NegationConditionForFilterTreeDTO_PK other = (NegationConditionForFilterTreeDTO_PK) obj;
		if (iFilterConditionRef != other.iFilterConditionRef)
			return false;
		if (iNegatedFCRef != other.iNegatedFCRef)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": iFilterConditionRef="
				+ this.iFilterConditionRef + ", iNegatedFCRef="
				+ this.iNegatedFCRef;
	}
}
