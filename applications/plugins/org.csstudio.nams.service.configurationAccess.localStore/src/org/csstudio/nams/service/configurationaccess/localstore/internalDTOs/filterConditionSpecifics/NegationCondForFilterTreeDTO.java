
package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.nams.service.configurationaccess.localstore.Mapper;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.InconsistentConfigurationException;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;

/**
 * Dieses FC wird verwendet, um andere Condtions im Filter zu verneinen. Diese
 * FC wird allerdings nicht in der Auflistung der FCs im Konfigurationswerkzeug
 * angezeigt!
 * 
 * <pre>
 * create table AMSFilterNegationCond4Filter (
 *    iFilterConditionRef			NUMBER(11) NOT NULL,
 *    iNegatedFCRef                 NUMBER(11) NOT NULL
 * );
 * </pre>
 * 
 * @author gs, mz
 */
@Entity
@PrimaryKeyJoinColumn(name = "iFilterConditionRef", referencedColumnName = "iFilterConditionID")
@Table(name = "AMS_FILTERCOND_NEGATION")
public class NegationCondForFilterTreeDTO extends FilterConditionDTO
		implements HasManuallyJoinedElements {

	@SuppressWarnings("unused")
	@Column(name = "iNegatedFCRef", nullable = false)
	private int iNegatedFCRef;

	@Transient
	private FilterConditionDTO negatedFilterCondition;

	public NegationCondForFilterTreeDTO() {
		this.setCName("NegationConditionForFilterTreeDTO");
		this
				.setCDesc("The type NegationConditionForFilterTreeDTO is not to be used directly! It is used internally on filters.");
	}

	@Override
    public void deleteJoinLinkData(final Mapper mapper) throws Throwable {
		if (this.negatedFilterCondition instanceof HasManuallyJoinedElements) {
			if ((this.negatedFilterCondition instanceof JunctorCondForFilterTreeDTO)
					|| (this.negatedFilterCondition instanceof NegationCondForFilterTreeDTO)) {
				mapper.delete(this.negatedFilterCondition);
			} else {
				((HasManuallyJoinedElements) this.negatedFilterCondition)
						.deleteJoinLinkData(mapper);
			}
		}
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof NegationCondForFilterTreeDTO)) {
			return false;
		}
		final NegationCondForFilterTreeDTO other = (NegationCondForFilterTreeDTO) obj;
		if (this.iNegatedFCRef != other.iNegatedFCRef) {
			return false;
		}
		if (this.negatedFilterCondition == null) {
			if (other.negatedFilterCondition != null) {
				return false;
			}
		} else if (!this.negatedFilterCondition
				.equals(other.negatedFilterCondition)) {
			return false;
		}
		return true;
	}

	/**
	 * TO BE USED BY {@link LocalStoreConfigurationService} ONLY!
	 * 
	 * Returns the database id of negated condition.
	 */
	public int getINegatedFCRef() {
		return this.iNegatedFCRef;
	}

	/**
	 * Returns the negated FilterCondition.
	 * 
	 * @return The filter condition, not null.
	 */
	public FilterConditionDTO getNegatedFilterCondition() {
		return this.negatedFilterCondition;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + this.iNegatedFCRef;
		result = prime
				* result
				+ ((this.negatedFilterCondition == null) ? 0
						: this.negatedFilterCondition.hashCode());
		return result;
	}

	@Override
    @SuppressWarnings("unchecked")
	public void loadJoinData(final Mapper mapper) throws Throwable {
		if (this.getIFilterConditionID() == this.getINegatedFCRef()) {
			throw new InconsistentConfigurationException(
					"NegationConditionForFilterTree id == iNegatedFCRef");
		}

		this.negatedFilterCondition = mapper.findForId(
				FilterConditionDTO.class, this.getINegatedFCRef(), true);
	}

	/**
	 * TO BE USED FOR MAPPING PURPOSES!
	 * 
	 * Sets the negated condition.
	 */
	public void setNegatedFilterCondition(
			final FilterConditionDTO negatedFilterCondition) {
		this.negatedFilterCondition = negatedFilterCondition;
		this.iNegatedFCRef = negatedFilterCondition.getIFilterConditionID();
	}

	@Override
    public void storeJoinLinkData(final Mapper mapper) throws Throwable {
		if (this.negatedFilterCondition instanceof HasManuallyJoinedElements) {
			if ((this.negatedFilterCondition instanceof JunctorCondForFilterTreeDTO)
					|| (this.negatedFilterCondition instanceof NegationCondForFilterTreeDTO)) {

				final FilterConditionDTO found = mapper.findForId(
						FilterConditionDTO.class, this.negatedFilterCondition
								.getIFilterConditionID(), false);

				if (found == null) {
					mapper.save(this.negatedFilterCondition);
				} else {
					if (found instanceof JunctorCondForFilterTreeDTO) {
						final JunctorCondForFilterTreeDTO oldJCFFT = (JunctorCondForFilterTreeDTO) found;
						final JunctorCondForFilterTreeDTO newJCFFT = (JunctorCondForFilterTreeDTO) this.negatedFilterCondition;
						oldJCFFT.setCDesc(newJCFFT.getCDesc());
						oldJCFFT.setCName(newJCFFT.getCName());
						oldJCFFT.setIGroupRef(newJCFFT.getIGroupRef());
						oldJCFFT.setOperands(newJCFFT.getOperands());
						oldJCFFT.setOperator(newJCFFT.getOperator());
						mapper.save(oldJCFFT);
					} else {
						final NegationCondForFilterTreeDTO oldNot = (NegationCondForFilterTreeDTO) found;
						final NegationCondForFilterTreeDTO newNot = (NegationCondForFilterTreeDTO) this.negatedFilterCondition;

						oldNot.setCDesc(newNot.getCDesc());
						oldNot.setCName(newNot.getCName());
						oldNot.setIGroupRef(newNot.getIGroupRef());
						oldNot.setNegatedFilterCondition(newNot
								.getNegatedFilterCondition());
						mapper.save(oldNot);
					}
				}
				this.iNegatedFCRef = this.negatedFilterCondition
						.getIFilterConditionID();
			} else {

				((HasManuallyJoinedElements) this.negatedFilterCondition)
						.storeJoinLinkData(mapper);
			}
		}
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": iFilterConditionRef="
				+ this.iNegatedFCRef + ", assigned FC by mapping: "
				+ this.negatedFilterCondition;
	}
}
