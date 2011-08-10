
package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author tr, mw
 * 
 * <pre>
 * create table AMS_History
 *  (
 *  iHistoryID		INT NOT NULL GENERATED ALWAYS AS IDENTITY,
 *  tTimeNew		BIGINT,
 *  cType			VARCHAR(16),
 *  cMsgHost		VARCHAR(64),
 *  cMsgProc		VARCHAR(64),
 *  cMsgName		VARCHAR(64),
 *  cMsgEventTime	VARCHAR(32),
 *  cDescription	VARCHAR(512),
 *  cActionType		VARCHAR(16),	
 *  iGroupRef		INT,
 *  cGroupName		VARCHAR(64),
 *  iReceiverPos	INT,
 *  iUserRef 		INT,
 *  cUserName 		VARCHAR(128),
 *  cDestType		VARCHAR(16),	
 *  cDestAdress		VARCHAR(128),
 *  PRIMARY KEY(iHistoryID)
 *  );
 * </pre>
 */
@Entity
@Table(name = "AMS_History")
public class HistoryDTO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "iHistoryID")
	private int iHistoryID;
	@Column(name = "tTimeNew")
	private long tTimeNew;
	@Column(name = "cType", length = 16)
	private String cType;
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

	public String getCActionType() {
		return this.cActionType;
	}

	public String getCDescription() {
		return this.cDescription;
	}

	public String getCDestAdress() {
		return this.cDestAdress;
	}

	public String getCDestType() {
		return this.cDestType;
	}

	public String getCGroupName() {
		return this.cGroupName;
	}

	public String getCMsgEventTime() {
		return this.cMsgEventTime;
	}

	public String getCMsgHost() {
		return this.cMsgHost;
	}

	public String getCMsgName() {
		return this.cMsgName;
	}

	public String getCMsgProc() {
		return this.cMsgProc;
	}

	public String getCUserName() {
		return this.cUserName;
	}

	public int getIGroupRef() {
		return this.iGroupRef;
	}

	public int getIHistoryID() {
		return this.iHistoryID;
	}

	public int getIReceiverPos() {
		return this.iReceiverPos;
	}

	public int getIUserRef() {
		return this.iUserRef;
	}

	public long getTTimeNew() {
		return this.tTimeNew;
	}

	public void setCActionType(final String actionType) {
		this.cActionType = actionType;
	}

	public void setCDescription(final String description) {
		this.cDescription = description;
	}

	public void setCDestAdress(final String destAdress) {
		this.cDestAdress = destAdress;
	}

	public void setCDestType(final String destType) {
		this.cDestType = destType;
	}

	public void setCGroupName(final String groupName) {
		this.cGroupName = groupName;
	}

	public void setCMsgEventTime(final String msgEventTime) {
		this.cMsgEventTime = msgEventTime;
	}

	public void setCMsgHost(final String msgHost) {
		this.cMsgHost = msgHost;
	}

	public void setCMsgName(final String msgName) {
		this.cMsgName = msgName;
	}

	public void setCMsgProc(final String msgProc) {
		this.cMsgProc = msgProc;
	}

	public void setCUserName(final String userName) {
		this.cUserName = userName;
	}

	public void setIGroupRef(final int groupRef) {
		this.iGroupRef = groupRef;
	}

	public void setIHistoryID(final int historyID) {
		this.iHistoryID = historyID;
	}

	public void setIReceiverPos(final int receiverPos) {
		this.iReceiverPos = receiverPos;
	}

	public void setIUserRef(final int userRef) {
		this.iUserRef = userRef;
	}

	public void setTTimeNewAsDate(final Date timeNew) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		String sdate = sdf.format(timeNew);
		
		this.tTimeNew = Long.parseLong(sdate);
	}

	public String getCType() {
		return cType;
	}

	public void setCType(String type) {
		cType = type;
	}
}
