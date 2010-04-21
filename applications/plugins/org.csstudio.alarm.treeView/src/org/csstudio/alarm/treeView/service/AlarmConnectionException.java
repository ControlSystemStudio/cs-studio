package org.csstudio.alarm.treeView.service;

public class AlarmConnectionException extends Exception {
    private static final long serialVersionUID = -6008674229748613308L;
    
    public AlarmConnectionException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public AlarmConnectionException(final String message) {
        super(message);
    }
    
    public AlarmConnectionException(final Throwable cause) {
        super(cause);
    }
    
    public AlarmConnectionException() {
        super();
    }
}
