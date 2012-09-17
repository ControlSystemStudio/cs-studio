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

import org.csstudio.ams.Log;
import org.csstudio.ams.dbAccess.DAO;

public abstract class FlagDAO extends DAO {
	public static short selectFlag(Connection conDB, String flagName)
			throws SQLException {
		final String query = "SELECT sFlagValue FROM AMS_Flag WHERE cFlagName=?";
		short sRet = 0;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			pst = conDB.prepareStatement(query);
			pst.setString(1, flagName);
			rs = pst.executeQuery();

			if (rs.next())
				sRet = rs.getShort(1);

			return sRet;
		} catch (SQLException ex) {
			Log.log(Log.FATAL, "Sql-Query failed: " + query, ex);
			throw ex;
		} finally {
			close(pst, rs);
		}
	}

	public static boolean bUpdateFlag(Connection masterDB, String flagName,
			short sOld, short sNew) throws SQLException {
		final String query = "UPDATE AMS_Flag SET sFlagValue=? WHERE cFlagName=? AND sFlagValue=?";
		PreparedStatement pst = null;

		try {
			pst = masterDB.prepareStatement(query);

			pst.setShort(1, sNew);
			pst.setString(2, flagName);
			pst.setShort(3, sOld);

			int iCount = pst.executeUpdate();
			Log.log(Log.INFO, "update sFlagValue=" + flagName + " from=" + sOld
					+ " to= " + sNew + " updCnt=" + iCount);

			return iCount > 0;
		} catch (SQLException ex) {
			Log.log(Log.FATAL, "Sql-Query failed: " + query, ex);
			throw ex;
		} finally {
			close(pst, null);
		}
	}

	public static void copyAllFlagStates(Connection masterDB, Connection targetDB) throws SQLException {
		final String query = "SELECT cFlagName,sFlagValue FROM AMS_Flag";
		ResultSet resultSet = null;
		PreparedStatement statement = null;

		try {
			statement = masterDB.prepareStatement(query);
			resultSet = statement.executeQuery();

			while (resultSet.next()) {
				String cFlagName = resultSet.getString(1);
				short sFlagValue = resultSet.getShort(2);
				short oldFlagValue = selectFlag(targetDB, cFlagName);
				
				bUpdateFlag(targetDB, cFlagName, oldFlagValue, sFlagValue);
			}
		} catch (SQLException ex) {
			Log.log(Log.FATAL, "Sql-Query failed: " + query, ex);
			throw ex;
		} finally {
			close(statement, resultSet);
		}
	}
}