/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
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
