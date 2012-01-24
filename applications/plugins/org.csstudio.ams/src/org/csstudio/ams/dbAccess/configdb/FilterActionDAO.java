
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
import java.util.Vector;

import org.csstudio.ams.Log;
import org.csstudio.ams.dbAccess.DAO;
import org.csstudio.ams.dbAccess.PreparedStatementHolder;

public abstract class FilterActionDAO extends DAO
{
	public static void copyFilterAction(Connection masterDB, Connection localDB) throws SQLException 
	{
		copyFilterAction(masterDB, localDB, DB_BACKUP_SUFFIX);
	}
	
	public static void copyFilterAction(Connection masterDB, Connection localDB, String masterDbSuffix) throws SQLException 
	{
		copyFilterAction(masterDB, localDB, masterDbSuffix, "");
	}
	
	public static void backupFilterAction(Connection masterDB) throws SQLException
	{
		copyFilterAction(masterDB, masterDB, "", DB_BACKUP_SUFFIX);
	}
	
	private static void copyFilterAction(Connection masterDB, Connection targetDB,
							String strMaster, String strTarget) throws SQLException
	{
		final String query = "SELECT iFilterActionID,iFilterActionTypeRef,iReceiverRef,cMessage FROM AMS_FilterAction" + strMaster;
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
				FilterActionTObject faObj = new FilterActionTObject(
						rs.getInt(1), 
						rs.getInt(2), 
						rs.getInt(3), 
						rs.getString(4));
				preparedInsertFilterAction(targetDB, strTarget, psth, faObj);
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
					preparedInsertFilterAction(null, strTarget, psth, null);
				}
			}
			catch (SQLException ex) 
			{
				Log.log(Log.WARN, ex);
			}
		}
	}
	
	public static void insert(Connection con, List<FilterActionTObject> filterActions) throws SQLException
	{
		PreparedStatementHolder psth = new PreparedStatementHolder();
		
		try
		{
			psth.bMode = PreparedStatementHolder.MODE_INIT;
			
			Iterator<FilterActionTObject> iter = filterActions.iterator();			
			
			while(iter.hasNext())
			{
				int newID = getNewID(con, "iFilterActionID", "AMS_FilterAction");
				
				FilterActionTObject action = iter.next();
				action.setFilterActionID(newID);
				preparedInsertFilterAction(con, "", psth, action);
			}
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
					preparedInsertFilterAction(con, "", psth, null);
				}
			}
			catch(Exception ex)
			{
				Log.log(Log.FATAL, ex);
			}
		}
	}
	
	private static void preparedInsertFilterAction(
							Connection targetDB,
							String strTarget,
							PreparedStatementHolder psth,
							FilterActionTObject faObj) throws SQLException 
	{
		final String query = "INSERT INTO AMS_FilterAction" + strTarget
			+ " (iFilterActionID,iFilterActionTypeRef,iReceiverRef,cMessage) VALUES (?,?,?,?)";

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
	    
			psth.pst.setInt(	1, faObj.getFilterActionID());
			psth.pst.setInt(	2, faObj.getFilterActionTypeRef());
			psth.pst.setInt(	3, faObj.getReceiverRef());
			psth.pst.setString(	4, faObj.getMessage());
			
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

	public static void remove(Connection con, int filterActionID) throws SQLException
	{
		remove(con, "", filterActionID, false);
	}
	
	private static void remove(Connection con, String strMasterSuffix,
							int filterActionID, boolean isComplete) throws SQLException
	{
		final String query = "DELETE FROM AMS_FilterAction" + strMasterSuffix
				+ (isComplete ? "" : " WHERE iFilterActionID = ?");
		PreparedStatement st = null;
		
		try
		{
			st = con.prepareStatement(query);
			if(!isComplete) {
				st.setInt(1, filterActionID);
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

	public static FilterActionTObject select(Connection con, int filterActionID) throws SQLException
	{
		final String query ="SELECT iFilterActionID, iFilterActionTypeRef, iReceiverRef, cMessage FROM AMS_FilterAction WHERE iFilterActionID = ?";

    	ResultSet rs = null;
		PreparedStatement st = null;
		FilterActionTObject fAction = null;
		
		try
		{
			st = con.prepareStatement(query);
			st.setInt(1, filterActionID);
			rs = st.executeQuery();
			
			if(rs.next())
				fAction = new FilterActionTObject(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getString(4));
			
			return fAction;
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
	 * Returns an array with all filter actions that are related to the filter.
	 * @param con
	 * @param filterID
	 * @return Array containing FilterActionTObject. It never returns <code>null</code>. If the
	 *         result set was empty, the method returns an empty array.
	 * @throws SQLException
	 */
    public static FilterActionTObject[] selectByFilter(Connection con, int filterID) throws SQLException
    {
        final String query = "SELECT fa.iFilterActionID, fa.iFilterActionTypeRef, fa.iReceiverRef, fa.cMessage"
        + " FROM AMS_FilterAction fa, AMS_Filter_FilterAction ffa"
        + " WHERE fa.iFilterActionID = ffa.iFilterActionRef"
        + " AND ffa.iFilterRef = ?";

        ResultSet rs = null;
        PreparedStatement st = null;
        FilterActionTObject fAction = null;
        FilterActionTObject[] result = null;
        Vector<FilterActionTObject> queryResult;
        
        queryResult = new Vector<FilterActionTObject>();
        
        try
        {
            st = con.prepareStatement(query);
            st.setInt(1, filterID);
            rs = st.executeQuery();
            
            while(rs.next())
            {
                fAction = new FilterActionTObject(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getString(4));
                queryResult.add(fAction);
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
        
        if(queryResult.isEmpty() == false)
        {
            result = new FilterActionTObject[queryResult.size()];
            result = queryResult.toArray(result);
        }
        else
        {
            result = new FilterActionTObject[0];
        }
        
        return result;
    }
}