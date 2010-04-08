package org.csstudio.alarm.table.database;

public class MessageDeletionException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    public MessageDeletionException() {
        super();
    }
    
    public MessageDeletionException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public MessageDeletionException(String message) {
        super(message);
    }
    
    public MessageDeletionException(Throwable cause) {
        super(cause);
    }
    
}
