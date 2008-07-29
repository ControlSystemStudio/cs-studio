package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.nams.service.configurationaccess.localstore.Mapper;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.JunctorConditionType;
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
 * iFilterConditionRef			INT NOT NULL,
 * iFirstFilterConditionRef	INT NOT NULL,
 * iSecondFilterConditionRef   INT NOT NULL,
 *  // * Operand                    SMALLINT
 * ;
 * </pre>
 */
@Entity
@PrimaryKeyJoinColumn(name = "iFilterConditionRef", referencedColumnName = "iFilterConditionID")
@Table(name = "AMS_FilterCond_Conj_Common")
public class JunctorConditionDTO extends FilterConditionDTO implements HasManuallyJoinedElements {

	@Column(name = "iFirstFilterConditionRef")
	private int firstFilterConditionRef;

	@Column(name = "iSecondFilterConditionRef")
	private int secondFilterConditionRef;

	@Transient
	@Deprecated
	private short operand = JunctorConditionType.shortOf(JunctorConditionType.OR);

	
	@Transient
	private FilterConditionDTO firstFilterCondition;
	@Transient
	private FilterConditionDTO secondFilterCondition;

	public JunctorConditionType getJunctor() {
		return JunctorConditionType.valueOf(operand);
	}

	public void setJunctor(JunctorConditionType junctor) {
		operand = JunctorConditionType.shortOf(junctor);
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
		if (operand != other.operand)
			return false;
		return true;
	}


	@SuppressWarnings("unused")
	public int getFirstFilterConditionRef() {
		return firstFilterConditionRef;
	}

	public void setFirstFilterConditionRef(int firstFilterConditionRef) {
		this.firstFilterConditionRef = firstFilterConditionRef;
	}

	@SuppressWarnings("unused")
	public int getSecondFilterConditionRef() {
		return secondFilterConditionRef;
	}

	public void setSecondFilterConditionRef(int secondFilterConditionRef) {
		this.secondFilterConditionRef = secondFilterConditionRef;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append(" + first filterCond: ");
		builder.append(this.firstFilterConditionRef);
		builder.append(", secondFilterCond: ");
		builder.append(this.secondFilterConditionRef);
		builder.append(", operand: ");
		builder.append(this.getJunctor());
		return builder.toString();
	}

	public FilterConditionDTO getFirstFilterCondition() {
		return firstFilterCondition;
	}

	public FilterConditionDTO getSecondFilterCondition() {
		return secondFilterCondition;
	}

	@SuppressWarnings("unused")
	public void setFirstFilterCondition(FilterConditionDTO filterCondition) {
		setFirstFilterConditionRef(filterCondition.getIFilterConditionID());
		firstFilterCondition = filterCondition;
	}

	@SuppressWarnings("unused")
	public void setSecondFilterCondition(FilterConditionDTO filterCondition) {
		setSecondFilterConditionRef(filterCondition.getIFilterConditionID());
		secondFilterCondition = filterCondition;
	}

	public void deleteJoinLinkData(Mapper mapper) throws Throwable {
		// Nichts zu tun, da es genau diese Tabellenzeile betrifft.
	}

	public void loadJoinData(Mapper mapper) throws Throwable {
		List<FilterConditionDTO> allFCs = mapper.loadAll(FilterConditionDTO.class, false);
		
		boolean firstFound = false;
		boolean secondFound = false;
		for (FilterConditionDTO fc : allFCs) {
			if( !firstFound && fc.getIFilterConditionID() == this.firstFilterConditionRef ) {
				this.firstFilterCondition = fc;
				firstFound = true;
			} else if( !secondFound && fc.getIFilterConditionID() == this.secondFilterConditionRef ) {
				this.secondFilterCondition = fc;
				secondFound = true;
			}
			
			if( firstFound && secondFound ) {
				break;
			}
		}
	}

	public void storeJoinLinkData(Mapper mapper) throws Throwable {
		throw new UnsupportedOperationException("This type is deprecated and should not be saved any more.");
	}

}
