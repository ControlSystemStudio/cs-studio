package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Dieses Daten-Transfer-Objekt stellt hält die Konfiguration einer
 * AMS_FilterCond_ArrStrVal.
 * 
 * Das Create-Statement für die Datenbank hat folgendes Aussehen:
 * 
 * <pre>
 * create table AMS_FilterCond_ArrStrVal
 *  (
 *  iFilterConditionRef	INT NOT NULL,
 *  cCompValue		VARCHAR(128)
 *  );
 * </pre>
 */
@Entity
@Table(name = "AMS_FilterCond_ArrStrVal")
public class StringArrayFilterConditionCompareValuesDTO {
	@Id
	@Column(name = "iFilterConditionRef", nullable = false, unique = true)
	private int filterConditionRef;

	@Column(name = "cCompValue", length = 128)
	private String compValue;

	/**
	 * @return the filterConditionRef
	 */
	private int getFilterConditionRef() {
		return filterConditionRef;
	}

	/**
	 * @param filterConditionRef the filterConditionRef to set
	 */
	@SuppressWarnings("unused")
	private void setFilterConditionRef(int filterConditionRef) {
		this.filterConditionRef = filterConditionRef;
	}

	/**
	 * @return the compValue
	 */
	private String getCompValue() {
		return compValue;
	}

	/**
	 * @param compValue the compValue to set
	 */
	@SuppressWarnings("unused")
	private void setCompValue(String compValue) {
		this.compValue = compValue;
	}
	
	@Override
	public String toString() {
		final StringBuilder resultBuilder = new StringBuilder(this.getClass().getSimpleName());
		resultBuilder.append(": ");
		resultBuilder.append(this.getFilterConditionRef());
		resultBuilder.append(", ");
		resultBuilder.append(this.getCompValue());
		return resultBuilder.toString();
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
		if (!(obj instanceof StringArrayFilterConditionCompareValuesDTO))
			return false;
		final StringArrayFilterConditionCompareValuesDTO other = (StringArrayFilterConditionCompareValuesDTO) obj;
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
