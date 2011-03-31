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
import java.util.HashSet;
import java.util.Set;

import org.csstudio.alarm.dbaccess.archivedb.Filter;
import org.csstudio.alarm.dbaccess.archivedb.FilterItem;
import org.csstudio.alarm.dbaccess.archivedb.ILogMessageArchiveAccess;
import org.csstudio.alarm.dbaccess.archivedb.Result;
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
    
    private DBConnectionHandler _connectionHandler = new DBConnectionHandler();

    private int _maxAnswerSize = 5000;

    SQLBuilder _sqlBuilder;

    /**
     * Get messages from DB for a time period and filter conditions.
     * 
     * @return ArrayList of messages in a HashMap
     */
    public void getLogMessages(Filter filter, Result result) {
        CentralLogger.getInstance().debug(
                this,
                "from time: " + filter.getFrom() + ", to time: "
                        + filter.getTo());
        _maxAnswerSize = filter.getMaximumMessageSize();
        result.setResult(queryDatabase(filter));
        result.setMaxSize(filter.getMaximumMessageSize());
    }

    /**
     * Export log messages from the DB into an excel file.
     */
    public String exportLogMessages(Filter filter, Result result, File path,
            String[] columnNames) {
        String exportResult = "Export completed";
        _maxAnswerSize = filter.getMaximumMessageSize();
        result.setResult(queryDatabase(filter));
        result.setMaxSize(filter.getMaximumMessageSize());
        ExcelMessageExporter exporter = new ExcelMessageExporter();
        try {
            exporter.exportExcelFile(result.getMessagesFromDatabase(), path,
                    columnNames);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return exportResult;
    }

    /**
     * Counts the real number of all messages that will be deleted with the
     * current filter settings (in the UI table are not more than 'MAXROWNUM'
     * messages displayed).
     * 
     * @param filter
     */
    public void countDeleteLogMessages(Filter filter, Result result) {

        int msgNumber = 0;
        try {
            _databaseConnection = _connectionHandler.getConnection();
            PreparedStatement getMessages = null;
            // the filter setting has to be separated at the OR relation
            // because the SQL statement is designed only for AND.
            ArrayList<ArrayList<FilterItem>> separatedFilterSettings = filter
                    .getSeparatedFilterSettings();
            ResultSet resultSet = null;
            _sqlBuilder = new SQLBuilder(new DBConnectionHandler());
            for (ArrayList<FilterItem> currentFilterSettingList : separatedFilterSettings) {
                String statement = _sqlBuilder
                        .composeSQLStmtForCount(currentFilterSettingList);
                getMessages = _databaseConnection.prepareStatement(statement);
                getMessages = setVariables(getMessages,
                        currentFilterSettingList, filter.getFrom(), filter
                                .getTo());
                resultSet = getMessages.executeQuery();
                resultSet.next();
                msgNumber = msgNumber
                        + Integer.parseInt(resultSet.getString(1));
                CentralLogger.getInstance().debug(this,
                        "Messages to delete: " + msgNumber);
            }
            result.setMsgNumber(msgNumber);
        } catch (Exception e) {
            CentralLogger.getInstance().error(this, e.getMessage());
        }
    }

    /**
     * Delete messages from DB. Because we have to delete the messages from
     * tables message and message_content we retrieve in the first step all
     * message_ids and delete them in the next step.
     * 
     */
    public String deleteLogMessages(Filter filter) {
        String operationResult = "Messages deleted";
        CentralLogger.getInstance().debug(this, "delete messages");
        _maxAnswerSize = -1;
        ArrayList<ResultSet> result = queryDatabase(filter);
        Set<String> messageIdsToDelete = readMessageIdFromResult(result);
        try {
            deleteFromDB(messageIdsToDelete);
        } catch (Exception e) {
            operationResult = "DB Exception. Delete operation canceled.";
            CentralLogger.getInstance().error(this,
                    "Delete operation error " + e.getMessage());
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
            CentralLogger.getInstance().error(this,
                    "SQL Exception while reading msg ids " + e.getMessage());
        }
        return messageIds;
    }

    /**
     * Delete messages with message_ids stored in 'messageIdsToDelete' from
     * tables 'message' and 'message_content'. Shrink table to release database
     * memory.
     * 
     * @param messageIdsToDelete
     * @throws SQLException
     */
    private void deleteFromDB(Set<String> messageIdsToDelete)
            throws Exception {
        _databaseConnection = _connectionHandler.getConnection();
        // Delete from table 'message_content'
        PreparedStatement deleteFromMessageContent = _databaseConnection
                .prepareStatement("delete from message_content mc where mc.message_id = ?");
        for (String msgID : messageIdsToDelete) {
            deleteFromMessageContent.setString(1, msgID);
            deleteFromMessageContent.execute();
        }

        CentralLogger.getInstance().debug(this,
                "Messages from table message_content deleted.");

        // Delete from table 'message'
        PreparedStatement deleteFromMessage = _databaseConnection
                .prepareStatement("delete from message m where m.id = ?");
        for (String msgID : messageIdsToDelete) {
            deleteFromMessage.setString(1, msgID);
            deleteFromMessage.execute();
        }

        CentralLogger.getInstance().debug(this,
                "Messages from table message deleted.");

        PreparedStatement enableRowMovement = _databaseConnection
                .prepareStatement("ALTER TABLE message_content ENABLE ROW MOVEMENT");
        PreparedStatement shrinkMessageContentTable = _databaseConnection
                .prepareStatement("ALTER TABLE message_content SHRINK SPACE CASCADE");
        PreparedStatement disableRowMovement = _databaseConnection
                .prepareStatement("ALTER TABLE message_content DISABLE ROW MOVEMENT");

        enableRowMovement.execute();
        shrinkMessageContentTable.execute();
        disableRowMovement.execute();

        if (disableRowMovement != null) {
            try {
                disableRowMovement.close();
            } catch (Exception e) {
            }
            disableRowMovement = null;
        }
        if (shrinkMessageContentTable != null) {
            try {
                shrinkMessageContentTable.close();
            } catch (Exception e) {
            }
            shrinkMessageContentTable = null;
        }
        if (enableRowMovement != null) {
            try {
                enableRowMovement.close();
            } catch (Exception e) {
            }
            enableRowMovement = null;
        }

        enableRowMovement = _databaseConnection
                .prepareStatement("ALTER TABLE message ENABLE ROW MOVEMENT");
        PreparedStatement shrinkMessageTable = _databaseConnection
                .prepareStatement("ALTER TABLE message SHRINK SPACE CASCADE");
        disableRowMovement = _databaseConnection
                .prepareStatement("ALTER TABLE message DISABLE ROW MOVEMENT");

        enableRowMovement.execute();
        shrinkMessageTable.execute();
        disableRowMovement.execute();

        if (disableRowMovement != null) {
            try {
                disableRowMovement.close();
            } catch (Exception e) {
            }
            disableRowMovement = null;
        }
        if (shrinkMessageTable != null) {
            try {
                shrinkMessageTable.close();
            } catch (Exception e) {
            }
            shrinkMessageTable = null;
        }
        if (enableRowMovement != null) {
            try {
                enableRowMovement.close();
            } catch (Exception e) {
            }
            enableRowMovement = null;
        }

        _databaseConnection.commit();
        
        CentralLogger
                .getInstance()
                .info(
                        this,
                        "Messages from table message_content and message deleted and table space released.");
    }

    /**
     * Select the appropriate SQL statement depending on the FilterSetting, set
     * the parameters for the prepared statement and executes the DB query.
     * 
     * @param filter
     * @return
     */
    private ArrayList<ResultSet> queryDatabase(Filter filter) {

        // list of result sets (for each OR relation in the FilterSetting
        // we get one more resultSet)
        ArrayList<ResultSet> resultSetList = new ArrayList<ResultSet>();

        try {
            _databaseConnection = _connectionHandler.getConnection();
            PreparedStatement getMessages = null;
            // the filter setting has to be separated at the OR relation
            // because the SQL statement is designed only for AND.
            ArrayList<ArrayList<FilterItem>> separatedFilterSettings = filter
                    .getSeparatedFilterSettings();
            ResultSet result = null;
            Integer maxRowNum = Integer.valueOf(_maxAnswerSize * 15);
            _sqlBuilder = new SQLBuilder(new DBConnectionHandler());
            _sqlBuilder.setMaxRowNum(maxRowNum);
            CentralLogger.getInstance().debug(this,
                    "set maxRowNum to " + maxRowNum);
            for (ArrayList<FilterItem> currentFilterSettingList : separatedFilterSettings) {
                String statement = _sqlBuilder
                        .composeSQLStmtForFilter(currentFilterSettingList);
                getMessages = _databaseConnection.prepareStatement(statement);

                getMessages = setVariables(getMessages,
                        currentFilterSettingList, filter.getFrom(), filter
                                .getTo());
                // getMessages.setString(1, "NAME");
                // getMessages.setString(1, "XMTSTTP1262B_ai");
                // getMessages.setString(2, "2008-10-15 10:06:49");
                // getMessages.setString(3, "2008-10-17 20:06:49");
                result = getMessages.executeQuery();
//                getMessages.close();
                getMessages = null;
                resultSetList.add(result);
            }
        } catch (Exception e) {
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
                if (filterSetting.getProperty().equalsIgnoreCase("inMessage")) {
                    parameterIndex++;
                    getMessages.setString(parameterIndex, filterSetting
                            .getConvertedValue());
                    CentralLogger.getInstance().debug(
                            this,
                            "DB query, filter Property: "
                                    + filterSetting.getProperty() + "  Value: "
                                    + filterSetting.getConvertedValue() + "  Relation: "
                                    + filterSetting.getRelation());
                }
            }
            // set filterItems with property in message_content table
            for (FilterItem filterSetting : currentFilterSettingList) {
                if (!filterSetting.getProperty().equalsIgnoreCase("inMessage")) {
                    parameterIndex++;
                    String propertyName = filterSetting.getProperty();
                    getMessages.setString(parameterIndex,
                            MessagePropertyTypeContent.getPropertyIDMapping()
                                    .get(propertyName));
                    parameterIndex++;
                    getMessages.setString(parameterIndex, filterSetting
                            .getConvertedValue());
                    CentralLogger.getInstance().debug(
                            this,
                            "DB query, filter Property: "
                                    + filterSetting.getProperty() + "  Value: "
                                    + filterSetting.getConvertedValue() + "  Relation: "
                                    + filterSetting.getRelation());
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
}
