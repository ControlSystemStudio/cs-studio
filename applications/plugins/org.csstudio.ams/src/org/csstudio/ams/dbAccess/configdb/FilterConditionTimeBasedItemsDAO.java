
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
import java.util.Date;
import java.util.List;
import org.csstudio.ams.AmsConstants;
import org.csstudio.ams.Log;
import org.csstudio.ams.dbAccess.DAO;

public class FilterConditionTimeBasedItemsDAO  extends DAO
{
	public static void remove(Connection con, int itemID) throws SQLException
	{
		final String query = "DELETE FROM AMS_FilterCond_TimeBasedItems WHERE itemID = ?";
		PreparedStatement st = null;
		
		try
		{
			st = con.prepareStatement(query);
			st.setInt(1, itemID);
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

    public static FilterConditionTimeBasedItemsTObject select(Connection con, int itemID) throws SQLException
	{
		final String query = "SELECT iItemID,iFilterConditionRef,iFilterRef,cIdentifier,sState,tStartTime,tEndTime,sTimeOutAction,iMessageRef FROM AMS_FilterCond_TimeBasedItems WHERE iItemID=?";

	    ResultSet rs = null;
	    PreparedStatement st = null;

        try
        {
            st = con.prepareStatement(query);
           	st.setInt(1, itemID);
            rs = st.executeQuery();

            if(rs.next()) 
            	return new FilterConditionTimeBasedItemsTObject(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getString(4),rs.getShort(5), getUtilDate(rs,6),getUtilDate(rs,7), rs.getShort(8),rs.getInt(9));
            
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
    
    public static FilterConditionTimeBasedItemsTObject selectWaitingByIdentifier(Connection con, int filterConditionRef, int filterRef, String identifier) throws SQLException
	{
		final String query = "SELECT iItemID,iFilterConditionRef,iFilterRef,cIdentifier,sState,tStartTime,tEndTime,sTimeOutAction,iMessageRef FROM AMS_FilterCond_TimeBasedItems WHERE iFilterConditionRef=? AND iFilterRef=? AND cIdentifier=? AND sState=?";

	    ResultSet rs = null;
	    PreparedStatement st = null;

        try
        {
            st = con.prepareStatement(query);
           	st.setInt(1, filterConditionRef);
           	st.setInt(2, filterRef);
           	st.setString(3, identifier);
           	st.setShort(4, AmsConstants.STATE_WAITING);
            rs = st.executeQuery();

            if(rs.next()) 
            	return new FilterConditionTimeBasedItemsTObject(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getString(4),rs.getShort(5), getUtilDate(rs,6), getUtilDate(rs,7), rs.getShort(8), rs.getInt(9));
            
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
    
    public static List<FilterConditionTimeBasedItemsTObject> selectTimeOutOrConfirmedForAlarm(Connection con) throws SQLException
	{
		final String query = "SELECT iItemID,iFilterConditionRef,iFilterRef,cIdentifier,sState,tStartTime,tEndTime,sTimeOutAction,iMessageRef " +
							 "FROM AMS_FilterCond_TimeBasedItems "+
		                     "WHERE (tEndTime<? AND sState=?) " +
		                     "OR (sState=? AND sTimeOutAction=?) ORDER BY iItemID";

	    ResultSet rs = null;
	    PreparedStatement st = null;
	    ArrayList<FilterConditionTimeBasedItemsTObject> array = new ArrayList<FilterConditionTimeBasedItemsTObject>();

        try
        {
            st = con.prepareStatement(query);
            
           	setUtilDate(st, 1, new Date());
           	st.setShort(2, AmsConstants.STATE_WAITING);
           	
           	st.setShort(3, AmsConstants.STATE_CONFIRMED);
           	st.setShort(4, AmsConstants.TIMEBEHAVIOR_CONFIRMED_THEN_ALARM);
           	
            rs = st.executeQuery();

            while(rs.next()) 
            	array.add(new FilterConditionTimeBasedItemsTObject(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getString(4),rs.getShort(5), getUtilDate(rs,6), getUtilDate(rs,7), rs.getShort(8), rs.getInt(9)));
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
    
	public static int updateState(Connection con, int itemID, short oldState, short newState) throws SQLException 
	{
		final String query = "UPDATE AMS_FilterCond_TimeBasedItems SET sState=? WHERE iItemID=? AND sState=?";
		
		PreparedStatement st = null;

		try
		{
			st = con.prepareStatement(query);			
			st.setShort(1, newState);			
			st.setInt(2, itemID);
			st.setShort(3, oldState);			
			return st.executeUpdate();			
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
	
	public static void insert(Connection con, FilterConditionTimeBasedItemsTObject item) throws SQLException 
	{
		final String query = "INSERT INTO AMS_FilterCond_TimeBasedItems (iItemID,iFilterConditionRef,iFilterRef,cIdentifier,sState,tStartTime,tEndTime,sTimeOutAction,iMessageRef) VALUES (?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement st = null;

		try
		{
			int itemID = getNewID(con, "iItemID", "AMS_FilterCond_TimeBasedItems");			
			
			st = con.prepareStatement(query);
			st.setInt(1, itemID);
			st.setInt(2, item.getFilterConditionRef());
			st.setInt(3, item.getFilterRef());
			st.setString(4, item.getIdentifier());
			st.setShort(5, item.getState());
			setUtilDate(st, 6, item.getStartTime());
			setUtilDate(st, 7, item.getEndTime());
			st.setShort(8, item.getTimeOutAction());
			st.setInt(9, item.getMessageRef());
			st.execute();
			
			item.setItemID(itemID);
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
