package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
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
@Table(name = "AMS_FilterConditionType")
public class FilterConditionTypeDTO {

	@Id
	@GeneratedValue
	@Column(name = "iFilterConditionTypeID", nullable=false, unique=true)
	private int iFilterConditionTypeID;

	@Column(name = "cName", length=128)
	private String cName;

	@Deprecated
	@Column(name = "cClass", length=256)
	private String cClass;

	/**
	 * @deprecated No more in use.
	 */
	@Deprecated
	@Column(name = "cClassUI", length=256)
	private String cClassUI = null;

	/**
	 * @return the iFilterConditionTypeID
	 */
	public int getIFilterConditionTypeID() {
		return iFilterConditionTypeID;
	}

	/**
	 * @param filterConditionTypeID
	 *            the iFilterConditionTypeID to set
	 */
	@SuppressWarnings("unused")
	private void setIFilterConditionTypeID(int filterConditionTypeID) {
		iFilterConditionTypeID = filterConditionTypeID;
	}

	/**
	 * @return the cName
	 */
	@SuppressWarnings("unused")
	public String getCName() {
		return cName;
	}

	/**
	 * @param name
	 *            the cName to set
	 */
	@SuppressWarnings("unused")
	private void setCName(String name) {
		cName = name;
	}

	/**
	 * @return the cClass
	 */
	@SuppressWarnings("unused")
	private String getCClass() {
		return cClass;
	}

	/**
	 * @param class1
	 *            the cClass to set
	 */
	@SuppressWarnings("unused")
	private void setCClass(String class1) {
		cClass = class1;
	}

	/**
	 * @return the cClassUI
	 */
	@SuppressWarnings("unused")
	private String getCClassUI() {
		return cClassUI;
	}

	/**
	 * @param classUI
	 *            the cClassUI to set
	 */
	@SuppressWarnings("unused")
	private void setCClassUI(String classUI) {
		cClassUI = classUI;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cClass == null) ? 0 : cClass.hashCode());
		result = prime * result
				+ ((cClassUI == null) ? 0 : cClassUI.hashCode());
		result = prime * result + ((cName == null) ? 0 : cName.hashCode());
		result = prime * result + iFilterConditionTypeID;
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof FilterConditionTypeDTO))
			return false;
		final FilterConditionTypeDTO other = (FilterConditionTypeDTO) obj;
		if (cClass == null) {
			if (other.cClass != null)
				return false;
		} else if (!cClass.equals(other.cClass))
			return false;
		if (cClassUI == null) {
			if (other.cClassUI != null)
				return false;
		} else if (!cClassUI.equals(other.cClassUI))
			return false;
		if (cName == null) {
			if (other.cName != null)
				return false;
		} else if (!cName.equals(other.cName))
			return false;
		if (iFilterConditionTypeID != other.iFilterConditionTypeID)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		final StringBuilder resultBuilder = new StringBuilder(this.getClass().getSimpleName());
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
}
