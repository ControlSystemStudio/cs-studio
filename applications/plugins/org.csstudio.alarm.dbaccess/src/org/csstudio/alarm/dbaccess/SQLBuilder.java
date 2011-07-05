package org.csstudio.alarm.dbaccess;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.csstudio.alarm.dbaccess.archivedb.FilterItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that builds prepared SQL statements for settings in the expert search
 * in the alarm archive log table.
 *
 * @author jhatje
 *
 */
public class SQLBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(SQLBuilder.class);

	private final Integer _maxRowNumLimit = 50000;
	private Integer _maxRowNum = _maxRowNumLimit;

	private final String _selectCommonColumnsStmtPrefix =
	    "select mc.message_id, mc.msg_property_type_id,  mc.value";

	private final String _commonSQLFromClause =
	    " from  message m, message_content mc where m.id = mc.MESSAGE_ID";

	private final String _selectCountPrefixForId =
	    "select count(m.id)";

	private final String commonSQLDate =
	    " and m.DATUM between to_date(? , 'YYYY-MM-DD HH24:MI:SS') and" +
	    " to_date(? , 'YYYY-MM-DD HH24:MI:SS')";

	private final String _commonRowNumLessThanClause =
	    " and ROWNUM <";

	private final String _commonOrderByClause =
	    " order by mc.MESSAGE_ID desc";

	private final String _subqueryCondition =
	    " and mc.message_id in (select mc.MESSAGE_ID from message_content mc"+
	    " where mc.msg_property_type_id = ? and upper(mc.VALUE) LIKE upper(?))";

    private final DBConnectionHandler _handler;

	/**
     * Constructor.
     */
    public SQLBuilder(final DBConnectionHandler handler) {
        _handler = handler;
    }

	/**
	 * Build a prepared statement from the list of filter items. Analyse message
	 * table for property columns and take them into account for the sql
	 * statement.
	 *
	 * @param currentFilterSettingList
	 * @return completed prepared sql statement.
	 */
	public String composeSQLStmtForFilter(final List<FilterItem> currentFilterSettingList) {

		final String statementConditions =
		    createSQLStatementConditions(currentFilterSettingList);

		final String preparedStatement =
		    _selectCommonColumnsStmtPrefix +
		    _commonSQLFromClause +
		    statementConditions +
		    commonSQLDate +
		    (hasRowNumClause() ? _commonRowNumLessThanClause + _maxRowNum : "") +
		    _commonOrderByClause;

		return preparedStatement;
	}

    private boolean hasRowNumClause() {
        return _maxRowNum != -1;
    }

	/**
	 * Build a prepared statement for count from the list of filter items.
	 * Analyse message table for property columns and take them into account for
	 * the sql statement.
	 *
	 * @param currentFilterSettingList
	 * @return completed prepared sql statement for counting.
	 */
	public String composeSQLStmtForCount(
			final List<FilterItem> currentFilterSettingList) {

		final String statementConditions =
		    createSQLStatementConditions(currentFilterSettingList);

		final String preparedStatement =
		    _selectCountPrefixForId +
		    _commonSQLFromClause +
		    statementConditions +
		    commonSQLDate;

		return preparedStatement;
	}

	/**
	 * Creating the SQL statement conditions.
	 *
	 * @param currentFilterSettingList
	 * @param propertiesInMessageTable
	 * @return
	 */
	private String createSQLStatementConditions(final List<FilterItem> currentFilterSettingList) {
		final StringBuilder simpleConditionPart = new StringBuilder();
		final StringBuilder subqueryConditionPart = new StringBuilder();

		// Read meta information from message table to find out which
		// properties/columns are added.
		final Set<String> propertiesFromMessageTable = retrievePropertiesFromMessageTable();

		for (final FilterItem item : currentFilterSettingList) {
			if (propertiesFromMessageTable.contains(item.getProperty())) {
				simpleConditionPart.append(" and upper(m." + item.getProperty() + ") LIKE upper(?)");

				// FIXME (jhatje) : you cannot set the internals of the filter items here!
				// You'll break anything!
				//Move the table structure check (is the property a column in the message table or
				//a record in the message content table) in a separated service.
				item.set_property("inMessage");
			} else {
				subqueryConditionPart.append(_subqueryCondition) ;
			}
		}
		return simpleConditionPart.toString() + subqueryConditionPart.toString();
	}

	/**
	 * Read column names from message table and ignore 'datum', 'id',
	 * 'msg_type_id', because they are not properties.
	 *
	 * @return list of available properties in message table.
	 */
	private Set<String> retrievePropertiesFromMessageTable() {

		ResultSetMetaData meta = null;
		ResultSet rs = null;
		Statement st = null;
		String name = null;
		int count = 0;

		try {
            final Connection connection = _handler.getConnection();

			st = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			rs = st.executeQuery("SELECT * FROM message WHERE id = 1");

			meta = rs.getMetaData();
			count = meta.getColumnCount();

			final Set<String> propertySet = new HashSet<String>();
			// FIXME (jhatje) : we don't know whether these columns exist??? if so ask for them explicitly
			for (int i = 1; i <= count; i++) {
				name = meta.getColumnName(i);

				if (name.compareToIgnoreCase("id") != 0
						&& name.compareToIgnoreCase("datum") != 0
						&& name.compareToIgnoreCase("msg_type_id") != 0) {
					propertySet.add(name);
				}
			}
			return propertySet;

		} catch (final Exception sqle) {
            LOG.error("SQLException: Cannot read table column names: {}",sqle.getMessage());
		} finally {
		    if(_handler != null) {
		        _handler.closeConnection();
		    }
		}

		return Collections.emptySet();
	}

	public void setMaxRowNum(final Integer num) {
	    if (num != -1 && (num <= 0 || num > _maxRowNumLimit)) {
	        throw new IllegalArgumentException("Maximum row number is " + num +
	                                           " but can only be set in interval (1, " + _maxRowNumLimit + "] or to -1 for 'no limit'!");
	    }
		this._maxRowNum = num;
	}

}
