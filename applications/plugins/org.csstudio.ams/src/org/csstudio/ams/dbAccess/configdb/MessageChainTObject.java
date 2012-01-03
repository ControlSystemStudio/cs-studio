
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

/**
	iMessageChainID		INT NOT NULL,
	iMessageRef			INT NOT NULL,
	iFilterRef			INT NOT NULL,
	iFilterActionRef	INT NOT NULL,
	iReceiverPos		INT NOT NULL,
	tSendTime			TIMESTAMP,
	tNextActTime		TIMESTAMP,
	sChainState			SMALLINT,	
	PRIMARY KEY(iMessageChainID)
*/
@SuppressWarnings("hiding")
public class MessageChainTObject {
	
    private int 	messageChainID;// PRIMARY KEY
	private int 	messageRef;
	private int 	filterRef;
	private int 	filterActionRef;
	private int 	receiverPos;
	private Date 	sendTime;
	private Date 	nextActTime;
	private short 	chainState;
	private String 	receiverAdress;

	public MessageChainTObject() {
	    // Nothing to do
	}
	
	public MessageChainTObject(int messageChainID, 
			int messageRef, 
			int filterRef, 
			int filterActionRef, 
			int receiverPos, 
			Date sendTime, 
			Date nextActTime, 
			short chainState,
			String 	receiverAdress)
	{
		this.messageChainID = messageChainID;
		this.messageRef = messageRef;
		this.filterRef = filterRef;
		this.filterActionRef = filterActionRef;
		this.receiverPos = receiverPos;
		this.sendTime = sendTime;
		this.nextActTime = nextActTime;
		this.chainState = chainState;
		this.receiverAdress = receiverAdress;
	}

	////////// Getter- and Setter-Methods //////////

	public short getChainState() {
		return chainState;
	}

	public void setChainState(short chainState) {
		this.chainState = chainState;
	}

	public int getFilterActionRef() {
		return filterActionRef;
	}

	public void setFilterActionRef(int filterActionRef) {
		this.filterActionRef = filterActionRef;
	}

	public int getMessageRef() {
		return messageRef;
	}

	public void setMessageRef(int messageRef) {
		this.messageRef = messageRef;
	}

	public int getMessageChainID() {
		return messageChainID;
	}

	public void setMessageChainID(int messageChainID) {
		this.messageChainID = messageChainID;
	}

	public Date getNextActTime() {
		return nextActTime;
	}

	public void setNextActTime(Date nextActTime) {
		this.nextActTime = nextActTime;
	}

	public int getReceiverPos() {
		return receiverPos;
	}

	public void setReceiverPos(int receiverPos) {
		this.receiverPos = receiverPos;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public int getFilterRef() {
		return filterRef;
	}

	public void setFilterRef(int filterRef) {
		this.filterRef = filterRef;
	}

	public String getReceiverAdress() {
		return receiverAdress;
	}

	public void setReceiverAdress(String receiverAdress) {
		this.receiverAdress = receiverAdress;
	}
}
