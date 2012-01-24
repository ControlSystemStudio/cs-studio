
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

public abstract class FilterFilterActionDAO extends DAO
{
	public static void copyFilterFilterAction(Connection masterDB, Connection localDB) throws SQLException 
	{
		copyFilterFilterAction(masterDB, localDB, DB_BACKUP_SUFFIX);
	}
	
	public static void copyFilterFilterAction(Connection masterDB, Connection localDB, String masterDbSuffix) throws SQLException 
	{
		copyFilterFilterAction(masterDB, localDB, masterDbSuffix, "");
	}
	
	public static void backupFilterFilterAction(Connection masterDB) throws SQLException
	{
		copyFilterFilterAction(masterDB, masterDB, "", DB_BACKUP_SUFFIX);
	}
	
	private static void copyFilterFilterAction(Connection masterDB, Connection targetDB,
							String strMaster, String strTarget) throws SQLException
	{
		final String query = "SELECT iFilterRef,iFilterActionRef,iPos FROM AMS_Filter_FilterAction" + strMaster;
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
				FilterFilterActionTObject ffaObj = new FilterFilterActionTObject(
						rs.getInt(1), 
						rs.getInt(2), 
						rs.getInt(3));
				preparedInsertFilterFilterAction(targetDB, strTarget, psth, ffaObj);
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
					preparedInsertFilterFilterAction(null, strTarget, psth, null);
				}
			}
			catch (SQLException ex) 
			{
				Log.log(Log.WARN, ex);
			}
		}
	}
	
	public static void insert(Connection con, List<FilterFilterActionTObject> filterFilterActions) throws SQLException
	{
		PreparedStatementHolder psth = new PreparedStatementHolder();
		
		try
		{
			psth.bMode = PreparedStatementHolder.MODE_INIT;
			
			Iterator<FilterFilterActionTObject> iter = filterFilterActions.iterator();
			
			while(iter.hasNext())
				preparedInsertFilterFilterAction(con, "", psth, iter.next());
			
		}
		catch(SQLException ex)
		{
			Log.log(Log.FATAL, ex);
			throw ex;
		}
		finally
		{
			try
			{
				if(psth.pst != null && psth.bMode == PreparedStatementHolder.MODE_EXEC)
				{
					psth.bMode = PreparedStatementHolder.MODE_CLOSE;
					preparedInsertFilterFilterAction(con, "", psth, null);
				}
			}
			catch(Exception ex)
			{
				Log.log(Log.FATAL, ex);
			}
		}
	}
	
	private static void preparedInsertFilterFilterAction(
							Connection targetDB,
							String strTarget,
							PreparedStatementHolder psth,
							FilterFilterActionTObject ffaObj) throws SQLException 
	{
		final String query = "INSERT INTO AMS_Filter_FilterAction" + strTarget
			+ " (iFilterRef,iFilterActionRef,iPos) VALUES (?,?,?)";

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
	    
			psth.pst.setInt(1, ffaObj.getFilterRef());
			psth.pst.setInt(2, ffaObj.getFilterActionRef());
			psth.pst.setInt(3, ffaObj.getPos());
			
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
		final String query = "DELETE FROM AMS_Filter_FilterAction" + strMasterSuffix
				+ (isComplete ? "" : " WHERE iFilterRef = ?");
		
		PreparedStatement st = null;
		
		try
		{
			st = con.prepareStatement(query);
			if(!isComplete)
			{
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

}
