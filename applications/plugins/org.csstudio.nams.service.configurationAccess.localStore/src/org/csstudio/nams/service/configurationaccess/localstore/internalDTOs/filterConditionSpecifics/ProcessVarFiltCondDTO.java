
package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.csstudio.nams.common.material.regelwerk.Operator;
import org.csstudio.nams.common.material.regelwerk.SuggestedProcessVariableType;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;

/**
 * Dieses Daten-Transfer-Objekt stellt hält die Konfiguration einer
 * AMS_FilterCondition_PV.
 * 
 * Das Create-Statement für die Datenbank hat folgendes Aussehen:
 * 
 * <pre>
 * create table AMS_FilterCondition_PV
 * 
 * iFilterConditionRef	INT NOT NULL,
 * cPvChannelName		VARCHAR(128),
 * sSuggestedPvTypeId	SMALLINT,
 * sOperatorId			SMALLINT,
 * cCompValue			VARCHAR(128)
 * ;
 * </pre>
 */
@Entity
@PrimaryKeyJoinColumn(name = "iFilterConditionRef", referencedColumnName = "iFilterConditionID")
@Table(name = "AMS_FilterCondition_PV")
public class ProcessVarFiltCondDTO extends FilterConditionDTO {

	@Column(name = "cPvChannelName")
	private String cPvChannelName;

	@Column(name = "sSuggestedPvTypeId")
	private short sSuggestedPvTypeId;

	@Column(name = "sOperatorId")
	private short sOperatorId;

	@Column(name = "cCompValue")
	private String cCompValue;

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof ProcessVarFiltCondDTO)) {
			return false;
		}
		final ProcessVarFiltCondDTO other = (ProcessVarFiltCondDTO) obj;
		if (this.cCompValue == null) {
			if (other.cCompValue != null) {
				return false;
			}
		} else if (!this.cCompValue.equals(other.cCompValue)) {
			return false;
		}
		if (this.cPvChannelName == null) {
			if (other.cPvChannelName != null) {
				return false;
			}
		} else if (!this.cPvChannelName.equals(other.cPvChannelName)) {
			return false;
		}
		if (this.sOperatorId != other.sOperatorId) {
			return false;
		}
		if (this.sSuggestedPvTypeId != other.sSuggestedPvTypeId) {
			return false;
		}
		return true;
	}

	public String getCCompValue() {
		return this.cCompValue;
	}

	@SuppressWarnings("unused")
	public String getCPvChannelName() {
		return this.cPvChannelName;
	}

	public IProcessVariableAddress getPVAddress() {
		return ProcessVariableAdressFactory.getInstance()
				.createProcessVariableAdress(this.cPvChannelName);
	}

	public Operator getPVOperator() {
		return Operator.findOperatorOfDBId(this.sOperatorId);
	}

	public SuggestedProcessVariableType getSuggestedPVType() {
		return SuggestedProcessVariableType
				.findOperatorOfDBId(this.sSuggestedPvTypeId);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((this.cCompValue == null) ? 0 : this.cCompValue.hashCode());
		result = prime
				* result
				+ ((this.cPvChannelName == null) ? 0 : this.cPvChannelName
						.hashCode());
		result = prime * result + this.sOperatorId;
		result = prime * result + this.sSuggestedPvTypeId;
		return result;
	}

	public void setCCompValue(final String compValue) {
		this.cCompValue = compValue;
	}

	public void setCPvChannelName(final String pvChannelName) {
		this.cPvChannelName = pvChannelName;
	}

	public void setPVAddress(final IProcessVariableAddress pvAddress) {
		this.cPvChannelName = pvAddress.getRawName();
	}

	public void setPVOperator(final Operator pvOperator) {
		this.sOperatorId = pvOperator.asDatabaseId();
	}

	public void setSuggestedPVType(
			final SuggestedProcessVariableType suggestedProcessVariableType) {
		this.sSuggestedPvTypeId = suggestedProcessVariableType.asDatabaseId();
	}

	@SuppressWarnings("unused")
	private short getSOperatorId() {
		return this.sOperatorId;
	}

	@SuppressWarnings("unused")
	private short getSSuggestedPvTypeId() {
		return this.sSuggestedPvTypeId;
	}

	@SuppressWarnings("unused")
	private void setSOperatorId(final short operatorId) {
		this.sOperatorId = operatorId;
	}

	@SuppressWarnings("unused")
	private void setSSuggestedPvTypeId(final short suggestedPvTypeId) {
		this.sSuggestedPvTypeId = suggestedPvTypeId;
	}

}
