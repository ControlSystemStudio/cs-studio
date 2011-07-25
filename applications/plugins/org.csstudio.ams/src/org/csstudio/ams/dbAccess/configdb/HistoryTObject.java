
/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.ams.dbAccess.configdb;

import java.util.Date;

import org.csstudio.ams.dbAccess.TObject;

@SuppressWarnings("hiding")
public class HistoryTObject extends TObject {
	
    private static final long serialVersionUID = 2093650845338572349L;
	
	private int 	historyID;
	private Date 	timeNew;
	private String	type;
	private String	msgHost;
	private String	msgProc;
	private String	msgName;
	private String	msgEventtime;
	private String	description;
	private String	actionType;	
	private int 	groupRef;
	private String	groupName;	
	private int 	receiverPos;
	private int 	userRef;
	private String	userName;
	private String	destType;
	private String	destAdress;
	
	public HistoryTObject() {
	    // Nothing to do
	}
	
	public HistoryTObject(	int historyID,
							Date 	timeNew,
							String	type,
							String	msgHost,
							String	msgProc,
							String	msgName,
							String	msgEventtime,
							String	description,
							String	actionType,	
							int 	groupRef,
							String	groupName,	
							int 	receiverPos,
							int 	userRef,
							String	userName,
							String	destType,
							String	destAdress)
	{
		this.historyID = historyID;
		this.timeNew = timeNew;
		this.type = type;
		this.msgHost = msgHost;
		this.msgProc = msgProc;
		this.msgName = msgName;
		this.msgEventtime = msgEventtime;
		this.description = description;
		this.actionType = actionType;
		this.groupRef = groupRef;
		this.groupName = groupName;
		this.receiverPos = receiverPos;
		this.userRef = userRef;
		this.userName = userName;
		this.destType = destType;
		this.destAdress = destAdress;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getGroupName() {
		return groupName;
	}
	
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	public String getDestAdress() {
		return destAdress;
	}

	public void setDestAdress(String destAdress) {
		this.destAdress = destAdress;
	}

	public String getDestType() {
		return destType;
	}

	public void setDestType(String destType) {
		this.destType = destType;
	}

	public int getGroupRef() {
		return groupRef;
	}

	public void setGroupRef(int groupRef) {
		this.groupRef = groupRef;
	}

	public int getHistoryID() {
		return historyID;
	}

	public void setHistoryID(int historyID) {
		this.historyID = historyID;
	}

	public String getMsgHost() {
		return msgHost;
	}

	public void setMsgHost(String msgHost) {
		this.msgHost = msgHost;
	}

	public String getMsgProc() {
		return msgProc;
	}

	public void setMsgProc(String msgProc) {
		this.msgProc = msgProc;
	}

	public int getReceiverPos() {
		return receiverPos;
	}

	public void setReceiverPos(int receiverPos) {
		this.receiverPos = receiverPos;
	}

	public Date getTimeNew() {
		return timeNew;
	}

	public void setTimeNew(Date timeNew) {
		this.timeNew = timeNew;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getUserRef() {
		return userRef;
	}

	public void setUserRef(int userRef) {
		this.userRef = userRef;
	}
	
	public String getMsgEventtime() {
		return msgEventtime;
	}
	
	public void setMsgEventtime(String msgEventtime) {
		this.msgEventtime = msgEventtime;
	}
	
	public String getMsgName() {
		return msgName;
	}
	
	public void setMsgName(String msgName) {
		this.msgName = msgName;
	}
}
