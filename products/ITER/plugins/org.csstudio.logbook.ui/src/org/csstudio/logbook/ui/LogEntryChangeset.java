/**
 * 
 */
package org.csstudio.logbook.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;

import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.LogEntryBuilder;

/**
 * Provides the property change support needed to share the logEntryBuilder
 * object between the various pieces which create and add properties to this
 * logEntry
 * 
 * @author shroffk
 * 
 */
public class LogEntryChangeset {

    private LogEntryBuilder logEntryBuilder;

    protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(
	    this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
	changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
	changeSupport.removePropertyChangeListener(listener);
    }

    public LogEntryChangeset() {
	logEntryBuilder = LogEntryBuilder.withText("");
    }

    public LogEntryChangeset(LogEntry logEntry) throws IOException {
	logEntryBuilder = LogEntryBuilder.logEntry(logEntry);
    }

    public LogEntry getLogEntry() throws IOException {
	return this.logEntryBuilder.build();
    }

    public void setLogEntryBuilder(LogEntryBuilder logEntryBuilder) {
	LogEntryBuilder oldValue = this.logEntryBuilder;
	this.logEntryBuilder = logEntryBuilder;
	changeSupport.firePropertyChange("logEntryBuilder", oldValue,
		this.logEntryBuilder);
    }
}
