package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.FilterConditionsToFilterDTO;
import org.hibernate.annotations.CollectionOfElements;

/**
 * Dieses Daten-Transfer-Objekt stellt hält die Konfiguration eines Filters dar
 * 
 * Das Create-Statement für die Datenbank hat folgendes Aussehen:
 * 
 * <pre>
 * Create table AMS_Filter
 * 
 * iFilterID		INT,
 * iGroupRef		INT default -1 NOT NULL,
 * cName			VARCHAR(128),
 * cDefaultMessage	VARCHAR(1024),
 * PRIMARY KEY (iFilterID)
 * ;
 * </pre>
 */
@Entity
@Table(name = "AMS_Filter")
public class FilterDTO {

	@OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL)//, targetEntity=FilterConditionsToFilterDTO.class, mappedBy="iFilterRef")
	@JoinColumn(name="iFilterRef")
//	@CollectionOfElements(fetch=FetchType.EAGER, targetElement=FilterConditionsToFilterDTO.class)
//	@OneToMany(mappedBy="filter")
	private List<FilterConditionsToFilterDTO> conditionsAggregators;
	
	@Id
	@Column(name="iFilterID")
	private int iFilterID; //		INT,
	
	@Column(name="iGroupRef")
	private int iGroupRef; //		INT default -1 NOT NULL,
	
	@Column(name="cName")
	private String cName; //			VARCHAR(128),
	
	@Column(name="cDefaultMessage")
	private String cDefaultMessage; //	VARCHAR(1024),

	@Deprecated
	public List<FilterConditionDTO> getFilterCondition() {
//		return filterCondition;
		return Collections.emptyList();
	}

	private int getIFilterID() {
		return iFilterID;
	}

	private void setIFilterID(int filterID) {
		iFilterID = filterID;
	}

	private int getIGroupRef() {
		return iGroupRef;
	}

	private void setIGroupRef(int groupRef) {
		iGroupRef = groupRef;
	}

	private String getCName() {
		return cName;
	}

	private void setCName(String name) {
		cName = name;
	}

	private String getCDefaultMessage() {
		return cDefaultMessage;
	}

	private void setCDefaultMessage(String defaultMessage) {
		cDefaultMessage = defaultMessage;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(this.getClass().getSimpleName());
		builder.append(": ");
		builder.append("iFilterID: ");
		builder.append(iFilterID);
		builder.append(", iGroupRef: ");
		builder.append(iGroupRef);
		builder.append(", cName: ");
		builder.append(cName);
		builder.append(", linked Conditions: ");
		builder.append(conditionsAggregators.toString());
		return builder.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((cDefaultMessage == null) ? 0 : cDefaultMessage.hashCode());
		result = prime * result + ((cName == null) ? 0 : cName.hashCode());
		result = prime * result + iFilterID;
		result = prime * result + iGroupRef;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof FilterDTO))
			return false;
		final FilterDTO other = (FilterDTO) obj;
		if (cDefaultMessage == null) {
			if (other.cDefaultMessage != null)
				return false;
		} else if (!cDefaultMessage.equals(other.cDefaultMessage))
			return false;
		if (cName == null) {
			if (other.cName != null)
				return false;
		} else if (!cName.equals(other.cName))
			return false;
		if (iFilterID != other.iFilterID)
			return false;
		if (iGroupRef != other.iGroupRef)
			return false;
		return true;
	}
	
}
