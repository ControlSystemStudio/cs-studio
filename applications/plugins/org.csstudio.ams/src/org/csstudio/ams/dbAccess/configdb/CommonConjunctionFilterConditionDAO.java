
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

/**
 * This class handles the database traffic for a conjuncted FilterCondition.
 * @author C1 WPS / KM, MZ
 *
 */
public class CommonConjunctionFilterConditionDAO extends DAO {

	/**
	 * The name of the database table, where the informations of conjuncted FilterCondition is stored.
	 */
	private static final String CONJUNCTION_COMMONS_TABLE_NAME = "AMS_FilterCond_Conj_Common";

	/**
	 * Used by {@link ConfigReplicator} to copy from 'syn' masterDb (Oracle) to
	 * localDB (Derby).
	 */
	public static void copy(Connection masterDB, Connection localDB)
			throws SQLException {
		copy(masterDB, localDB, DB_BACKUP_SUFFIX);
	}

	public static void copy(Connection masterDB, Connection localDB, String masterDbSuffix)
			throws SQLException {
		copyCommonConjunctionFilterCondition(masterDB, localDB, masterDbSuffix, "");
	}
	
	/**
	 * Used by {@link ConfigReplicator} to copy from db (currentDB) to 'syn' db
	 * (currentDB).
	 */
	public static void backup(Connection db) throws SQLException {
		copyCommonConjunctionFilterCondition(db, db, "", DB_BACKUP_SUFFIX);
	}

	private static void copyCommonConjunctionFilterCondition(Connection masterDB,
			Connection targetDB, String strMaster, String strTarget)
			throws SQLException {
		final String query = "SELECT iFilterConditionRef"
				+ ",iFirstFilterConditionRef,iSecondFilterConditionRef"
				+ " FROM " + CONJUNCTION_COMMONS_TABLE_NAME + strMaster;
		ResultSet rs = null;
		PreparedStatement st = null;
		PreparedStatementHolder psth = null;

		try {
			psth = new PreparedStatementHolder();
			st = masterDB.prepareStatement(query);
			rs = st.executeQuery();

			while (rs.next()) {
				CommonConjunctionFilterConditionTObject filterConfiguration = new CommonConjunctionFilterConditionTObject(
						rs.getInt("iFilterConditionRef"), 
						rs.getInt("iFirstFilterConditionRef"), 
						rs.getInt("iSecondFilterConditionRef"));
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
					insertIntoTargetDatabase(null, strTarget, psth, null);
				}
			} catch (SQLException ex) {
				Log.log(Log.WARN, ex);
			}
		}
	}

	private static void insertIntoTargetDatabase(Connection targetDB,
			String strTarget, PreparedStatementHolder psth,
			CommonConjunctionFilterConditionTObject filterConfiguration)
			throws SQLException {
		final String query = "INSERT INTO "
				+ CONJUNCTION_COMMONS_TABLE_NAME
				+ strTarget
				+ " (iFilterConditionRef"
				+ ",iFirstFilterConditionRef,iSecondFilterConditionRef) VALUES (?,?,?)";

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

			int ownFilterConditionReference = filterConfiguration
					.getOwnFilterConditionReference();
			psth.pst.setInt(1, ownFilterConditionReference);
			int firstFilterConditionReference = filterConfiguration
					.getFirstFilterConditionReference();
			psth.pst.setInt(2, firstFilterConditionReference);
			int secondFilterConditionReference = filterConfiguration
					.getSecondFilterConditionReference();
			psth.pst.setInt(3, secondFilterConditionReference);

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
	 * @param filterConditionRef the filter condition reference
	 * @return the corresponding {@link FilterConditionProcessVariableTObject},
	 *         may null.
	 * @throws SQLException
	 *             if query fails
	 */
	public static CommonConjunctionFilterConditionTObject select(
			Connection con, int filterConditionRef) throws SQLException {
		final String query = "SELECT iFilterConditionRef"
				+ ",iFirstFilterConditionRef,iSecondFilterConditionRef"
				+ " FROM " + CONJUNCTION_COMMONS_TABLE_NAME
				+ " WHERE iFilterConditionRef = ?";

		ResultSet rs = null;
		PreparedStatement st = null;
		CommonConjunctionFilterConditionTObject result = null;

		try {
			st = con.prepareStatement(query);
			st.setInt(1, filterConditionRef);
			rs = st.executeQuery();

			if (rs.next()) {
				int firstRef = rs.getShort("iFirstFilterConditionRef");
				int secondRef = rs.getShort("iSecondFilterConditionRef");
				result = new CommonConjunctionFilterConditionTObject(
						filterConditionRef, firstRef, secondRef);

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
			CommonConjunctionFilterConditionTObject filterConfiguration)
			throws SQLException {
		final String query = "INSERT INTO "
				+ CONJUNCTION_COMMONS_TABLE_NAME
				+ " (iFilterConditionRef"
				+ ",iFirstFilterConditionRef,iSecondFilterConditionRef) VALUES (?,?,?)";

		PreparedStatement st = null;

		try {
			st = con.prepareStatement(query);
			st.setInt(1, filterConfiguration.getOwnFilterConditionReference());
			st.setInt(2, filterConfiguration.getFirstFilterConditionReference());
			st.setInt(3, filterConfiguration.getSecondFilterConditionReference());
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
			CommonConjunctionFilterConditionTObject filterConfiguration)
			throws SQLException {
		final String query = "UPDATE "
				+ CONJUNCTION_COMMONS_TABLE_NAME
				+ " SET iFirstFilterConditionRef=?,iSecondFilterConditionRef=?"
				+ " WHERE iFilterConditionRef=?";

		PreparedStatement st = null;

		try {
			st = conDb.prepareStatement(query);
			st.setInt(1, filterConfiguration.getFirstFilterConditionReference());
			st.setInt(2, filterConfiguration.getSecondFilterConditionReference());
			st.setInt(3, filterConfiguration.getOwnFilterConditionReference());
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
		final String query = "DELETE FROM " + CONJUNCTION_COMMONS_TABLE_NAME
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
