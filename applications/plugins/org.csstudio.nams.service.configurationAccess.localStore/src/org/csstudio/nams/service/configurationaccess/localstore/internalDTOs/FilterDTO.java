package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

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

	@OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	@JoinColumn(name="iFilterConditionRef", referencedColumnName="iFilterConditionRef")
	@CollectionOfElements(fetch=FetchType.EAGER, targetElement=FilterConditionDTO.class)
	private List<FilterConditionDTO> filterCondition;
	
	@Column(name="iFilterID")
	private int iFilterID; //		INT,
	
	@Column(name="iGroupRef")
	private int iGroupRef; //		INT default -1 NOT NULL,
	
	@Column(name="cName")
	private String cName; //			VARCHAR(128),
	
	@Column(name="cDefaultMessage")
	private String cDefaultMessage; //	VARCHAR(1024),

	public List<FilterConditionDTO> getFilterCondition() {
		return filterCondition;
	}

	public void setFilterCondition(List<FilterConditionDTO> filterCondition) {
		this.filterCondition = filterCondition;
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
}
