package org.csstudio.alarm.dbaccess.archivedb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.csstudio.alarm.dbaccess.MessagePropertyTypeContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Result {

    private static final Logger LOG = LoggerFactory.getLogger(Result.class);

    /**
     * result of database query for messages
     */
    ArrayList<HashMap<String, String>> _messagesFromDatabase = new ArrayList<HashMap<String, String>>();

    /**
     * maxSize is true if maxrow in the SQL statement has cut off more messages.
     */
    boolean _maxSize = false;

    /**
     * number of messages to delete from database.
     */
    Integer _msgNumberToDelete = -1;

    /**
     * message of the database operation (success, error, ...)
     */
    String _accessResult = null;

    public ArrayList<HashMap<String, String>> getMessagesFromDatabase() {
        return _messagesFromDatabase;
    }

    // public void setMessagesFromDatabase(
    // ArrayList<HashMap<String, String>> fromDatabase) {
    // _messagesFromDatabase = fromDatabase;
    // }

    public boolean isMaxSize() {
        return _maxSize;
    }

    public void setMaxSize(final boolean size) {
        _maxSize = size;
    }

    public Integer getMsgNumberToDelete() {
        return _msgNumberToDelete;
    }

    public void setMsgNumber(final Integer numberToDelete) {
        _msgNumberToDelete = numberToDelete;
    }

    public String get_accessResult() {
        return _accessResult;
    }

    public void setAccessResult(final String result) {
        _accessResult = result;
    }

    /**
     * <pre>
     * Assemble 'real' log messages from DBResult. The DB query returns a list
     * of message_id with property and value. To get a complete log message you
     * have to put all property-value pairs with the same message_id in one log
     * message together.
     *
     * Example (There are separated ResultSets for each OR connections of filter
     * settings):
     * ResultSet1 for (Severity == MAJOR) AND (Type == EVENT) from DB
     * ID   PROPERTY_NAME   PROPERTY_VALUE
     * 215  Type            EVENT
     * 215  Severity        MAJOR
     * 215  Name            Valve17_ai
     * 287  Type            EVENT
     * 287  Severity        MAJOR
     * 287  Name            Valve20_ai
     * 287  Text            Important Error!!
     *
     * ResultSet2 for (Severity == MINOR) AND (Name == Valve10_ai) from DB
     * ID   PROPERTY_NAME   PROPERTY_VALUE
     * 250  Type            EVENT
     * 250  Severity        MINOR
     * 250  Name            Valve10_ai
     * 250  Text            Not important
     * 299  Type            LOG
     * 299  Severity        MINOR
     * 299  Name            Valve10_ai
     *
     * But we want to get ONE list of all messages in ArrayList&lt;HashMap&lt;String, String&gt;&gt;
     * format:
     * HashMap for Msg1: Type-EVENT; Severity-MAJOR; Name-Valve17_ai;
     * HashMap for Msg2: Type-EVENT; Severity-MAJOR; Name-Valve20_ai; Text-Important Error!!
     * HashMap for Msg3: Type-EVENT; Severity-MINOR; Name-Valve10_ai; Text-Not important
     * HashMap for Msg4: Type-LOG; Severity-MINOR; Name-Valve10_ai;
     * </pre>
     *
     * &#064;param results List of Result Sets (we get more than one result set
     * if there is an OR relation, because the SQL Statement is designed only
     * for AND relations)
     *
     * @return
     */
    public void setResult(final List<ResultSet> results) {

        // number of rows in all result sets (to check for max row num.
        int currentRowNum = 1;
        _maxSize = false;

        try {
            // the current 'real' log message
            HashMap<String, String> message = null;
            // run through all result sets (for each OR relation in the
            // FilterSetting
            // we get one more resultSet)
            for (final ResultSet resultSet : results) {
                // identifier for the current message. initialized with a not
                // existing message id
                int currentMessageID = -1;
                while (resultSet.next()) {
                    currentRowNum++;
                    if (currentMessageID == resultSet.getInt(1)) {
                        // current row has the same message_id->
                        // it belongs to the current message
                        if (message != null) {
                            final String s = resultSet.getString(2);
                            final String property = MessagePropertyTypeContent
                                    .getIDPropertyMapping().get(s);
                            message.put(property, resultSet.getString(3));
                        }
                    } else {
                        // this result row belongs to a new message
                        // if there is already a previous message put it to the
                        // messageResultList.
                        if (message != null) {
                            _messagesFromDatabase.add(message);
                        }
                        // update current message id and
                        // put the first property value pair in the new
                        // message
                        currentMessageID = Integer.parseInt(resultSet
                                .getString(1));
                        message = new HashMap<String, String>();
                        // get property name from MessagePropertyTypeContent
                        // that holds id, property mapping
                        final String s = resultSet.getString(2);
                        final String property = MessagePropertyTypeContent
                                .getIDPropertyMapping().get(s);
                        message.put(property, resultSet.getString(3));
                    }
                }
                // put the last message to the messageResultList
                // (the message should be not null anyway)
                if (message != null) {
                    _messagesFromDatabase.add(message);
                }
            }
        } catch (final SQLException e) {
            LOG.error("", e);
        }
    }

    /**
     * Set field {@code _maxSize} depending on parameter.
     *
     * @param maximumMessageSize
     */
    public void setMaxSize(final int maximumMessageSize) {
        if (maximumMessageSize < _messagesFromDatabase.size()) {
            _maxSize = true;
        } else {
            _maxSize = false;
        }
    }
}
