
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
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("hiding")
public class AggrUserGroupTObject implements Serializable
{
	private static final long serialVersionUID = 3090490658348259313L;
	
	private UserGroupTObject usergroup = new UserGroupTObject();
	private List<AggrUserGroupUserTObject> users = new ArrayList<AggrUserGroupUserTObject>();
	
	public UserGroupKey getKey() {
		return new UserGroupKey(usergroup.getUserGroupID(),
		                        usergroup.getName(),usergroup.getGroupRef());
	}
	
	public UserGroupTObject getUsergroup() {
		return usergroup;
	}
	public void setUsergroup(UserGroupTObject usergroup) {
		this.usergroup = usergroup;
	}
	public List<AggrUserGroupUserTObject> getUsers() {
		return users;
	}
	public void setUsers(List<AggrUserGroupUserTObject> users) {
		this.users = users;
	}
	
	public boolean isEquals(Object obj) {
		
	    if(!(obj instanceof AggrUserGroupTObject))
			return false;
		
		AggrUserGroupTObject compare = (AggrUserGroupTObject)obj;
		
		if(compare.getUsergroup() != null && !compare.getUsergroup().equals(getUsergroup()))
			return false;
		
		if(compare.getUsers() == null && getUsers() == null)
			return true;
		
		if(compare.getUsers() != null && getUsers() != null) {
			if(compare.getUsers().size() != getUsers().size())
				return false;
			
			for(int i = 0; i < compare.getUsers().size(); i++) {
				if(!compare.getUsers().get(i).getUserGroupUser().equals(getUsers().get(i).getUserGroupUser()))
						return false;
			}
		}
		else
			return false;

		return true;
	}
}
