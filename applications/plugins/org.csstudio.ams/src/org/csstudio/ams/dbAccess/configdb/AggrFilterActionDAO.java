
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

public class AggrFilterActionDAO extends DAO 
{
	public static List<FilterActionTObject> select(Connection con, int filterID) throws SQLException
	{
		final String query =
	       "SELECT FA.iFilterActionID, FA.iFilterActionTypeRef, FA.iReceiverRef, FA.cMessage, FFA.iPos" 
    	+ " FROM AMS_Filter_FilterAction FFA, AMS_FilterAction FA"
    	+ " WHERE FFA.iFilterActionRef = FA.iFilterActionID"
    	+ " AND FFA.iFilterRef = ?"
    	+ " ORDER BY FFA.iPos ASC";

    	ResultSet rs = null;
		PreparedStatement st = null;
		ArrayList<FilterActionTObject> fActions = new ArrayList<FilterActionTObject>();
		
		try
		{
			st = con.prepareStatement(query);
			st.setInt(1, filterID);
			rs = st.executeQuery();
			
			while(rs.next())
				fActions.add(new FilterActionTObject(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getString(4)));
			
		}
		catch(SQLException ex)
		{
			Log.log(Log.FATAL, ex);
			throw ex;
		}
		finally
		{
			close(st,rs);
		}
		return fActions;
	}
	
	public static void remove(Connection con, int filterID) throws SQLException
	{
		final String query =
		      "DELETE FROM  AMS_FilterAction WHERE iFilterActionID IN " 
	    	+ " (SELECT iFilterActionRef FROM AMS_Filter_FilterAction "
	    	+ " WHERE iFilterRef = ?"
	    	+ ") ";

		PreparedStatement st = null;
			
		try
		{
			st = con.prepareStatement(query);
			st.setInt(1, filterID);
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
}