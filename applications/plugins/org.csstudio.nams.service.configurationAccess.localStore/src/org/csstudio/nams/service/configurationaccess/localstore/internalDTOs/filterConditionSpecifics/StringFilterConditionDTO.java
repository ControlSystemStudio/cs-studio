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
@PrimaryKeyJoinColumn(name = "iFilterConditionRef", referencedColumnName="iFilterConditionID")
public class StringFilterConditionDTO extends FilterConditionDTO {

	
	
	@Column(name = "iFilterConditionRef", nullable = false, updatable = false, insertable = false)
	private int iFilterConditionRef;

	@Column(name = "cKeyValue", length = 16)
	private String keyValue;

	@Column(name = "sOperator")
	private short operator;

	@Column(name = "cCompValue", length = 128)
	private String compValue;

	/**
	 * @return the filterConditionRef
	 */
	@SuppressWarnings("unused")
	private int getIFilterConditionRef() {
		return iFilterConditionRef;
	}

	/**
	 * @param filterConditionRef
	 *            the filterConditionRef to set
	 */
	@SuppressWarnings("unused")
	private void setIFilterConditionRef(int filterConditionRef) {
		this.iFilterConditionRef = filterConditionRef;
	}

	/**
	 * @return the keyValue
	 */
	private String getKeyValue() {
		return keyValue;
	}

	public MessageKeyEnum getKeyValueEnum() {
		MessageKeyEnum valueOf = MessageKeyEnum.getEnumFor(keyValue);
		return valueOf;
	}

	/**
	 * @param keyValue
	 *            the keyValue to set
	 */
	protected void setKeyValue(String keyValue) {
		this.keyValue = keyValue;
	}
	
	public void setKeyValue(MessageKeyEnum keyValue){
		Contract.requireNotNull("keyValue", keyValue);
		
		setKeyValue( keyValue.getStringValue() );
	}

	/**
	 * @return the operator
	 */
	@SuppressWarnings("unused")
	private short getOperator() {
		return operator;
	}

	public StringRegelOperator getOperatorEnum(){
		return StringRegelOperator.valueOf(operator);
	}
	
	/**
	 * TODO Rename to sth. like setStringOperator
	 */
	public void setOperatorEnum(StringRegelOperator op){
		setOperator((short) op.databaseValue());
	}
	
	/**
	 * @param operator
	 *            the operator to set
	 */
	@SuppressWarnings("unused")
	private void setOperator(short operator) {
		this.operator = operator;
	}

	/**
	 * @return the compValue
	 */
	@SuppressWarnings("unused")
	public String getCompValue() {
		return compValue;
	}

	/**
	 * @param compValue
	 *            the compValue to set
	 */
	public void setCompValue(String compValue) {
		this.compValue = compValue;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append(" + key: ");
		builder.append(this.keyValue);
		builder.append(", operator: ");
		builder.append(this.operator);
		builder.append(", compareValue: ");
		builder.append(this.compValue);
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((compValue == null) ? 0 : compValue.hashCode());
		result = prime * result + iFilterConditionRef;
		result = prime * result
				+ ((keyValue == null) ? 0 : keyValue.hashCode());
		result = prime * result + operator;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof StringFilterConditionDTO))
			return false;
		final StringFilterConditionDTO other = (StringFilterConditionDTO) obj;
		if (compValue == null) {
			if (other.compValue != null)
				return false;
		} else if (!compValue.equals(other.compValue))
			return false;
		if (iFilterConditionRef != other.iFilterConditionRef)
			return false;
		if (keyValue == null) {
			if (other.keyValue != null)
				return false;
		} else if (!keyValue.equals(other.keyValue))
			return false;
		if (operator != other.operator)
			return false;
		return true;
	}
	
	
}
