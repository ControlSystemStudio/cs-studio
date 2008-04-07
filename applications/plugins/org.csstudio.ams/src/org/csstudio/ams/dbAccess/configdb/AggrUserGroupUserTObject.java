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

import java.io.Serializable;
import java.util.Date;

public class AggrUserGroupUserTObject implements Serializable
{
	private static final long serialVersionUID = 4054741037803777785L;
	
	private UserGroupUserTObject userGroupUser;
	private UserTObject user;

	public AggrUserGroupUserTObject(UserGroupUserTObject userGroupUser, UserTObject user)
	{
		this.userGroupUser = userGroupUser;
		this.user = user;
	}
	
	public AggrUserGroupUserTObject(int userGroupId, int userRef, String userName, int iPos, short isActive, String activeReason, Date timeChange)
	{
		userGroupUser = new UserGroupUserTObject(userGroupId, userRef, iPos, isActive, activeReason, timeChange);
		user = new UserTObject();
		user.setName(userName);
	}
	
	////////// Getter- and Setter-Methods //////////

	public UserKey getKey()
	{
		return new UserKey(userGroupUser.getUserRef(), user.getName(), 0);
	}

	public UserTObject getUser() {
		return user;
	}

	public void setUser(UserTObject user) {
		this.user = user;
	}

	public UserGroupUserTObject getUserGroupUser() {
		return userGroupUser;
	}

	public void setUserGroupUser(UserGroupUserTObject userGroupUser) {
		this.userGroupUser = userGroupUser;
	}
	
	public String toString()
	{
		return getKey().toString();
	}
}
