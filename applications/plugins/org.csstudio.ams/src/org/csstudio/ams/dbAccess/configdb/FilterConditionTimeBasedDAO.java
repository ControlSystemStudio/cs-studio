
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
import org.csstudio.ams.Log;
import org.csstudio.ams.dbAccess.DAO;
import org.csstudio.ams.dbAccess.PreparedStatementHolder;

public class FilterConditionTimeBasedDAO  extends DAO
{
	public static void copyFilterConditionTimeBased(Connection masterDB, Connection localDB) throws SQLException 
	{
		copyFilterConditionTimeBased(masterDB, localDB, DB_BACKUP_SUFFIX);
	}
	
	public static void copyFilterConditionTimeBased(Connection masterDB, Connection localDB, String masterDbSuffix) throws SQLException 
	{
		copyFilterConditionTimeBased(masterDB, localDB, masterDbSuffix, "");
	}
	
	public static void backupFilterConditionTimeBased(Connection masterDB) throws SQLException
	{
		copyFilterConditionTimeBased(masterDB, masterDB, "", DB_BACKUP_SUFFIX);
	}

	private static void copyFilterConditionTimeBased(Connection masterDB, Connection targetDB,
							String strMaster, String strTarget) throws SQLException
	{
		final String query = "SELECT iFilterConditionRef,cStartKeyValue,sStartOperator,cStartCompValue,cConfirmKeyValue,sConfirmOperator,cConfirmCompValue,sTimePeriod,sTimeBehavior FROM AMS_FilterCond_TimeBased" + strMaster;
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
				FilterConditionTimeBasedTObject fctbObj = new FilterConditionTimeBasedTObject(
						rs.getInt(1), 
						rs.getString(2), 
						rs.getShort(3), 
						rs.getString(4),
						rs.getString(5), 
						rs.getShort(6), 
						rs.getString(7),
						rs.getShort(8), 
						rs.getShort(9));
				
				preparedInsertFilterConditionTimeBased(targetDB, strTarget, psth, fctbObj);
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
					preparedInsertFilterConditionTimeBased(null, strTarget, psth, null);
				}
			}
			catch (SQLException ex) 
			{
				Log.log(Log.WARN, ex);
			}
		}
	}
	
	private static void preparedInsertFilterConditionTimeBased(
							Connection targetDB,
							String strTarget,
							PreparedStatementHolder psth,
							FilterConditionTimeBasedTObject fctbObj) throws SQLException 
	{
		final String query = "INSERT INTO AMS_FilterCond_TimeBased" + strTarget
			+ " (iFilterConditionRef,cStartKeyValue,sStartOperator,cStartCompValue,cConfirmKeyValue,sConfirmOperator,cConfirmCompValue,sTimePeriod,sTimeBehavior) VALUES (?,?,?,?,?,?,?,?,?)";

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
	    
			psth.pst.setInt(	1, fctbObj.getFilterConditionRef());
			psth.pst.setString(	2, fctbObj.getStartKeyValue());
			psth.pst.setShort(	3, fctbObj.getStartOperator());
			psth.pst.setString(	4, fctbObj.getStartCompValue());
			psth.pst.setString(	5, fctbObj.getConfirmKeyValue());
			psth.pst.setShort(	6, fctbObj.getConfirmOperator());
			psth.pst.setString(	7, fctbObj.getConfirmCompValue());
			psth.pst.setShort(	8, fctbObj.getTimePeriod());
			psth.pst.setShort(	9, fctbObj.getTimeBehavior());
			
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

	public static void remove(Connection con, int filterConditionRef) throws SQLException
	{
		remove(con, "", filterConditionRef, false);
	}
	
	private static void remove(Connection con, String strMasterSuffix,
							int filterConditionRef, boolean isComplete) throws SQLException
	{
		final String query = "DELETE FROM AMS_FilterCond_TimeBased" + strMasterSuffix
				+ (isComplete ? "" : " WHERE iFilterConditionRef = ?");
		PreparedStatement st = null;
		
		try
		{
			st = con.prepareStatement(query);
			if(!isComplete) {
				st.setInt(1, filterConditionRef);
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

    public static FilterConditionTimeBasedTObject select(Connection con, int filterConditionRef) throws SQLException
	{
		final String query = "SELECT iFilterConditionRef,cStartKeyValue,sStartOperator,cStartCompValue,cConfirmKeyValue,sConfirmOperator,cConfirmCompValue,sTimePeriod,sTimeBehavior FROM AMS_FilterCond_TimeBased WHERE iFilterConditionRef = ?";

	    ResultSet rs = null;
	    PreparedStatement st = null;

        try
        {
            st = con.prepareStatement(query);
           	st.setInt(1, filterConditionRef);
            rs = st.executeQuery();

            if(rs.next()) 
            	return new FilterConditionTimeBasedTObject(rs.getInt(1), rs.getString(2), rs.getShort(3), rs.getString(4),rs.getString(5), rs.getShort(6), rs.getString(7),rs.getShort(8),rs.getShort(9));
            
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
    
	public static void update(Connection con, FilterConditionTimeBasedTObject condition) throws SQLException 
	{
		final String query = "UPDATE AMS_FilterCond_TimeBased SET cStartKeyValue=?,sStartOperator=?,cStartCompValue=?,cConfirmKeyValue=?,sConfirmOperator=?,cConfirmCompValue=?,sTimePeriod=?,sTimeBehavior=? WHERE iFilterConditionRef=?";
		
		PreparedStatement st = null;

		try
		{
			st = con.prepareStatement(query);			
			st.setString(1, condition.getStartKeyValue());
			st.setShort(2, condition.getStartOperator());
			st.setString(3, condition.getStartCompValue());
			st.setString(4, condition.getConfirmKeyValue());
			st.setShort(5, condition.getConfirmOperator());
			st.setString(6, condition.getConfirmCompValue());
			st.setShort(7, condition.getTimePeriod());
			st.setShort(8, condition.getTimeBehavior());
			
			st.setInt(9, condition.getFilterConditionRef());
			st.execute();			
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
	
	public static void insert(Connection con, FilterConditionTimeBasedTObject condition) throws SQLException 
	{
		final String query = "INSERT INTO AMS_FilterCond_TimeBased (iFilterConditionRef,cStartKeyValue,sStartOperator,cStartCompValue,cConfirmKeyValue,sConfirmOperator,cConfirmCompValue,sTimePeriod,sTimeBehavior) VALUES (?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement st = null;

		try
		{
			st = con.prepareStatement(query);
			st.setInt(1, condition.getFilterConditionRef());
			st.setString(2, condition.getStartKeyValue());
			st.setShort(3, condition.getStartOperator());
			st.setString(4, condition.getStartCompValue());
			st.setString(5, condition.getConfirmKeyValue());
			st.setShort(6, condition.getConfirmOperator());
			st.setString(7, condition.getConfirmCompValue());
			st.setShort(8, condition.getTimePeriod());
			st.setShort(9, condition.getTimeBehavior());
			st.execute();			
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
