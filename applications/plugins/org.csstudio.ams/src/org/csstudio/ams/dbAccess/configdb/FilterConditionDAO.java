
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

public abstract class FilterConditionDAO extends DAO 
{
	public static void copyFilterCondition(Connection masterDB, Connection localDB) throws SQLException 
	{
		copyFilterCondition(masterDB, localDB, DB_BACKUP_SUFFIX);
	}
	
	public static void copyFilterCondition(Connection masterDB, Connection localDB, String masterDbSuffix) throws SQLException 
	{
		copyFilterCondition(masterDB, localDB, masterDbSuffix, "");
	}
	
	public static void backupFilterCondition(Connection masterDB) throws SQLException
	{
		copyFilterCondition(masterDB, masterDB, "", DB_BACKUP_SUFFIX);
	}
	
	private static void copyFilterCondition(Connection masterDB, Connection targetDB,
							String strMaster, String strTarget) throws SQLException
	{
    	final String query = "SELECT iFilterConditionID,iGroupRef,cName,cDesc,iFilterConditionTypeRef FROM AMS_FilterCondition" + strMaster;
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
				FilterConditionTObject fcObj = new FilterConditionTObject(
						rs.getInt(1), 
						rs.getInt(2), 
						rs.getString(3), 
						rs.getString(4), 
						rs.getInt(5));
				preparedInsertFilterCondition(targetDB, strTarget, psth, fcObj);
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
					preparedInsertFilterCondition(null, strTarget, psth, null);
				}
			}
			catch (SQLException ex) 
			{
				Log.log(Log.WARN, ex);
			}
		}
	}
	
	private static void preparedInsertFilterCondition(
							Connection targetDB,
							String strTarget,
							PreparedStatementHolder psth,
							FilterConditionTObject fcObj) throws SQLException 
	{
		final String query = "INSERT INTO AMS_FilterCondition" + strTarget
			+ " (iFilterConditionID,iGroupRef,cName,cDesc,iFilterConditionTypeRef) VALUES (?,?,?,?,?)";

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
	    
			psth.pst.setInt(	1, fcObj.getFilterConditionID());
			psth.pst.setInt(	2, fcObj.getGroupRef());
			psth.pst.setString(	3, fcObj.getName());
			psth.pst.setString(	4, fcObj.getDesc());
			psth.pst.setInt(	5, fcObj.getFilterConditionTypeRef());
			
			psth.pst.executeUpdate();
		}
		catch(SQLException ex)
		{
			Log.log(Log.FATAL, "Sql-Query failed: " + query, ex);
			throw ex;
		}
	}

	public static void insert(Connection con, FilterConditionTObject filterCondition) throws SQLException
	{
		final String query = "INSERT INTO AMS_FilterCondition (iFilterConditionID,iGroupRef,cName,cDesc,iFilterConditionTypeRef) VALUES(?,?,?,?,?)";
	
		PreparedStatement st = null;
	
		try
		{
			int newID = getNewID(con, "iFilterConditionID", "AMS_FilterCondition");
			st = con.prepareStatement(query);
			st.setInt(		1, newID);
			st.setInt(		2, filterCondition.getGroupRef());
			st.setString(	3, filterCondition.getName());
			st.setString(	4, filterCondition.getDesc());
			st.setInt(		5, filterCondition.getFilterConditionTypeRef());
	
			st.executeUpdate();
			filterCondition.setFilterConditionID(newID);
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

	public static void remove(Connection con, int filterConditionID) throws SQLException
	{
		remove(con, "", filterConditionID, false);
	}
	
	private static void remove(Connection con, String strMasterSuffix,
							int filterConditionID, boolean isComplete) throws SQLException
	{
		final String query = "DELETE FROM AMS_FilterCondition" + strMasterSuffix 
				+ (isComplete ? "" : " WHERE iFilterConditionID = ?");
		PreparedStatement st = null;

		try
		{
			st = con.prepareStatement(query);
			if(!isComplete) {
				st.setInt(1, filterConditionID);
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

    public static FilterConditionTObject select(Connection con, int filterConditionID) throws SQLException
	{
    	final String query = "SELECT iFilterConditionID,iGroupRef,cName,cDesc,iFilterConditionTypeRef FROM AMS_FilterCondition WHERE iFilterConditionID = ?";

	    ResultSet rs = null;
	    PreparedStatement st = null;

        try
        {
            st = con.prepareStatement(query);
           	st.setInt(1, filterConditionID);
            rs = st.executeQuery();

            if(rs.next())
            	return new FilterConditionTObject(rs.getInt(1), rs.getInt(2),rs.getString(3), rs.getString(4), rs.getInt(5));
            
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
    
    public static List<FilterConditionKey> selectKeyList(Connection con) throws SQLException
	{
    	final String query = "SELECT iFilterConditionID,cName,iGroupRef FROM AMS_FilterCondition";

	    ResultSet rs = null;
	    PreparedStatement st = null;
	    ArrayList<FilterConditionKey> array = new ArrayList<FilterConditionKey>();

        try
        {
            st = con.prepareStatement(query);
            rs = st.executeQuery();

            while(rs.next())
            	array.add(new FilterConditionKey(rs.getInt(1),rs.getString(2), rs.getInt(3)));
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
    
    public static List<FilterConditionTObject> selectList(Connection con) throws SQLException
	{
    	final String query = "SELECT iFilterConditionID,iGroupRef,cName,cDesc,iFilterConditionTypeRef FROM AMS_FilterCondition ORDER BY 1";

	    ResultSet rs = null;
	    PreparedStatement st = null;
	    ArrayList<FilterConditionTObject> array = new ArrayList<FilterConditionTObject>();

        try
        {
            st = con.prepareStatement(query);
            rs = st.executeQuery();

            while(rs.next())
            	array.add(new FilterConditionTObject(rs.getInt(1), rs.getInt(2),rs.getString(3), rs.getString(4), rs.getInt(5)));
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
	
	public static void update(Connection con, FilterConditionTObject filterCondition) throws SQLException
	{
		final String query = "UPDATE AMS_FilterCondition SET iGroupRef=?,cName=?,cDesc=?,iFilterConditionTypeRef=? WHERE iFilterConditionID = ?";
	
		PreparedStatement st = null;

		try
		{
			st = con.prepareStatement(query);
			st.setInt(		1, filterCondition.getGroupRef());
			st.setString(	2, filterCondition.getName());
			st.setString(	3, filterCondition.getDesc());
			st.setInt(		4, filterCondition.getFilterConditionTypeRef());

			st.setInt(		5, filterCondition.getFilterConditionID());

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
