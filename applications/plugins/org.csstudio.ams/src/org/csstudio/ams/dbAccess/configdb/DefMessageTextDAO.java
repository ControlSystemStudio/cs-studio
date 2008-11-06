
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

public abstract class DefMessageTextDAO extends DAO
{
	public static void insert(Connection con, DefMessageTextTObject defMessage) throws SQLException
	{
		final String query = "INSERT INTO AMS_DefMessageText (iDefMessageTextID,cName,cText) VALUES (?,?,?)";
	
		PreparedStatement st = null;

		try
		{
			int defMessageTextID = getNewID(con, "iDefMessageTextID", "AMS_DefMessageText");
			
			st = con.prepareStatement(query);
			st.setInt(		1, defMessageTextID);
			st.setString(	2, defMessage.getName());
			st.setString(	3, defMessage.getText());
				
			st.executeUpdate();
			
			defMessage.setDefMessageTextID(defMessageTextID);
		}	
		catch(SQLException ex)
		{
			Log.log(Log.ERROR, ex);
			throw ex;
		}
		finally
		{
			close(st,null);
		}
	}

	public static void removeAll(Connection con) throws SQLException
	{
		remove(con, -1, true);
	}

	public static void remove(Connection con, int defMessageTextID) throws SQLException
	{
		remove(con, defMessageTextID, false);
	}

	private static void remove(Connection con, int defMessageTextID, boolean isComplete) throws SQLException
	{
		final String query = "DELETE FROM AMS_DefMessageText" + (isComplete ? "" : " WHERE iDefMessageTextID = ?");
		
		PreparedStatement st = null;
		
		try
		{
			st = con.prepareStatement(query);
			if(!isComplete) {
				st.setInt(1, defMessageTextID);
			}
			st.executeUpdate();
		}
		catch(SQLException ex)
		{
			Log.log(Log.ERROR, ex);
			throw ex;
		}
		finally
		{
			close(st,null);
		}
	}

	public static DefMessageTextTObject select(Connection con, int defMessageTextID) throws SQLException
	{
		final String query = "SELECT iDefMessageTextID,cName,sText FROM AMS_DefMessageText WHERE iDefMessageTextID=?";
		
		ResultSet rs = null;
		PreparedStatement st = null;
		
		try
		{
			st = con.prepareStatement(query);
			st.setInt(1, defMessageTextID);
			rs = st.executeQuery();
			
			if(rs.next())
				return new DefMessageTextTObject(rs.getInt(1), rs.getString(2), rs.getString(3));
			return null;
		}
		catch(SQLException ex)
		{
			Log.log(Log.ERROR, ex);
			throw ex;
		}
		finally
		{
			close(st,rs);
		}
	}

	public static List<DefMessageTextTObject> selectList(Connection con) throws SQLException
	{
		final String query = "SELECT iDefMessageTextID,cName,cText FROM AMS_DefMessageText";
		
		ResultSet rs = null;
		PreparedStatement st = null;
		ArrayList<DefMessageTextTObject> array = new ArrayList<DefMessageTextTObject>();
		
		try
		{
			st = con.prepareStatement(query);
			rs = st.executeQuery();
			
			while(rs.next())
				array.add(new DefMessageTextTObject(rs.getInt(1), rs.getString(2), rs.getString(3)));
			
			return array;
		}
		catch(SQLException ex)
		{
			Log.log(Log.ERROR, ex);
			throw ex;
		}
		finally
		{
			close(st,rs);
		}
	}
	
	public static void update(Connection con, DefMessageTextTObject defMessage) throws Exception
	{
		final String query = "UPDATE AMS_DefMessageText SET cName=?,cText=? WHERE iDefMessageTextID=?";
		
		PreparedStatement st = null;

		try
		{
			st = con.prepareStatement(query);
			st.setString(	1, defMessage.getName());
			st.setString(   2, defMessage.getText());
			st.setInt(		3, defMessage.getDefMessageTextID());

			st.executeUpdate();
		}	
		catch(SQLException ex)
		{
			Log.log(Log.ERROR, ex);
			throw ex;
		}
		finally
		{
			close(st,null);
		}		
	}
}