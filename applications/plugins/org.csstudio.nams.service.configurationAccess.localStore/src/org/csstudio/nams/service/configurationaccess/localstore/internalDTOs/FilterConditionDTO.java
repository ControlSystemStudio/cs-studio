package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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
@Table(name = "AMS_FilterCondition")
public class FilterConditionDTO {

	@OneToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	@JoinColumn(name="iFilterConditionTypeRef", referencedColumnName="iFilterConditionTypeID")
	private FilterConditionTypeDTO type;
	
	/**
	 * @return the type
	 */
	@SuppressWarnings("unused")
	private FilterConditionTypeDTO getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	@SuppressWarnings("unused")
	private void setType(FilterConditionTypeDTO type) {
		this.type = type;
	}
	
	@Id
	@GeneratedValue
	@Column(name = "iFilterConditionID", nullable=false, unique=true)
	private int iFilterConditionID;

	@Column(name = "iGroupRef", nullable=false)
	private int iGroupRef = -1;

	@Column(name = "cName", length=128)
	private String cName;

	@Column(name = "cDesc", length=256)
	private String cDesc;

	@Column(name = "iFilterConditionTypeRef", insertable=false, updatable=false)
	private int iFilterConditionTypeRef;

	/**
	 * @return the iFilterConditionID
	 */
	@SuppressWarnings("unused")
	private int getIFilterConditionID() {
		return iFilterConditionID;
	}

	/**
	 * @param filterConditionID the iFilterConditionID to set
	 */
	@SuppressWarnings("unused")
	private void setIFilterConditionID(int filterConditionID) {
		iFilterConditionID = filterConditionID;
	}

	/**
	 * @return the iGroupRef
	 */
	@SuppressWarnings("unused")
	private int getIGroupRef() {
		return iGroupRef;
	}

	/**
	 * @param groupRef the iGroupRef to set
	 */
	@SuppressWarnings("unused")
	private void setIGroupRef(int groupRef) {
		iGroupRef = groupRef;
	}

	/**
	 * @return the cName
	 */
	@SuppressWarnings("unused")
	private String getCName() {
		return cName;
	}

	/**
	 * @param name the cName to set
	 */
	@SuppressWarnings("unused")
	private void setCName(String name) {
		cName = name;
	}

	/**
	 * @return the cDesc
	 */
	@SuppressWarnings("unused")
	private String getCDesc() {
		return cDesc;
	}

	/**
	 * @param desc the cDesc to set
	 */
	@SuppressWarnings("unused")
	private void setCDesc(String desc) {
		cDesc = desc;
	}

	/**
	 * @return the iFilterConditionTypeRef
	 */
	@SuppressWarnings("unused")
	private int getIFilterConditionTypeRef() {
		return iFilterConditionTypeRef;
	}

	/**
	 * @param filterConditionTypeRef the iFilterConditionTypeRef to set
	 */
	@SuppressWarnings("unused")
	private void setIFilterConditionTypeRef(int filterConditionTypeRef) {
		iFilterConditionTypeRef = filterConditionTypeRef;
	}

	@Override
	public String toString() {
		final StringBuilder resultBuilder = new StringBuilder(this.getClass().getSimpleName());
		resultBuilder.append(": ");
		resultBuilder.append(this.getCName());
		resultBuilder.append(" (");
		resultBuilder.append(this.getIFilterConditionID());
		resultBuilder.append("), refers to type: ");
		FilterConditionTypeDTO conditionTypeDTO = this.getType();
		resultBuilder.append(conditionTypeDTO != null ? conditionTypeDTO.toString() : "NULL");
		resultBuilder.append(" (");
		resultBuilder.append(this.getIFilterConditionTypeRef());
		resultBuilder.append("), ");
		resultBuilder.append(this.getIGroupRef());
		resultBuilder.append(", ");
		resultBuilder.append(this.getCDesc());
		return resultBuilder.toString();
	}

	
}
