
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
public class GroupKey  extends Key {
	
    private static final long serialVersionUID = -7878978224239403371L;
	
	public static final short GROUP_USER = 1;
	public static final short GROUP_USERGROUP = 2;
	public static final short GROUP_FILTER_CONDITION = 3;
	public static final short GROUP_FILTER = 4;
	public static final short GROUP_TOPIC = 5;

	public static final int NO_GROUP = -1;
	
	public int groupID;
	public String groupName;
	public short groupType;
	
	public GroupKey(int groupID, String name, short sGroupType)
	{
		super(Key.GROUP_KEY);
		this.groupID = groupID;
		this.groupName = name;
		this.groupType = sGroupType;
	}
	
	
	@Override
    public String toString() {
		return groupName != null ? groupName : "";
	}
	
	@Override
    public int hashCode() {
		return (groupID + " " + groupName).hashCode();
	}
	
	@Override
    public boolean equals(Object obj) {
		if(obj instanceof GroupKey)
			return ((GroupKey)obj).groupID == groupID;
		return false;
	}
	
	@Override
    public int getID() {
		return groupID;
	}
	
	@Override
    public int getGroupRef() {
		return groupID;
	}
}
