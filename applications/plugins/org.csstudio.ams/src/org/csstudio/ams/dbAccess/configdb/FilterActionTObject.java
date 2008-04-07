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

import org.csstudio.ams.dbAccess.ItemInterface;
import org.csstudio.ams.dbAccess.TObject;

/**
	iFilterActionID			NUMBER(11) NOT NULL,
	iFilterActionTypeRef	NUMBER(11) NOT NULL,
	iReceiverRef			NUMBER(11),
	cMessage				VARCHAR2(1024),
	PRIMARY KEY(iFilterActionID)
*/
public class FilterActionTObject extends TObject implements ItemInterface 
{
	private static final long serialVersionUID = 5996354666434626305L;
	
	private int 	filterActionID;// PRIMARY KEY
	private	int 	filterActionTypeRef;
	private	int 	receiverRef;
	private	String 	message;
	
	public FilterActionTObject()
	{
		this.filterActionID = -1;
		this.filterActionTypeRef = -1;
	}
	
	public FilterActionTObject(int filterActionID, int filterActionTypeRef, int receiverRef, String message)
	{
		this.filterActionID = filterActionID;
		this.filterActionTypeRef = filterActionTypeRef;
		this.receiverRef = receiverRef;
		this.message = message;
	}

	public int getID()
	{
		return filterActionID;
	}

	////////// Getter- and Setter-Methods //////////
	
	public int getFilterActionID() {
		return filterActionID;
	}
	public void setFilterActionID(int filterActionID) {
		this.filterActionID = filterActionID;
	}

	public int getFilterActionTypeRef() {
		return filterActionTypeRef;
	}
	public void setFilterActionTypeRef(int filterActionTypeRef) {
		this.filterActionTypeRef = filterActionTypeRef;
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public int getReceiverRef() {
		return receiverRef;
	}
	public void setReceiverRef(int receiverRef) {
		this.receiverRef = receiverRef;
	}
	
	public boolean equals(Object obj)
	{
		if(!(obj instanceof FilterActionTObject))
			return false;
		
		FilterActionTObject compare = (FilterActionTObject)obj;

		if(compare.getFilterActionID() != getFilterActionID())
			return false;
		if(compare.getFilterActionTypeRef() != getFilterActionTypeRef())
			return false;
		if(compare.getReceiverRef() != getReceiverRef())
			return false;
		if(!strEquals(compare.getMessage(), getMessage()))
			return false;
		return true;
	}
}
