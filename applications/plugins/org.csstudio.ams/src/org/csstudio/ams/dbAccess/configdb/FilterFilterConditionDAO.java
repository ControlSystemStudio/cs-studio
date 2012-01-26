
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
import java.util.Iterator;
import java.util.List;
import org.csstudio.ams.Log;
import org.csstudio.ams.dbAccess.DAO;
import org.csstudio.ams.dbAccess.PreparedStatementHolder;

public abstract class FilterFilterConditionDAO extends DAO 
{
	public static void copyFilterFilterCondition(Connection masterDB, Connection localDB) throws SQLException 
	{
		copyFilterFilterCondition(masterDB, localDB, DB_BACKUP_SUFFIX);
	}
	
	public static void copyFilterFilterCondition(Connection masterDB, Connection localDB, String masterDbSuffix) throws SQLException 
	{
		copyFilterFilterCondition(masterDB, localDB, masterDbSuffix, "");
	}
	
	public static void backupFilterFilterCondition(Connection masterDB) throws SQLException
	{
		copyFilterFilterCondition(masterDB, masterDB, "", DB_BACKUP_SUFFIX);
	}

	private static void copyFilterFilterCondition(Connection masterDB, Connection targetDB,
							String strMaster, String strTarget) throws SQLException
	{
		final String query = "SELECT iFilterRef,iFilterConditionRef,iPos FROM AMS_Filter_FilterCondition" + strMaster;
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
				FilterFilterConditionTObject ffcObj = new FilterFilterConditionTObject(
						rs.getInt(1), 
						rs.getInt(2), 
						rs.getInt(3));
				preparedInsertFilterFilterCondition(targetDB, strTarget, psth, ffcObj);
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
					preparedInsertFilterFilterCondition(null, strTarget, psth, null);
				}
			}
			catch (SQLException ex) 
			{
				Log.log(Log.WARN, ex);
			}
		}
	}
	
	private static void preparedInsertFilterFilterCondition(
							Connection targetDB,
							String strTarget,
							PreparedStatementHolder psth,
							FilterFilterConditionTObject ffcObj) throws SQLException 
	{
		final String query = "INSERT INTO AMS_Filter_FilterCondition" + strTarget
			+ " (iFilterRef,iFilterConditionRef,iPos) VALUES (?,?,?)";

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
	    
			psth.pst.setInt(1, ffcObj.getFilterRef());
			psth.pst.setInt(2, ffcObj.getFilterConditionRef());
			psth.pst.setInt(3, ffcObj.getPos());
			
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

	public static void remove(Connection con, int filterRef) throws SQLException
	{
		remove(con, "", filterRef, false);
	}

	private static void remove(Connection con, String strMasterSuffix,
							int filterRef, boolean isComplete) throws SQLException
	{
		final String query = "DELETE FROM AMS_Filter_FilterCondition" + strMasterSuffix
				+ (isComplete ? "" : " WHERE iFilterRef = ?");
		
		PreparedStatement st = null;
		
		try
		{
			st = con.prepareStatement(query);
			if(!isComplete) {
				st.setInt(1, filterRef);
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
	
	public static void insert(Connection con, int filterRef, List<FilterConditionKey> filterFilterConditions) throws SQLException
	{
		final String query = "INSERT INTO AMS_Filter_FilterCondition (iFilterRef,iFilterConditionRef,iPos) VALUES (?,?,?)";
		
		PreparedStatement st = null;
			
		try
		{
			st = con.prepareStatement(query);
			
			Iterator<FilterConditionKey> iter = filterFilterConditions.iterator();
			
			int i = 0;
			
			while(iter.hasNext())
			{
				FilterConditionKey item = (FilterConditionKey)iter.next();
				
				st.setInt(1, filterRef);
				st.setInt(2, item.filterConditionID);
				st.setInt(3, ++i);
				st.executeUpdate();
				st.clearParameters();
			}
		}	
		catch(SQLException ex)
		{
			con.rollback();
			Log.log(Log.FATAL, "Sql-Query failed: " + query, ex);
			throw ex;
		}
		finally
		{
			close(st,null);
		}
	}
}
