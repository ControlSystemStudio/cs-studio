
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

public class FilterActionTypeDAO extends DAO
{
	public static void copyFilterActionType(Connection masterDB, Connection localDB) throws SQLException 
	{
		copyFilterActionType(masterDB, localDB, DB_BACKUP_SUFFIX);
	}
	
	public static void copyFilterActionType(Connection masterDB, Connection localDB, String masterDbSuffix) throws SQLException 
	{
		copyFilterActionType(masterDB, localDB, masterDbSuffix, "");
	}
	
	public static void backupFilterActionType(Connection masterDB) throws SQLException
	{
		copyFilterActionType(masterDB, masterDB, "", DB_BACKUP_SUFFIX);
	}
	
	private static void copyFilterActionType(Connection masterDB, Connection targetDB,
							String strMaster, String strTarget) throws SQLException
	{
		final String query = "SELECT iFilterActionTypeID,cName,iTopicRef FROM AMS_FilterActionType" + strMaster;
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
				FilterActionTypeTObject fatObj = new FilterActionTypeTObject(
						rs.getInt(1), 
						rs.getString(2), 
						rs.getInt(3));
				preparedInsertFilterActionType(targetDB, strTarget, psth, fatObj);
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
					preparedInsertFilterActionType(null, strTarget, psth, null);
				}
			}
			catch (SQLException ex) 
			{
				Log.log(Log.WARN, ex);
			}
		}
	}
	
	private static void preparedInsertFilterActionType(
							Connection targetDB,
							String strTarget,
							PreparedStatementHolder psth,
							FilterActionTypeTObject fatObj) throws SQLException 
	{
		final String query = "INSERT INTO AMS_FilterActionType" + strTarget 
			+ " (iFilterActionTypeID,cName,iTopicRef) VALUES (?,?,?)";

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
	    
			psth.pst.setInt(	1, fatObj.getFilterActionTypeID());
			psth.pst.setString(	2, fatObj.getName());
			psth.pst.setInt(	3, fatObj.getTopicRef());
			
			psth.pst.executeUpdate();
		}
		catch(SQLException ex)
		{
			Log.log(Log.FATAL, "Sql-Query failed: " + query, ex);
			throw ex;
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

	public static void remove(Connection con, int filterActionTypeID) throws SQLException
	{
		remove(con, "", filterActionTypeID, false);
	}
	
	private static void remove(Connection con, String strMasterSuffix,
							int filterActionTypeID, boolean isComplete) throws SQLException
	{
		final String query = "DELETE FROM AMS_FilterActionType" + strMasterSuffix
				+ (isComplete ? "" : " WHERE iFilterActionTypeID = ?");
		PreparedStatement st = null;
		
		try
		{
			st = con.prepareStatement(query);
			if(!isComplete) {
				st.setInt(1, filterActionTypeID);
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
	
	public static FilterActionTypeTObject select(Connection con, int faTypeID) throws SQLException
	{
		final String query = "SELECT iFilterActionTypeID, cName, iTopicRef FROM AMS_FilterActionType WHERE iFilterActionTypeID = ?";
		
		ResultSet rs = null;
		PreparedStatement st = null;
		FilterActionTypeTObject faTypeObj = null;

		try
		{
			st = con.prepareStatement(query);
			st.setInt(1, faTypeID);
			rs = st.executeQuery();
			
			if(rs.next())
				faTypeObj = new FilterActionTypeTObject(rs.getInt(1), rs.getString(2), rs.getInt(3));
			
			return faTypeObj;
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
	
	public static List<FilterActionTypeKey> selectKeyList(Connection con) throws SQLException 
	{
		final String query = "SELECT iFilterActionTypeID,cName FROM AMS_FilterActionType";
		
		ResultSet rs = null;
		PreparedStatement st = null;
		ArrayList<FilterActionTypeKey> array = new ArrayList<FilterActionTypeKey>();
		
		try
		{
			st = con.prepareStatement(query);
			rs = st.executeQuery();
			
			while(rs.next())
				array.add(new FilterActionTypeKey(rs.getInt(1), rs.getString(2)));
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

