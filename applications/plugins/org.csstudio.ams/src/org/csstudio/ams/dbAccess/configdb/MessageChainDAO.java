
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

public abstract class MessageChainDAO extends DAO 
{
	public static void insert(Connection con, MessageChainTObject msgChain) throws SQLException
	{
		final String query = "INSERT INTO AMS_MessageChain (iMessageChainID,iMessageRef,iFilterRef,iFilterActionRef,iReceiverPos,tSendTime,tNextActTime,sChainState,cReceiverAdress) VALUES(?,?,?,?,?,?,?,?,?)";
		PreparedStatement st = null;
	
		try
		{
			int newID = getNewID(con, "iMessageChainID", "AMS_MessageChain");
			st = con.prepareStatement(query);
			st.setInt(1, newID);
			st.setInt(2, msgChain.getMessageRef());
			st.setInt(3, msgChain.getFilterRef());
			st.setInt(4, msgChain.getFilterActionRef());
			st.setInt(5, msgChain.getReceiverPos());
			setUtilDate(st, 6, msgChain.getSendTime());
			setUtilDate(st, 7, msgChain.getNextActTime());
			st.setShort(8, msgChain.getChainState());
			st.setString(9, msgChain.getReceiverAdress());
			
			st.execute();
			msgChain.setMessageChainID(newID);
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

	public static MessageChainTObject select(Connection con, int iMessageChainID) throws SQLException
	{
		final String query = "SELECT iMessageChainID,iMessageRef,iFilterRef,iFilterActionRef,iReceiverPos,tSendTime,tNextActTime,sChainState,cReceiverAdress FROM AMS_MessageChain WHERE iMessageChainID = ?";
	
		ResultSet rs = null;
		PreparedStatement st = null;
		
		try
		{
			MessageChainTObject msgChain = null;
			st = con.prepareStatement(query);
			st.setInt(1, iMessageChainID);
			rs = st.executeQuery();
			
			if(rs.next()) 
			{
				msgChain = new MessageChainTObject(rs.getInt(1),
						rs.getInt(2),
						rs.getInt(3),
						rs.getInt(4),
						rs.getInt(5),
						getUtilDate(rs, 6),
						getUtilDate(rs, 7),
						rs.getShort(8),
						rs.getString(9));
			}
			return msgChain;
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
	
	public static List<Integer> selectKeyList(Connection con, short chainState) throws SQLException
	{
		final String query = "SELECT iMessageChainID FROM AMS_MessageChain WHERE sChainState = ? ORDER BY tNextActTime ASC";
		ResultSet rs = null;
		PreparedStatement st = null;
		ArrayList<Integer> array = new ArrayList<Integer>();
		   
		try
		{
			st = con.prepareStatement(query);
			st.setShort(1, chainState);
			rs = st.executeQuery();
			while(rs.next())
			{
				array.add(new Integer(rs.getInt(1)));
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
		}
	}

	public static void update(Connection con, MessageChainTObject msgChain) throws SQLException
	{
		final String query = "UPDATE AMS_MessageChain set iReceiverPos=?, tSendTime=?, tNextActTime=?, sChainState=?, cReceiverAdress=? WHERE iMessageChainID=?";
	
		PreparedStatement st = null;
			
		try
		{
			st = con.prepareStatement(query);
			st.setInt(1, msgChain.getReceiverPos());
			setUtilDate(st, 2, msgChain.getSendTime());
			setUtilDate(st, 3, msgChain.getNextActTime());
			st.setShort(4, msgChain.getChainState());
			st.setString(5, msgChain.getReceiverAdress());
			st.setInt(6, msgChain.getMessageChainID());
	
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
	
	public static List<MessageChainTObject> selectKeyListByReceiverAdress(Connection con, short chainState, String recAdress) throws SQLException
	{
		final String query = "SELECT iMessageChainID,iMessageRef,iFilterRef,iFilterActionRef,iReceiverPos"
			+ ",tSendTime,tNextActTime,sChainState,cReceiverAdress FROM AMS_MessageChain"
			+ " WHERE sChainState = ?"
			+ " AND cReceiverAdress LIKE ? ORDER BY tNextActTime ASC";

		ResultSet rs = null;
		PreparedStatement st = null;
		ArrayList<MessageChainTObject> array = new ArrayList<MessageChainTObject>();
		   
		try
		{
			st = con.prepareStatement(query);
			st.setShort(1, chainState);
			st.setString(2, recAdress);
			rs = st.executeQuery();
			while(rs.next())
			{
				array.add(new MessageChainTObject(rs.getInt(1),
						rs.getInt(2),
						rs.getInt(3),
						rs.getInt(4),
						rs.getInt(5),
						getUtilDate(rs, 6),
						getUtilDate(rs, 7),
						rs.getShort(8),
						rs.getString(9)));
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
		}
	}

    public static List<MessageChainTObject> selectKeyListByState(Connection con, short chainState) throws SQLException
    {
        final String query = "SELECT iMessageChainID,iMessageRef,iFilterRef,iFilterActionRef,iReceiverPos"
            + ",tSendTime,tNextActTime,sChainState,cReceiverAdress FROM AMS_MessageChain"
            + " WHERE sChainState = ?" 
            + " ORDER BY tNextActTime ASC";

        ResultSet rs = null;
        PreparedStatement st = null;
        ArrayList<MessageChainTObject> array = new ArrayList<MessageChainTObject>();
           
        try
        {
            st = con.prepareStatement(query);
            st.setShort(1, chainState);
            rs = st.executeQuery();
            while(rs.next())
            {
                array.add(new MessageChainTObject(rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getInt(4),
                        rs.getInt(5),
                        getUtilDate(rs, 6),
                        getUtilDate(rs, 7),
                        rs.getShort(8),
                        rs.getString(9)));
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
        }
    }
}