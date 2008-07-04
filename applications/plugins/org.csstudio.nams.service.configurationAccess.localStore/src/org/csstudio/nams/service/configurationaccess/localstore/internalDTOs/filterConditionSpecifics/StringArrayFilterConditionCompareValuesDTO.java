package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
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
	
	@EmbeddedId
	private StringArrayFilterConditionCompareValuesDTO_PK pk;
	
	/**
	 * @return the filterConditionRef
	 */
	public int getFilterConditionRef() {
		return pk.getFilterConditionRef();
	}

	/**
	 * @param filterConditionRef the filterConditionRef to set
	 */
	@SuppressWarnings("unused")
	private void setFilterConditionRef(int filterConditionRef) {
		this.pk.setFilterConditionRef(filterConditionRef);
	}

	/**
	 * @return the compValue
	 */
	public String getCompValue() {
		return pk.getCompValue();
	}

	/**
	 * @param compValue the compValue to set
	 */
	@SuppressWarnings("unused")
	private void setCompValue(String compValue) {
		this.pk.setCompValue(compValue);
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
		result = prime * result + ((pk == null) ? 0 : pk.hashCode());
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
		final StringArrayFilterConditionCompareValuesDTO other = (StringArrayFilterConditionCompareValuesDTO) obj;
		if (pk == null) {
			if (other.pk != null)
				return false;
		} else if (!pk.equals(other.pk))
			return false;
		return true;
	}
}