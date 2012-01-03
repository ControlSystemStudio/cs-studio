
package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.NewAMSConfigurationElementDTO;

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
public class StrgArFiltCondCompValDTO implements
		NewAMSConfigurationElementDTO {

	@EmbeddedId
	private StrgArFiltCondCompValDTOPK pk;

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
		final StrgArFiltCondCompValDTO other = (StrgArFiltCondCompValDTO) obj;
		if (this.pk == null) {
			if (other.pk != null) {
				return false;
			}
		} else if (!this.pk.equals(other.pk)) {
			return false;
		}
		return true;
	}

	/**
	 * @return the compValue
	 */
	public String getCompValue() {
		return this.pk.getCompValue();
	}

	/**
	 * @return the filterConditionRef
	 */
	public int getFilterConditionRef() {
		return this.pk.getFilterConditionRef();
	}

	@Override
    public String getUniqueHumanReadableName() {
		return this.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.pk == null) ? 0 : this.pk.hashCode());
		return result;
	}

	@Override
    public boolean isInCategory(final int categoryDBId) {
		return false;
	}

	public void setPk(final StrgArFiltCondCompValDTOPK pk) {
		this.pk = pk;
	}

	public void setPK(final int filterConditionID) {
		this.pk.setFilterConditionRef(filterConditionID);

	}

	@Override
	public String toString() {
		final StringBuilder resultBuilder = new StringBuilder(this.getClass()
				.getSimpleName());
		resultBuilder.append(": ");
		resultBuilder.append(this.getFilterConditionRef());
		resultBuilder.append(", ");
		resultBuilder.append(this.getCompValue());
		return resultBuilder.toString();
	}

	/**
	 * @param compValue
	 *            the compValue to set
	 */
	@SuppressWarnings("unused")
	private void setCompValue(final String compValue) {
		this.pk.setCompValue(compValue);
	}

	/**
	 * @param filterConditionRef
	 *            the filterConditionRef to set
	 */
	@SuppressWarnings("unused")
	private void setFilterConditionRef(final int filterConditionRef) {
		this.pk.setFilterConditionRef(filterConditionRef);
	}
}