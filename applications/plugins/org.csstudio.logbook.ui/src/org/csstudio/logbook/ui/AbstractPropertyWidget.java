/**
 * 
 */
package org.csstudio.logbook.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.Property;
import org.csstudio.logbook.PropertyBuilder;
import org.eclipse.swt.widgets.Composite;

/**
 * @author shroffk
 * 
 */
public abstract class AbstractPropertyWidget extends Composite {

	// property that this widget is intended to be used with

	public AbstractPropertyWidget(Composite parent, int style,
			String propertyName) {
		super(parent, style);
	}

	private boolean editable;
	private LogEntryChangeset logEntryChangeset;

	protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(
			this);

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(listener);
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		boolean oldValue = this.editable;
		this.editable = editable;
		changeSupport.firePropertyChange("editable", oldValue, this.editable);
	}
	
	public LogEntryChangeset getLogEntryChangeset() {
		return logEntryChangeset;
	}

	public void setLogEntrychangeset(LogEntryChangeset logEntryChangeset) {
		LogEntryChangeset oldValue = this.logEntryChangeset;
		this.logEntryChangeset = logEntryChangeset;
		changeSupport.firePropertyChange("logEntryChangeset", oldValue, this.logEntryChangeset);
	}
}
