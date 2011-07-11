
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
	iUserGroupId		NUMBER(11) NOT NULL,
	iGroupRef			NUMBER(11) default -1 NOT NULL,
	cUserGroupName		VARCHAR2(128),
	sMinGroupMember		NUMBER(6),
	iTimeOutSec			NUMBER(11),
	PRIMARY KEY (iUserGroupId)						
*/
@SuppressWarnings("hiding")
public class UserGroupTObject extends TObject implements ItemInterface {
	
    private static final long serialVersionUID = 4181263580167586575L;
	
	private int 	userGroupID;// PRIMARY KEY
	private int 	groupRef;
	private String 	name;
	private short 	minGroupMember;
	private int 	timeOutSec;
	private int     isActive;
	
	public UserGroupTObject()
	{
		this.userGroupID = -1;
		this.groupRef = -1;
	}
	
	public UserGroupTObject(int userGroupID, int groupRef, String name, short minGroupMember, int timeOutSec, int isActive)
	{
		this.userGroupID = userGroupID;
		this.groupRef = groupRef;
		this.name = name;
		this.minGroupMember = minGroupMember;
		this.timeOutSec = timeOutSec;
		this.isActive = isActive;
	}
	
	public UserGroupKey getKey() {
		return new UserGroupKey(userGroupID, name, groupRef);
	}

	@Override
    public int getID() {
		return userGroupID;
	}

	////////// Getter- and Setter-Methods //////////

	public int getGroupRef() {
		return groupRef;
	}

	public void setGroupRef(int groupRef) {
		this.groupRef = groupRef;
	}

	public short getMinGroupMember() {
		return minGroupMember;
	}

	public void setMinGroupMember(short minGroupMember) {
		this.minGroupMember = minGroupMember;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIsActive()
	{
	    return this.isActive;
	}
	
	public void setIsActive(int isActive)
	{
	    this.isActive = isActive;
	}
	
	public int getTimeOutSec() {
		return timeOutSec;
	}

	public void setTimeOutSec(int timeOutSec) {
		this.timeOutSec = timeOutSec;
	}

	public int getUserGroupID() {
		return userGroupID;
	}

	public void setUserGroupID(int userGroupID) {
		this.userGroupID = userGroupID;
	}
	
	@Override
    public boolean equals(Object obj) {
		
	    if(!(obj instanceof UserGroupTObject))
			return false;
		
		UserGroupTObject compare = (UserGroupTObject)obj;
		
		if(compare.getGroupRef() != getGroupRef())
			return false;
		if(compare.getMinGroupMember() != getMinGroupMember())
			return false;
		if(!strEquals(compare.getName(), getName()))
			return false;
		if(compare.getTimeOutSec() != getTimeOutSec())
			return false;
		if(compare.getUserGroupID() != getUserGroupID())
			return false;
		return true;
	}
}
