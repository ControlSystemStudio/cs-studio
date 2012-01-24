
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
import org.csstudio.ams.dbAccess.DAO;
import org.csstudio.ams.dbAccess.PreparedStatementHolder;

public abstract class UserDAO extends DAO 
{
	private static final String SELECT = "SELECT iUserId,iGroupRef,cUserName,cEmail,cMobilePhone,"
		+"cPhone,cStatusCode,cConfirmCode,sActive,sPreferredAlarmingTypeRR FROM AMS_User";

	public static void copyUser(Connection masterDB, Connection localDB) throws SQLException 
	{
		copyUser(masterDB, localDB, DB_BACKUP_SUFFIX);
	}
	
	public static void copyUser(Connection masterDB, Connection localDB, String masterDbSuffix) throws SQLException 
	{
		copyUser(masterDB, localDB, masterDbSuffix, "");
	}
	
	public static void backupUser(Connection masterDB) throws SQLException
	{
		copyUser(masterDB, masterDB, "", DB_BACKUP_SUFFIX);
	}

	private static void copyUser(Connection masterDB, Connection targetDB,
							String strMaster, String strTarget) throws SQLException
	{
		ResultSet rs = null;
		PreparedStatement st = null;
		PreparedStatementHolder psth = null;
		
		try
		{
			psth = new PreparedStatementHolder();			
			st = masterDB.prepareStatement(SELECT + strMaster);
			rs = st.executeQuery();
			
			while(rs.next())
			{
				UserTObject uObj = new UserTObject(
						rs.getInt(1), 
						rs.getInt(2), 
						rs.getString(3), 
						rs.getString(4), 
						rs.getString(5), 
						rs.getString(6), 
						rs.getString(7), 
						rs.getString(8), 
						rs.getShort(9), 
						rs.getShort(10));
				preparedInsertUser(targetDB, strTarget, psth, uObj);
			}
		}
		catch(SQLException ex)
		{
			Log.log(Log.FATAL, "Sql-Query failed: " + SELECT, ex);
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
					preparedInsertUser(null, strTarget, psth, null);
				}
			}
			catch (SQLException ex) 
			{
				Log.log(Log.WARN, ex);
			}
		}
	}
	
	private static void preparedInsertUser(
							Connection targetDB, 
							String strTarget,
							PreparedStatementHolder psth,
							UserTObject uObj) throws SQLException 
	{
		final String query = "INSERT INTO AMS_User" + strTarget
			+ " (iUserId,iGroupRef,cUserName,cEmail,cMobilePhone,cPhone,cStatusCode,cConfirmCode,"
			+ "sActive,sPreferredAlarmingTypeRR) VALUES (?,?,?,?,?,?,?,?,?,?)";

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
	    
			psth.pst.setInt(	1, uObj.getUserID());
			psth.pst.setInt(	2, uObj.getGroupRef());
			psth.pst.setString(	3, uObj.getName());
			psth.pst.setString(	4, uObj.getEmail());
			psth.pst.setString(	5, uObj.getMobilePhone());
			psth.pst.setString(	6, uObj.getPhone());
			psth.pst.setString(	7, uObj.getStatusCode());
			psth.pst.setString(	8, uObj.getConfirmCode());
			psth.pst.setShort(	9, uObj.getActive());
			psth.pst.setShort(	10, uObj.getPrefAlarmingTypeRR());
			
			psth.pst.executeUpdate();
		}
		catch(SQLException ex)
		{
			Log.log(Log.FATAL, "Sql-Query failed: " + query, ex);
			throw ex;
		}
	}

	public static void insert(Connection con, UserTObject user) throws SQLException
	{
		final String query = "INSERT INTO AMS_User (iUserId,iGroupRef,cUserName,cEmail,cMobilePhone,"
			+"cPhone,cStatusCode,cConfirmCode,sActive,sPreferredAlarmingTypeRR) "
			+"VALUES (?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement st = null;

		try
		{
			st = con.prepareStatement(query);
			int userID = getNewID(con, "iUserId", "AMS_User");

			st.setInt(		1, userID);
			st.setInt(		2, user.getGroupRef());
			st.setString(	3, user.getName());
			st.setString(	4, user.getEmail());
			st.setString(	5, user.getMobilePhone());
			st.setString(	6, user.getPhone());
			st.setString(	7, user.getStatusCode());
			st.setString(	8, user.getConfirmCode());
			st.setShort(	9, user.getActive());
			st.setShort(	10, user.getPrefAlarmingTypeRR());

			st.executeUpdate();
			user.setUserID(userID);
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

	public static void remove(Connection con, int userID) throws SQLException
	{
		remove(con, "", userID, false);
	}

	private static void remove(Connection con, String strMasterSuffix,
							int userID, boolean isComplete) throws SQLException
	{
		final String query = "DELETE FROM AMS_User" + strMasterSuffix
				+ (isComplete ? "" : " WHERE iUserID = ?");
		
		PreparedStatement st = null;
		
		try
		{
			st = con.prepareStatement(query);
			if(!isComplete) {
				st.setInt(1, userID);
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

	public static List<UserKey> selectKeyList(Connection con) throws SQLException
	{
		final String query = "SELECT iUserId,cUserName,iGroupRef FROM AMS_User ORDER BY 2";
		
		ResultSet rs = null;
		PreparedStatement st = null;
		ArrayList<UserKey> array = new ArrayList<UserKey>();
		
		try
		{
			st = con.prepareStatement(query);
			rs = st.executeQuery();
			
			while(rs.next())
				array.add(new UserKey(rs.getInt(1), rs.getString(2), rs.getInt(3)));
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
	
	public static void update(Connection con, UserTObject user) throws SQLException
	{
		final String query = "UPDATE AMS_User SET cUserName=?,iGroupRef=?,cEmail=?,cMobilePhone=?,"
			+"cPhone=?,cStatusCode=?,cConfirmCode=?,sActive=?,sPreferredAlarmingTypeRR=? "
			+"WHERE iUserId = ?";
		
		PreparedStatement st = null;
			
		try
		{
			st = con.prepareStatement(query);
			st.setString(	1, user.getName());
			st.setInt(		2, user.getGroupRef());
			st.setString(	3, user.getEmail());
			st.setString(	4, user.getMobilePhone());
			st.setString(	5, user.getPhone());
			st.setString(	6, user.getStatusCode());
			st.setString(	7, user.getConfirmCode());
			st.setShort(	8, user.getActive());
			st.setShort(	9, user.getPrefAlarmingTypeRR());

			st.setInt(		10, user.getUserID());

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

	public static UserTObject select(Connection con, int userID) throws SQLException
	{
		final String query = SELECT + " WHERE iUserId = ?";
		
		ResultSet rs = null;
		PreparedStatement st = null;
		
		try
		{
			st = con.prepareStatement(query);
			st.setInt(1, userID);
			rs = st.executeQuery();
			
			if(rs.next()) 
				return new UserTObject(rs.getInt(1), rs.getInt(2), rs.getString(3), 
						rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), 
						rs.getString(8), rs.getShort(9), rs.getShort(10));
			
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
	
	public static void select(Connection con, List<AggrUserGroupUserTObject> users) throws SQLException
	{
		final String query = SELECT + " WHERE iUserId = ?";
		
		ResultSet rs = null;
		PreparedStatement st = null;
		
		try
		{
			st = con.prepareStatement(query);

			Iterator<?> iter = users.iterator();
			while (iter.hasNext())
			{
				AggrUserGroupUserTObject user = (AggrUserGroupUserTObject)iter.next();
				st.setInt(1, user.getUserGroupUser().getUserRef());
				
				rs = st.executeQuery();
			
				if(rs.next()) 
					user.setUser(new UserTObject(rs.getInt(1), rs.getInt(2), rs.getString(3), 
							rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), 
							rs.getString(8), rs.getShort(9), rs.getShort(10)));
			
				close(null,rs);
				rs = null;
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
	}
	
	public static UserTObject select(Connection con, String mobilePhone) throws SQLException
	{
	    final String query = SELECT + " WHERE cMobilePhone = ?";

	    ResultSet rs = null;
	    PreparedStatement st = null;

	    try
	    {
	        st = con.prepareStatement(query);
	        st.setString(1, mobilePhone);
	        rs = st.executeQuery();
    
	        if(rs.next()) 
	            return new UserTObject(rs.getInt(1), rs.getInt(2), rs.getString(3), 
                rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), 
                rs.getString(8), rs.getShort(9), rs.getShort(10));
    
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
}