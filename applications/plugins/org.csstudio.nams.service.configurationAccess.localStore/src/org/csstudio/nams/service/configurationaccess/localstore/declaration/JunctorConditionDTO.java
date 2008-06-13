package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;

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
 * iFirstFilterConditionRef	INT NOT NULL,
 * iSecondFilterConditionRef   INT NOT NULL,
// * Operand                    SMALLINT
 * ;
 * </pre>
 */
@Entity
@PrimaryKeyJoinColumn(name = "iFilterConditionRef", referencedColumnName="iFilterConditionID")
@Table(name = "AMS_FilterCond_Conj_Common")
public class JunctorConditionDTO extends FilterConditionDTO {

	@Column(name = "iFilterConditionRef", nullable = false, updatable = false, insertable = false)
	private int iFilterConditionRef;

	@Column(name = "iFirstFilterConditionRef")
	private int firstFilterConditionRef;

	@Column(name = "iSecondFilterConditionRef")
	private int secondFilterConditionRef;
	
//	@Column(name = "Operand")
	@Transient // TODO Spalte hinzufuegen!!!
	private short operand = 0;
	
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

	public JunctorConditionType getJunctor() {
		return JunctorConditionType.valueOf(operand);
	}
	
	public void setJunctor(JunctorConditionType junctor) {
		operand = junctor.shortOf(junctor);
	}
	
	@SuppressWarnings("unused")
	private short getOperand() {
		return operand;
	}
	@SuppressWarnings("unused")
	private void setOperand(short operand) {
		this.operand = operand;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + iFilterConditionRef;
		result = prime * result + operand;
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
		if (iFilterConditionRef != other.iFilterConditionRef)
			return false;
		if (operand != other.operand)
			return false;
		return true;
	}

	@SuppressWarnings("unused")
	private int getIFilterConditionRef() {
		return iFilterConditionRef;
	}

	@SuppressWarnings("unused")
	private void setIFilterConditionRef(int filterConditionRef) {
		iFilterConditionRef = filterConditionRef;
	}

	@SuppressWarnings("unused")
	private int getFirstFilterConditionRef() {
		return firstFilterConditionRef;
	}

	@SuppressWarnings("unused")
	private void setFirstFilterConditionRef(int firstFilterConditionRef) {
		this.firstFilterConditionRef = firstFilterConditionRef;
	}

	@SuppressWarnings("unused")
	private int getSecondFilterConditionRef() {
		return secondFilterConditionRef;
	}

	@SuppressWarnings("unused")
	private void setSecondFilterConditionRef(int secondFilterConditionRef) {
		this.secondFilterConditionRef = secondFilterConditionRef;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append(" + first filterCond: ");
		builder.append(this.firstFilterConditionRef);
		builder.append(", secondFilterCond: ");
		builder.append(this.secondFilterConditionRef);
		return builder.toString();
	}

	public FilterConditionDTO getFirstFilterCondition() {
		// TODO Auto-generated method stub
		throw new RuntimeException("not impl.");
	}
	
	public FilterConditionDTO getSecondFilterCondition() {
		// TODO Auto-generated method stub
		throw new RuntimeException("not impl.");
	}
	
}
