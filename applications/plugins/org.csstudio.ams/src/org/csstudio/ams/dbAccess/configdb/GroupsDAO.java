
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

public abstract class GroupsDAO extends DAO
{
	public static void insert(Connection con, GroupsTObject group) throws SQLException
	{
		final String query = "INSERT INTO AMS_Groups (iGroupId,cGroupName,sType) VALUES (?,?,?)";
	
		PreparedStatement st = null;

		try
		{
			int groupID = getNewID(con, "iGroupId", "AMS_Groups");
			
			st = con.prepareStatement(query);
			st.setInt(		1, groupID);
			st.setString(	2, group.getName());
			st.setShort(	3, group.getType());
				
			st.executeUpdate();
			
			group.setGroupID(groupID);
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

	public static void remove(Connection con, int groupId) throws SQLException
	{
		final String query = "DELETE FROM AMS_Groups WHERE iGroupId = ?";
		
		PreparedStatement st = null;
		
		try
		{
			st = con.prepareStatement(query);
			st.setInt(1, groupId);
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

	public static GroupsTObject select(Connection con, int groupID) throws SQLException
	{
		final String query = "SELECT iGroupId,cGroupName,sType FROM AMS_Groups WHERE iGroupId=?";
		
		ResultSet rs = null;
		PreparedStatement st = null;
		
		try
		{
			st = con.prepareStatement(query);
			st.setInt(1, groupID);
			rs = st.executeQuery();
			
			if(rs.next())
				return new GroupsTObject(rs.getInt(1), rs.getString(2), rs.getShort(3));
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

	public static List<GroupKey> selectKeyList(Connection con, int sGroupType) throws SQLException
	{
		final String query = "SELECT iGroupId,cGroupName,sType FROM AMS_Groups WHERE sType=? ORDER BY 2";
		
		ResultSet rs = null;
		PreparedStatement st = null;
		ArrayList<GroupKey> array = new ArrayList<GroupKey>();
		
		try
		{
			st = con.prepareStatement(query);
			st.setInt(1, sGroupType);
			rs = st.executeQuery();
			
			while(rs.next())
				array.add(new GroupKey(rs.getInt(1), rs.getString(2), rs.getShort(3)));
			
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
	
	public static void update(Connection con, GroupsTObject group) throws Exception
	{
		final String query = "UPDATE AMS_Groups SET cGroupName=? WHERE iGroupId=?";
		
		PreparedStatement st = null;

		try
		{
			st = con.prepareStatement(query);
			st.setString(	1, group.getName());
			st.setInt(		2, group.getGroupID());

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