
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.csstudio.ams.Log;
import org.csstudio.ams.OutOfDateException;
import org.csstudio.ams.dbAccess.DAO;

public class AggrUserGroupDAO extends DAO 
{
	public static AggrUserGroupTObject select(Connection con, int userGroupID, int pos) throws SQLException
	{
		AggrUserGroupTObject userGroup = new AggrUserGroupTObject();
		UserGroupTObject usergroup = UserGroupDAO.select(con, userGroupID);
		if (usergroup == null)
			return null;
		
		userGroup.setUsergroup(usergroup);
		ArrayList<AggrUserGroupUserTObject> array = new ArrayList<AggrUserGroupUserTObject>();
		UserGroupUserTObject ugu = UserGroupUserDAO.selectByPos(con, userGroupID, pos);
		if (ugu == null)
			return null;
		
		array.add(new AggrUserGroupUserTObject(ugu, null));
		userGroup.setUsers(array);
		UserDAO.select(con, userGroup.getUsers());
		
		return userGroup;
	}
	
	public static AggrUserGroupTObject selectList(Connection con, int userGroupID) throws SQLException
	{
		AggrUserGroupTObject userGroup = new AggrUserGroupTObject();
		
		userGroup.setUsergroup(UserGroupDAO.select(con, userGroupID));
		userGroup.setUsers(UserGroupUserDAO.selectList(con, userGroupID));
		UserDAO.select(con, userGroup.getUsers());
		
		return userGroup;
	}
	
	public static void insert(Connection con, AggrUserGroupTObject userGroup) throws SQLException
	{
		UserGroupDAO.insert(con, userGroup.getUsergroup());
		
		Iterator<AggrUserGroupUserTObject> iter = userGroup.getUsers().iterator();
		short i = 1;
		while(iter.hasNext())
		{
			UserGroupUserTObject ugu = iter.next().getUserGroupUser();
			ugu.setUserGroupRef(userGroup.getUsergroup().getUserGroupID());
			ugu.setPos(i++);
		}
		
		UserGroupUserDAO.insertList(con, userGroup.getUsers());
	}
	
	public static void update(Connection con,
			AggrUserGroupTObject userGroup) throws SQLException, OutOfDateException
	{
		if (UserGroupUserDAO.updateInTransaction(con, 
				userGroup.getUsergroup().getUserGroupID(), userGroup.getUsers()))
		{
			UserGroupDAO.update(con, userGroup.getUsergroup());
		}
		else
			throw new OutOfDateException("AMS_UserGroup_User data has changed - reload data.");
	}
	
	public static void remove(Connection con, int userGroupID) throws SQLException
	{
		UserGroupDAO.remove(con, userGroupID);
		UserGroupUserDAO.remove(con, userGroupID);
	}
	
	public static List<UserGroupKey> selectUserGroupsForUser(Connection con, int userRef) throws SQLException
	{
		String query = "SELECT iUserGroupRef, cUserGroupName, iGroupRef "
			+"FROM AMS_UserGroup, AMS_UserGroup_User " 
			+"WHERE iUserGroupRef = iUserGroupRef AND iUserRef=?";
		
		PreparedStatement st = null;
		ResultSet rs = null;
		ArrayList<UserGroupKey> array = new ArrayList<UserGroupKey>();
		
		try
		{
			st = con.prepareStatement(query);
			st.setInt(1, userRef);
			rs = st.executeQuery();
			
			while(rs.next())
				array.add(new UserGroupKey(rs.getInt(1),rs.getString(2), rs.getInt(3)));
			return array;
		}
		catch(SQLException ex)
		{
			Log.log(Log.FATAL, "Sql-Query failed: " + query, ex);
			throw ex;
		}
		finally
		{
			close(st,rs);
		}
	}
}
