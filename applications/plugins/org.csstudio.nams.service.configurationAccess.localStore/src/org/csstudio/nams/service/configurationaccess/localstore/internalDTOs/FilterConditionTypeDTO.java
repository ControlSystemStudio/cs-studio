
package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Dieses Daten-Transfer-Objekt stellt hält die Konfiguration eines
 * AMS_FilterConditionType.
 * 
 * Das Create-Statement für die Datenbank hat folgendes Aussehen:
 * 
 * <pre>
 * create table AMS_FilterConditionType
 *  (
 *  iFilterConditionTypeID	INT,
 *  cName			VARCHAR(128),
 *  cClass			VARCHAR(256),
 *  cClassUI		VARCHAR(256),
 *  PRIMARY KEY(iFilterConditionTypeID)
 *  );
 * </pre>
 */
@Entity
//@SequenceGenerator(name="filter_condition_type_id", sequenceName="AMS_FilterConditionType_ID")
@Table(name = "AMS_FilterConditionType")
public class FilterConditionTypeDTO {

	@Id
//	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="filter_condition_type_id")
	@Column(name = "iFilterConditionTypeID", nullable = false, unique = true)
	private int iFilterConditionTypeID;

	@Column(name = "cName", length = 128)
	private String cName;

	@Deprecated
	@Column(name = "cClass", length = 256)
	private String cClass;

	/**
	 * @deprecated No more in use.
	 */
	@Deprecated
	@Column(name = "cClassUI", length = 256)
	private String cClassUI = null;

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof FilterConditionTypeDTO)) {
			return false;
		}
		final FilterConditionTypeDTO other = (FilterConditionTypeDTO) obj;
		if (this.cClass == null) {
			if (other.cClass != null) {
				return false;
			}
		} else if (!this.cClass.equals(other.cClass)) {
			return false;
		}
		if (this.cClassUI == null) {
			if (other.cClassUI != null) {
				return false;
			}
		} else if (!this.cClassUI.equals(other.cClassUI)) {
			return false;
		}
		if (this.cName == null) {
			if (other.cName != null) {
				return false;
			}
		} else if (!this.cName.equals(other.cName)) {
			return false;
		}
		if (this.iFilterConditionTypeID != other.iFilterConditionTypeID) {
			return false;
		}
		return true;
	}

	/**
	 * @return the cName
	 */
	@SuppressWarnings("unused")
	public String getCName() {
		return this.cName;
	}

	/**
	 * @return the iFilterConditionTypeID
	 */
	public int getIFilterConditionTypeID() {
		return this.iFilterConditionTypeID;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.cClass == null) ? 0 : this.cClass.hashCode());
		result = prime * result
				+ ((this.cClassUI == null) ? 0 : this.cClassUI.hashCode());
		result = prime * result
				+ ((this.cName == null) ? 0 : this.cName.hashCode());
		result = prime * result + this.iFilterConditionTypeID;
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder resultBuilder = new StringBuilder(this.getClass()
				.getSimpleName());
		resultBuilder.append(": ");
		resultBuilder.append(this.getCName());
		resultBuilder.append(" (");
		resultBuilder.append(this.getIFilterConditionTypeID());
		resultBuilder.append("), Class: ");
		resultBuilder.append(this.getCClass());
		resultBuilder.append("), UIClass: ");
		resultBuilder.append(this.getCClassUI());
		return resultBuilder.toString();
	}

	/**
	 * @return the cClass
	 */
	@SuppressWarnings("unused")
	private String getCClass() {
		return this.cClass;
	}

	/**
	 * @return the cClassUI
	 */
	@SuppressWarnings("unused")
	private String getCClassUI() {
		return this.cClassUI;
	}

	/**
	 * @param class1
	 *            the cClass to set
	 */
	@SuppressWarnings("unused")
	private void setCClass(final String class1) {
		this.cClass = class1;
	}

	/**
	 * @param classUI
	 *            the cClassUI to set
	 */
	@SuppressWarnings("unused")
	private void setCClassUI(final String classUI) {
		this.cClassUI = classUI;
	}

	/**
	 * @param name
	 *            the cName to set
	 */
	@SuppressWarnings("unused")
	private void setCName(final String name) {
		this.cName = name;
	}

	/**
	 * @param filterConditionTypeID
	 *            the iFilterConditionTypeID to set
	 */
	@SuppressWarnings("unused")
	private void setIFilterConditionTypeID(final int filterConditionTypeID) {
		this.iFilterConditionTypeID = filterConditionTypeID;
	}
}
