
package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.nams.common.contract.Contract;
import org.csstudio.nams.service.configurationaccess.localstore.Mapper;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.JunctorConditionType;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.JunctorConditionForFilterTreeConditionJoinDTO;

/**
 * Dieses FC wird verwendet, um in den Filtern den Conjunction-Baum
 * darzustellen. Sie wird intern wie eine normale FC benutzt
 * (Abwärtskompatibilität, die alten Filter können nicht verändert werden).
 * Diese FC wird allerdings nicht in der Auflistung der FCs im
 * Konfigurationswerkzeug angezeigt!
 * 
 * Das Join der Operanden erfolgt via
 * {@link JunctorConditionForFilterTreeConditionJoinDTO}.
 * 
 * <pre>
 * create table AMSFilterCondConj4FilterCommon (
 *    iFilterConditionRef			NUMBER(11) NOT NULL,
 *    Operator                     VARCHAR2(3) NOT NULL, 
 *    CONSTRAINT AMSFilterCondConj4FilterCommon CHECK (Operator IN ('AND', 'OR'))
 * );
 * </pre>
 * 
 * @author gs, mz
 * @see JunctorConditionForFilterTreeConditionJoinDTO
 */
@Entity
@PrimaryKeyJoinColumn(name = "iFilterConditionRef", referencedColumnName = "iFilterConditionID")
@Table(name = "AMS_FILTERCOND_JUNCTION")
public class JunctorCondForFilterTreeDTO extends FilterConditionDTO
		implements HasManuallyJoinedElements {

	@Column(name = "Operator", nullable = false)
	private String operator;

	@Transient
	private FilterConditionDTO[] operands = new FilterConditionDTO[0];

	@SuppressWarnings("unchecked")
	public synchronized void deleteJoinLinkData(final Mapper mapper)
			throws Throwable {
		final List<JunctorConditionForFilterTreeConditionJoinDTO> allJoins = mapper
				.loadAll(JunctorConditionForFilterTreeConditionJoinDTO.class,
						false);

		for (final JunctorConditionForFilterTreeConditionJoinDTO joinElement : allJoins) {
			if (joinElement.getJoinParentsDatabaseId() == this
					.getIFilterConditionID()) {
				final int joinId = joinElement.getJoinedConditionsDatabaseId();
				mapper.delete(joinElement);

				final JunctorCondForFilterTreeDTO jcfft = mapper
						.findForId(JunctorCondForFilterTreeDTO.class,
								joinId, false);
				if (jcfft != null) {
					mapper.delete(jcfft);
				}

				final NegationCondForFilterTreeDTO ncfft = mapper
						.findForId(NegationCondForFilterTreeDTO.class,
								joinId, false);
				if (ncfft != null) {
					mapper.delete(ncfft);
				}

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
		if (!(obj instanceof JunctorCondForFilterTreeDTO)) {
			return false;
		}
		final JunctorCondForFilterTreeDTO other = (JunctorCondForFilterTreeDTO) obj;
		if (!Arrays.equals(this.operands, other.operands)) {
			return false;
		}
		if (this.operator == null) {
			if (other.operator != null) {
				return false;
			}
		} else if (!this.operator.equals(other.operator)) {
			return false;
		}
		return true;
	}

	/**
	 * ONLY USED FOR MAPPING PURPOSES!
	 * 
	 * Liefert die Operanden der Junction. Dieses wird nicht durch das Mapping
	 * vorbereitet sondern manuell nach dem Laden.
	 * 
	 * @return Eine, potentiell leere, Menge von Operanden, null, wenn die DTO
	 *         noch nicht gefüllt wurde.
	 */
	@Transient
	public Set<FilterConditionDTO> getOperands() {
		final List<FilterConditionDTO> asList = Arrays.asList(this.operands);
		return new HashSet<FilterConditionDTO>(asList);
	}

	/**
	 * Liefert den Operator für diese Conjunction.
	 * 
	 * @return Der Operator, null, wenn das DTO noch nicht gefüllt wurde.
	 */
	public JunctorConditionType getOperator() {
		return JunctorConditionType.valueOf(this.operator);
	}

	// private <T extends FilterConditionDTO> T findForId(int id, Collection<T>
	// fcs) {
	// for (T t : fcs) {
	// if (t.getIFilterConditionID() == id) {
	// return t;
	// }
	// }
	// return null;
	// }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Arrays.hashCode(this.operands);
		result = prime * result
				+ ((this.operator == null) ? 0 : this.operator.hashCode());
		return result;
	}

	/**
	 * ONLY USED FOR MAPPING PURPOSES!
	 * 
	 * This method is used to load the join-data and set
	 * {@link FilterConditionDTO}s (see: {@link #setOperands(Set)}. IMPORTANT:
	 * This method has to be called in a valid open transaction!
	 * 
	 * Here also sub-conditions of same type are ordered to load their join
	 * data!
	 * 
	 * @param mapper
	 *            The session to store to; it is guaranteed that only
	 *            {@link JunctorCondForFilterTreeDTO} will be loaded and
	 *            nothing be deleted.
	 * @throws If
	 *             an error occurred
	 */
	@SuppressWarnings("unchecked")
	public synchronized void loadJoinData(final Mapper mapper) throws Throwable {

		final List<JunctorConditionForFilterTreeConditionJoinDTO> allJoins = mapper
				.loadAll(JunctorConditionForFilterTreeConditionJoinDTO.class,
						false);

		final Set<FilterConditionDTO> foundOperands = new HashSet<FilterConditionDTO>();

		for (final JunctorConditionForFilterTreeConditionJoinDTO joinElement : allJoins) {
			if (joinElement.getJoinParentsDatabaseId() == this
					.getIFilterConditionID()) {
				final FilterConditionDTO conditionDTO = mapper.findForId(
						FilterConditionDTO.class, joinElement
								.getJoinedConditionsDatabaseId(), true);
				foundOperands.add(conditionDTO);
			}
		}

		this.operands = foundOperands
				.toArray(new FilterConditionDTO[foundOperands.size()]);
	}

	/**
	 * ONLY USED FOR MAPPING PURPOSES!
	 * 
	 * Setzt die Operanden der Junction. Dieses geschiet nicht durch das Mapping
	 * sondern manuell nach dem Laden.
	 * 
	 * @param operands
	 *            Eine, potentiell leere, Menge von Operanden, nicht null.
	 */
	@Transient
	public void setOperands(final Set<FilterConditionDTO> operands) {
		Contract.requireNotNull("operands", operands);

		this.operands = operands
				.toArray(new FilterConditionDTO[operands.size()]);
	}

	/**
	 * Setzt den Operator für diese Conjunction.
	 * 
	 * @param operator
	 *            Der Operator, nicht null.
	 */
	public void setOperator(final JunctorConditionType operator) {
		Contract.requireNotNull("operator", operator);

		this.operator = operator.name();
	}

	/**
	 * ONLY USED FOR MAPPING PURPOSES!
	 * 
	 * This method is used to store the join-data for previously set
	 * {@link FilterConditionDTO}s (see: {@link #setOperands(Set)}. IMPORTANT:
	 * This method has to be called in a valid open transaction!
	 * 
	 * @param session
	 *            The session to store to; it is guaranteed that only
	 *            {@link JunctorCondForFilterTreeDTO} will be stored and/or
	 *            deleted.
	 * @throws If
	 *             an error occurred
	 */
	public synchronized void storeJoinLinkData(final Mapper mapper)
			throws Throwable {

		// List<FilterConditionDTO> allFC =
		// mapper.loadAll(FilterConditionDTO.class, false);
		final List<JunctorConditionForFilterTreeConditionJoinDTO> joins = mapper
				.loadAll(JunctorConditionForFilterTreeConditionJoinDTO.class,
						true);

		final List<FilterConditionDTO> ehemalsReferenziert = new LinkedList<FilterConditionDTO>();

		for (final JunctorConditionForFilterTreeConditionJoinDTO join : joins) {
			if (join.getJoinParentsDatabaseId() == this.getIFilterConditionID()) {
				// FilterConditionDTO found =
				// findForId(join.getJoinedConditionsDatabaseId(), allFC);
				final FilterConditionDTO found = mapper.findForId(
						FilterConditionDTO.class, join
								.getJoinedConditionsDatabaseId(), false);
				ehemalsReferenziert.add(found);
			}
		}

		final Set<FilterConditionDTO> operands = this.getOperands();

		for (final FilterConditionDTO operand : operands) {
			// FilterConditionDTO fc =
			// findForId(operand.getIFilterConditionID(), allFC);
			final FilterConditionDTO fc = mapper.findForId(
					FilterConditionDTO.class, operand.getIFilterConditionID(),
					false);

			if (fc != null) {
				if (!ehemalsReferenziert.remove(fc)) {
					final JunctorConditionForFilterTreeConditionJoinDTO newJoin = new JunctorConditionForFilterTreeConditionJoinDTO(
							this, fc);
					mapper.save(newJoin);
				}
				if ((operand instanceof JunctorCondForFilterTreeDTO)
						|| (operand instanceof NegationCondForFilterTreeDTO)) {
					((HasManuallyJoinedElements) operand)
							.storeJoinLinkData(mapper);
				}
			} else {
				mapper.save(operand);
				final JunctorConditionForFilterTreeConditionJoinDTO newJoin = new JunctorConditionForFilterTreeConditionJoinDTO(
						this, operand);
				mapper.save(newJoin);
			}
		}

		for (final FilterConditionDTO toRemove : ehemalsReferenziert) {
			final JunctorConditionForFilterTreeConditionJoinDTO found = this
					.findForId(toRemove.getIFilterConditionID(), joins);
			mapper.delete(found);
			if (toRemove instanceof JunctorCondForFilterTreeDTO) {
				mapper.delete(toRemove);
			}
			if (toRemove instanceof NegationCondForFilterTreeDTO) {
				mapper.delete(toRemove);
			}
		}
	}

	private JunctorConditionForFilterTreeConditionJoinDTO findForId(
			final int id,
			final Collection<JunctorConditionForFilterTreeConditionJoinDTO> fcs) {
		for (final JunctorConditionForFilterTreeConditionJoinDTO t : fcs) {
			if (t.getJoinedConditionsDatabaseId() == id) {
				return t;
			}
		}
		return null;
	}
}
