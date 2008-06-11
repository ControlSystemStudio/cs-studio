package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.hibernate.annotations.ForeignKey;

/**
 * Dieses Daten-Transfer-Objekt stellt hält die Konfiguration einer
 * AMS_FilterCond_Conj_Common.
 * 
 * Das Create-Statement für die Datenbank hat folgendes Aussehen:
 * 
 * <pre>
 * create table AMS_FilterCond_Conj_Common
 * 
 * FilterConditionRef			INT NOT NULL,
 * FirstFilterConditionRef	INT NOT NULL,
 * SecondFilterConditionRef   INT NOT NULL,
 * Operand                    SMALLINT
 * ;
 * </pre>
 */
@Entity
@Table(name = "AMS_FilterCond_Conj_Common")

@PrimaryKeyJoinColumn(name = "iFilterConditionRef", referencedColumnName = "iFilterConditionID")
public class JunctorConditionDTO extends FilterConditionDTO {

//	@ForeignKey(name="iFilterConditionRef", inverseName="iFilterConditionID")
	@Column(name = "iFilterConditionRef", nullable = false, updatable = false, insertable = false)
	private int iFilterConditionRef;

//	@Column(name = "FirstFilterConditionRef", length = 16)
//	private int firstFilterConditionRef;
//
//	@Column(name = "SecondFilterConditionRef", length = 16)
//	private int secondFilterConditionRef;
//	@ForeignKey(name="FirstFilterConditionRef", inverseName="iFilterConditionID")
	@ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	@JoinColumn(name="FirstFilterConditionRef", referencedColumnName="iFilterConditionID")
	private FilterConditionDTO firstFilterCondition;
	
//	@ForeignKey(name="SecondFilterConditionRef", inverseName="iFilterConditionID")
	@ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	@JoinColumn(name="SecondFilterConditionRef", referencedColumnName="iFilterConditionID")
	private FilterConditionDTO secondFilterCondition;
	
	@Column(name = "Operand", length = 16)
	private short operand;

	/**
	 * @return the filterConditionRef
	 */
	@SuppressWarnings("unused")
	private int getFilterConditionRef() {
		return iFilterConditionRef;
	}

	/**
	 * @param filterConditionRef
	 *            the filterConditionRef to set
	 */
	@SuppressWarnings("unused")
	private void setFilterConditionRef(int filterConditionRef) {
		this.iFilterConditionRef = filterConditionRef;
	}

	@SuppressWarnings("unused")
	private short getOperand() {
		return operand;
	}
	@SuppressWarnings("unused")
	private void setOperand(short operand) {
		this.operand = operand;
	}
	@SuppressWarnings("unused")
	public FilterConditionDTO getFirstFilterCondition() {
		return firstFilterCondition;
	}
	@SuppressWarnings("unused")
	public void setFirstFilterCondition(FilterConditionDTO firstFilterCondition) {
		this.firstFilterCondition = firstFilterCondition;
//		this.firstFilterConditionRef = firstFilterCondition.getIFilterConditionID();
	}
	@SuppressWarnings("unused")
	public FilterConditionDTO getSecondFilterCondition() {
		return secondFilterCondition;
	}
	@SuppressWarnings("unused")
	public void setSecondFilterCondition(FilterConditionDTO secondFilterCondition) {
		this.secondFilterCondition = secondFilterCondition;
//		this.secondFilterConditionRef = secondFilterCondition.getIFilterConditionID();
	}

//	private int getFirstFilterConditionRef() {
//		return firstFilterConditionRef;
//	}
//
//	private void setFirstFilterConditionRef(int firstFilterConditionRef) {
//		this.firstFilterConditionRef = firstFilterConditionRef;
//	}
//
//	private int getSecondFilterConditionRef() {
//		return secondFilterConditionRef;
//	}
//
//	private void setSecondFilterConditionRef(int secondFilterConditionRef) {
//		this.secondFilterConditionRef = secondFilterConditionRef;
//	}


}
