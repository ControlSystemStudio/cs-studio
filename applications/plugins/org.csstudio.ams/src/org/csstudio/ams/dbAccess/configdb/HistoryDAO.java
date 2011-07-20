
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

import org.csstudio.ams.Log;
import org.csstudio.ams.dbAccess.DAO;
import org.csstudio.platform.util.StringUtil;

public abstract class HistoryDAO extends DAO {
	
    private static PreparedStatement pstInsert = null;
	public static final int LEN_TYPE = 16;
	public static final int LEN_MSGHOST = 64;
	public static final int LEN_MSGPROC = 64;
	public static final int LEN_MSGNAME = 64;
	public static final int LEN_MSGEVT = 32;
	public static final int LEN_DESC = 512;
	public static final int LEN_ATYPE = 16;
	public static final int LEN_GNAME = 64;
	public static final int LEN_UNAME = 128;
	public static final int LEN_DTYPE = 16;
	public static final int LEN_DADRESS = 128;

	public synchronized static void insert(final Connection con, final HistoryTObject history) throws SQLException  //synchronized better if one calls it multiple
	{
		final String query = "INSERT INTO AMS_History (tTimeNew,cType,cMsgHost,cMsgProc,cMsgName" +
		               		",cMsgEventTime,cDescription,cActionType,iGroupRef,cGroupName,iReceiverPos,iUserRef" +
		               		",cUserName,cDestType,cDestAdress) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try
		{
			Connection conOld = null;

			try {
				conOld = pstInsert.getConnection();
			}
			catch (final Exception e) {
				conOld = null;
			}

			// Statement wird nur einmal erzeugt, wird automatisch freigegeben wenn Connection geschlossen wird
			if((pstInsert == null) || (conOld == null) || !conOld.equals(con)) {
                pstInsert = con.prepareStatement(query);
            }

			pstInsert.clearParameters();

			setUtilDate(pstInsert, 1, history.getTimeNew());
			pstInsert.setString(2, StringUtil.checkedSubstring(history.getType(), LEN_TYPE));
			pstInsert.setString(3, StringUtil.checkedSubstring(history.getMsgHost(), LEN_MSGHOST));
			pstInsert.setString(4, StringUtil.checkedSubstring(history.getMsgProc(), LEN_MSGPROC));
			pstInsert.setString(5, StringUtil.checkedSubstring(history.getMsgName(), LEN_MSGNAME));

			pstInsert.setString(6, StringUtil.checkedSubstring(history.getMsgEventtime(), LEN_MSGEVT));
			pstInsert.setString(7, StringUtil.checkedSubstring(history.getDescription(), LEN_DESC));
			pstInsert.setString(8, StringUtil.checkedSubstring(history.getActionType(), LEN_ATYPE));
			pstInsert.setInt(9, history.getGroupRef());
			pstInsert.setString(10, StringUtil.checkedSubstring(history.getGroupName(), LEN_GNAME));
			pstInsert.setInt(11, history.getReceiverPos());
			pstInsert.setInt(12, history.getUserRef());

			pstInsert.setString(13, StringUtil.checkedSubstring(history.getUserName(), LEN_UNAME));
			pstInsert.setString(14, StringUtil.checkedSubstring(history.getDestType(), LEN_DTYPE));
			pstInsert.setString(15, StringUtil.checkedSubstring(history.getDestAdress(), LEN_DADRESS));

			pstInsert.execute();
		}
		catch(final SQLException ex)
		{
			Log.log(Log.FATAL, "Sql-Query failed(" + ex.getSQLState()+ "): " + query, ex);
			throw ex;
		}
	}

	private static final String selectString =  "SELECT iHistoryID,tTimeNew,cType,cMsgHost,cMsgProc" +
		",cMsgName,cMsgEventTime,cDescription,cActionType,iGroupRef,cGroupName,iReceiverPos,iUserRef" +
   		",cUserName,cDestType,cDestAdress FROM AMS_History";

	private static final HistoryTObject getObjectFromResultSet(final ResultSet rs) throws SQLException
	{
		return new HistoryTObject(rs.getInt(1),
				getUtilDate(rs, 2),
				rs.getString(3),
				rs.getString(4),
				rs.getString(5),
				rs.getString(6),
				rs.getString(7),
				rs.getString(8),
				rs.getString(9),
				rs.getInt(10),
				rs.getString(11),
				rs.getInt(12),
				rs.getInt(13),
				rs.getString(14),
				rs.getString(15),
				rs.getString(16));
	}

	public static List<HistoryTObject> selectList(final Connection con, int dataSetCount, final int minHistoryId) throws SQLException
	{
		final String query = selectString +  " WHERE iHistoryID > " + minHistoryId + " ORDER BY tTimeNew DESC,iHistoryID DESC";

		ResultSet 					rs = null;
		PreparedStatement 			st = null;
		final ArrayList<HistoryTObject> 	array = new ArrayList<HistoryTObject>();

		try
		{
			st = con.prepareStatement(query);
			rs = st.executeQuery();

			while(rs.next() && (dataSetCount-- > 0)) {
                array.add(getObjectFromResultSet(rs));
            }
			return array;
		}
		catch(final SQLException ex)
		{
			Log.log(Log.FATAL, "Sql-Query failed: " + query, ex);
			throw ex;
		}
		finally
		{
			close(st,rs);
		}
	}

	public static List<HistoryTObject> selectList(final Connection con, final Date periodStart, final Date periodEnd, int dataSetCount) throws SQLException
	{
		String query = selectString;

		if((periodStart != null) && (periodEnd != null)) {
            query += " WHERE tTimeNew BETWEEN ? AND ?";
        } else if(periodStart != null) {
            query += " WHERE tTimeNew>=?";
        } else if(periodEnd != null) {
            query += " WHERE tTimeNew<=?";
        }

		query += " ORDER BY tTimeNew DESC,iHistoryID DESC";

		ResultSet 					rs = null;
		PreparedStatement 			st = null;
		final ArrayList<HistoryTObject> 	array = new ArrayList<HistoryTObject>();

		try
		{
			st = con.prepareStatement(query);

			if((periodStart != null) && (periodEnd != null))
			{
				setUtilDate(st, 1,periodStart);
				setUtilDate(st, 2,periodEnd);
			}
			else if(periodStart != null) {
                setUtilDate(st, 1,periodStart);
            } else if(periodEnd != null) {
                setUtilDate(st, 1,periodEnd);
            }

			rs = st.executeQuery();

			while(rs.next() && (dataSetCount-- > 0)) {
                array.add(getObjectFromResultSet(rs));
            }
			return array;
		}
		catch(final SQLException ex)
		{
			Log.log(Log.FATAL, "Sql-Query failed: " + query, ex);
			throw ex;
		}
		finally
		{
			close(st,rs);
		}
	}

	public static int getLastHistoryID(final Connection con) throws SQLException
	{
		final String query = "SELECT iHistoryID FROM AMS_History ORDER BY tTimeNew DESC,iHistoryID DESC";

		ResultSet 	rs = null;
		PreparedStatement 	st = null;

		try
		{
			st = con.prepareStatement(query);
			rs = st.executeQuery();

			if(rs.next()) {
                return rs.getInt(1);
            }
			return 0;
		}
		catch(final SQLException ex)
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