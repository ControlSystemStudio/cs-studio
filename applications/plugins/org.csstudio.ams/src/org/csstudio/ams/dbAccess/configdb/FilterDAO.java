
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
import org.csstudio.ams.Log;
import org.csstudio.ams.dbAccess.DAO;
import org.csstudio.ams.dbAccess.PreparedStatementHolder;

public abstract class FilterDAO extends DAO 
{
	public static void copyFilter(Connection masterDB, Connection localDB) throws SQLException 
	{
		copyFilter(masterDB, localDB, DB_BACKUP_SUFFIX);
	}
	
	public static void copyFilter(Connection masterDB, Connection localDB, String masterDbSuffix) throws SQLException 
	{
		copyFilter(masterDB, localDB, masterDbSuffix, "");
	}
	
	public static void backupFilter(Connection masterDB) throws SQLException
	{
		copyFilter(masterDB, masterDB, "", DB_BACKUP_SUFFIX);
	}

	private static void copyFilter(Connection masterDB, Connection targetDB,
							String strMaster, String strTarget) throws SQLException
	{
		final String query = "SELECT iFilterID,iGroupRef,cName,cDefaultMessage FROM AMS_Filter" + strMaster;
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
				FilterTObject fObj = new FilterTObject(
						rs.getInt(1), 
						rs.getInt(2), 
						rs.getString(3), 
						rs.getString(4));
				preparedInsertFilter(targetDB, strTarget, psth, fObj);
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
					preparedInsertFilter(null, strTarget, psth, null);
				}
			}
			catch (SQLException ex) 
			{
				Log.log(Log.WARN, ex);
			}
		}
	}
	
	private static void preparedInsertFilter(
							Connection targetDB,
							String strTarget,
							PreparedStatementHolder psth,
							FilterTObject fObj) throws SQLException 
	{
		final String query = "INSERT INTO AMS_Filter" + strTarget
			+ " (iFilterID,iGroupRef,cName,cDefaultMessage) VALUES(?,?,?,?)";

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
	    
			psth.pst.setInt(	1, fObj.getFilterID());
			psth.pst.setInt(	2, fObj.getGroupRef());
			psth.pst.setString(	3, fObj.getName());
			psth.pst.setString(	4, fObj.getDefaultMessage());
			
			psth.pst.executeUpdate();
		}
		catch(SQLException ex)
		{
			Log.log(Log.FATAL, "Sql-Query failed: " + query, ex);
			throw ex;
		}
	}

	public static FilterTObject select(Connection con, int filterID) throws SQLException
	{
		final String query = "SELECT iFilterID, iGroupRef, cName, cDefaultMessage FROM AMS_Filter WHERE iFilterID = ?";
	
		ResultSet rs = null;
		PreparedStatement st = null;
		FilterTObject filter = null;
		
		try
		{
			st = con.prepareStatement(query);
			st.setInt(1, filterID);
			rs = st.executeQuery();
			
			if(rs.next())
				filter = new FilterTObject(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4));
			
			return filter;
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
	
	public static void insert(Connection con, FilterTObject filter) throws SQLException
	{
		final String query = "INSERT INTO AMS_Filter (iFilterID,iGroupRef,cName,cDefaultMessage) VALUES(?,?,?,?)";
	
		PreparedStatement st = null;
	
		try
		{
			int newID = getNewID(con, "iFilterID", "AMS_Filter");
			st = con.prepareStatement(query);
			st.setInt(		1, newID);
			st.setInt(		2, filter.getGroupRef());
			st.setString(	3, filter.getName());
			st.setString(	4, filter.getDefaultMessage());
			
			st.executeUpdate();
			filter.setFilterID(newID);
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

	public static void remove(Connection con, int filterID) throws SQLException
	{
		remove(con, "", filterID, false);
	}

	private static void remove(Connection con, String strMasterSuffix,
							int filterID, boolean isComplete) throws SQLException
	{
		final String query = "DELETE FROM AMS_Filter" + strMasterSuffix
				+ (isComplete ? "" : " WHERE iFilterID = ?");
		PreparedStatement st = null;
		
		try
		{
			st = con.prepareStatement(query);
			if(!isComplete) {
				st.setInt(1, filterID);
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

	public static ArrayList<?> selectKeyList(Connection con) throws SQLException
	{
		final String query = "SELECT iFilterID,cName,iGroupRef FROM AMS_Filter";
	
		ResultSet rs = null;
		PreparedStatement st = null;
		ArrayList<FilterKey> array = new ArrayList<FilterKey>();
		
		try
		{
			st = con.prepareStatement(query);
			rs = st.executeQuery();
			
			while(rs.next())
				array.add(new FilterKey(rs.getInt(1),rs.getString(2),rs.getInt(3)));
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
	
	public static void update(Connection con, FilterTObject filter) throws SQLException
	{
		final String query = "UPDATE AMS_Filter SET iGroupRef=?,cName=?,cDefaultMessage=? WHERE iFilterID = ?";
	
		PreparedStatement st = null;
			
		try
		{
			st = con.prepareStatement(query);
			st.setInt(1, 	filter.getGroupRef());
			st.setString(2, filter.getName());
			st.setString(3, filter.getDefaultMessage());

			st.setInt(4, 	filter.getFilterID());
	
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
}
