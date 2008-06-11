package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.ams.configurationStoreService.util.Operator;
import org.csstudio.ams.configurationStoreService.util.SuggestedProcessVariableType;

import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;

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
@Table(name = "AMS_FilterCondition_PV")
@PrimaryKeyJoinColumn(name = "iFilterConditionRef")
public class ProcessVariableFilterConditionDTO extends FilterConditionDTO {

	@Column(name = "iFilterConditionRef", nullable = false, updatable = false, insertable = false)
	private int iFilterConditionRef;

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
	private int getIFilterConditionRef() {
		return iFilterConditionRef;
	}
	
	@SuppressWarnings("unused")
	private void setIFilterConditionRef(int filterConditionRef) {
		iFilterConditionRef = filterConditionRef;
	}

	@SuppressWarnings("unused")
	private String getCPvChannelName() {
		return cPvChannelName;
	}
	
	@SuppressWarnings("unused")
	private void setCPvChannelName(String pvChannelName) {
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
}
