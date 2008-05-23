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
 * 
 * @author jhatje
 * @author $Author$
 * @version $Revision$
 * @since 22.05.2008
 */
public final class ArchiveDBAccess implements ILogMessageArchiveAccess {

	private Connection _databaseConnection;
	private static ArchiveDBAccess _archiveDBAccess;

	private ArchiveDBAccess() {
	}

	/**
	 * Singleton instance.
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
	 * Get messages from DB for a time period.
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
	 * Get messages from DB for a time period and filter
	 * conditions.
	 * 
	 * @return ArrayList of messages in a HashMap 
	 */
	public ArrayList<HashMap<String, String>> getLogMessages(Calendar from,
			Calendar to, String filter, ArrayList<FilterSetting> filterSetting,
			int maxAnserSize) {
		ArrayList<ResultSet> result = queryDatabase(filterSetting, from, to);
		ArrayList<HashMap<String, String>> ergebniss = processResult(result);
		return ergebniss;
	}

	private ArrayList<HashMap<String, String>> processResult(
			ArrayList<ResultSet> result) {
		ArrayList<HashMap<String, String>> messageResultList = new ArrayList<HashMap<String, String>>();
		try {
			HashMap<String, String> message = null;
			for (ResultSet resultSet : result) {
				int currentMessageID = -1;
				while (resultSet.next()) {
					if (currentMessageID == resultSet.getInt(1)) {
						// this result row belongs to the current message
						if (message != null) {
							message.put(resultSet.getString(4), resultSet
									.getString(5));
						}
					} else {
						// this result row belongs to a new message
						// if there is already a message put it to the
						// messageResultList.
						if (message != null) {
							messageResultList.add(message);
						}
						currentMessageID = Integer.parseInt(resultSet
								.getString(1));
						message = new HashMap<String, String>();
						message.put(resultSet.getString(4), resultSet
								.getString(5));
					}
					System.out.println(resultSet.getString(1));
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

	private ArrayList<ResultSet> queryDatabase(ArrayList<FilterSetting> filter,
			Calendar from, Calendar to) {

		ArrayList<ResultSet> resultSetList = new ArrayList<ResultSet>();

		try {
			_databaseConnection = DBConnection.getInstance().getConnection();
			PreparedStatement getMessages = null;
			ArrayList<ArrayList<FilterSetting>> separatedFilterSettings = separateFilterSettings(filter);
			ResultSet result = null;
			for (ArrayList<FilterSetting> currentFilterSettingList : separatedFilterSettings) {
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
				if (getMessages != null) {
					for (FilterSetting filterSetting : currentFilterSettingList) {
						parameterIndex++;
						getMessages.setString(parameterIndex, filterSetting
								.get_property());
						parameterIndex++;
						getMessages.setString(parameterIndex, filterSetting
								.get_value());
					}
				}
				String fromDate = buildDateString(from);
				String toDate = buildDateString(to);
				parameterIndex++;
				getMessages.setString(parameterIndex, fromDate);
				parameterIndex++;
				getMessages.setString(parameterIndex, toDate);

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

	private String buildDateString(Calendar date) {
		return date.get(GregorianCalendar.YEAR) + "-"
				+ (date.get(GregorianCalendar.MONTH) + 1) + "-"
				+ date.get(GregorianCalendar.DAY_OF_MONTH) + " "
				+ date.get(GregorianCalendar.HOUR_OF_DAY) + ":"
				+ date.get(GregorianCalendar.MINUTE) + ":"
				+ date.get(GregorianCalendar.SECOND);
	}

	/**
	 * The list of Filter settings consists of of property value pairs
	 * associated with AND or OR. Because the sql statement is created only for
	 * the AND parts (for a better performance the or will be merged in java)
	 * this method split the FilterSetting list on the OR association and
	 * returns a list of lists of filtersettings associated only with AND.
	 * 
	 * @param filter
	 * @return
	 */
	private ArrayList<ArrayList<FilterSetting>> separateFilterSettings(
			ArrayList<FilterSetting> filter) {
		ArrayList<ArrayList<FilterSetting>> separatedFilterSettings = new ArrayList<ArrayList<FilterSetting>>();
		ArrayList<FilterSetting> filterSettingsAndAssociated = null;
		// if filter is null (user searches only for time period) set one
		// empty list of filter settings.
		if (filter == null) {
			filterSettingsAndAssociated = new ArrayList<FilterSetting>();
			separatedFilterSettings.add(filterSettingsAndAssociated);
			return separatedFilterSettings;
		}
		String association = "BEGIN";
		for (FilterSetting setting : filter) {
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
				filterSettingsAndAssociated = new ArrayList<FilterSetting>();
				filterSettingsAndAssociated.add(setting);
				continue;
			}
			if (association.equalsIgnoreCase("BEGIN")) {
				filterSettingsAndAssociated = new ArrayList<FilterSetting>();
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
