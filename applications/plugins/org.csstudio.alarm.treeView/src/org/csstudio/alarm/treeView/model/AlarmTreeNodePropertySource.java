package org.csstudio.alarm.treeView.model;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * Provides the properties of an {@link IAlarmTreeNode}.
 * 
 * @author Joerg Rathlev
 */
public class AlarmTreeNodePropertySource implements IPropertySource {

	/**
	 * The descriptors of the properties provided by this source.
	 */
	private static final IPropertyDescriptor[] propertyDescriptors;
	
	/**
	 * The node for which this property source provides properties.
	 */
	private IAlarmTreeNode node;
	
	/**
	 * IDs for the properties.
	 */
	private static enum PropertyID {
		NAME,
		ALARM,
	}

	static {
		propertyDescriptors = new IPropertyDescriptor[2];
		PropertyDescriptor descriptor;
		
		// name
		descriptor = new PropertyDescriptor(PropertyID.NAME, "Name");
		descriptor.setAlwaysIncompatible(true);
		propertyDescriptors[0] = descriptor;
		
		// alarm state
		descriptor = new PropertyDescriptor(PropertyID.ALARM, "Alarm");
		propertyDescriptors[1] = descriptor;
	}
	
	/**
	 * Creates a new property source for the given node.
	 * @param node the node.
	 */
	public AlarmTreeNodePropertySource(IAlarmTreeNode node) {
		this.node = node;
	}
	
	public Object getEditableValue() {
		// not editable
		return null;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return propertyDescriptors;
	}
	
	public Object getPropertyValue(Object id) {
		if (id instanceof PropertyID) {
			switch ((PropertyID) id) {
			case NAME:
				return node.getName();
			case ALARM:
				return node.getAlarmSeverity();
			}
		}
		return null;
	}

	public boolean isPropertySet(Object id) {
		if (id instanceof PropertyID) {
			switch ((PropertyID) id) {
			case NAME:
				// Name doesn't have a default value, always return false.
				return false;
			case ALARM:
				// For alarms, the default is NO_ALARM. Return true if the
				// current value is different from that default.
				return !node.getAlarmSeverity().equals(Severity.NO_ALARM);
			}
		}
		return false;
	}

	public void resetPropertyValue(Object id) {
		// do nothing (values cannot be reset)
	}

	public void setPropertyValue(Object id, Object value) {
		// do nothing (values cannot be changed)
	}
}
