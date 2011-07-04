
package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.csstudio.nams.common.contract.Contract;
import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.material.regelwerk.StringRegelOperator;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;

/**
 * Dieses Daten-Transfer-Objekt stellt hält die Konfiguration einer
 * AMS_FilterCondition_String.
 * 
 * Das Create-Statement für die Datenbank hat folgendes Aussehen:
 * 
 * <pre>
 *  create table AMS_FilterCondition_String
 *  (
 *  iFilterConditionRef	INT NOT NULL,
 *  cKeyValue		VARCHAR(16),
 *  sOperator		SMALLINT,
 *  cCompValue		VARCHAR(128)
 *  );
 * </pre>
 */
@Entity
@Table(name = "AMS_FilterCondition_String")
@PrimaryKeyJoinColumn(name = "iFilterConditionRef", referencedColumnName = "iFilterConditionID")
public class StringFilterConditionDTO extends FilterConditionDTO {

	@Column(name = "cKeyValue", length = 16)
	private String keyValue;

	@Column(name = "sOperator")
	private short operator;

	@Column(name = "cCompValue", length = 128)
	private String compValue;

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof StringFilterConditionDTO)) {
			return false;
		}
		final StringFilterConditionDTO other = (StringFilterConditionDTO) obj;
		if (this.compValue == null) {
			if (other.compValue != null) {
				return false;
			}
		} else if (!this.compValue.equals(other.compValue)) {
			return false;
		}
		if (this.keyValue == null) {
			if (other.keyValue != null) {
				return false;
			}
		} else if (!this.keyValue.equals(other.keyValue)) {
			return false;
		}
		if (this.operator != other.operator) {
			return false;
		}
		return true;
	}

	/**
	 * @return the compValue
	 */
	@SuppressWarnings("unused")
	public String getCompValue() {
		return this.compValue;
	}

	public MessageKeyEnum getKeyValueEnum() {
		final MessageKeyEnum valueOf = MessageKeyEnum.getEnumFor(this.keyValue);
		return valueOf;
	}

	public StringRegelOperator getOperatorEnum() {
		return StringRegelOperator.valueOf(this.operator);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((this.compValue == null) ? 0 : this.compValue.hashCode());
		result = prime * result
				+ ((this.keyValue == null) ? 0 : this.keyValue.hashCode());
		result = prime * result + this.operator;
		return result;
	}

	/**
	 * @param compValue
	 *            the compValue to set
	 */
	public void setCompValue(final String compValue) {
		this.compValue = compValue;
	}

	public void setKeyValue(final MessageKeyEnum keyValue) {
		Contract.requireNotNull("keyValue", keyValue);

		this.setKeyValue(keyValue.getStringValue());
	}

	/**
	 * TODO Rename to sth. like setStringOperator
	 */
	public void setOperatorEnum(final StringRegelOperator op) {
		this.setOperator(op.databaseValue());
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder(super.toString());
		builder.append(" + key: ");
		builder.append(this.keyValue);
		builder.append(", operator: ");
		builder.append(this.operator);
		builder.append(", compareValue: ");
		builder.append(this.compValue);
		return builder.toString();
	}

	/**
	 * @param keyValue
	 *            the keyValue to set
	 */
	protected void setKeyValue(final String keyValue) {
		this.keyValue = keyValue;
	}

	/**
	 * @return the keyValue
	 */
	@SuppressWarnings("unused")
	private String getKeyValue() {
		return this.keyValue;
	}

	/**
	 * @return the operator
	 */
	@SuppressWarnings("unused")
	private short getOperator() {
		return this.operator;
	}

	/**
	 * @param operator
	 *            the operator to set
	 */
	@SuppressWarnings("unused")
	private void setOperator(final short operator) {
		this.operator = operator;
	}

}
