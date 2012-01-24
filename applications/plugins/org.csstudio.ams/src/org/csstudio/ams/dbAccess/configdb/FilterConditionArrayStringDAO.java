
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

public class FilterConditionArrayStringDAO  extends DAO
{
	public static void copyFilterConditionArrayString(Connection masterDB, Connection localDB) throws SQLException 
	{
		copyFilterConditionArrayString(masterDB, localDB, DB_BACKUP_SUFFIX);
	}
	
	public static void copyFilterConditionArrayString(Connection masterDB, Connection localDB, String masterDbSuffix) throws SQLException 
	{
		copyFilterConditionArrayString(masterDB, localDB, masterDbSuffix, "");
	}
	
	public static void backupFilterConditionArrayString(Connection masterDB) throws SQLException
	{
		copyFilterConditionArrayString(masterDB, masterDB, "", DB_BACKUP_SUFFIX);
	}

	private static void copyFilterConditionArrayString(Connection masterDB, Connection targetDB,
							String strMaster, String strTarget) throws SQLException
	{
		final String query = "SELECT iFilterConditionRef,cKeyValue,sOperator FROM AMS_FilterCond_ArrStr" + strMaster;
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
				FilterConditionArrayStringTObject fcasObj = new FilterConditionArrayStringTObject(
						rs.getInt(1), 
						rs.getString(2), 
						rs.getShort(3));
				preparedInsertFilterConditionArrayString(targetDB, strTarget, psth, fcasObj);
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
					preparedInsertFilterConditionArrayString(null, strTarget, psth, null);
				}
			}
			catch (SQLException ex) 
			{
				Log.log(Log.WARN, ex);
			}
		}
	}
	
	private static void preparedInsertFilterConditionArrayString(
							Connection targetDB,
							String strTarget,
							PreparedStatementHolder psth,
							FilterConditionArrayStringTObject fcasObj) throws SQLException 
	{
		final String query = "INSERT INTO AMS_FilterCond_ArrStr" + strTarget
			+ " (iFilterConditionRef,cKeyValue,sOperator) VALUES (?,?,?)";

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
	    
			psth.pst.setInt(	1, fcasObj.getFilterConditionRef());
			psth.pst.setString(	2, fcasObj.getKeyValue());
			psth.pst.setShort(	3, fcasObj.getOperator());
			
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
		final String query = "DELETE FROM AMS_FilterCond_ArrStr" + strMasterSuffix
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

    public static FilterConditionArrayStringTObject select(Connection con, int filterConditionRef) throws SQLException
	{
		final String query = "SELECT iFilterConditionRef,cKeyValue,sOperator FROM AMS_FilterCond_ArrStr WHERE iFilterConditionRef = ?";

	    ResultSet rs = null;
	    PreparedStatement st = null;

        try
        {
            st = con.prepareStatement(query);
           	st.setInt(1, filterConditionRef);
            rs = st.executeQuery();

            if(rs.next()) 
            	return new FilterConditionArrayStringTObject(rs.getInt(1), rs.getString(2), rs.getShort(3));
            
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
    
	public static void update(Connection con, FilterConditionArrayStringTObject condition) throws SQLException 
	{
		final String query = "UPDATE AMS_FilterCond_ArrStr SET cKeyValue=?,sOperator=? WHERE iFilterConditionRef=?";
		
		PreparedStatement st = null;

		try
		{
			st = con.prepareStatement(query);			
			st.setString(1, condition.getKeyValue());
			st.setShort(2, condition.getOperator());
			st.setInt(3, condition.getFilterConditionRef());
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
	
	public static void insert(Connection con, FilterConditionArrayStringTObject condition) throws SQLException 
	{
		final String query = "INSERT INTO AMS_FilterCond_ArrStr (iFilterConditionRef,cKeyValue,sOperator) VALUES (?,?,?)";
		
		PreparedStatement st = null;

		try
		{
			st = con.prepareStatement(query);
			st.setInt(1, condition.getFilterConditionRef());
			st.setString(2, condition.getKeyValue());
			st.setShort(3, condition.getOperator());
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
