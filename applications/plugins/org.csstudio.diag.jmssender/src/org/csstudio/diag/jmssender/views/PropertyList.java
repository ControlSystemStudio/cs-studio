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
 package org.csstudio.diag.jmssender.views;


import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.preferences.log.LogViewPreferenceConstants;
import org.csstudio.desy.startuphelper.CSSPlatformInfo;

public class PropertyList {

	private Vector<Property> properties = new Vector<Property>();
	private Set<IMessageViewer> changeListeners = new HashSet<IMessageViewer>();

	public PropertyList() {
		this.initData();
	}

	public void initData() {
		createProperties();
		setDefaultPropertyValues();
	}

	/**
	 * Sets the properties to default values. 
	 */
	private void setDefaultPropertyValues() {
		for (Property prop : properties) {
			if (prop.getProperty().equalsIgnoreCase("type")) prop.setValue("event"); //$NON-NLS-1$ //$NON-NLS-2$
			if (prop.getProperty().equalsIgnoreCase("host")) prop.setValue("krykpcp"); //$NON-NLS-1$ //$NON-NLS-2$
			if (prop.getProperty().equalsIgnoreCase("name")) prop.setValue("jmsSender"); //$NON-NLS-1$ //$NON-NLS-2$
			if (prop.getProperty().equalsIgnoreCase("severity")) prop.setValue("NO_ALARM"); //$NON-NLS-1$ //$NON-NLS-2$
			if (prop.getProperty().equalsIgnoreCase("status")) prop.setValue("NO_ALARM"); //$NON-NLS-1$ //$NON-NLS-2$
			if (prop.getProperty().equalsIgnoreCase("type")) prop.setValue("event"); //$NON-NLS-1$ //$NON-NLS-2$
			if (prop.getProperty().equalsIgnoreCase("eventtime")) prop.setValue("<Current time>"); //$NON-NLS-1$
			if (prop.getProperty().equalsIgnoreCase("host")) prop.setValue(CSSPlatformInfo.getInstance().getHostId()); //$NON-NLS-1$
			if (prop.getProperty().equalsIgnoreCase("user")) prop.setValue(CSSPlatformInfo.getInstance().getUserId()); //$NON-NLS-1$
		}
	}

	/**
	 * Creates the property objects based on the preferences of the Log Table
	 * View.
	 */
	private void createProperties() {
		properties.clear();
		Property property;
		String[] columnNames = JmsLogsPlugin.getDefault().getPluginPreferences()
		.getString(LogViewPreferenceConstants.P_STRING).split("\\?")[0].split(";"); //$NON-NLS-1$
		for (int i = 0; i < columnNames.length; i++) {
			property = new Property();
			property.setProperty((String) columnNames[i].split(",")[0]); //$NON-NLS-1$
			property.setValue(""); //$NON-NLS-1$
			properties.add(property);
		}
	}

	public Vector<Property> getProperties() {
		// TODO: don't return internal reference
		return properties;
	}

	public void firePropertyChangedEvent(Property property) {
		Iterator<?> iterator = changeListeners.iterator();
		while (iterator.hasNext())
			((IMessageViewer) iterator.next()).updateProperty(property);
	}

	public void removeChangeListener(IMessageViewer viewer) {
		changeListeners.remove(viewer);
	}

	public void addChangeListener(IMessageViewer viewer) {
		changeListeners.add(viewer);
	}
}
