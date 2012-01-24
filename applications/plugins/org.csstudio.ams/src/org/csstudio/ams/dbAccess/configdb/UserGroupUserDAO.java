
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
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.csstudio.ams.Log;
import org.csstudio.ams.dbAccess.DAO;
import org.csstudio.ams.dbAccess.PreparedStatementHolder;

public abstract class UserGroupUserDAO extends DAO 
{    
    /*
        iUserGroupRef   INT NOT NULL,
    iUserRef        INT NOT NULL,
    iPos            INT NOT NULL,
    sActive         SMALLINT,
    cActiveReason   VARCHAR(128),
    tTimeChange     BIGINT,
 
     */
    
	public static void copyUserGroupUser(Connection masterDB, Connection localDB) throws SQLException 
	{
		copyUserGroupUser(masterDB, localDB, DB_BACKUP_SUFFIX);
	}
	
	public static void copyUserGroupUser(Connection masterDB, Connection localDB, String masterDbSuffix) throws SQLException 
	{
		copyUserGroupUser(masterDB, localDB, masterDbSuffix, "");
	}
	
	public static void backupUserGroupUser(Connection masterDB) throws SQLException
	{
		copyUserGroupUser(masterDB, masterDB, "", DB_BACKUP_SUFFIX);
	}
	
	private static void copyUserGroupUser(Connection masterDB, Connection targetDB,
							String strMaster, String strTarget) throws SQLException
	{
		final String query = "SELECT iUserGroupRef,iUserRef,iPos,sActive,cActiveReason,tTimeChange"
			+ " FROM AMS_UserGroup_User" + strMaster;
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
				UserGroupUserTObject uguObj = new UserGroupUserTObject(
						rs.getInt(1), 
						rs.getInt(2), 
						rs.getInt(3), 
						rs.getShort(4), 
						rs.getString(5),
						getUtilDate(rs, 6));

				preparedInsertUserGroupUser(targetDB, strTarget, psth, uguObj);
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
					preparedInsertUserGroupUser(null, strTarget, psth, null);
				}
			}
			catch (SQLException ex) 
			{
				Log.log(Log.WARN, ex);
			}
		}
	}
	
	private static void preparedInsertUserGroupUser(
							Connection targetDB,
							String strTarget,
							PreparedStatementHolder psth,
							UserGroupUserTObject uguObj) throws SQLException 
	{
		final String query = "INSERT INTO AMS_UserGroup_User" + strTarget
			+ " (iUserGroupRef,iUserRef,iPos,sActive,cActiveReason,tTimeChange)"
			+ " VALUES (?,?,?,?,?,?)";

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
	    
			psth.pst.setInt(		1, uguObj.getUserGroupRef());
			psth.pst.setInt(		2, uguObj.getUserRef());
			psth.pst.setInt(		3, uguObj.getPos());
			psth.pst.setShort(		4, uguObj.getActive());
			psth.pst.setString(		5, uguObj.getActiveReason());
			setUtilDate(psth.pst, 	6, uguObj.getTimeChange());
			
			psth.pst.executeUpdate();
		}
		catch(SQLException ex)
		{
			Log.log(Log.FATAL, "Sql-Query failed: " + query, ex);
			throw ex;
		}
	}

	public static void insertList(Connection con, 
			List<AggrUserGroupUserTObject> array) throws SQLException
	{
		final String query = "INSERT INTO AMS_UserGroup_User"
			+ " (iUserGroupRef,iUserRef,iPos,sActive,cActiveReason,tTimeChange)"
			+ " VALUES (?,?,?,?,?,?)";
	
		PreparedStatement pst = null;

		try
		{
			pst = con.prepareStatement(query);

			for (int i = 0 ; i < array.size() ; i++)
			{
				Date sysDate = new Date();
				
				pst.setInt(			1, array.get(i).getUserGroupUser().getUserGroupRef());
				pst.setInt(			2, array.get(i).getUserGroupUser().getUserRef());
				pst.setInt(			3, array.get(i).getUserGroupUser().getPos());
				pst.setShort(		4, array.get(i).getUserGroupUser().getActive());
				pst.setString(		5, array.get(i).getUserGroupUser().getActiveReason());
				setUtilDate(pst, 	6, sysDate);
				
				if (pst.executeUpdate() > 0)
					array.get(i).getUserGroupUser().setTimeChange(sysDate);		// set only if insert o.k.
			}
		}
		catch(SQLException ex)
		{
			Log.log(Log.FATAL, "Sql-Query failed: " + query, ex);
			throw ex;
		}
		finally
		{
			close(pst,null);
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

	public static void remove(Connection con, int userGroupRef) throws SQLException
	{
		remove(con, "", userGroupRef, false);
	}
	
	private static void remove(Connection con, String strMasterSuffix, 
			int userGroupRef, boolean isComplete) throws SQLException
	{
		final String query = "DELETE FROM AMS_UserGroup_User" + strMasterSuffix
				+ (isComplete ? "" : " WHERE iUserGroupRef = ?");
		
		PreparedStatement st = null;
		
		try
		{
			st = con.prepareStatement(query);
			if(!isComplete) {
				st.setInt(1, userGroupRef);
			}
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

	public static UserGroupUserTObject select(Connection con, 
			int userGroupRef, int userRef) throws SQLException
	{
		final String query = "SELECT iUserGroupRef,iUserRef,iPos,sActive,cActiveReason,tTimeChange"
			+ " FROM AMS_UserGroup_User WHERE iUserGroupRef = ? and iUserRef = ?";
	
		ResultSet rs = null;
		PreparedStatement st = null;
		
		try
		{
			st = con.prepareStatement(query);
			st.setInt(1, userGroupRef);
			st.setInt(2, userRef);
			rs = st.executeQuery();
			
			if(rs.next())
				return new UserGroupUserTObject(rs.getInt(1)
						, rs.getInt(2)
						, rs.getInt(3)
						, rs.getShort(4)
						, rs.getString(5)
						, getUtilDate(rs, 6));

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
	
	public static UserGroupUserTObject selectByPos(Connection con, 
			int userGroupRef, int pos) throws SQLException
	{
		final String query = "SELECT iUserGroupRef,iUserRef,iPos,sActive,cActiveReason,tTimeChange"
			+ " FROM AMS_UserGroup_User WHERE iUserGroupRef = ? and iPos = ?";
	
		ResultSet rs = null;
		PreparedStatement st = null;
		
		try
		{
			st = con.prepareStatement(query);
			st.setInt(1, userGroupRef);
			st.setInt(2, pos);
			rs = st.executeQuery();
			
			if(rs.next())
				return new UserGroupUserTObject(rs.getInt(1)
						, rs.getInt(2)
						, rs.getInt(3)
						, rs.getShort(4)
						, rs.getString(5)
						, getUtilDate(rs, 6));

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
	
	public static List<AggrUserGroupUserTObject> selectList(Connection con, 
			int userGroupRef) throws SQLException
	{
		final String query = "SELECT iUserGroupRef,iUserRef,iPos,sActive,cActiveReason,tTimeChange"
			+ " FROM AMS_UserGroup_User WHERE iUserGroupRef = ? ORDER BY iPos";
		
		ResultSet rs = null;
		PreparedStatement st = null;
		ArrayList<AggrUserGroupUserTObject> array = new ArrayList<AggrUserGroupUserTObject>();
		
		try
		{
			st = con.prepareStatement(query);
			st.setInt(1, userGroupRef);
			rs = st.executeQuery();
			
			while(rs.next()) 
			{
				array.add(new AggrUserGroupUserTObject(
						new UserGroupUserTObject(rs.getInt(1)
								, rs.getInt(2)
								, rs.getInt(3)
								, rs.getShort(4)
								, rs.getString(5)
								, getUtilDate(rs, 6)), null));
			}
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
	
	/**
	 * Used by <code>Distributor.changeStatus()</code> only.
	 * Updates <code>sActive,cActiveReason,tTimeChange</code>
	 * by checking if <code>tTimeChange</code> has not changed.
	 * 
	 * @param con
	 * @param ugu
	 * @return
	 * @throws SQLException
	 * @see #updateInTransaction(Connection, int, List)
	 */
	public static boolean update(Connection con, UserGroupUserTObject ugu) throws SQLException
	{
		final String query = "UPDATE AMS_UserGroup_User SET sActive=?,cActiveReason=?,tTimeChange=?"
			+ " WHERE iUserGroupRef = ? AND iUserRef = ? AND tTimeChange = ?";

		PreparedStatement st = null;
		
		try
		{
			Date sysDate = new Date();
			
			st = con.prepareStatement(query);
			st.setShort(	1, ugu.getActive());
			st.setString(	2, ugu.getActiveReason());
			setUtilDate(st, 3, sysDate);
			
			st.setInt(		4, ugu.getUserGroupRef());
			st.setInt(		5, ugu.getUserRef());
			setUtilDate(st, 6, ugu.getTimeChange());
			
			int ret = st.executeUpdate();
			if (ret > 0)
				ugu.setTimeChange(sysDate);										// set only if update o.k.
			
			return ret > 0;
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

	/**
	 * Used by Configurator (<code>AggrUserGroupDAO.update</code>) only.
	 * Removes <code>UserGroup</code> specified by <code>userGroupRef</code>
	 * from Table, by checking if <code>tTimeChange</code> has not changed,
	 * then insert new.
	 * 
	 * @param con
	 * @param userGroupRef
	 * @param listAggrUGU
	 * @return
	 * @throws SQLException
	 * @see #update(Connection, UserGroupUserTObject)
	 */
	public static boolean updateInTransaction(Connection con, int userGroupRef, 
			List<AggrUserGroupUserTObject> listAggrUGU) throws SQLException
	{
		final String query = "DELETE FROM AMS_UserGroup_User WHERE"
			+ " iUserGroupRef = ? AND iUserRef = ? AND tTimeChange = ?";

		PreparedStatement st = null;
		boolean err = false;
		
		try
		{
			con.setAutoCommit(false);
			st = con.prepareStatement(query);

			Iterator<?> iter = listAggrUGU.iterator();
			while (iter.hasNext())
			{
				AggrUserGroupUserTObject augu = (AggrUserGroupUserTObject)iter.next();

				st.setInt(1, userGroupRef);
				st.setInt(2, augu.getUserGroupUser().getUserRef());
				setUtilDate(st, 3, augu.getUserGroupUser().getTimeChange());
				
				if (st.executeUpdate() < 1)										// if no rows updated
				{
					if (select(con, userGroupRef, augu.getUserGroupUser().getUserRef()) == null)
						continue;												// if Configurator add a new User to UserGroup

					err = true;
					break;
				}
			}

			if (err)
			{
				con.rollback();
				Log.log(Log.INFO, "AMS_UserGroup_User data has changed - reload data.");
				return false;
			}
			
			remove(con, userGroupRef);											// remove all other (as example: in Configurator deleted UserGroup Users) 
			
			for (int i = 0 ; listAggrUGU != null && i < listAggrUGU.size() ; i++)
				listAggrUGU.get(i).getUserGroupUser().setPos(i+1);
		
			insertList(con, listAggrUGU);	
			
			con.commit();
			return true;
		}
		catch(SQLException ex)
		{
			try{
				con.rollback();
			}catch(Exception e)
			{
				Log.log(Log.WARN, "remove/insert UserGroup=" + userGroupRef
						+ " from UserGroup_User rollback failed.", e);
			}
			
			Log.log(Log.FATAL, "Sql-Query failed: " + query, ex);
			throw ex;
		}
		finally
		{
			try{
				con.setAutoCommit(true);
			}catch(Exception e){}

			close(st,null);
		}
	}
	
    public static Vector<UserTObject> selectByGroupAndState(Connection con, int groupRef, int active) throws SQLException
    {
        final String query = "SELECT iUserRef"
        + " FROM AMS_UserGroup_User WHERE iUserGroupRef = ? AND sActive = ?";

        Vector<Integer> userRefList = new Vector<Integer>();
        Vector<UserTObject> userList = new Vector<UserTObject>();
        
        ResultSet rs = null;
        PreparedStatement st = null;

        try
        {
            st = con.prepareStatement(query);
            st.setInt(1, groupRef);
            st.setShort(2, (short)active);
            rs = st.executeQuery();
            
            while(rs.next())
            {
                userRefList.add(new Integer(rs.getInt(1)));
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
        }
        
        if(!userRefList.isEmpty())
        {
            UserTObject u = null;
            
            for(int i = 0;i < userRefList.size();i++)
            {
                u = UserDAO.select(con, userRefList.get(i).intValue());
                if(u != null)
                {
                    userList.add(u);
                }
            }
        }
        
        userRefList.clear();
        userRefList = null;
        
        return userList;
    }
}
