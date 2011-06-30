
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

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.csstudio.ams.AMSException;
import org.csstudio.ams.Log;
import org.csstudio.ams.dbAccess.DAO;
import org.csstudio.ams.dbAccess.Key;

public abstract class AggrGroupsDAO extends DAO  {
	
    public static void deleteGroup(Connection con, int groupID) throws AMSException {
		
        try {
			
            GroupsTObject group = GroupsDAO.select(con, groupID);
			
			if(group == null)
				return;
			
			String table = null;
			
			// TODO: Gibt es ein anderes Verhalten?
			//       Vorher GroupKey.FILTER_KEY
			switch(group.getType())
			{
			case Key.FILTER_KEY:
				table = "AMS_Filter";
				break;
			case Key.FILTERCONDITION_KEY:
				table = "AMS_FilterCondition";
				break;
			case GroupKey.GROUP_USERGROUP:
				table = "AMS_UserGroup";
				break;
			case GroupKey.GROUP_USER:
				table = "AMS_User";
				break;
			case GroupKey.GROUP_TOPIC:
				table = "AMS_Topic";
				break;
			default:
				return;
			}
			
			String query = "UPDATE " + table + " SET iGroupRef=? WHERE iGroupRef=?";
			
			PreparedStatement st = null;
			
			try
			{	
				con.setAutoCommit(false);
				st = con.prepareStatement(query);
				st.setInt(1, -1);
				st.setInt(2, groupID);
				st.executeUpdate();
			
				GroupsDAO.remove(con, groupID);
				con.commit();
			}
			catch(Exception ex)
			{
				con.rollback();
				throw ex;
			}
			finally
			{
				con.setAutoCommit(true);
				close(st,null);
			}			
		}
		catch(Exception ex)
		{
			Log.log(Log.ERROR, ex);
			throw new AMSException(ex);
		}
	}
	
	public static void updateGroupName(Connection con, int groupID, String name) throws Exception
	{
		GroupsTObject group = GroupsDAO.select(con, groupID);
		
		group.setName(name);
		GroupsDAO.update(con, group);
	}
}
