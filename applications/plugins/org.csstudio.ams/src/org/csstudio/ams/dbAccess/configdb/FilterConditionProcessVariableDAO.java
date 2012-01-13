
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
import org.csstudio.ams.configReplicator.ConfigReplicator;
import org.csstudio.ams.dbAccess.DAO;
import org.csstudio.ams.dbAccess.PreparedStatementHolder;
import org.csstudio.ams.filter.FilterConditionProcessVariable;
import org.csstudio.ams.filter.FilterConditionProcessVariable.Operator;
import org.csstudio.ams.filter.FilterConditionProcessVariable.SuggestedProcessVariableType;

/**
 * DAO to access filter configuration of a PV based filter.
 * 
 * @see FilterConditionProcessVariable
 */
public class FilterConditionProcessVariableDAO extends DAO {

	/**
	 * Used by {@link ConfigReplicator} to copy from 'syn' masterDb (Oracle) to localDB (Derby).
	 */
	public static void copy(Connection masterDB,
			Connection localDB) throws SQLException {
		copy(masterDB, localDB, DB_BACKUP_SUFFIX);
	}

	public static void copy(Connection masterDB,
			Connection localDB, String masterDbSuffix) throws SQLException {
		copyFilterConditionString(masterDB, localDB, masterDbSuffix, "");
	}
	
	/**
	 * Used by {@link ConfigReplicator} to copy from db (currentDB) to 'syn' db (currentDB).
	 */
	public static void backup(Connection db)
			throws SQLException {
		copyFilterConditionString(db, db, "", DB_BACKUP_SUFFIX);
	}

	private static void copyFilterConditionString(Connection masterDB,
			Connection targetDB, String strMaster, String strTarget)
			throws SQLException {
		final String query = "SELECT iFilterConditionRef"
				+ ",cPVChannelName,sOperatorId,sSuggestedPvTypeId,cCompValue"
				+ " FROM AMS_FilterCondition_PV" + strMaster;
		// final String query = "SELECT
		// iFilterConditionRef,cKeyValue,sOperator,cCompValue FROM
		// AMS_FilterCondition_String" + strMaster;
		ResultSet rs = null;
		PreparedStatement st = null;
		PreparedStatementHolder psth = null;

		try {
			psth = new PreparedStatementHolder();
			st = masterDB.prepareStatement(query);
			rs = st.executeQuery();

			while (rs.next()) {
				short typeID = rs.getShort("sSuggestedPvTypeId");
				String rawCompareValue = rs.getString("cCompValue");
				SuggestedProcessVariableType suggestedType = FilterConditionProcessVariable.SuggestedProcessVariableType
						.findOperatorOfDBId(typeID);
				Object compareValue = suggestedType
						.parseDatabaseValue(rawCompareValue);
				FilterConditionProcessVariableTObject filterConfiguration = new FilterConditionProcessVariableTObject(
						rs.getInt("iFilterConditionRef"),
						rs.getString("cPVChannelName"),
						Operator.findOperatorOfDBId(rs.getShort("sOperatorId")),
						suggestedType, compareValue);
				insertIntoTargetDatabase(targetDB, strTarget, psth,
						filterConfiguration);
			}
		} catch (SQLException ex) {
			Log.log(Log.FATAL, "Sql-Query failed: " + query, ex);
			throw ex;
		} finally {
			close(st, rs);

			try {
				if (psth.pst != null) {
					psth.bMode = PreparedStatementHolder.MODE_CLOSE;
					insertIntoTargetDatabase(null, strTarget, psth,
							null);
				}
			} catch (SQLException ex) {
				Log.log(Log.WARN, ex);
			}
		}
	}

	private static void insertIntoTargetDatabase(
			Connection targetDB, String strTarget,
			PreparedStatementHolder psth,
			FilterConditionProcessVariableTObject filterConfiguration) throws SQLException {
		final String query = "INSERT INTO AMS_FilterCondition_PV"
				+ strTarget
				+ " (iFilterConditionRef"
				+ ",cPVChannelName,sOperatorId,sSuggestedPvTypeId,cCompValue) VALUES (?,?,?,?,?)";
		// final String query = "INSERT INTO AMS_FilterCondition_String"
		// + strTarget
		// + " (iFilterConditionRef,cKeyValue,sOperator,cCompValue) VALUES
		// (?,?,?,?)";

		if (psth.bMode == PreparedStatementHolder.MODE_CLOSE) {
			try {
				psth.pst.close();
			} catch (SQLException ex) {
				throw ex;
			}
			return;
		}

		try {
			if (psth.bMode == PreparedStatementHolder.MODE_INIT) {
				psth.pst = targetDB.prepareStatement(query);
				psth.bMode = PreparedStatementHolder.MODE_EXEC;
			}

			psth.pst.setInt(1, filterConfiguration.getFilterConditionRef());
			psth.pst.setString(2, filterConfiguration
							.getProcessVariableChannelName());
			psth.pst.setShort(3, filterConfiguration.getOperator().asDatabaseId());
			psth.pst.setShort(4, filterConfiguration.getSuggestedType()
					.asDatabaseId());
			psth.pst.setString(5, filterConfiguration.getSuggestedType().toDbString(
					filterConfiguration.getCompValue()));
			
//			psth.pst.setInt(1, fcsObj.getFilterConditionRef());
//			psth.pst.setString(2, fcsObj.getKeyValue());
//			psth.pst.setShort(3, fcsObj.getOperator());
//			psth.pst.setString(4, fcsObj.getCompValue());

			psth.pst.executeUpdate();
		} catch (SQLException ex) {
			Log.log(Log.FATAL, "Sql-Update failed: " + query, ex);
			throw ex;
		}
	}

	/**
	 * Loads the configuration of a PV based filter.
	 * 
	 * @param con
	 *            The database connection, not null.
	 * @param filterConditionRef
	 *            TODO Check: The filter id or the reference to a concrete
	 *            filter ??????????
	 * @return the corresponding {@link FilterConditionProcessVariableTObject},
	 *         may null.
	 * @throws SQLException
	 *             if query fails
	 */
	public static FilterConditionProcessVariableTObject select(Connection con,
			int filterConditionRef) throws SQLException {
		final String query = "SELECT iFilterConditionRef"
				+ ",cPVChannelName,sOperatorId,sSuggestedPvTypeId,cCompValue"
				+ " FROM AMS_FilterCondition_PV"
				+ " WHERE iFilterConditionRef = ?";

		ResultSet rs = null;
		PreparedStatement st = null;
		FilterConditionProcessVariableTObject result = null;

		try {
			st = con.prepareStatement(query);
			st.setInt(1, filterConditionRef);
			rs = st.executeQuery();

			if (rs.next()) {
				short operatorID = rs.getShort("sOperatorId");
				short typeID = rs.getShort("sSuggestedPvTypeId");
				String rawCompareValue = rs.getString("cCompValue");
				SuggestedProcessVariableType suggestedType = FilterConditionProcessVariable.SuggestedProcessVariableType
						.findOperatorOfDBId(typeID);
				Object compareValue = suggestedType
						.parseDatabaseValue(rawCompareValue);
				result = new FilterConditionProcessVariableTObject(
						filterConditionRef, rs.getString("cPVChannelName"),
						FilterConditionProcessVariable.Operator
								.findOperatorOfDBId(operatorID), suggestedType,
						compareValue);

			}
		} catch (SQLException ex) {
			Log.log(Log.FATAL, "Sql-Query failed: " + query, ex);
			ex.fillInStackTrace();
			throw ex;
		} finally {
			close(st, rs);
		}
		return result;
	}

	/**
	 * Insert a new record into the database.
	 * 
	 * @param con
	 *            The connection to the database to be insert to.
	 * @param filterConfiguration
	 *            The configuration to be stored; must hold an valid
	 *            filter-Condition-id.
	 * @throws SQLException
	 *             If any storage error occurs.
	 */
	public static void insert(Connection con,
			FilterConditionProcessVariableTObject filterConfiguration)
			throws SQLException {
		final String query = "INSERT INTO AMS_FilterCondition_PV (iFilterConditionRef"
				+ ",cPVChannelName,sOperatorId,sSuggestedPvTypeId,cCompValue) VALUES (?,?,?,?,?)";

		PreparedStatement st = null;

		try {
			st = con.prepareStatement(query);
			st.setInt(1, filterConfiguration.getFilterConditionRef());
			st
					.setString(2, filterConfiguration
							.getProcessVariableChannelName());
			st.setShort(3, filterConfiguration.getOperator().asDatabaseId());
			st.setShort(4, filterConfiguration.getSuggestedType()
					.asDatabaseId());
			st.setString(5, filterConfiguration.getSuggestedType().toDbString(
					filterConfiguration.getCompValue()));
			st.execute();
		} catch (SQLException ex) {
			Log.log(Log.FATAL, "Sql-insert failed: " + query, ex);
			throw ex;
		} finally {
			close(st, null);
		}
	}

	/**
	 * Updates the database with given dataset.
	 * 
	 * @param conDb
	 *            The database connection to be write on.
	 * @param filterConfiguration
	 *            The new configuration of filter specified by contained filter
	 *            id.
	 * @throws SQLException
	 *             If a update exception occours.
	 */
	public static void update(Connection conDb,
			FilterConditionProcessVariableTObject filterConfiguration)
			throws SQLException {
		final String query = "UPDATE AMS_FilterCondition_PV SET cPVChannelName=?,sOperatorId=?"
				+ ",sSuggestedPvTypeId=?,cCompValue=? WHERE iFilterConditionRef=?";

		PreparedStatement st = null;

		try {
			st = conDb.prepareStatement(query);
			st
					.setString(1, filterConfiguration
							.getProcessVariableChannelName());
			st.setShort(2, filterConfiguration.getOperator().asDatabaseId());
			short asDatabaseId = filterConfiguration.getSuggestedType()
					.asDatabaseId();
			st.setShort(3, asDatabaseId);
			st.setString(4, filterConfiguration.getSuggestedType().toDbString(
					filterConfiguration.getCompValue()));
			st.setInt(5, filterConfiguration.getFilterConditionRef());
			st.execute();
		} catch (SQLException ex) {
			Log.log(Log.FATAL, "Sql-update failed: " + query, ex);
			throw ex;
		} finally {
			close(st, null);
		}

	}

	/**
	 * Used by {@link ConfigReplicator} to clear the 'syn' target database.
	 */
	public static void removeAllFromBackup(Connection masterDB)
			throws SQLException {
		remove(masterDB, DB_BACKUP_SUFFIX, -1, true);
	}

	/**
	 * Used by {@link ConfigReplicator} to clear the target database.
	 */
	public static void removeAll(Connection con) throws SQLException {
		remove(con, "", -1, true);
	}

	public static void remove(Connection con, int filterConditionRef)
			throws SQLException {
		remove(con, "", filterConditionRef, false);
	}

	private static void remove(Connection con, String strMasterSuffix,
			int filterConditionRef, boolean isComplete) throws SQLException {
		final String query = "DELETE FROM AMS_FilterCondition_PV"
				+ strMasterSuffix
				+ (isComplete ? "" : " WHERE iFilterConditionRef = ?");
		PreparedStatement st = null;

		try {
			st = con.prepareStatement(query);
			if (!isComplete) {
				st.setInt(1, filterConditionRef);
			}
			st.executeUpdate();
		} catch (SQLException ex) {
			Log.log(Log.FATAL, ex);
			throw ex;
		} finally {
			close(st, null);
		}
	}

}
