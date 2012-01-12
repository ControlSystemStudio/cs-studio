
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
import java.util.List;
import org.csstudio.ams.Log;
import org.csstudio.ams.dbAccess.DAO;
import org.csstudio.ams.dbAccess.PreparedStatementHolder;

public abstract class UserGroupDAO extends DAO 
{
	public static void copyUserGroup(Connection masterDB, Connection localDB) throws SQLException 
	{
		copyUserGroup(masterDB, localDB, DB_BACKUP_SUFFIX);
	}
	
	public static void copyUserGroup(Connection masterDB, Connection localDB, String masterDbSuffix) throws SQLException 
	{
		copyUserGroup(masterDB, localDB, masterDbSuffix, "");
	}
	
	public static void backupUserGroup(Connection masterDB) throws SQLException
	{
		copyUserGroup(masterDB, masterDB, "", DB_BACKUP_SUFFIX);
	}
	
	private static void copyUserGroup(Connection masterDB, Connection targetDB,
							String strMaster, String strTarget) throws SQLException
	{
		final String query = "SELECT iUserGroupId,iGroupRef,cUserGroupName,sMinGroupMember,iTimeOutSec,sActive "
			+"FROM AMS_UserGroup" + strMaster;
		ResultSet rs = null;
		PreparedStatement st = null;
		PreparedStatementHolder psth = null;
		
		try
		{
			psth = new PreparedStatementHolder();			
			st = masterDB.prepareStatement(query);
			rs = st.executeQuery();
			
			while(rs.next())
			{
				UserGroupTObject ugObj = new UserGroupTObject(
						rs.getInt(1), 
						rs.getInt(2), 
						rs.getString(3), 
						rs.getShort(4), 
						rs.getInt(5),
						rs.getInt(6));
				preparedInsertUserGroup(targetDB, strTarget, psth, ugObj);
			}
		}
		catch(SQLException ex)
		{
			Log.log(Log.FATAL, "Sql-Query failed: " + query, ex);
			throw ex;
		}
		finally
		{
			close(st,rs);

			try
			{
				if (psth.pst != null)
				{
					psth.bMode = PreparedStatementHolder.MODE_CLOSE;
					preparedInsertUserGroup(null, strTarget, psth, null);
				}
			}
			catch (SQLException ex) 
			{
				Log.log(Log.WARN, ex);
			}
		}
	}
	
	private static void preparedInsertUserGroup(
							Connection targetDB,
							String strTarget,
							PreparedStatementHolder psth,
							UserGroupTObject ugObj) throws SQLException 
	{
		final String query = "INSERT INTO AMS_UserGroup" + strTarget
			+ " (iUserGroupId,iGroupRef,cUserGroupName,sMinGroupMember,iTimeOutSec,sActive) VALUES (?,?,?,?,?,?)";

		if (psth.bMode == PreparedStatementHolder.MODE_CLOSE)
		{
			try
			{
				psth.pst.close();
			}
			catch (SQLException ex){throw ex;}
			return;
		}
 
		try
		{
			if (psth.bMode == PreparedStatementHolder.MODE_INIT)
			{
				psth.pst = targetDB.prepareStatement(query);
				psth.bMode = PreparedStatementHolder.MODE_EXEC;
			}
	    
			psth.pst.setInt(	1, ugObj.getUserGroupID());
			psth.pst.setInt(	2, ugObj.getGroupRef());
			psth.pst.setString(	3, ugObj.getName());
			psth.pst.setShort(	4, ugObj.getMinGroupMember());
			psth.pst.setInt(	5, ugObj.getTimeOutSec());
		    psth.pst.setInt(    6, ugObj.getIsActive());

			psth.pst.executeUpdate();
		}
		catch(SQLException ex)
		{
			Log.log(Log.FATAL, "Sql-Query failed: " + query, ex);
			throw ex;
		}
	}

	public static void insert(Connection con, UserGroupTObject usergroup) throws SQLException
	{
		final String query = "INSERT INTO AMS_UserGroup (iUserGroupId,iGroupRef,cUserGroupName,"
			+"sMinGroupMember,iTimeOutSec,sActive) VALUES (?,?,?,?,?,?)";
	
		PreparedStatement st = null;

		try
		{
			int newID = getNewID(con, "iUserGroupId", "AMS_UserGroup");
			st = con.prepareStatement(query);
			st.setInt(		1, newID);
			st.setInt(		2, usergroup.getGroupRef());
			st.setString(	3, usergroup.getName());
			st.setShort(	4, usergroup.getMinGroupMember());
			st.setInt(		5, usergroup.getTimeOutSec());
            st.setInt(      6, usergroup.getIsActive());
	
			st.executeUpdate();
			usergroup.setUserGroupID(newID);
			}	
			catch(SQLException ex)
			{
				Log.log(Log.FATAL, "Sql-Query failed: " + query, ex);
				throw ex;
			}
			finally
			{
				close(st,null);
			}
	}

	public static void removeAllBackupFromMasterDB(Connection masterDB) throws SQLException
	{
		remove(masterDB, DB_BACKUP_SUFFIX, -1, true);
	}
	
	public static void removeAll(Connection con) throws SQLException
	{
		remove(con, "", -1, true);
	}

	public static void remove(Connection con, int userGroupID) throws SQLException
	{
		remove(con, "", userGroupID, false);
	}

	private static void remove(Connection con, String strMasterSuffix,
							int userGroupID, boolean isComplete) throws SQLException
	{
		final String query = "DELETE FROM AMS_UserGroup" + strMasterSuffix
				+ (isComplete ? "" : " WHERE iUserGroupId = ?");
		
		PreparedStatement st = null;
		
		try
		{
			st = con.prepareStatement(query);
			if(!isComplete) {
				st.setInt(1, userGroupID);
			}
			st.executeUpdate();
		}
		catch(SQLException ex)
		{
			Log.log(Log.FATAL, ex);
			throw ex;
		}
		finally
		{
			close(st,null);
		}
	}

	public static UserGroupTObject select(Connection con, int userGroupID) throws SQLException
	{
		final String query = "SELECT iUserGroupId,iGroupRef,cUserGroupName,sMinGroupMember,iTimeOutSec,sActive "
			+"FROM AMS_UserGroup WHERE iUserGroupId = ?";
	
		ResultSet rs = null;
		PreparedStatement st = null;
		
		try
		{
			st = con.prepareStatement(query);
			st.setInt(1, userGroupID);
			rs = st.executeQuery();
			
			if(rs.next())
				return new UserGroupTObject(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getShort(4), rs.getInt(5), rs.getInt(6));

			return null;
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
	
	public static List<UserGroupKey> selectKeyList(Connection con) throws SQLException
	{
		final String query = "SELECT iUserGroupId,cUserGroupName,iGroupRef FROM AMS_UserGroup ORDER BY 2";

		ResultSet rs = null;
		PreparedStatement st = null;
		ArrayList<UserGroupKey> array = new ArrayList<UserGroupKey>();
		
		try
		{
			st = con.prepareStatement(query);
			rs = st.executeQuery();
			
			while(rs.next())
				array.add(new UserGroupKey(rs.getInt(1), rs.getString(2),rs.getInt(3)));
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
	
	public static void update(Connection con, UserGroupTObject userGroup) throws SQLException
	{
		final String query = "UPDATE AMS_UserGroup SET iGroupRef=?,cUserGroupName=?,sMinGroupMember=?,"
			+"iTimeOutSec=?,sActive=? WHERE iUserGroupId = ?";
		
		PreparedStatement st = null;
			
		try
		{
			st = con.prepareStatement(query);
			st.setInt(		1, userGroup.getGroupRef());
			st.setString(	2, userGroup.getName());
			st.setShort(	3, userGroup.getMinGroupMember());
			st.setInt(		4, userGroup.getTimeOutSec());			
            st.setInt(      5, userGroup.getIsActive());

			st.setInt(		6, userGroup.getUserGroupID());
			
			st.executeUpdate();
		}	
		catch(SQLException ex)
		{
			Log.log(Log.FATAL, "Sql-Query failed: " + query, ex);
			throw ex;
		}
		finally
		{
			close(st,null);
		}
	}
	
    public static boolean update2(Connection con, UserGroupTObject userGroup) throws SQLException
    {
        boolean result = false;
        
        final String query = "UPDATE AMS_UserGroup SET iGroupRef=?,cUserGroupName=?,sMinGroupMember=?,"
                +"iTimeOutSec=?,sActive=? WHERE iUserGroupId = ?";
    
        PreparedStatement st = null;
            
        try
        {
            st = con.prepareStatement(query);
            st.setInt(      1, userGroup.getGroupRef());
            st.setString(   2, userGroup.getName());
            st.setShort(    3, userGroup.getMinGroupMember());
            st.setInt(      4, userGroup.getTimeOutSec());          
            st.setInt(      5, userGroup.getIsActive());
        
            st.setInt(      6, userGroup.getUserGroupID());
            
            st.executeUpdate();
            
            result = true;
        }   
        catch(SQLException ex)
        {
            Log.log(Log.FATAL, "Sql-Query failed: " + query, ex);
                throw ex;
        }
        finally
        {
            close(st,null);
        }
        
        return result;
    }
}