
package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.NewAMSConfigurationElementDTO;

/**
 * Dieses Daten-Transfer-Objekt stellt hält die Konfiguration einer
 * AMS_FilterCondition.
 * 
 * Das Create-Statement für die Datenbank hat folgendes Aussehen:
 * 
 * <pre>
 * create table AMS_FilterCondition
 *  (
 *  iFilterConditionID	INT NOT NULL,
 *  iGroupRef		INT default -1 NOT NULL,
 *  cName			VARCHAR(128),
 *  cDesc			VARCHAR(256),
 *  iFilterConditionTypeRef INT,
 *  PRIMARY KEY(iFilterConditionID)
 *  );
 * </pre>
 */
@Entity
@SequenceGenerator(name="filter_condition_id", sequenceName="AMS_FilterCondition_ID", allocationSize=1)
@Table(name = "AMS_FilterCondition")
@Inheritance(strategy = InheritanceType.JOINED)
public class FilterConditionDTO implements NewAMSConfigurationElementDTO {

	/**
	 * Dieses Feld wird von NAMS nicht benötigt und ist lediglich der
	 * Vollständigkeithalber mit angebenen.
	 */
	@SuppressWarnings("unused")
	@Deprecated
	@Column(name = "iFilterConditionTypeRef")
	private int filterCondtionTypeRef;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="filter_condition_id")
	@Column(name = "iFilterConditionID", nullable = false, unique = true)
	private int iFilterConditionID;

	@Column(name = "iGroupRef", nullable = false)
	private int iGroupRef = -1;

	@Column(name = "cName", length = 128)
	private String cName;

	@Column(name = "cDesc", length = 256)
	private String cDesc;

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof FilterConditionDTO)) {
			return false;
		}
		final FilterConditionDTO other = (FilterConditionDTO) obj;
		if (this.cDesc == null) {
			if (other.cDesc != null) {
				return false;
			}
		} else if (!this.cDesc.equals(other.cDesc)) {
			return false;
		}
		if (this.cName == null) {
			if (other.cName != null) {
				return false;
			}
		} else if (!this.cName.equals(other.cName)) {
			return false;
		}
		if (this.iFilterConditionID != other.iFilterConditionID) {
			return false;
		}
		if (this.iGroupRef != other.iGroupRef) {
			return false;
		}
		return true;
	}

	/**
	 * @return the cDesc
	 */
	public String getCDesc() {
		return this.cDesc;
	}

	/**
	 * @return the cName
	 */
	public String getCName() {
		return this.cName;
	}

	/**
	 * ONLY TO BE USED FOR MAPPING PURPOSES.
	 * 
	 * Returns the database id of this condition.
	 */
	@SuppressWarnings("unused")
	public int getIFilterConditionID() {
		return this.iFilterConditionID;
	}

	/**
	 * @return the iGroupRef
	 */
	@SuppressWarnings("unused")
	public int getIGroupRef() {
		return this.iGroupRef;
	}

	public String getUniqueHumanReadableName() {
		return this.getCName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.cDesc == null) ? 0 : this.cDesc.hashCode());
		result = prime * result
				+ ((this.cName == null) ? 0 : this.cName.hashCode());
		result = prime * result + this.iFilterConditionID;
		result = prime * result + this.iGroupRef;
		return result;
	}

	public boolean isInCategory(final int categoryDBId) {
		return this.getIGroupRef() == categoryDBId;
	}

	/**
	 * @param desc
	 *            the cDesc to set
	 */
	public void setCDesc(final String desc) {
		this.cDesc = desc;
	}

	/**
	 * @param name
	 *            the cName to set
	 */
	@SuppressWarnings("unused")
	public void setCName(final String name) {
		this.cName = name;
	}

	/**
	 * ONLY TO BE USED FOR MAPPING PURPOSES.
	 * 
	 * Sets the database id of this condition.
	 */
	@SuppressWarnings("unused")
	public void setIFilterConditionID(final int filterConditionID) {
		this.iFilterConditionID = filterConditionID;
	}

	/**
	 * ONLY TO BE USED FOR MAPPING PURPOSES.
	 * 
	 * @param groupRef
	 *            Die Rubrik dieses DTOs.
	 */
	@SuppressWarnings("unused")
	public void setIGroupRef(final int groupRef) {
		this.iGroupRef = groupRef;
	}

	@Override
	public String toString() {
		final StringBuilder resultBuilder = new StringBuilder(this.getClass()
				.getSimpleName());
		resultBuilder.append(": ");
		resultBuilder.append(this.getCName());
		resultBuilder.append(" (");
		resultBuilder.append(this.getIFilterConditionID());
		resultBuilder.append(")");
		resultBuilder.append(", description: ");
		resultBuilder.append(this.getCDesc());
		return resultBuilder.toString();
	}

}
