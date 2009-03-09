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

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import oracle.jdbc.OracleResultSet;
import oracle.jdbc.OracleStatement;

import org.csstudio.alarm.dbaccess.archivedb.FilterItem;
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
public class ArchiveDBAccess implements ILogMessageArchiveAccess {

	private Connection _databaseConnection;

	private int _maxAnswerSize = 5000;

	/** Singleton instance */
	private static ArchiveDBAccess _archiveDBAccess;

	/**
	 * maxSize is true if maxrow in the SQL statement has cut off more messages.
	 */
	private boolean _maxSize = false;

	SQLBuilder _sqlBuilder;

	public boolean is_maxSize() {
		return _maxSize;
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
	 * Get messages from DB for a time period and filter conditions.
	 * 
	 * @return ArrayList of messages in a HashMap
	 */
	public ArrayList<HashMap<String, String>> getLogMessages(Calendar from,
			Calendar to, ArrayList<FilterItem> filterSetting, int maxAnswerSize) {
		CentralLogger.getInstance().debug(this,
				"from time: " + from + ", to time: " + to);
		_maxAnswerSize = maxAnswerSize;
		CentralLogger.getInstance().debug(this, "set maxAnswerSize to " + _maxAnswerSize);
		ArrayList<ResultSet> dbResult = queryDatabase(filterSetting, from, to);
		ArrayList<HashMap<String, String>> messageList = processResult(dbResult);
		return messageList;
	}

	/**
	 * Export log messages from the DB into an excel file.
	 */
	public String exportLogMessages(Calendar from, Calendar to,
			ArrayList<FilterItem> filterSetting, int maxAnswerSize, File path, String[] columnNames) {
		String exportResult = "Export completed";
		_maxAnswerSize = maxAnswerSize;
		ArrayList<ResultSet> dbResult = queryDatabase(filterSetting, from, to);
		ArrayList<HashMap<String, String>> messageList = processResult(dbResult);
		LogMessageExporter exporter = new LogMessageExporter();
		try {
			exporter.exportExcelFile(messageList, path, columnNames);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return exportResult;
	}

	/**
	 * Count messages from DB for a time period and filter conditions that will
	 * be deleted.
	 * 
	 */
	public int countDeleteLogMessages(Calendar from, Calendar to,
			ArrayList<FilterItem> filterSetting) {
		int msgToDelete = countMessagesToDelete(filterSetting, from, to);
		return msgToDelete;
	}

	/**
	 * Delete messages from DB. Because we have to delete the messages from
	 * tables message and message_content we retrieve in the first step all
	 * message_ids and delete them in the next step.
	 * 
	 */
	public String deleteLogMessages(Calendar from, Calendar to,
			ArrayList<FilterItem> settings) {
		String operationResult = "Messages deleted";
		CentralLogger.getInstance().debug(this, "delete messages");
		_maxAnswerSize = -1;
		ArrayList<ResultSet> result = queryDatabase(settings, from, to);
		Set<String> messageIdsToDelete = readMessageIdFromResult(result);
		try {
			deleteFromDB(messageIdsToDelete);
		} catch (SQLException e) {
			operationResult = "DB Exception. Delete operation canceled.";
			CentralLogger.getInstance().error(this, "Delete operation error " + e.getMessage());
		}
		return operationResult;
	}

	private Set<String> readMessageIdFromResult(ArrayList<ResultSet> result) {
		Set<String> messageIds = new HashSet<String>();
		try {
			for (ResultSet resultSet : result) {
				while (resultSet.next()) {
					messageIds.add(resultSet.getString(1));
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return messageIds;
	}

	/**
	 * Counts the real number of all messages that will be deleted with the
	 * current filter settings (in the UI table are not more than 'MAXROWNUM'
	 * messages displayed).
	 * 
	 * @param filterSetting
	 * @param to
	 * @param from
	 * @return
	 */
	public int countMessagesToDelete(ArrayList<FilterItem> filterSetting,
			Calendar from, Calendar to) {

		int msgNumber = 0;
		try {
			_databaseConnection = DBConnection.getInstance().getConnection();
			PreparedStatement getMessages = null;
			// the filter setting has to be separated at the OR relation
			// because the SQL statement is designed only for AND.
			ArrayList<ArrayList<FilterItem>> separatedFilterSettings = separateFilterSettings(filterSetting);
			ResultSet result = null;
			_sqlBuilder = new SQLBuilder();
			for (ArrayList<FilterItem> currentFilterSettingList : separatedFilterSettings) {
				String statement = _sqlBuilder
						.generateSQLCount(currentFilterSettingList);
				getMessages = _databaseConnection.prepareStatement(statement);
				getMessages = setVariables(getMessages,
						currentFilterSettingList, from, to);
				result = getMessages.executeQuery();
				result.next();
				msgNumber = msgNumber + Integer.parseInt(result.getString(1));
				CentralLogger.getInstance().debug(this,
						"Messages to delete: " + msgNumber);
			}
		} catch (SQLException e) {
			CentralLogger.getInstance().error(this, e.getMessage());
		}
		return msgNumber;
	}

	/**
	 * Delete messages with message_ids stored in 'messageIdsToDelete' from
	 * tables 'message' and 'message_content'.
	 * 
	 * @param messageIdsToDelete
	 * @throws SQLException 
	 */
	private void deleteFromDB(Set<String> messageIdsToDelete) throws SQLException {
		_databaseConnection = DBConnection.getInstance().getConnection();
		//Delete from table 'message'
		PreparedStatement deleteFromMessage = _databaseConnection
				.prepareStatement("delete from message m where m.id = ?");
		for (String msgID : messageIdsToDelete) {
			deleteFromMessage.setString(1, msgID);
			deleteFromMessage.execute();
		}
		
		//Delete from table 'message_content'
		PreparedStatement deleteFromMessageContent = _databaseConnection
				.prepareStatement("delete from message_content mc where mc.message_id = ?");
		for (String msgID : messageIdsToDelete) {
			deleteFromMessageContent.setString(1, msgID);
			deleteFromMessageContent.execute();
		}
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
			String maxRownum = Integer.toString(_maxAnswerSize * 15);
			_sqlBuilder = new SQLBuilder();
			_sqlBuilder.setRownum(maxRownum);
			CentralLogger.getInstance().debug(this, "set maxRowNum to " + maxRownum);
			for (ArrayList<FilterItem> currentFilterSettingList : separatedFilterSettings) {
				String statement = _sqlBuilder
						.generateSQL(currentFilterSettingList);
				getMessages = _databaseConnection.prepareStatement(statement);

				getMessages = setVariables(getMessages,
						currentFilterSettingList, from, to);
				// getMessages.setString(1, "NAME");
				// getMessages.setString(1, "XMTSTTP1262B_ai");
				// getMessages.setString(2, "2008-10-15 10:06:49");
				// getMessages.setString(3, "2008-10-17 20:06:49");
				result = getMessages.executeQuery();
				resultSetList.add(result);
			}
		} catch (SQLException e) {
			CentralLogger.getInstance().error(this, e.getMessage());
		}

		return resultSetList;
	}

	/**
	 * Set the variables from the filter settings in the prepared statement.
	 * 
	 * @param getMessages
	 * @param currentFilterSettingList
	 * @param from
	 * @param to
	 * @return
	 * @throws SQLException
	 */
	private PreparedStatement setVariables(PreparedStatement getMessages,
			ArrayList<FilterItem> currentFilterSettingList, Calendar from,
			Calendar to) throws SQLException {

		int parameterIndex = 0;
		// set the preparedStatement parameters
		if (getMessages != null) {
			// set filterItems with property in message table first
			for (FilterItem filterSetting : currentFilterSettingList) {
				if (filterSetting.get_property().equalsIgnoreCase("inMessage")) {
					parameterIndex++;
					getMessages.setString(parameterIndex, filterSetting
							.get_value());
					CentralLogger.getInstance().debug(
							this,
							"DB query, filter Property: "
									+ filterSetting.get_property()
									+ "  Value: " + filterSetting.get_value()
									+ "  Relation: "
									+ filterSetting.get_relation());
				}
			}
			// set filterItems with property in message_content table
			for (FilterItem filterSetting : currentFilterSettingList) {
				if (!filterSetting.get_property().equalsIgnoreCase("inMessage")) {
					parameterIndex++;
					String propertyName = filterSetting.get_property();
					getMessages.setString(parameterIndex,
							MessagePropertyTypeContent.getPropertyIDMapping()
									.get(propertyName));
					parameterIndex++;
					getMessages.setString(parameterIndex, filterSetting
							.get_value());
					CentralLogger.getInstance().debug(
							this,
							"DB query, filter Property: "
									+ filterSetting.get_property()
									+ "  Value: " + filterSetting.get_value()
									+ "  Relation: "
									+ filterSetting.get_relation());
				}
			}
		}
		String fromDate = buildDateString(from);
		String toDate = buildDateString(to);
		parameterIndex++;
		getMessages.setString(parameterIndex, fromDate);
		parameterIndex++;
		getMessages.setString(parameterIndex, toDate);
		CentralLogger.getInstance().debug(this,
				"DB query, start time: " + fromDate + "  end time: " + toDate);
		return getMessages;
	}

	/**
	 * Assemble 'real' log messages from DBResult. The DB query returns a list
	 * of message_id with property and value. To get a complete log message you
	 * have to put all property-value pairs with the same message_id in one log
	 * message together.
	 * 
	 * @param results
	 *            List of Result Sets (we get more than one result set if there
	 *            is an OR relation, because the SQL Statement is designed only
	 *            for AND relations)
	 * @return
	 */
	ArrayList<HashMap<String, String>> processResult(
			List<ResultSet> results) {

		// number of rows in all result sets (to check for max row num.
		int currentRowNum = 1;
		_maxSize = false;

		// List of 'real' log messages (we want to see in the table)
		ArrayList<HashMap<String, String>> messageResultList = new ArrayList<HashMap<String, String>>();
		try {
			// the current 'real' log message
			HashMap<String, String> message = null;
			// run through all result sets (for each OR relation in the
			// FilterSetting
			// we get one more resultSet)
			for (ResultSet resultSet : results) {
				// identifier for the current message. initialized with a not
				// existing message id
				int currentMessageID = -1;
				while (resultSet.next()) {
					currentRowNum++;
					if (currentMessageID == resultSet.getInt(1)) {
						// current row has the same message_id->
						// it belongs to the current message
						if (message != null) {
							String s = resultSet.getString(2);
							String property = getIDPropertyMapping().get(s);
							message.put(property, resultSet.getString(3));
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
						// get property name from MessagePropertyTypeContent
						// that holds id, property mapping
						String s = resultSet.getString(2);
						String property = getIDPropertyMapping().get(s);
						message.put(property, resultSet.getString(3));
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
		if (currentRowNum == Integer.parseInt(_sqlBuilder.getRownum())) {
			_maxSize = true;
		}
		return messageResultList;
	}

    protected Map<String, String> getIDPropertyMapping() {
        return MessagePropertyTypeContent
        		.getIDPropertyMapping();
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
	 * The list of Filter settings consists of property value pairs associated
	 * with AND or OR. Because the sql statement is created only for the AND
	 * parts (for a better performance the or will be merged in java) this
	 * method split the FilterSetting list on the OR association and returns a
	 * list of lists of filter settings associated only with AND.
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
