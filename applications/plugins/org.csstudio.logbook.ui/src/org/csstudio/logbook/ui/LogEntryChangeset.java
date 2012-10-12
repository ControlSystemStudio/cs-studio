/**
 * 
 */
package org.csstudio.logbook.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.LogEntryBuilder;

/**
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
	
	public LogEntryChangeset(){
		logEntryBuilder = LogEntryBuilder.withText("");
	}
	
	public LogEntryChangeset(LogEntry logEntry){
		logEntryBuilder = LogEntryBuilder.logEntry(logEntry);
	}
	
	public LogEntry getLogEntry(){
		return this.logEntryBuilder.build();
	}
	
	public void setLogEntryBuilder(LogEntryBuilder logEntryBuilder){
		LogEntryBuilder oldValue = this.logEntryBuilder;
		this.logEntryBuilder = logEntryBuilder;
		changeSupport.firePropertyChange("logEntryBuilder", oldValue, this.logEntryBuilder);
	}
}
