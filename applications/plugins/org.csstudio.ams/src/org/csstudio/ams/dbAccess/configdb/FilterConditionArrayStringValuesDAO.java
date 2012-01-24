
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

public class FilterConditionArrayStringValuesDAO  extends DAO
{
	public static void copyFilterConditionArrayStringValues(Connection masterDB, Connection localDB) throws SQLException 
	{
		copyFilterConditionArrayStringValues(masterDB, localDB, DB_BACKUP_SUFFIX);
	}
	
	public static void copyFilterConditionArrayStringValues(Connection masterDB, Connection localDB, String masterDbSuffix) throws SQLException 
	{
		copyFilterConditionArrayStringValues(masterDB, localDB, masterDbSuffix, "");
	}
	
	public static void backupFilterConditionArrayStringValues(Connection masterDB) throws SQLException
	{
		copyFilterConditionArrayStringValues(masterDB, masterDB, "", DB_BACKUP_SUFFIX);
	}

	private static void copyFilterConditionArrayStringValues(Connection masterDB, Connection targetDB,
							String strMaster, String strTarget) throws SQLException
	{
		final String query = "SELECT iFilterConditionRef,cCompValue FROM AMS_FilterCond_ArrStrVal" + strMaster;
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
				FilterConditionArrayStringValuesTObject fcasvObj = new FilterConditionArrayStringValuesTObject(
						rs.getInt(1), 
						rs.getString(2));
				preparedInsertFilterConditionArrayStringValues(targetDB, strTarget, psth, fcasvObj);
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
					preparedInsertFilterConditionArrayStringValues(null, strTarget, psth, null);
				}
			}
			catch (SQLException ex) 
			{
				Log.log(Log.WARN, ex);
			}
		}
	}
	
	private static void preparedInsertFilterConditionArrayStringValues(
							Connection targetDB,
							String strTarget,
							PreparedStatementHolder psth,
							FilterConditionArrayStringValuesTObject fcasvObj) throws SQLException 
	{
		final String query = "INSERT INTO AMS_FilterCond_ArrStrVal" + strTarget
			+ " (iFilterConditionRef,cCompValue) VALUES (?,?)";

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
	    
			psth.pst.setInt(	1, fcasvObj.getFilterConditionRef());
			psth.pst.setString(	2, fcasvObj.getCompValue());
			
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
		final String query = "DELETE FROM AMS_FilterCond_ArrStrVal" + strMasterSuffix
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

    public static List<FilterConditionArrayStringValuesTObject> select(Connection con, int filterConditionRef) throws SQLException
	{
		final String query = "SELECT iFilterConditionRef,cCompValue FROM AMS_FilterCond_ArrStrVal WHERE iFilterConditionRef = ?";

	    ResultSet rs = null;
	    PreparedStatement st = null;
	    ArrayList<FilterConditionArrayStringValuesTObject> array = new ArrayList<FilterConditionArrayStringValuesTObject>();

        try
        {
            st = con.prepareStatement(query);
           	st.setInt(1, filterConditionRef);
            rs = st.executeQuery();

            while(rs.next()) 
            	array.add(new FilterConditionArrayStringValuesTObject(rs.getInt(1), rs.getString(2)));
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
        return array;
    }
    
    public static void insert(Connection con, List<FilterConditionArrayStringValuesTObject> conditions) throws SQLException 
	{
		final String query = "INSERT INTO AMS_FilterCond_ArrStrVal (iFilterConditionRef,cCompValue) VALUES (?,?)";
		
		PreparedStatement st = null;

		try
		{
			Iterator<FilterConditionArrayStringValuesTObject> iter = conditions.iterator();
			
			st = con.prepareStatement(query);
			
			while(iter.hasNext())
			{
				FilterConditionArrayStringValuesTObject condition = iter.next();
				st.setInt(1, condition.getFilterConditionRef());
				st.setString(2, condition.getCompValue());
				st.execute();	
				st.clearParameters();
			}
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
