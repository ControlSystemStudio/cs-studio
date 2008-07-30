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
package org.csstudio.alarm.dbaccess;

import oracle.jdbc.OracleResultSet;
import oracle.jdbc.OracleStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import org.csstudio.alarm.dbaccess.archivedb.ILogMessageArchiveAccess;
import org.csstudio.platform.logging.CentralLogger;

/**
 * Main Class for DB query. Builds accordingly to the current FilterSettings the
 * SQL Statement, assembles the log messages from the DB result and returns the
 * messages in an ArrayList.
 * 
 * @author jhatje
 * @author $Author$
 * @version $Revision$
 * @since 22.05.2008
 */
public final class ArchiveDBAccess implements ILogMessageArchiveAccess {

	private Connection _databaseConnection;

	/** Singleton instance */
	private static ArchiveDBAccess _archiveDBAccess;

	private ArchiveDBAccess() {
	}

	/**
	 * Get singleton instance.
	 * 
	 * @return instance
	 */
	public static ArchiveDBAccess getInstance() {
		if (_archiveDBAccess == null) {
			_archiveDBAccess = new ArchiveDBAccess();
		}
		return _archiveDBAccess;
	}

	/**
	 * Get messages from DB for a time period without FilterSettings.
	 * 
	 * @return ArrayList of messages in a HashMap
	 */
	public ArrayList<HashMap<String, String>> getLogMessages(Calendar from,
			Calendar to, int maxAnserSize) {
		ArrayList<ResultSet> result = queryDatabase(null, from, to);
		ArrayList<HashMap<String, String>> ergebniss = processResult(result);
		return ergebniss;
	}

	/**
	 * Get messages from DB for a time period and filter conditions.
	 * 
	 * @return ArrayList of messages in a HashMap
	 */
	public ArrayList<HashMap<String, String>> getLogMessages(Calendar from,
			Calendar to, String filter, ArrayList<FilterItem> filterSetting,
			int maxAnserSize) {
		ArrayList<ResultSet> result = queryDatabase(filterSetting, from, to);
		ArrayList<HashMap<String, String>> ergebniss = processResult(result);
		return ergebniss;
	}

	/**
	 * Assemble 'real' log messages from DBResult. The DB query returns a list
	 * of message_id with property and value. To get a complete log message you
	 * have to put all property-value pairs with the same message_id in one log
	 * message together.
	 * 
	 * @param result
	 *            List of Result Sets (we get more than one result set if there
	 *            is an OR relation, because the SQL Statement is designed only
	 *            for AND relations)
	 * @return
	 */
	private ArrayList<HashMap<String, String>> processResult(
			ArrayList<ResultSet> result) {

		// List of 'real' log messages (we want to see in the table)
		ArrayList<HashMap<String, String>> messageResultList = new ArrayList<HashMap<String, String>>();
		try {
			// the current 'real' log message
			HashMap<String, String> message = null;
			// run through all result sets (for each OR relation in the
			// FilterSetting
			// we get one more resultSet)
			for (ResultSet resultSet : result) {
				// identifier for the current message. initialized with a not
				// existing message id
				int currentMessageID = -1;
				while (resultSet.next()) {
					if (currentMessageID == resultSet.getInt(1)) {
						// current row has the same message_id->
						// it belongs to the current message
						if (message != null) {
							message.put(resultSet.getString(2), resultSet
									.getString(3));
						}
					} else {
						// this result row belongs to a new message
						// if there is already a previous message put it to the
						// messageResultList.
						if (message != null) {
							messageResultList.add(message);
						}
						// update current message id and
						// put the first property value pair in the new
						// message
						currentMessageID = Integer.parseInt(resultSet
								.getString(1));
						message = new HashMap<String, String>();
						message.put(resultSet.getString(2), resultSet
								.getString(3));
					}
				}
				// put the last message to the messageResultList
				// (the message should be not null anyway)
				if (message != null) {
					messageResultList.add(message);
				}
			}
		} catch (SQLException e) {
			CentralLogger.getInstance().error(this, e.getMessage());
		}
		return messageResultList;
	}

	/**
	 * Select the appropriate SQL statement depending on the FilterSetting, set
	 * the parameters for the prepared statement and executes the DB query.
	 * 
	 * @param filter
	 * @param from
	 * @param to
	 * @return
	 */
	private ArrayList<ResultSet> queryDatabase(ArrayList<FilterItem> filter,
			Calendar from, Calendar to) {

		// list of result sets (for each OR relation in the FilterSetting
		// we get one more resultSet)
		ArrayList<ResultSet> resultSetList = new ArrayList<ResultSet>();

		try {
			_databaseConnection = DBConnection.getInstance().getConnection();
			PreparedStatement getMessages = null;
			// the filter setting has to be separated at the OR relation
			// because the SQL statement is designed only for AND.
			ArrayList<ArrayList<FilterItem>> separatedFilterSettings = separateFilterSettings(filter);
			ResultSet result = null;
			for (ArrayList<FilterItem> currentFilterSettingList : separatedFilterSettings) {
				switch (currentFilterSettingList.size()) {
				case 0:
					getMessages = _databaseConnection
							.prepareStatement(SQLStatements.ARCHIVE_SIMPLE);
					break;
				case 1:
					getMessages = _databaseConnection
							.prepareStatement(SQLStatements.ARCHIVE_MESSAGES_1);
					break;
				case 2:
					getMessages = _databaseConnection
							.prepareStatement(SQLStatements.ARCHIVE_MESSAGES_2);
					break;
				case 3:
					getMessages = _databaseConnection
							.prepareStatement(SQLStatements.ARCHIVE_MESSAGES_3);
					break;
				default:
					break;
				}
				int parameterIndex = 0;
				// set the preparedStatement parameters
				if (getMessages != null) {
					for (FilterItem filterSetting : currentFilterSettingList) {
						parameterIndex++;
						getMessages.setString(parameterIndex, filterSetting
								.get_property());
						parameterIndex++;
						getMessages.setString(parameterIndex, filterSetting
								.get_value());
						CentralLogger.getInstance().debug(
								this,
								"DB query, filter Property: "
										+ filterSetting.get_property()
										+ "  Value: "
										+ filterSetting.get_value()
										+ "  Realtion: "
										+ filterSetting.get_relation());
					}
				}
				String fromDate = buildDateString(from);
				String toDate = buildDateString(to);
				parameterIndex++;
				getMessages.setString(parameterIndex, fromDate);
				parameterIndex++;
				getMessages.setString(parameterIndex, toDate);
				CentralLogger.getInstance().debug(
						this,
						"DB query, start time: " + fromDate + "  end time: "
								+ toDate);

				// getMessages.setString(1, "NAME");
				// getMessages.setString(2, "CMTBSTP4R21_temp");
				// getMessages.setString(3, "2008-05-21 15:6:49");
				// getMessages.setString(4, "2008-05-22 15:6:49");
				result = getMessages.executeQuery();
				resultSetList.add(result);
			}
		} catch (SQLException e) {
			CentralLogger.getInstance().error(this, e.getMessage());
		}

		return resultSetList;
	}

	/**
	 * Create from Calendar a date of type String in the format of the SQL
	 * statement.
	 * 
	 * @param date
	 * @return
	 */
	private String buildDateString(Calendar date) {
		return date.get(GregorianCalendar.YEAR) + "-"
				+ (date.get(GregorianCalendar.MONTH) + 1) + "-"
				+ date.get(GregorianCalendar.DAY_OF_MONTH) + " "
				+ date.get(GregorianCalendar.HOUR_OF_DAY) + ":"
				+ date.get(GregorianCalendar.MINUTE) + ":"
				+ date.get(GregorianCalendar.SECOND);
	}

	/**
	 * The list of Filter settings consists of property value pairs
	 * associated with AND or OR. Because the sql statement is created only for
	 * the AND parts (for a better performance the or will be merged in java)
	 * this method split the FilterSetting list on the OR association and
	 * returns a list of lists of filter settings associated only with AND.
	 * 
	 * @param filter
	 * @return
	 */
	private ArrayList<ArrayList<FilterItem>> separateFilterSettings(
			ArrayList<FilterItem> filter) {

		// list of list of AND associated Filter settings we want to return.
		ArrayList<ArrayList<FilterItem>> separatedFilterSettings = new ArrayList<ArrayList<FilterItem>>();
		// list of filterSettings associated with AND to put in
		// separatedFilterSettings.
		ArrayList<FilterItem> filterSettingsAndAssociated = null;
		// if filter is null (user searches only for time period) set one
		// empty list of filter settings.
		if (filter == null) {
			filterSettingsAndAssociated = new ArrayList<FilterItem>();
			separatedFilterSettings.add(filterSettingsAndAssociated);
			return separatedFilterSettings;
		}
		String association = "BEGIN";
		for (FilterItem setting : filter) {
			if (association.equalsIgnoreCase("AND")) {
				if (filterSettingsAndAssociated != null) {
					association = setting.get_relation();
					filterSettingsAndAssociated.add(setting);
				} else {
					CentralLogger.getInstance().error(this,
							"invalid filter configuration");
				}
				continue;
			}
			if (association.equalsIgnoreCase("OR")) {
				separatedFilterSettings.add(filterSettingsAndAssociated);
				association = setting.get_relation();
				filterSettingsAndAssociated = new ArrayList<FilterItem>();
				filterSettingsAndAssociated.add(setting);
				continue;
			}
			if (association.equalsIgnoreCase("BEGIN")) {
				filterSettingsAndAssociated = new ArrayList<FilterItem>();
				filterSettingsAndAssociated.add(setting);
				association = setting.get_relation();
				continue;
			}

		}
		separatedFilterSettings.add(filterSettingsAndAssociated);

		return separatedFilterSettings;
	}

	/**
	 * @return the Answer [0] is the Id and [1] is the Type.
	 * 
	 */
	public String[][] getMsgTypes() {
		String sql = "select * from msg_property_type mpt order by id";
		try {
			_databaseConnection = DBConnection.getInstance().getConnection();
			OracleStatement stmt = (OracleStatement) _databaseConnection
					.createStatement();

			stmt.execute(sql);

			OracleResultSet rset = (OracleResultSet) stmt.getResultSet();
			ArrayList<String[]> ans = new ArrayList<String[]>();
			while (rset.next()) {
				String id = rset.getString("ID");
				String name = rset.getString(2);
				ans.add(new String[] { id, name });
			}
			return ans.toArray(new String[0][2]);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void close() {
		try {
			_databaseConnection.close();
		} catch (SQLException e) {
			CentralLogger.getInstance().warn(this,
					"Can not close DB connection", e);
		}
	}
}
