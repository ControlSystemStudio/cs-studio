
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

public class AggrFilterConditionActionDAO extends DAO 
{
	public static AggrFilterConditionActionTObject select(Connection con, int filterID) throws SQLException
	{
		AggrFilterConditionActionTObject filter = new AggrFilterConditionActionTObject();
		filter.setFilter(FilterDAO.select(con, filterID));
		filter.setFilterConditions(AggrFilterConditionDAO.select(con, filterID));		
		filter.setFilterActions(AggrFilterActionDAO.select(con, filterID));

		return filter;
	}

	public static void insert(Connection con, AggrFilterConditionActionTObject filter) throws Exception
	{
		int iPos = 0;
		
		FilterDAO.insert(con, filter.getFilter());		
		FilterFilterConditionDAO.insert(con, filter.getFilter().getFilterID(), filter.getFilterConditions());
		FilterActionDAO.insert(con, filter.getFilterActions());
		
		ArrayList<FilterFilterActionTObject> filterFilterActions = new ArrayList<FilterFilterActionTObject>();
		Iterator<FilterActionTObject> iter = filter.getFilterActions().iterator();
		while(iter.hasNext())
			filterFilterActions.add(new FilterFilterActionTObject(filter.getFilter().getFilterID(), iter.next().getFilterActionID(), ++iPos));

		FilterFilterActionDAO.insert(con, filterFilterActions);		
		
	}

	public static void update(Connection con, AggrFilterConditionActionTObject filter) throws Exception
	{
		int iPos = 0;
		
		con.setAutoCommit(false);
					
		FilterFilterConditionDAO.remove(con, filter.getFilter().getFilterID());
		AggrFilterActionDAO.remove(con, filter.getFilter().getFilterID());
		FilterFilterActionDAO.remove(con, filter.getFilter().getFilterID());
		
		FilterDAO.update(con, filter.getFilter());
		FilterFilterConditionDAO.insert(con, filter.getFilter().getFilterID(), filter.getFilterConditions());		
		FilterActionDAO.insert(con, filter.getFilterActions());
		
		ArrayList<FilterFilterActionTObject> filterFilterActions = new ArrayList<FilterFilterActionTObject>();
		Iterator<FilterActionTObject> iter = filter.getFilterActions().iterator();
		while(iter.hasNext())
			filterFilterActions.add(new FilterFilterActionTObject(filter.getFilter().getFilterID(), iter.next().getFilterActionID(), ++iPos));

		FilterFilterActionDAO.insert(con, filterFilterActions);		
		
	}

	
	public static void remove(Connection con, int filterID) throws Exception
	{
		FilterDAO.remove(con, filterID);
		FilterFilterConditionDAO.remove(con, filterID);
		AggrFilterActionDAO.remove(con, filterID);
		FilterFilterActionDAO.remove(con, filterID);
	}
	
	public static List<FilterKey> selectFilterForUserGroups(Connection con, int userGroupRef) throws SQLException
	{
		String query = "SELECT DISTINCT F.iFilterID, F.cName, F.iGroupRef FROM AMS_Filter F, AMS_Filter_FilterAction FFA, AMS_FilterAction FA " +
		               "WHERE F.iFilterID = FFA.iFilterRef " +
		               "AND FFA.iFilterActionRef = FA.iFilterActionID " +
		               "AND FA.iFilterActionTypeRef IN ("
		               + FILTERACTIONTYPE_SMS_G + "," 
		               + FILTERACTIONTYPE_SMS_GR + "," 
		               + FILTERACTIONTYPE_VM_G + "," 
		               + FILTERACTIONTYPE_VM_GR + "," 
		               + FILTERACTIONTYPE_MAIL_G + "," 
		               + FILTERACTIONTYPE_MAIL_GR +") AND iReceiverRef=?";
		
		PreparedStatement st = null;
		ResultSet rs = null;
		ArrayList<FilterKey> array = new ArrayList<FilterKey>();
		
		try
		{
			st = con.prepareStatement(query);
			st.setInt(1, userGroupRef);
			rs = st.executeQuery();
			
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
	
	public static List<FilterKey> selectFilterForUser(Connection con, int userRef) throws SQLException
	{
		String query = "SELECT DISTINCT F.iFilterID, F.cName, F.iGroupRef FROM AMS_Filter F, AMS_Filter_FilterAction FFA, AMS_FilterAction FA " +
		               "WHERE F.iFilterID = FFA.iFilterRef " +
		               "AND FFA.iFilterActionRef = FA.iFilterActionID " +
		               "AND FA.iFilterActionTypeRef IN ("
		               + FILTERACTIONTYPE_SMS + ","
		               + FILTERACTIONTYPE_VM + ","
		               + FILTERACTIONTYPE_MAIL + ") AND iReceiverRef=?";

		PreparedStatement st = null;
		ResultSet rs = null;
		ArrayList<FilterKey> array = new ArrayList<FilterKey>();
		
		try
		{
			st = con.prepareStatement(query);
			st.setInt(1, userRef);
			rs = st.executeQuery();
			
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
}
