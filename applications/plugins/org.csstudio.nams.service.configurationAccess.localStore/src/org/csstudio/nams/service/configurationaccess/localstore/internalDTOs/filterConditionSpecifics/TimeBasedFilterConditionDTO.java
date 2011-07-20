
package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.material.regelwerk.StringRegelOperator;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;

/**
 * Dieses Daten-Transfer-Objekt stellt hält die Konfiguration einer
 * AMS_FilterCond_TimeBasedItems.
 * 
 * Das Create-Statement für die Datenbank hat folgendes Aussehen:
 * 
 * <pre>
 * create table AMS_FilterCond_TimeBased
 * 
 * iFilterConditionRef	INT NOT NULL,
 * cStartKeyValue		VARCHAR(16),
 * sStartOperator		SMALLINT,
 * cStartCompValue		VARCHAR(128),
 * cConfirmKeyValue	VARCHAR(16),
 * sConfirmOperator	SMALLINT,
 * cConfirmCompValue	VARCHAR(128),
 * sTimePeriod			SMALLINT,
 * sTimeBehavior		SMALLINT
 * ;
 * </pre>
 */
@Entity
@Table(name = "AMS_FilterCond_TimeBased")
@PrimaryKeyJoinColumn(name = "iFilterConditionRef")
public class TimeBasedFilterConditionDTO extends FilterConditionDTO {

	@Column(name = "cConfirmCompValue")
	private String cConfirmCompValue;
	@Column(name = "cConfirmKeyValue")
	private String cConfirmKeyValue;

	@Column(name = "cStartCompValue")
	private String cStartCompValue;

	@Column(name = "cStartKeyValue")
	private String cStartKeyValue;

	@Column(name = "sConfirmOperator")
	private short sConfirmOperator;
	@Column(name = "sStartOperator")
	private short sStartOperator;

	@Column(name = "sTimeBehavior")
	private short sTimeBehavior;
	@Column(name = "sTimePeriod")
	private short sTimePeriod;

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof TimeBasedFilterConditionDTO)) {
			return false;
		}
		final TimeBasedFilterConditionDTO other = (TimeBasedFilterConditionDTO) obj;
		if (this.cConfirmCompValue == null) {
			if (other.cConfirmCompValue != null) {
				return false;
			}
		} else if (!this.cConfirmCompValue.equals(other.cConfirmCompValue)) {
			return false;
		}
		if (this.cConfirmKeyValue == null) {
			if (other.cConfirmKeyValue != null) {
				return false;
			}
		} else if (!this.cConfirmKeyValue.equals(other.cConfirmKeyValue)) {
			return false;
		}
		if (this.cStartCompValue == null) {
			if (other.cStartCompValue != null) {
				return false;
			}
		} else if (!this.cStartCompValue.equals(other.cStartCompValue)) {
			return false;
		}
		if (this.cStartKeyValue == null) {
			if (other.cStartKeyValue != null) {
				return false;
			}
		} else if (!this.cStartKeyValue.equals(other.cStartKeyValue)) {
			return false;
		}
		if (this.sConfirmOperator != other.sConfirmOperator) {
			return false;
		}
		if (this.sStartOperator != other.sStartOperator) {
			return false;
		}
		if (this.sTimeBehavior != other.sTimeBehavior) {
			return false;
		}
		if (this.sTimePeriod != other.sTimePeriod) {
			return false;
		}
		return true;
	}

	public String getCConfirmCompValue() {
		return this.cConfirmCompValue;
	}

	public MessageKeyEnum getConfirmKeyValue() {
		return MessageKeyEnum.getEnumFor(this.cConfirmKeyValue);
	}

	public String getCStartCompValue() {
		return this.cStartCompValue;
	}

	public MessageKeyEnum getStartKeyValue() {
		return MessageKeyEnum.getEnumFor(this.cStartKeyValue);
	}

	public StringRegelOperator getTBConfirmOperator() {
		return StringRegelOperator.valueOf(this.sConfirmOperator);
	}

	public StringRegelOperator getTBStartOperator() {
		return StringRegelOperator.valueOf(this.sStartOperator);
	}

	public TimeBasedType getTimeBehavior() {
		return TimeBasedType.valueOf(this.sTimeBehavior);
	}

	public Millisekunden getTimePeriod() {
		return Millisekunden.valueOf(this.sTimePeriod * 1000);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((this.cConfirmCompValue == null) ? 0
						: this.cConfirmCompValue.hashCode());
		result = prime
				* result
				+ ((this.cConfirmKeyValue == null) ? 0 : this.cConfirmKeyValue
						.hashCode());
		result = prime
				* result
				+ ((this.cStartCompValue == null) ? 0 : this.cStartCompValue
						.hashCode());
		result = prime
				* result
				+ ((this.cStartKeyValue == null) ? 0 : this.cStartKeyValue
						.hashCode());
		result = prime * result + this.sConfirmOperator;
		result = prime * result + this.sStartOperator;
		result = prime * result + this.sTimeBehavior;
		result = prime * result + this.sTimePeriod;
		return result;
	}

	public void setCConfirmCompValue(final String confirmCompValue) {
		this.cConfirmCompValue = confirmCompValue;
	}

	public void setCConfirmKeyValue(final String confirmKeyValue) {
		this.cConfirmKeyValue = confirmKeyValue;
	}

	public void setConfirmKeyValue(final MessageKeyEnum startKeyValue) {
		this.cConfirmKeyValue = startKeyValue.getStringValue();
	}

	public void setCStartCompValue(final String startCompValue) {
		this.cStartCompValue = startCompValue;
	}

	public void setStartKeyValue(final MessageKeyEnum value) {
		this.cStartKeyValue = value.getStringValue();
	}

	public void setTBConfirmOperator(final StringRegelOperator operator) {
		this.sConfirmOperator = operator.databaseValue();
	}

	public void setTBStartOperator(final StringRegelOperator operator) {
		this.sStartOperator = operator.databaseValue();
	}

	public void setTimeBehavior(final TimeBasedType timeBasedType) {
		this.sTimeBehavior = timeBasedType.asShort();
	}

	public void setTimePeriod(final Millisekunden millisekunden) {
		this.sTimePeriod = (short) (millisekunden.alsLongVonMillisekunden() / 1000);
	}

	@SuppressWarnings("unused")
	private String getCConfirmKeyValue() {
		return this.cConfirmKeyValue;
	}

	@SuppressWarnings("unused")
	private String getCStartKeyValue() {
		return this.cStartKeyValue;
	}

	@SuppressWarnings("unused")
	private short getSConfirmOperator() {
		return this.sConfirmOperator;
	}

	@SuppressWarnings("unused")
	private short getSStartOperator() {
		return this.sStartOperator;
	}

	@SuppressWarnings("unused")
	private short getSTimeBehavior() {
		return this.sTimeBehavior;
	}

	@SuppressWarnings("unused")
	private short getSTimePeriod() {
		return this.sTimePeriod;
	}

	@SuppressWarnings("unused")
	private void setCStartKeyValue(final String startKeyValue) {
		this.cStartKeyValue = startKeyValue;
	}

	@SuppressWarnings("unused")
	private void setSConfirmOperator(final short confirmOperator) {
		this.sConfirmOperator = confirmOperator;
	}

	@SuppressWarnings("unused")
	private void setSStartOperator(final short startOperator) {
		this.sStartOperator = startOperator;
	}

	@SuppressWarnings("unused")
	private void setSTimeBehavior(final short timeBehavior) {
		this.sTimeBehavior = timeBehavior;
	}

	@SuppressWarnings("unused")
	private void setSTimePeriod(final short timePeriod) {
		this.sTimePeriod = timePeriod;
	}

}
