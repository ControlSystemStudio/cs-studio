/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz Association, (DESY), HAMBURG,
 * GERMANY. THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. WITHOUT WARRANTY OF ANY KIND, EXPRESSED
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, THE USER
 * ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN
 * ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER. DESY HAS
 * NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING
 * FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
 * DISTRIBUTION OF THIS PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY AT
 * HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
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
import java.util.HashSet;
import java.util.Set;

import org.csstudio.alarm.dbaccess.archivedb.Filter;
import org.csstudio.alarm.dbaccess.archivedb.FilterItem;
import org.csstudio.alarm.dbaccess.archivedb.ILogMessageArchiveAccess;
import org.csstudio.alarm.dbaccess.archivedb.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main Class for DB query. Builds accordingly to the current FilterSettings the SQL Statement, assembles the log
 * messages from the DB result and returns the messages in an ArrayList.
 *
 * @author jhatje
 * @author $Author$
 * @version $Revision$
 * @since 22.05.2008
 */
public class ArchiveDBAccess implements ILogMessageArchiveAccess {

    private static final Logger LOG = LoggerFactory.getLogger(ArchiveDBAccess.class);

    private Connection _databaseConnection;

    private final DBConnectionHandler _connectionHandler = new DBConnectionHandler();

    private int _maxAnswerSize = 5000;

    SQLBuilder _sqlBuilder;

    /**
     * Get messages from DB for a time period and filter conditions.
     *
     * @return ArrayList of messages in a HashMap
     */
    public void getLogMessages(final Filter filter, final Result result) {
        LOG.debug("from time: {}, to time: {}", filter.getFrom(), filter.getTo());
        _maxAnswerSize = filter.getMaximumMessageSize();
        result.setResult(queryDatabase(filter));
        result.setMaxSize(filter.getMaximumMessageSize());
    }

    /**
     * Export log messages from the DB into an excel file.
     */
    public String exportLogMessages(final Filter filter,
                                    final Result result,
                                    final File path,
                                    final String[] columnNames) {
        final String exportResult = "Export completed";
        _maxAnswerSize = filter.getMaximumMessageSize();
        result.setResult(queryDatabase(filter));
        result.setMaxSize(filter.getMaximumMessageSize());
        final ExcelMessageExporter exporter = new ExcelMessageExporter();
        try {
            exporter.exportExcelFile(result.getMessagesFromDatabase(), path, columnNames);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return exportResult;
    }

    /**
     * Counts the real number of all messages that will be deleted with the current filter settings (in the UI table are
     * not more than 'MAXROWNUM' messages displayed).
     *
     * @param filter
     */
    public void countDeleteLogMessages(final Filter filter, final Result result) {

        int msgNumber = 0;
        try {
            _databaseConnection = _connectionHandler.getConnection();
            PreparedStatement getMessages = null;
            // the filter setting has to be separated at the OR relation
            // because the SQL statement is designed only for AND.
            final ArrayList<ArrayList<FilterItem>> separatedFilterSettings =
                    filter.getSeparatedFilterSettings();
            ResultSet resultSet = null;
            _sqlBuilder = new SQLBuilder(new DBConnectionHandler());
            for (final ArrayList<FilterItem> currentFilterSettingList : separatedFilterSettings) {
                final String statement =
                        _sqlBuilder.composeSQLStmtForCount(currentFilterSettingList);
                getMessages = _databaseConnection.prepareStatement(statement);
                getMessages =
                        setVariables(getMessages,
                                     currentFilterSettingList,
                                     filter.getFrom(),
                                     filter.getTo());
                resultSet = getMessages.executeQuery();
                resultSet.next();
                msgNumber = msgNumber + Integer.parseInt(resultSet.getString(1));
                LOG.debug("Messages to delete: {}", msgNumber);
            }
            result.setMsgNumber(msgNumber);
        } catch (final Exception e) {
            LOG.error("", e);
        }
    }

    /**
     * Delete messages from DB. Because we have to delete the messages from tables message and message_content we
     * retrieve in the first step all message_ids and delete them in the next step.
     */
    public String deleteLogMessages(final Filter filter) {
        String operationResult = "Messages deleted";
        LOG.debug("delete messages");
        _maxAnswerSize = -1;
        final ArrayList<ResultSet> result = queryDatabase(filter);
        final Set<String> messageIdsToDelete = readMessageIdFromResult(result);
        try {
            deleteFromDB(messageIdsToDelete);
        } catch (final Exception e) {
            operationResult = "DB Exception. Delete operation canceled.";
            LOG.error("Delete operation error ", e);
        }
        return operationResult;
    }

    private Set<String> readMessageIdFromResult(final ArrayList<ResultSet> result) {
        final Set<String> messageIds = new HashSet<String>();
        try {
            for (final ResultSet resultSet : result) {
                while (resultSet.next()) {
                    messageIds.add(resultSet.getString(1));
                }
            }
        } catch (final SQLException e) {
            LOG.error("SQL Exception while reading msg ids ", e);
        }
        return messageIds;
    }

    /**
     * Delete messages with message_ids stored in 'messageIdsToDelete' from tables 'message' and 'message_content'.
     * Shrink table to release database memory.
     *
     * @param messageIdsToDelete
     * @throws SQLException
     */
    private void deleteFromDB(final Set<String> messageIdsToDelete) throws Exception {
        _databaseConnection = _connectionHandler.getConnection();
        // Delete from table 'message_content'
        final PreparedStatement deleteFromMessageContent =
                _databaseConnection
                        .prepareStatement("delete from message_content mc where mc.message_id = ?");
        for (final String msgID : messageIdsToDelete) {
            deleteFromMessageContent.setString(1, msgID);
            deleteFromMessageContent.execute();
        }

        LOG.debug("Messages from table message_content deleted.");

        // Delete from table 'message'
        final PreparedStatement deleteFromMessage =
                _databaseConnection.prepareStatement("delete from message m where m.id = ?");
        for (final String msgID : messageIdsToDelete) {
            deleteFromMessage.setString(1, msgID);
            deleteFromMessage.execute();
        }

        LOG.debug("Messages from table message deleted.");

        PreparedStatement enableRowMovement =
                _databaseConnection
                        .prepareStatement("ALTER TABLE message_content ENABLE ROW MOVEMENT");
        PreparedStatement shrinkMessageContentTable =
                _databaseConnection
                        .prepareStatement("ALTER TABLE message_content SHRINK SPACE CASCADE");
        PreparedStatement disableRowMovement =
                _databaseConnection
                        .prepareStatement("ALTER TABLE message_content DISABLE ROW MOVEMENT");

        enableRowMovement.execute();
        shrinkMessageContentTable.execute();
        disableRowMovement.execute();

        if (disableRowMovement != null) {
            try {
                disableRowMovement.close();
            } catch (final Exception e) {
            }
            disableRowMovement = null;
        }
        if (shrinkMessageContentTable != null) {
            try {
                shrinkMessageContentTable.close();
            } catch (final Exception e) {
            }
            shrinkMessageContentTable = null;
        }
        if (enableRowMovement != null) {
            try {
                enableRowMovement.close();
            } catch (final Exception e) {
            }
            enableRowMovement = null;
        }

        enableRowMovement =
                _databaseConnection.prepareStatement("ALTER TABLE message ENABLE ROW MOVEMENT");
        PreparedStatement shrinkMessageTable =
                _databaseConnection.prepareStatement("ALTER TABLE message SHRINK SPACE CASCADE");
        disableRowMovement =
                _databaseConnection.prepareStatement("ALTER TABLE message DISABLE ROW MOVEMENT");

        enableRowMovement.execute();
        shrinkMessageTable.execute();
        disableRowMovement.execute();

        if (disableRowMovement != null) {
            try {
                disableRowMovement.close();
            } catch (final Exception e) {
            }
            disableRowMovement = null;
        }
        if (shrinkMessageTable != null) {
            try {
                shrinkMessageTable.close();
            } catch (final Exception e) {
            }
            shrinkMessageTable = null;
        }
        if (enableRowMovement != null) {
            try {
                enableRowMovement.close();
            } catch (final Exception e) {
            }
            enableRowMovement = null;
        }

        _databaseConnection.commit();

        LOG.info("Messages from table message_content and message deleted and table space released.");
    }

    /**
     * Select the appropriate SQL statement depending on the FilterSetting, set the parameters for the prepared
     * statement and executes the DB query.
     *
     * @param filter
     * @return
     */
    private ArrayList<ResultSet> queryDatabase(final Filter filter) {

        // list of result sets (for each OR relation in the FilterSetting
        // we get one more resultSet)
        final ArrayList<ResultSet> resultSetList = new ArrayList<ResultSet>();

        try {
            _databaseConnection = _connectionHandler.getConnection();
            PreparedStatement getMessages = null;
            // the filter setting has to be separated at the OR relation
            // because the SQL statement is designed only for AND.
            final ArrayList<ArrayList<FilterItem>> separatedFilterSettings =
                    filter.getSeparatedFilterSettings();
            ResultSet result = null;
            final Integer maxRowNum = Integer.valueOf(_maxAnswerSize * 15);
            _sqlBuilder = new SQLBuilder(new DBConnectionHandler());
            _sqlBuilder.setMaxRowNum(maxRowNum);
            LOG.debug("set maxRowNum to {}", maxRowNum);
            for (final ArrayList<FilterItem> currentFilterSettingList : separatedFilterSettings) {
                final String statement =
                        _sqlBuilder.composeSQLStmtForFilter(currentFilterSettingList);
                getMessages = _databaseConnection.prepareStatement(statement);

                getMessages =
                        setVariables(getMessages,
                                     currentFilterSettingList,
                                     filter.getFrom(),
                                     filter.getTo());
                // getMessages.setString(1, "NAME");
                // getMessages.setString(1, "XMTSTTP1262B_ai");
                // getMessages.setString(2, "2008-10-15 10:06:49");
                // getMessages.setString(3, "2008-10-17 20:06:49");
                result = getMessages.executeQuery();
                // getMessages.close();
                getMessages = null;
                resultSetList.add(result);
            }
        } catch (final Exception e) {
            LOG.error("", e);
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
    private PreparedStatement setVariables(final PreparedStatement getMessages,
                                           final ArrayList<FilterItem> currentFilterSettingList,
                                           final Calendar from,
                                           final Calendar to) throws SQLException {

        int parameterIndex = 0;
        // set the preparedStatement parameters
        if (getMessages != null) {
            // set filterItems with property in message table first
            for (final FilterItem filterSetting : currentFilterSettingList) {
                if (filterSetting.getProperty().equalsIgnoreCase("inMessage")) {
                    parameterIndex++;
                    getMessages.setString(parameterIndex, filterSetting.getConvertedValue());
                    final Object[] args = new Object[] {filterSetting.getProperty(),
                                  filterSetting.getConvertedValue(),
                                  filterSetting.getRelation() };
                    LOG.debug("DB query, filter Property: {}  Value: {}  Relation: {}", args);
                }
            }
            // set filterItems with property in message_content table
            for (final FilterItem filterSetting : currentFilterSettingList) {
                if (!filterSetting.getProperty().equalsIgnoreCase("inMessage")) {
                    parameterIndex++;
                    final String propertyName = filterSetting.getProperty();
                    getMessages.setString(parameterIndex, MessagePropertyTypeContent
                            .getPropertyIDMapping().get(propertyName));
                    parameterIndex++;
                    getMessages.setString(parameterIndex, filterSetting.getConvertedValue());
                    final Object[] args = new Object[] {filterSetting.getProperty(),
                                                        filterSetting.getConvertedValue(),
                                                        filterSetting.getRelation() };
                    LOG.debug("DB query, filter Property: {}  Value: {}  Relation: {}", args);
                }
            }
        }
        final String fromDate = buildDateString(from);
        final String toDate = buildDateString(to);
        parameterIndex++;
        getMessages.setString(parameterIndex, fromDate);
        parameterIndex++;
        getMessages.setString(parameterIndex, toDate);
        LOG.debug("DB query, start time: {}  end time: {}",fromDate, toDate);
        return getMessages;
    }

    /**
     * Create from Calendar a date of type String in the format of the SQL statement.
     *
     * @param date
     * @return
     */
    private String buildDateString(final Calendar date) {
        return date.get(GregorianCalendar.YEAR) + "-" + (date.get(GregorianCalendar.MONTH) + 1) +
               "-" + date.get(GregorianCalendar.DAY_OF_MONTH) + " " +
               date.get(GregorianCalendar.HOUR_OF_DAY) + ":" + date.get(GregorianCalendar.MINUTE) +
               ":" + date.get(GregorianCalendar.SECOND);
    }
}
