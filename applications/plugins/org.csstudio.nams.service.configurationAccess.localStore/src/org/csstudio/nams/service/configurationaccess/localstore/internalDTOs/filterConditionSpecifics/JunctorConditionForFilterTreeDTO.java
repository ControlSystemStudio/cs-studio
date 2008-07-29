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
	
	private JunctorConditionForFilterTreeConditionJoinDTO findForId(int id, Collection<JunctorConditionForFilterTreeConditionJoinDTO> fcs) {
		for (JunctorConditionForFilterTreeConditionJoinDTO t : fcs) {
			if( t.getJoinedConditionsDatabaseId() == id ) {
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
		
		List<FilterConditionDTO> allFC = mapper.loadAll(FilterConditionDTO.class, true);
		List<JunctorConditionForFilterTreeConditionJoinDTO> joins = mapper.loadAll(JunctorConditionForFilterTreeConditionJoinDTO.class, true);
		
		
		List<FilterConditionDTO> ehemalsReferenziert = new LinkedList<FilterConditionDTO>();
		
		for (JunctorConditionForFilterTreeConditionJoinDTO join : joins) {
			if (join.getJoinParentsDatabaseId() == this.getIFilterConditionID()) {
				FilterConditionDTO found = findForId(join.getJoinedConditionsDatabaseId(), allFC);
				ehemalsReferenziert.add(found);
			}
		}
		
		Set<FilterConditionDTO> operands = this.getOperands();
		
		for (FilterConditionDTO operand : operands) {
			FilterConditionDTO fc = findForId(operand.getIFilterConditionID(), allFC);
			
			if (fc != null) {
				if (!ehemalsReferenziert.remove(fc)) {
					JunctorConditionForFilterTreeConditionJoinDTO newJoin = new JunctorConditionForFilterTreeConditionJoinDTO(this, fc);
					mapper.save(newJoin);
				}
				if (operand instanceof JunctorConditionForFilterTreeDTO || operand instanceof NegationConditionForFilterTreeDTO) {
					((HasManuallyJoinedElements)operand).storeJoinLinkData(mapper);
				}
			} else {
				mapper.save(operand);
				JunctorConditionForFilterTreeConditionJoinDTO newJoin = new JunctorConditionForFilterTreeConditionJoinDTO(this, operand);
				mapper.save(newJoin);
			}
		}
		
		for (FilterConditionDTO toRemove : ehemalsReferenziert) {
			JunctorConditionForFilterTreeConditionJoinDTO found = findForId(toRemove.getIFilterConditionID(), joins);
			mapper.delete(found);
			if (toRemove instanceof JunctorConditionForFilterTreeDTO) {
				mapper.delete(toRemove);
			}
			if (toRemove instanceof NegationConditionForFilterTreeDTO) {
				mapper.delete(toRemove);
			}
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
							.getIFilterConditionID() == joinId) {
						mapper.delete(junctorConditionForFilterTreeDTO);
					}
				}
				
				List<NegationConditionForFilterTreeDTO> negList = mapper.loadAll(
						NegationConditionForFilterTreeDTO.class, true);
				for (NegationConditionForFilterTreeDTO negationConditionForFilterTreeDTO: negList) {
					if (negationConditionForFilterTreeDTO.getIFilterConditionID() == joinId) {
						mapper.delete(negationConditionForFilterTreeDTO);					
					}
				}
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
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
