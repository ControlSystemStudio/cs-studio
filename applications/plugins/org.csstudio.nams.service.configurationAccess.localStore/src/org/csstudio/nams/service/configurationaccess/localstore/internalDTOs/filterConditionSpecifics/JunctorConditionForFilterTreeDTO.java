package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.nams.common.contract.Contract;
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
 * Das Join der Operanden erfolgt via {@link JunctorConditionForFilterTreeConditionJoinDTO}.
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
public class JunctorConditionForFilterTreeDTO extends FilterConditionDTO {

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
