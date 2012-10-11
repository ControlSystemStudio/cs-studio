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
	private final String propertyName;

	public String getPropertyName() {
		return propertyName;
	}

	public AbstractPropertyWidget(Composite parent, int style,
			String propertyName) {
		super(parent, style);
		this.propertyName = propertyName;
	}

	private boolean editable;
	private boolean attached = false;
	private Property property;
	private PropertyBuilder propertyBuilder;

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
	
	public boolean isAttached() {
		return attached;
	}

	public void setAttached(boolean attached) {
		boolean oldValue = this.attached;
		this.attached = attached;
		changeSupport.firePropertyChange("attached", oldValue, this.attached);
	}

	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		if (property.getName().equalsIgnoreCase(propertyName)) {
			Property oldValue = this.property;
			this.property = property;
			changeSupport.firePropertyChange("property", oldValue,
					this.property);
		} else {
			throw new IllegalArgumentException("property " + property.getName()
					+ " does not match the property type (" + this.propertyName
					+ ") that this widget can handle");
		}
	}

	public PropertyBuilder getPropertyBuilder() {
		return propertyBuilder;
	}

	public void setPropertyBuilder(PropertyBuilder propertyBuilder) {
		PropertyBuilder oldValue = this.propertyBuilder;
		this.propertyBuilder = propertyBuilder;
		changeSupport.firePropertyChange("propertyBuilder", oldValue,
				this.propertyBuilder);
	}
	
	/**
	 * This method provides a hook to perform operation after the log entry has been created.
	 * @param logEntry
	 */
	public void afterCreate(LogEntry logEntry){
		
	}

}
