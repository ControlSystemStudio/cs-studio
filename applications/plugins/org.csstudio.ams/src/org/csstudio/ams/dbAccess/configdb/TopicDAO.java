
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
import org.csstudio.ams.dbAccess.PreparedStatementHolder;

public class TopicDAO extends DAO {

	public static void copyTopic(Connection masterDB, Connection localDB)
			throws SQLException {
		copyTopic(masterDB, localDB, DB_BACKUP_SUFFIX);
	}

	public static void copyTopic(Connection masterDB, Connection localDB, String masterDbSuffix)
			throws SQLException {
		copyTopic(masterDB, localDB, masterDbSuffix, "");
	}
	
	public static void backupTopic(Connection masterDB) throws SQLException {
		copyTopic(masterDB, masterDB, "", DB_BACKUP_SUFFIX);
	}
	
	private static void copyTopic(Connection masterDB, Connection targetDB,
			String strMaster, String strTarget) throws SQLException {
		final String query = "SELECT iTopicID, iGroupRef, cTopicName, cName, cDescription FROM AMS_Topic"
				+ strMaster;
		ResultSet rs = null;
		PreparedStatement st = null;
		PreparedStatementHolder psth = null;

		try {
			psth = new PreparedStatementHolder();
			st = masterDB.prepareStatement(query);
			rs = st.executeQuery();

			while (rs.next()) {
				TopicTObject tObj = new TopicTObject(rs.getInt("iTopicId"), rs
						.getString("cTopicName"), rs.getString("cName"), rs
						.getInt("iGroupRef"), rs.getString("cDescription"));
				preparedInsertTopic(targetDB, strTarget, psth, tObj);
			}
		} catch (SQLException ex) {
			Log.log(Log.FATAL, "Sql-Query failed: " + query, ex);
			throw ex;
		} finally {
			close(st, rs);

			try {
				if (psth.pst != null) {
					psth.bMode = PreparedStatementHolder.MODE_CLOSE;
					preparedInsertTopic(null, strTarget, psth, null);
				}
			} catch (SQLException ex) {
				Log.log(Log.WARN, ex);
			}
		}
	}

	/**
	 * Stores the given {@link TopicTObject} into the db. If
	 * {@link TopicTObject#getTopicID()} ==
	 * {@link TopicTObject#INITIAL_NON_DB_KEY} a new row will be created.
	 * 
	 * @param con
	 *            the db connection, not null.
	 * @return the list of all topic-ids, not null.
	 * @throws SQLException
	 *             if a db-access error occurs
	 * 
	 * @author Kai Meyer, Matthias Zeimer
	 */
	public static void save(Connection con, TopicTObject topic)
			throws SQLException {
		if (topic.getTopicID() == TopicTObject.INITIAL_NON_DB_KEY) {
			insert(con, topic);
		} else {
			update(con, topic);
		}
	}

	/**
	 * Insert a new topic into the db.
	 */
	private static void insert(Connection con, TopicTObject topic)
			throws SQLException {
		final String query = "INSERT INTO AMS_Topic (iTopicID,iGroupRef,cTopicName,cName,cDescription) "
				+ "VALUES (?,?,?,?,?)";

		PreparedStatement st = null;

		try {
			st = con.prepareStatement(query);
			int topicID = getNewID(con, "iTopicID", "AMS_Topic");

			st.setInt(1, topicID);
			st.setInt(2, topic.getGroupRef());
			st.setString(3, topic.getTopicName());
			st.setString(4, topic.getHumanReadableName());
			st.setString(5, topic.getDescription());

			st.executeUpdate();
			topic.setTopicID(topicID);
		} catch (SQLException ex) {
			Log.log(Log.FATAL, "Sql-Query failed: " + query, ex);
			ex.fillInStackTrace();
			throw ex;
		} finally {
			close(st, null);
		}
	}

	private static void preparedInsertTopic(Connection targetDB,
			String strTarget, PreparedStatementHolder psth, TopicTObject tObj)
			throws SQLException {
		final String query = "INSERT INTO AMS_Topic"
				+ strTarget
				+ " (iTopicID,cTopicName,cName,iGroupRef,cDescription) VALUES (?,?,?,?,?)";

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

			psth.pst.setInt(1, tObj.getTopicID());
			psth.pst.setString(2, tObj.getTopicName());
			psth.pst.setString(3, tObj.getHumanReadableName());
			psth.pst.setInt(4, tObj.getGroupRef());
			psth.pst.setString(5, tObj.getDescription());

			psth.pst.executeUpdate();
		} catch (SQLException ex) {
			Log.log(Log.FATAL, "Sql-Query failed: " + query, ex);
			ex.fillInStackTrace();
			throw ex;
		}
	}

	public static void remove(Connection con, int topicID) throws SQLException {
		remove(con, "", topicID, false);
	}

	private static void remove(Connection con, String strMasterSuffix,
			int topicID, boolean isComplete) throws SQLException {
		final String query = "DELETE FROM AMS_Topic" + strMasterSuffix
				+ (isComplete ? "" : " WHERE iTopicID = ?");
		PreparedStatement st = null;

		try {
			st = con.prepareStatement(query);
			if (!isComplete) {
				st.setInt(1, topicID);
			}
			st.executeUpdate();
		} catch (SQLException ex) {
			Log.log(Log.FATAL, ex);
			throw ex;
		} finally {
			close(st, null);
		}
	}

	public static void removeAll(Connection con) throws SQLException {
		remove(con, "", -1, true);
	}

	public static void removeAllBackupFromMasterDB(Connection masterDB)
			throws SQLException {
		remove(masterDB, DB_BACKUP_SUFFIX, -1, true);
	}

	public static TopicTObject select(Connection con, int topicID)
			throws SQLException {
		final String query = "SELECT iTopicID, iGroupRef, cTopicName, cName, cDescription FROM AMS_Topic"
		                     + " WHERE iTopicID = ?";

		ResultSet rs = null;
		PreparedStatement st = null;
		TopicTObject topicObj = null;

		try {
			st = con.prepareStatement(query);
			st.setInt(1, topicID);
			rs = st.executeQuery();

			if (rs.next())
				topicObj = new TopicTObject(rs.getInt("iTopicId"), rs
						.getString("cTopicName"), rs.getString("cName"), rs
						.getInt("iGroupRef"), rs.getString("cDescription"));

			return topicObj;
		} catch (SQLException ex) {
			Log.log(Log.FATAL, "Sql-Query failed: " + query, ex);
			throw ex;
		} finally {
			close(st, rs);
		}
	}

	/**
	 * Loads the keys of all Topics.
	 * 
	 * @param con
	 *            the db connection, not null.
	 * @return the list of all topic-ids, not null.
	 * @throws SQLException
	 *             if a db-access error occurs
	 * 
	 * @author Kai Meyer, Matthias Zeimer
	 */
	public static List<TopicKey> selectKeyList(Connection con)
			throws SQLException {
		assert con != null : "Vorbedingung verletzt: con != null";

		final String query = "SELECT iTopicID, iGroupRef, cName FROM AMS_Topic ORDER BY cName";

		final List<TopicKey> result = new ArrayList<TopicKey>();

		ResultSet rs = null;
		PreparedStatement st = null;

		try {
			st = con.prepareStatement(query);
			rs = st.executeQuery();

			while (rs.next()) {
				result.add(new TopicKey(rs.getInt("iTopicID"), rs
						.getString("cName"), rs.getInt("iGroupRef")));
			}
		} catch (SQLException ex) {
			Log.log(Log.FATAL, "Sql-Query failed: " + query, ex);
			ex.fillInStackTrace();
			throw ex;
		} finally {
			close(st, rs);
		}

		assert result != null : "Nachbedingung verletzt: $result != null";
		return result;
	}

	/**
	 * Updates the db-data-set of given {@link TopicTObject}.
	 */
	private static void update(Connection con, TopicTObject topic)
			throws SQLException {
		final String query = "UPDATE AMS_Topic SET iGroupRef=?,cTopicName=?,cName=?,"
				+ "cDescription=? " + "WHERE iTopicID = ?";

		PreparedStatement st = null;

		try {
			st = con.prepareStatement(query);
			st.setInt(1, topic.getGroupRef());
			st.setString(2, topic.getTopicName());
			st.setString(3, topic.getHumanReadableName());
			st.setString(4, topic.getDescription());

			st.setInt(5, topic.getID());

			st.executeUpdate();
		} catch (SQLException ex) {
			Log.log(Log.FATAL, "Sql-Query failed: " + query, ex);
			ex.fillInStackTrace();
			throw ex;
		} finally {
			close(st, null);
		}
	}
}
