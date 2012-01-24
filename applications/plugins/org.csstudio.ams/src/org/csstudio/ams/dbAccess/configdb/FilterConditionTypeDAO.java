
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
import org.csstudio.ams.dbAccess.PreparedStatementHolder;
import org.csstudio.ams.dbAccess.DAO;

public abstract class FilterConditionTypeDAO extends DAO 
{
	public static void copyFilterConditionType(Connection masterDB, Connection localDB) throws SQLException 
	{
		copyFilterConditionType(masterDB, localDB, DB_BACKUP_SUFFIX);
	}
	
	public static void backupFilterConditionType(Connection masterDB) throws SQLException
	{
		copyFilterConditionType(masterDB, masterDB, "", DB_BACKUP_SUFFIX);
	}
	
	public static void copyFilterConditionType(Connection masterDB, Connection localDB, String masterDbSuffix) throws SQLException 
	{
		copyFilterConditionType(masterDB, localDB, masterDbSuffix, "");		
	}
	
	private static void copyFilterConditionType(Connection masterDB, Connection targetDB, 
							String strMaster, String strTarget) throws SQLException
	{
		final String query = "SELECT iFilterConditionTypeID,cName,cClass,cClassUI FROM AMS_FilterConditionType" + strMaster;
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
				FilterConditionTypeTObject fctObj = new FilterConditionTypeTObject(
						rs.getInt(1), 
						rs.getString(2), 
						rs.getString(3), 
						rs.getString(4));
				preparedInsertFilterConditionType(targetDB, strTarget, psth, fctObj);
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
					preparedInsertFilterConditionType(null, strTarget, psth, null);
				}
			}
			catch (SQLException ex) 
			{
				Log.log(Log.WARN, ex);
			}
		}
	}
	
	private static void preparedInsertFilterConditionType(
							Connection targetDB,
							String strTarget,
							PreparedStatementHolder psth,
							FilterConditionTypeTObject fctObj) throws SQLException 
	{
		final String query = "INSERT INTO AMS_FilterConditionType" + strTarget
			+ " (iFilterConditionTypeID,cName,cClass,cClassUI) VALUES (?,?,?,?)";

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
	    
			psth.pst.setInt(	1, fctObj.getFilterConditionTypeID());
			psth.pst.setString(	2, fctObj.getName());
			psth.pst.setString(	3, fctObj.getClassName());
			psth.pst.setString(	4, fctObj.getClassNameUI());
			
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

	public static void remove(Connection con, int filterConditionTypeID) throws SQLException
	{
		remove(con, "", filterConditionTypeID, false);
	}

	private static void remove(Connection con, String strMasterSuffix,
							int filterConditionTypeID, boolean isComplete) throws SQLException
	{
		final String query = "DELETE FROM AMS_FilterConditionType" + strMasterSuffix
				+ (isComplete ? "" : " WHERE iFilterConditionTypeID = ?");
		PreparedStatement st = null;
		
		try
		{
			st = con.prepareStatement(query);
			if(!isComplete) {
				st.setInt(1, filterConditionTypeID);
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
	
	public static List<FilterConditionTypeTObject> selectAll(Connection con) throws SQLException 
	{
		final String query = "SELECT iFilterConditionTypeID,cName,cClass,cClassUI FROM AMS_FilterConditionType";
		ResultSet rs = null;
		PreparedStatement st = null;
		ArrayList<FilterConditionTypeTObject> array = new ArrayList<FilterConditionTypeTObject>();
		
		try
		{
			st = con.prepareStatement(query);
			rs = st.executeQuery();
			
			while(rs.next())
			{
				array.add(new FilterConditionTypeTObject(
						rs.getInt(1), 
						rs.getString(2), 
						rs.getString(3), 
						rs.getString(4)));
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

	public static FilterConditionTypeTObject select(Connection con, int fctID) throws SQLException 
	{
		final String query = "SELECT cName,cClass,cClassUI FROM AMS_FilterConditionType WHERE iFilterConditionTypeID = ?";
		ResultSet rs = null;
		PreparedStatement st = null;
		
		try
		{
			st = con.prepareStatement(query);
			st.setInt(1, fctID);
			rs = st.executeQuery();
			
			if(rs.next())
				return new FilterConditionTypeTObject(fctID, rs.getString(1), rs.getString(2), rs.getString(3));

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
