
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
	iUserGroupRef		NUMBER(11) NOT NULL,
	iUserRef			NUMBER(11) NOT NULL,
	iPos				NUMBER(11) NOT NULL,
	sActive				NUMBER(6),
	cActiveReason		VARCHAR2(128),
	tTimeChange			TIMESTAMP,
	PRIMARY KEY(iUserGroupRef,iUserRef)
*/
@SuppressWarnings("hiding")
public class UserGroupUserTObject extends TObject
{
	private static final long serialVersionUID = -3076166471173673897L;
	
	private int 	userGroupRef;// PRIMARY KEY (Part 1/2)
	private int 	userRef;// PRIMARY KEY (Part 2/2)
	private int 	pos;
	private short 	isActive;
	private String 	activeReason;
	private Date 	timeChange;

	public UserGroupUserTObject()
	{
		this.userGroupRef = -1;
		this.userRef = -1;
		this.pos = -1;
	}
	
	public UserGroupUserTObject(int userGroupRef
			, int userRef
			, int pos
			, short isActive
			, String activeReason
			, Date timeChange)
	{
		this.userGroupRef = userGroupRef;
		this.userRef = userRef;
		this.pos = pos;
		this.isActive = isActive;
		this.activeReason = activeReason;
		this.timeChange = timeChange;
	}

	////////// Getter- and Setter-Methods //////////

	public short getActive() {
		return isActive;
	}
	public void setActive(short isActive) {
		this.isActive = isActive;
	}

	public int getPos() {
		return pos;
	}
	public void setPos(int pos) {
		this.pos = pos;
	}

	public int getUserGroupRef() {
		return userGroupRef;
	}
	public void setUserGroupRef(int userGroupRef) {
		this.userGroupRef = userGroupRef;
	}

	public int getUserRef() {
		return userRef;
	}
	public void setUserRef(int userRef) {
		this.userRef = userRef;
	}


	public String getActiveReason() {
		return activeReason;
	}
	public void setActiveReason(String activeReason) {
		this.activeReason = activeReason;
	}

	public Date getTimeChange() {
		return timeChange;
	}
	/**
	 * Do not set <code>timeChange</code> directly,
	 * method is used by replication only!
	 * @param timeChange
	 */
	public void setTimeChange(Date timeChange) {
		this.timeChange = timeChange;
	}

	@Override
    public boolean equals(Object obj) {
		
	    if(!(obj instanceof UserGroupUserTObject))
			return false;
		
		UserGroupUserTObject compare = (UserGroupUserTObject)obj;
		
		if(compare.getUserGroupRef() != getUserGroupRef())
			return false;
		if(compare.getUserRef() != getUserRef())
			return false;
		if(compare.getActive() !=  getActive())
			return false;

		return strEquals(compare.getActiveReason(), getActiveReason());
	}
}
