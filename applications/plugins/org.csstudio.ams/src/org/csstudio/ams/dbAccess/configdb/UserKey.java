
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

import org.csstudio.ams.dbAccess.Key;

@SuppressWarnings("hiding")
public class UserKey extends Key {
	
    private static final long serialVersionUID = -3259015761968404121L;
	
	public int userID;
	public String userName;
	public int groupRef;
	
	public UserKey() {
		this(-1,"",-1);
	}
	
	public UserKey(int userID, String userName, int groupRef) {
		super(Key.USER_KEY);
		this.userID = userID;
		this.userName = userName;
		this.groupRef = groupRef;
	}
	
	@Override
    public String toString() {
		return userName == null ? "" : userName;
	}
	
	@Override
    public int hashCode() {
		return (userID + " " + userName).hashCode();
	}
	
	@Override
    public boolean equals(Object obj) {
		if(obj instanceof UserKey)
			return ((UserKey)obj).userID == userID;
		return false;
	}
	
	@Override
    public int getID() {
		return userID;
	}
	
	@Override
    public int getGroupRef() {
		return groupRef;
	}
	
	@Override
    public void setGroupRef(int groupRef) {
		this.groupRef = groupRef;
	}
}