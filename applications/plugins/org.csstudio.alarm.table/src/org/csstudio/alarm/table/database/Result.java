package org.csstudio.alarm.table.database;

import java.util.ArrayList;
import java.util.HashMap;

public class Result {

    /**
     * result of database query for messages
     */
    ArrayList<HashMap<String, String>> _messagesFromDatabase;

    /** 
     *  maxSize is true if maxrow in the SQL statement has cut off more messages.
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
    
    public void setMessagesFromDatabase(
            ArrayList<HashMap<String, String>> fromDatabase) {
        _messagesFromDatabase = fromDatabase;
    }
    
    public boolean isMaxSize() {
        return _maxSize;
    }
    
    public void setMaxSize(boolean size) {
        _maxSize = size;
    }
    
    public Integer getMsgNumberToDelete() {
        return _msgNumberToDelete;
    }
    
    public void setMsgNumber(Integer numberToDelete) {
        _msgNumberToDelete = numberToDelete;
    }
    
    public String get_accessResult() {
        return _accessResult;
    }
    
    public void setAccessResult(String result) {
        _accessResult = result;
    }
}
