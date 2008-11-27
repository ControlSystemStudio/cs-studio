package org.csstudio.alarm.dbaccess;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.csstudio.platform.logging.CentralLogger;

/**
 * Class that builds prepared SQL statements for settings in the expert search
 * in the alarm archive log table.
 * 
 * @author jhatje
 * 
 */
public class SQLBuilder {

	private String commonSQLBegin = "select mc.message_id, mc.msg_property_type_id,  mc.value "
			+ "from  message m, message_content mc "
			+ "where m.id = mc.MESSAGE_ID ";

	private String commonSQLEnd = "and m.DATUM between to_date(? , 'YYYY-MM-DD HH24:MI:SS') and "
			+ "to_date(? , 'YYYY-MM-DD HH24:MI:SS') "
			+ "and ROWNUM < "
			+ "50000 order by mc.MESSAGE_ID desc ";

	private String subqueryCondition = "and mc.message_id in (select mc.MESSAGE_ID from message_content mc "
			+ "where mc.msg_property_type_id = ? and mc.VALUE = ? "
			+ ") "
;

	/**
	 * Build a prepared statement from the list of filter items. Analyse message
	 * table for property columns and take them into account for the sql
	 * statement.
	 * 
	 * @param currentFilterSettingList
	 * @return completed prepared sql statement.
	 */
	public String generateSQL(ArrayList<FilterItem> currentFilterSettingList) {

		// complete statement
		String preparedStatement = "";

		// Read meta information from message table to find out which
		// properties/columns are added.
		Set<String> propertiesInMessageTable = getPropertiesInMessageTable();

		String statementConditions = createContitions(currentFilterSettingList,
				propertiesInMessageTable);

		preparedStatement = commonSQLBegin + statementConditions + commonSQLEnd;

		return preparedStatement;
	}

	/**
	 * Creating the conditions.
	 * 
	 * @param currentFilterSettingList
	 * @param propertiesInMessageTable
	 * @return
	 */
	private String createContitions(
			ArrayList<FilterItem> currentFilterSettingList,
			Set<String> propertiesInMessageTable) {
		String simpleConditionPart = "";
		String subqueryConditionPart = "";

		for (FilterItem item : currentFilterSettingList) {
			if (propertiesInMessageTable.contains(item.get_property())) {
				simpleConditionPart = simpleConditionPart + "and m."
						+ item.get_property() + " = ? ";
				item.set_property("inMessage");
			} else {
				subqueryConditionPart = subqueryConditionPart
						+ subqueryCondition;
			}
		}
		return simpleConditionPart + subqueryConditionPart;
	}

	/**
	 * Read column names from message table and ignore 'datum', 'id',
	 * 'msg_type_id', because they are not properties.
	 * 
	 * @return list of available properties in message table.
	 */
	private Set<String> getPropertiesInMessageTable() {
		Set<String> properties = new HashSet<String>();

		ResultSetMetaData meta = null;
		ResultSet rs = null;
		Statement st = null;
		String name = null;
		int count = 0;

		try {
			Connection connection = DBConnection.getInstance().getConnection();
			st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			rs = st.executeQuery("SELECT * FROM message WHERE id = 1");

			meta = rs.getMetaData();
			count = meta.getColumnCount();

			for (int i = 1; i <= count; i++) {
				name = meta.getColumnName(i);

				if ((name.compareToIgnoreCase("id") != 0)
						&& (name.compareToIgnoreCase("datum") != 0)
						&& (name.compareToIgnoreCase("msg_type_id") != 0)) {
					properties.add(name);
				}
			}
		} catch (SQLException sqle) {
			CentralLogger.getInstance().error(
					this,
					"SQLException: Cannot read table column names: "
							+ sqle.getMessage());
		}
		return properties;
	}

}
