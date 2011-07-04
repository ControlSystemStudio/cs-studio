
package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics;

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
public class JunctorConditionDTO extends FilterConditionDTO implements
		HasManuallyJoinedElements {

	@Column(name = "iFirstFilterConditionRef")
	private int firstFilterConditionRef;

	@Column(name = "iSecondFilterConditionRef")
	private int secondFilterConditionRef;

	@Transient
	@Deprecated
	private short operand = JunctorConditionType
			.asShort(JunctorConditionType.OR);

	@Transient
	private FilterConditionDTO firstFilterCondition;
	@Transient
	private FilterConditionDTO secondFilterCondition;

	public void deleteJoinLinkData(final Mapper mapper) throws Throwable {
		// Nichts zu tun, da es genau diese Tabellenzeile betrifft.
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final JunctorConditionDTO other = (JunctorConditionDTO) obj;
		if (this.operand != other.operand) {
			return false;
		}
		return true;
	}

	public FilterConditionDTO getFirstFilterCondition() {
		return this.firstFilterCondition;
	}

	@SuppressWarnings("unused")
	public int getFirstFilterConditionRef() {
		return this.firstFilterConditionRef;
	}

	public JunctorConditionType getJunctor() {
		return JunctorConditionType.valueOf(this.operand);
	}

	public FilterConditionDTO getSecondFilterCondition() {
		return this.secondFilterCondition;
	}

	@SuppressWarnings("unused")
	public int getSecondFilterConditionRef() {
		return this.secondFilterConditionRef;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.operand;
		return result;
	}

	public void loadJoinData(final Mapper mapper) throws Throwable {
		this.firstFilterCondition = mapper.findForId(FilterConditionDTO.class,
				this.firstFilterConditionRef, false);
		this.secondFilterCondition = mapper.findForId(FilterConditionDTO.class,
				this.secondFilterConditionRef, false);
	}

	@SuppressWarnings("unused")
	public void setFirstFilterCondition(final FilterConditionDTO filterCondition) {
		this
				.setFirstFilterConditionRef(filterCondition
						.getIFilterConditionID());
		this.firstFilterCondition = filterCondition;
	}

	public void setFirstFilterConditionRef(final int firstFilterConditionRef) {
		this.firstFilterConditionRef = firstFilterConditionRef;
	}

	public void setJunctor(final JunctorConditionType junctor) {
		this.operand = JunctorConditionType.asShort(junctor);
	}

	@SuppressWarnings("unused")
	public void setSecondFilterCondition(
			final FilterConditionDTO filterCondition) {
		this.setSecondFilterConditionRef(filterCondition
				.getIFilterConditionID());
		this.secondFilterCondition = filterCondition;
	}

	public void setSecondFilterConditionRef(final int secondFilterConditionRef) {
		this.secondFilterConditionRef = secondFilterConditionRef;
	}

	public void storeJoinLinkData(final Mapper mapper) throws Throwable {
		throw new UnsupportedOperationException(
				"This type is deprecated and should not be saved any more.");
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder(super.toString());
		builder.append(" + first filterCond: ");
		builder.append(this.firstFilterConditionRef);
		builder.append(", secondFilterCond: ");
		builder.append(this.secondFilterConditionRef);
		builder.append(", operand: ");
		builder.append(this.getJunctor());
		return builder.toString();
	}

	@SuppressWarnings("unused")
	private short getOperand() {
		return this.operand;
	}

	@SuppressWarnings("unused")
	private void setOperand(final short operand) {
		this.operand = operand;
	}

}
