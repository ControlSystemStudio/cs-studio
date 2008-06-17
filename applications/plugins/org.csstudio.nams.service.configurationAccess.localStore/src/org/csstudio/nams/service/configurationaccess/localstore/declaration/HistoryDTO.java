package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author tr, mw
 * 
 * create table AMS_History ( iHistoryID INT NOT NULL GENERATED ALWAYS AS
 * IDENTITY, tTimeNew BIGINT, cMsgHost VARCHAR(64), cMsgProc VARCHAR(64),
 * cMsgName VARCHAR(64), cMsgEventTime VARCHAR(32), cDescription VARCHAR(512),
 * cActionType VARCHAR(16), iGroupRef INT, cGroupName VARCHAR(64), iReceiverPos
 * INT, iUserRef INT, cUserName VARCHAR(128), cDestType VARCHAR(16), cDestAdress
 * VARCHAR(128), PRIMARY KEY(iHistoryID) );
 */
@Entity
@Table(name = "AMS_History")
public class HistoryDTO {
	@Id
	@Column(name = "iHistoryID")
	private int iHistoryID;
	@Column(name = "tTimeNew")
	private long tTimeNew;
	@Column(name = "cMsgHost")
	private String cMsgHost;
	@Column(name = "cMsgProc")
	private String cMsgProc;
	@Column(name = "cMsgName")
	private String cMsgName;
	@Column(name = "cMsgEventTime")
	private String cMsgEventTime;
	@Column(name = "cDescription")
	private String cDescription;
	@Column(name = "cActionType")
	private String cActionType;
	@Column(name = "iGroupRef")
	private int iGroupRef;
	@Column(name = "cGroupName")
	private String cGroupName;
	@Column(name = "iReceiverPos")
	private int iReceiverPos;
	@Column(name = "iUserRef")
	private int iUserRef;
	@Column(name = "cUserName")
	private String cUserName;
	@Column(name = "cDestType")
	private String cDestType;
	@Column(name = "cDestAdress")
	private String cDestAdress;
	public int getIHistoryID() {
		return iHistoryID;
	}
	public void setIHistoryID(int historyID) {
		iHistoryID = historyID;
	}
	public long getTTimeNew() {
		return tTimeNew;
	}
	public void setTTimeNew(long timeNew) {
		tTimeNew = timeNew;
	}
	public String getCMsgHost() {
		return cMsgHost;
	}
	public void setCMsgHost(String msgHost) {
		cMsgHost = msgHost;
	}
	public String getCMsgProc() {
		return cMsgProc;
	}
	public void setCMsgProc(String msgProc) {
		cMsgProc = msgProc;
	}
	public String getCMsgName() {
		return cMsgName;
	}
	public void setCMsgName(String msgName) {
		cMsgName = msgName;
	}
	public String getCMsgEventTime() {
		return cMsgEventTime;
	}
	public void setCMsgEventTime(String msgEventTime) {
		cMsgEventTime = msgEventTime;
	}
	public String getCDescription() {
		return cDescription;
	}
	public void setCDescription(String description) {
		cDescription = description;
	}
	public String getCActionType() {
		return cActionType;
	}
	public void setCActionType(String actionType) {
		cActionType = actionType;
	}
	public int getIGroupRef() {
		return iGroupRef;
	}
	public void setIGroupRef(int groupRef) {
		iGroupRef = groupRef;
	}
	public String getCGroupName() {
		return cGroupName;
	}
	public void setCGroupName(String groupName) {
		cGroupName = groupName;
	}
	public int getIReceiverPos() {
		return iReceiverPos;
	}
	public void setIReceiverPos(int receiverPos) {
		iReceiverPos = receiverPos;
	}
	public int getIUserRef() {
		return iUserRef;
	}
	public void setIUserRef(int userRef) {
		iUserRef = userRef;
	}
	public String getCUserName() {
		return cUserName;
	}
	public void setCUserName(String userName) {
		cUserName = userName;
	}
	public String getCDestType() {
		return cDestType;
	}
	public void setCDestType(String destType) {
		cDestType = destType;
	}
	public String getCDestAdress() {
		return cDestAdress;
	}
	public void setCDestAdress(String destAdress) {
		cDestAdress = destAdress;
	}
}
