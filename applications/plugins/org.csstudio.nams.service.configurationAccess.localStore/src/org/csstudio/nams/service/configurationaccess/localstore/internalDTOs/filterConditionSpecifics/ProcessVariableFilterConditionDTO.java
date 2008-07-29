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
@PrimaryKeyJoinColumn(name = "iFilterConditionRef", referencedColumnName="iFilterConditionID")
@Table(name = "AMS_FilterCondition_PV")
public class ProcessVariableFilterConditionDTO extends FilterConditionDTO {

	@Column(name = "cPvChannelName")
	private String cPvChannelName;
	
	@Column(name = "sSuggestedPvTypeId")
	private short sSuggestedPvTypeId;
	
	@Column(name = "sOperatorId")
	private short sOperatorId;
	
	@Column(name = "cCompValue")
	private String cCompValue;

	public IProcessVariableAddress getPVAddress() {
		return ProcessVariableAdressFactory.getInstance().createProcessVariableAdress(cPvChannelName);
	}
	
	public void setPVAddress (IProcessVariableAddress pvAddress) {
		cPvChannelName = pvAddress.getRawName();
	}
	
	public Operator getPVOperator() {
		return Operator.findOperatorOfDBId(sOperatorId);
	}
	
	public void setPVOperator (Operator pvOperator) {
		sOperatorId = pvOperator.asDatabaseId();
	}
	
	public SuggestedProcessVariableType getSuggestedPVType(){
		return SuggestedProcessVariableType.findOperatorOfDBId(sSuggestedPvTypeId);
	}
	
	public void setSuggestedPVType(SuggestedProcessVariableType suggestedProcessVariableType){
		sSuggestedPvTypeId = suggestedProcessVariableType.asDatabaseId();
	}
	
	public String getCCompValue() {
		return cCompValue;
	}
	
	public void setCCompValue(String compValue) {
		cCompValue = compValue;
	}
	
	@SuppressWarnings("unused")
	public String getCPvChannelName() {
		return cPvChannelName;
	}
	
	public void setCPvChannelName(String pvChannelName) {
		cPvChannelName = pvChannelName;
	}
	
	@SuppressWarnings("unused")
	private short getSSuggestedPvTypeId() {
		return sSuggestedPvTypeId;
	}
	
	@SuppressWarnings("unused")
	private void setSSuggestedPvTypeId(short suggestedPvTypeId) {
		sSuggestedPvTypeId = suggestedPvTypeId;
	}
	
	@SuppressWarnings("unused")
	private short getSOperatorId() {
		return sOperatorId;
	}
	
	@SuppressWarnings("unused")
	private void setSOperatorId(short operatorId) {
		sOperatorId = operatorId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((cCompValue == null) ? 0 : cCompValue.hashCode());
		result = prime * result
				+ ((cPvChannelName == null) ? 0 : cPvChannelName.hashCode());
		result = prime * result + sOperatorId;
		result = prime * result + sSuggestedPvTypeId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof ProcessVariableFilterConditionDTO))
			return false;
		final ProcessVariableFilterConditionDTO other = (ProcessVariableFilterConditionDTO) obj;
		if (cCompValue == null) {
			if (other.cCompValue != null)
				return false;
		} else if (!cCompValue.equals(other.cCompValue))
			return false;
		if (cPvChannelName == null) {
			if (other.cPvChannelName != null)
				return false;
		} else if (!cPvChannelName.equals(other.cPvChannelName))
			return false;
		if (sOperatorId != other.sOperatorId)
			return false;
		if (sSuggestedPvTypeId != other.sSuggestedPvTypeId)
			return false;
		return true;
	}
	
	
}
