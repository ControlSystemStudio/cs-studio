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
@PrimaryKeyJoinColumn(name = "iFilterConditionRef")
public class JunctorConditionDTO extends FilterConditionDTO {

	@Column(name = "iFilterConditionRef", nullable = false, updatable = false, insertable = false)
	private int iFilterConditionRef;

	@Column(name = "iFirstFilterConditionRef")
	private int firstFilterConditionRef;

	@Column(name = "iSecondFilterConditionRef")
	private int secondFilterConditionRef;
	
	@ForeignKey(name="AMS_FilterCondition")
	@ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	@JoinColumn(name="FirstFilterConditionRef", referencedColumnName="iFilterConditionID")
	private FilterConditionDTO firstFilterCondition;
	
	@ForeignKey(name="AMS_FilterCondition")
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
	@SuppressWarnings("unused")
	private int getFirstFilterConditionRef() {
		return firstFilterCondition.getIFilterConditionID();
	}
	@SuppressWarnings("unused")
	private void setFirstFilterConditionRef(int firstFilterConditionRef) {
		firstFilterCondition.setIFilterConditionID(firstFilterConditionRef);
	}
	@SuppressWarnings("unused")
	private int getSecondFilterConditionRef() {
		return secondFilterCondition.getIFilterConditionID();
	}
	@SuppressWarnings("unused")
	private void setSecondFilterConditionRef(int secondFilterConditionRef) {
		firstFilterCondition.setIFilterConditionID(secondFilterConditionRef);
	}
//
//	private int getFirstFilterConditionRef() {
//		return firstFilterConditionRef;
//	}
//
//	public void setFirstFilterConditionRef(int firstFilterConditionRef) {
//		this.firstFilterConditionRef = firstFilterConditionRef;
//	}
//
//	private int getSecondFilterConditionRef() {
//		return secondFilterConditionRef;
//	}
//
//	public void setSecondFilterConditionRef(int secondFilterConditionRef) {
//		this.secondFilterConditionRef = secondFilterConditionRef;
//	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((firstFilterCondition == null) ? 0 : firstFilterCondition
						.hashCode());
		result = prime * result + iFilterConditionRef;
		result = prime * result + operand;
		result = prime
				* result
				+ ((secondFilterCondition == null) ? 0 : secondFilterCondition
						.hashCode());
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
		final JunctorConditionDTO other = (JunctorConditionDTO) obj;
		if (firstFilterCondition == null) {
			if (other.firstFilterCondition != null)
				return false;
		} else if (!firstFilterCondition.equals(other.firstFilterCondition))
			return false;
		if (iFilterConditionRef != other.iFilterConditionRef)
			return false;
		if (operand != other.operand)
			return false;
		if (secondFilterCondition == null) {
			if (other.secondFilterCondition != null)
				return false;
		} else if (!secondFilterCondition.equals(other.secondFilterCondition))
			return false;
		return true;
	}

	
}
