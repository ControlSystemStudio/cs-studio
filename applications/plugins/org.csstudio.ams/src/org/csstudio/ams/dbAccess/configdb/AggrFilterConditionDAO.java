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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.ams.Log;
import org.csstudio.ams.dbAccess.DAO;
import org.csstudio.ams.dbAccess.PreparedStatementHolder;

public class AggrFilterConditionDAO extends DAO 
{
	public static List<FilterConditionKey> select(Connection con, int filterID) throws SQLException
	{
		final String query =
	       "SELECT FC.iFilterConditionID, FC.cName, FC.iGroupRef, FFC.iPos" 
    	+ " FROM AMS_Filter_FilterCondition FFC, AMS_FilterCondition FC"
    	+ " WHERE FFC.iFilterConditionRef = FC.iFilterConditionID"
    	+ " AND FFC.iFilterRef = " + filterID
    	+ " ORDER BY FFC.iPos ASC";

    	ResultSet rs = null;
		Statement st = null;
		ArrayList<FilterConditionKey> fc = new ArrayList<FilterConditionKey>();
		
		try
		{
			st = con.createStatement();
			rs = st.executeQuery(query);
			
			while(rs.next())
				fc.add(new FilterConditionKey(rs.getInt(1), rs.getString(2), rs.getInt(3)));
			
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
		return fc;
	}
	
	public static List<FilterKey> selectFilterForFilterConditions(Connection con, int filterConditionID) throws SQLException
	{
		String query = "SELECT DISTINCT F.iFilterID, F.cName, F.iGroupRef FROM AMS_Filter F, AMS_Filter_FilterCondition FFC " +
        "WHERE F.iFilterID = FFC.iFilterRef AND FFC.iFilterConditionRef = " + filterConditionID;

		Statement st = null;
		ResultSet rs = null;
		ArrayList<FilterKey> array = new ArrayList<FilterKey>();

		try
		{
			st = con.createStatement();
			rs = st.executeQuery(query);

			while(rs.next())
				array.add(new FilterKey(rs.getInt(1),rs.getString(2), rs.getInt(3)));
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

	public static Map<Integer, String> selectFCTList(Connection conDb) throws SQLException
	{
		final String query = "SELECT iFilterConditionTypeID, cClass FROM AMS_FilterConditionType ORDER BY iFilterConditionTypeID ASC";

		ResultSet rs = null;
		Statement st = null;
		HashMap<Integer, String> hmFCT = new HashMap<Integer, String>();
		
		try
		{
			st = conDb.createStatement();
			rs = st.executeQuery(query);
			
			while(rs.next())
				hmFCT.put(new Integer(rs.getInt(1)), rs.getString(2));

			return hmFCT;
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
	
	public static List<AggrFilterTObject> selectFilterList(Connection conDb) throws SQLException
	{
		final String query = "SELECT iFilterID FROM AMS_Filter ORDER BY iFilterID ASC";

		ResultSet rs = null;
		Statement st = null;
		PreparedStatementHolder psth = null;
		ArrayList<AggrFilterTObject> array = new ArrayList<AggrFilterTObject>();
		
		try
		{
			st = conDb.createStatement();
			rs = st.executeQuery(query);
			psth = new PreparedStatementHolder();
			while(rs.next())
			{
				AggrFilterTObject aFilter = new AggrFilterTObject(rs.getInt(1), null);
				aFilter.setFilterConditions(preparedSelectFCList(conDb, psth, aFilter));			
				array.add(aFilter);
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

			try
			{
				if (psth.pst != null)
				{
					psth.bMode = PreparedStatementHolder.MODE_CLOSE;
					preparedSelectFCList(null, psth, null);
				}
			}
			catch (SQLException ex) 
			{
				Log.log(Log.WARN, ex);
			}
		}
	}

	private static List<AggrFilterConditionTObject> preparedSelectFCList(
			Connection conDb,
			PreparedStatementHolder psth,
			AggrFilterTObject aFilter) throws SQLException
	{
		final String query =
			   "SELECT FFC.iPos, FC.iFilterConditionID, FC.iFilterConditionTypeRef"
				+ " FROM AMS_FilterCondition FC, AMS_Filter_FilterCondition FFC"
				+ " WHERE FFC.iFilterConditionRef = FC.iFilterConditionID"
				+ " AND FFC.iFilterRef = ?"
				+ " ORDER BY FFC.iPos ASC";
		
		if (psth.bMode == PreparedStatementHolder.MODE_CLOSE)
		{
			try{
				psth.pst.close();
			} catch (SQLException ex){throw ex;}
			return null;
		}

		ResultSet rs = null;
		ArrayList<AggrFilterConditionTObject> array = new ArrayList<AggrFilterConditionTObject>();

		try
		{
			if (psth.bMode == PreparedStatementHolder.MODE_INIT)
			{
				psth.pst = conDb.prepareStatement(query);
				psth.bMode = PreparedStatementHolder.MODE_EXEC;
			}

			psth.pst.setInt(1, aFilter.getFilterId());
			rs = psth.pst.executeQuery();
			while(rs.next())
				array.add(new AggrFilterConditionTObject(rs.getInt(2), rs.getInt(3)));

			return array;
		}
		catch(SQLException ex)
		{
			Log.log(Log.FATAL, "Sql-Query failed: " + query, ex);
			throw ex;
		}
		finally
		{
			close(null,rs);
		}
	}
}