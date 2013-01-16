/**
 * 
 */
package org.csstudio.logbook.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.Property;
import org.csstudio.logbook.PropertyBuilder;
import org.eclipse.swt.widgets.Composite;

/**
 * An Abstract class which provides the basic functionality expected from a
 * Composite used to represent a logbook property
 * 
 * @author shroffk
 * 
 */
public abstract class AbstractPropertyWidget extends Composite {

	private boolean editable;
	private LogEntryChangeset logEntryChangeset;

	// property that this widget is intended to be used with

	/**
	 * A constructor which creates the composite, registers the appropriate
	 * listeners and initializes it with the logEntryChangeset
	 * 
	 * @param parent
	 * @param style
	 * @param logEntryChangeset
	 */
	public AbstractPropertyWidget(Composite parent, int style,
			LogEntryChangeset logEntryChangeset) {
		super(parent, style);
		this.logEntryChangeset = logEntryChangeset;
		this.logEntryChangeset
				.addPropertyChangeListener(new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						updateUI();
					}
				});
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
		updateUI();
	}

	public LogEntryChangeset getLogEntryChangeset() {
		return this.logEntryChangeset;
	}

	public abstract void updateUI();
}
