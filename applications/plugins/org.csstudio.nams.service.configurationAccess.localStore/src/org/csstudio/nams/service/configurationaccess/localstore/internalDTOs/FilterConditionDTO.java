package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
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
@Inheritance(strategy=InheritanceType.JOINED)
public class FilterConditionDTO {

	@Column(name="iFilterConditionTypeRef")
	protected int filterCondtionTypeRef;
	
	protected void setFilterConditionTypeRef(int iD){
		filterCondtionTypeRef = -1;
	}
	
	public int getFilterConditionTypeRef(){
		return filterCondtionTypeRef;
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "iFilterConditionID", nullable=false, unique=true)
	private int iFilterConditionID;

	@Column(name = "iGroupRef", nullable=false)
	private int iGroupRef = -1;

	@Column(name = "cName", length=128)
	private String cName;

	@Column(name = "cDesc", length=256)
	private String cDesc;

	/**
	 * @return the iFilterConditionID
	 */
	@SuppressWarnings("unused")
	public int getIFilterConditionID() {
		return iFilterConditionID;
	}

	/**
	 * @param filterConditionID the iFilterConditionID to set
	 */
	@SuppressWarnings("unused")
	public void setIFilterConditionID(int filterConditionID) {
		iFilterConditionID = filterConditionID;
	}

	/**
	 * @return the iGroupRef
	 */
	@SuppressWarnings("unused")
	public int getIGroupRef() {
		return iGroupRef;
	}

	/**
	 * @param groupRef the iGroupRef to set
	 */
	@SuppressWarnings("unused")
	public void setIGroupRef(int groupRef) {
		iGroupRef = groupRef;
	}

	/**
	 * @return the cName
	 */
	public String getCName() {
		return cName;
	}

	/**
	 * @param name the cName to set
	 */
	@SuppressWarnings("unused")
	public void setCName(String name) {
		cName = name;
	}

	/**
	 * @return the cDesc
	 */
	public String getCDesc() {
		return cDesc;
	}

	/**
	 * @param desc the cDesc to set
	 */
	@SuppressWarnings("unused")
	private void setCDesc(String desc) {
		cDesc = desc;
	}

	@Override
	public String toString() {
		final StringBuilder resultBuilder = new StringBuilder(this.getClass().getSimpleName());
		resultBuilder.append(": ");
		resultBuilder.append(this.getCName());
		resultBuilder.append(" (");
		resultBuilder.append(this.getIFilterConditionID());
		resultBuilder.append(")");
		resultBuilder.append(", description: ");
		resultBuilder.append(this.getCDesc());
		resultBuilder.append(", FilterConditionType: ");
		resultBuilder.append(this.getFilterConditionTypeRef());
		return resultBuilder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cDesc == null) ? 0 : cDesc.hashCode());
		result = prime * result + ((cName == null) ? 0 : cName.hashCode());
		result = prime * result + iFilterConditionID;
		result = prime * result + iGroupRef;
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
		final FilterConditionDTO other = (FilterConditionDTO) obj;
		if (cDesc == null) {
			if (other.cDesc != null)
				return false;
		} else if (!cDesc.equals(other.cDesc))
			return false;
		if (cName == null) {
			if (other.cName != null)
				return false;
		} else if (!cName.equals(other.cName))
			return false;
		if (iFilterConditionID != other.iFilterConditionID)
			return false;
		if (iGroupRef != other.iGroupRef)
			return false;
		return true;
	}

	
}
