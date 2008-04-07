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

/**
	iItemID			INT
	iFilterConditionRef	INT NOT NULL,
	iFilterRef		INT NOT NULL,
	cIdentifier		INT NOT NULL,
	sState			SMALLINT,
	xStartTime		TIMESTAMP,
	xEndTime		TIMESTAMP,
	sTimeOutAction		SMALLINT,
	iMessageRef		INT,
	PRIMARY KEY(iHistoryID)
*/

public class FilterConditionTimeBasedItemsTObject extends TObject
{
	private static final long serialVersionUID = 7335953694768484815L;
	
	private int     itemID;
	private int 	filterConditionRef;
	private int 	filterRef;
	private String 	identifier;
	private short 	state;
	private Date 	startTime;
	private Date 	endTime;
	private short 	timeOutAction;
	private int		messageRef;

	public FilterConditionTimeBasedItemsTObject()
	{
		this.filterConditionRef = -1;
	}
	
	public FilterConditionTimeBasedItemsTObject(int itemID, int filterConditionRef, int filterRef, String identifier, short state, Date startTime, Date endTime, short timeOutAction, int messageRef) 
	{
		super();
		this.itemID = itemID;
		this.filterConditionRef = filterConditionRef;
		this.filterRef = filterRef;
		this.identifier = identifier;
		this.state = state;
		this.startTime = startTime;
		this.endTime = endTime;
		this.timeOutAction = timeOutAction;
		this.messageRef = messageRef;
	}

	////////// Getter- and Setter-Methods //////////
	
	public int getFilterConditionRef() {
		return filterConditionRef;
	}

	public void setFilterConditionRef(int filterConditionRef) {
		this.filterConditionRef = filterConditionRef;
	}

	public int getFilterRef() {
		return filterRef;
	}

	public void setFilterRef(int filterRef) {
		this.filterRef = filterRef;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public short getState() {
		return state;
	}

	public void setState(short state) {
		this.state = state;
	}

	public short getTimeOutAction() {
		return timeOutAction;
	}

	public void setTimeOutAction(short timeOutAction) {
		this.timeOutAction = timeOutAction;
	}

	public int getItemID() {
		return itemID;
	}

	public void setItemID(int itemID) {
		this.itemID = itemID;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public int getMessageRef() {
		return messageRef;
	}

	public void setMessageRef(int messageRef) {
		this.messageRef = messageRef;
	}	
}
