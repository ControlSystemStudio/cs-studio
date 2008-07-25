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
@Table(name = "AMSFilterCondConj4FilterCommon")
public class JunctorConditionForFilterTreeDTO extends FilterConditionDTO
		implements HasManuallyJoinedElements {

	@SuppressWarnings("unused")
	@Column(name = "iFilterConditionRef", nullable = false, updatable = false, insertable = false)
	private int iFilterConditionRef;

	@Column(name = "Operator", nullable = false)
	private String operator;

	@Transient
	private FilterConditionDTO[] operands = new FilterConditionDTO[0];

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
	 * Liefert den Operator für diese Conjunction.
	 * 
	 * @return Der Operator, null, wenn das DTO noch nicht gefüllt wurde.
	 */
	public JunctorConditionType getOperator() {
		return JunctorConditionType.valueOf(this.operator);
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
		List<FilterConditionDTO> asList = Arrays.asList(this.operands);
		return new HashSet<FilterConditionDTO>(asList);
	}

	private <T extends FilterConditionDTO> T findForId(int id, Collection<T> fcs) {
		for (T t : fcs) {
			if( t.getIFilterConditionID() == id ) {
				return t;
			}
		}
		return null;
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
	 *            {@link JunctorConditionForFilterTreeDTO} will be stored and/or
	 *            deleted.
	 * @throws If
	 *             an error occurred
	 */
	public synchronized void storeJoinLinkData(Mapper mapper) throws Throwable {
		Collection<JunctorConditionForFilterTreeConditionJoinDTO> joinsToKeep = new HashSet<JunctorConditionForFilterTreeConditionJoinDTO>();

		List<JunctorConditionForFilterTreeDTO> allJCFFT = mapper.loadAll(
				JunctorConditionForFilterTreeDTO.class, true);
		List<NegationConditionForFilterTreeDTO> allNots = mapper.loadAll(
				NegationConditionForFilterTreeDTO.class, true);

		List<JunctorConditionForFilterTreeDTO> ehemalsReferenziert = new LinkedList<JunctorConditionForFilterTreeDTO>();
		for (JunctorConditionForFilterTreeDTO inDb : allJCFFT) {
			if( inDb.getIFilterConditionID() == this.getIFilterConditionID() ) {
				ehemalsReferenziert.add(inDb);
			}
		}
		
		for (FilterConditionDTO operand : this.getOperands()) {
			if (operand instanceof JunctorConditionForFilterTreeDTO && operand != this) {
				JunctorConditionForFilterTreeDTO existingJCFFT = findForId(operand.getIFilterConditionID(), allJCFFT);
				
				if( existingJCFFT != null ) {
					existingJCFFT.storeJoinLinkData(mapper);
					ehemalsReferenziert.remove(existingJCFFT);
				} else {
					mapper.save(operand);
				}
				
			}
			if (operand instanceof NegationConditionForFilterTreeDTO) {
				NegationConditionForFilterTreeDTO existingNot = findForId(operand.getIFilterConditionID(), allNots);
				
				if( existingNot != null ) {
					existingNot.storeJoinLinkData(mapper);
				} else {
					mapper.save(operand);
				}
			}
		}

		for (JunctorConditionForFilterTreeDTO toBeDeleted : ehemalsReferenziert) {
			mapper.delete(toBeDeleted);
		}

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
	 *            {@link JunctorConditionForFilterTreeDTO} will be loaded and
	 *            nothing be deleted.
	 * @throws If
	 *             an error occurred
	 */
	@SuppressWarnings("unchecked")
	public synchronized void loadJoinData(Mapper mapper) throws Throwable {
		// GEHT NICHT WEIL SONST ENDLOSSCHLEIFE BEIM LADEN DER FCs -
		Collection<FilterConditionDTO> allFilterConditions = mapper.loadAll(
				FilterConditionDTO.class, false);

		Set<FilterConditionDTO> foundOperands = new HashSet<FilterConditionDTO>();

		List<JunctorConditionForFilterTreeConditionJoinDTO> allJoins = mapper
				.loadAll(JunctorConditionForFilterTreeConditionJoinDTO.class,
						true);
		for (JunctorConditionForFilterTreeConditionJoinDTO joinElement : allJoins) {
			if (joinElement.getJoinParentsDatabaseId() == this
					.getIFilterConditionID()) {
				for (FilterConditionDTO conditionDTO : allFilterConditions) {
					if (conditionDTO.getIFilterConditionID() == joinElement
							.getJoinedConditionsDatabaseId()) {
						foundOperands.add(conditionDTO);
					}
				}
			}
		}

		this.operands = foundOperands
				.toArray(new FilterConditionDTO[foundOperands.size()]);
	}

	@SuppressWarnings("unchecked")
	public synchronized void deleteJoinLinkData(Mapper mapper) throws Throwable {
		List<JunctorConditionForFilterTreeConditionJoinDTO> allJoins = mapper
				.loadAll(JunctorConditionForFilterTreeConditionJoinDTO.class,
						true);

		for (JunctorConditionForFilterTreeConditionJoinDTO joinElement : allJoins) {
			if (joinElement.getJoinParentsDatabaseId() == this
					.getIFilterConditionID()) {
				int joinId = joinElement.getJoinedConditionsDatabaseId();
				mapper.delete(joinElement);
				List<JunctorConditionForFilterTreeDTO> list = mapper.loadAll(
						JunctorConditionForFilterTreeDTO.class, true);
				for (JunctorConditionForFilterTreeDTO junctorConditionForFilterTreeDTO : list) {
					if (junctorConditionForFilterTreeDTO
							.getIFilterConditionID() != joinId) {
						continue;
					}

					junctorConditionForFilterTreeDTO.deleteJoinLinkData(mapper);
					mapper.delete(junctorConditionForFilterTreeDTO);
				}
			}
		}
		// Lösche auch Conditions dieses Typs, da diese nur für den Filter
		// relevant und somit nicht als normale conditions genutzt werden
		// (Achtung: Sonderfall!!)
		// for (FilterConditionDTO operand : this.operands) {
		// if (operand instanceof JunctorConditionForFilterTreeDTO) {
		// ((JunctorConditionForFilterTreeDTO) operand)
		// .deleteJoinLinkData(session);
		// session.delete(operand);
		// }
		// }
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + iFilterConditionRef;
		result = prime * result + Arrays.hashCode(operands);
		result = prime * result
				+ ((operator == null) ? 0 : operator.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof JunctorConditionForFilterTreeDTO))
			return false;
		final JunctorConditionForFilterTreeDTO other = (JunctorConditionForFilterTreeDTO) obj;
		if (iFilterConditionRef != other.iFilterConditionRef)
			return false;
		if (!Arrays.equals(operands, other.operands))
			return false;
		if (operator == null) {
			if (other.operator != null)
				return false;
		} else if (!operator.equals(other.operator))
			return false;
		return true;
	}
}
