package org.csstudio.nams.configurator.treeviewer.model.treecomponents;

import java.beans.PropertyChangeSupport;

import org.csstudio.nams.configurator.treeviewer.model.ConfigurationBean;

public class AlarmbearbeitergruppenBean extends ConfigurationBean {

	public static enum AlarmbearbeitergruppenBeanPropertyNames {
		groupID, name, minGroupMember, timeOutSec, active

	}

	private int groupID;// PRIMARY KEY
	private String name;
	private short minGroupMember;
	private int timeOutSec;
	private boolean isActive;
	private PropertyChangeSupport propertyChangeSupport;

	public AlarmbearbeitergruppenBean() {
		groupID = -1;
		propertyChangeSupport = getPropertyChangeSupport();
	}

	public int getGroupID() {
		return groupID;
	}

	public void setGroupID(int groupID) {
		int oldValue = getGroupID();
		this.groupID = groupID;

		propertyChangeSupport.firePropertyChange(
				AlarmbearbeitergruppenBeanPropertyNames.groupID.name(),
				oldValue, groupID);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		String oldValue = getName();
		this.name = name;
		propertyChangeSupport.firePropertyChange(
				AlarmbearbeitergruppenBeanPropertyNames.name.name(), oldValue,
				getName());
	}

	public short getMinGroupMember() {
		return minGroupMember;
	}

	public void setMinGroupMember(short minGroupMember) {
		short oldValue = getMinGroupMember();
		this.minGroupMember = minGroupMember;
		propertyChangeSupport.firePropertyChange(
				AlarmbearbeitergruppenBeanPropertyNames.minGroupMember.name(),
				oldValue, getMinGroupMember());
	}

	public int getTimeOutSec() {
		return timeOutSec;
	}

	public void setTimeOutSec(int timeOutSec) {
		int oldValue = getTimeOutSec();
		this.timeOutSec = timeOutSec;
		propertyChangeSupport.firePropertyChange(
				AlarmbearbeitergruppenBeanPropertyNames.timeOutSec.name(),
				oldValue, getTimeOutSec());
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		boolean oldValue = isActive();
		this.isActive = isActive;
		propertyChangeSupport.firePropertyChange(
				AlarmbearbeitergruppenBeanPropertyNames.active.name(),
				oldValue, isActive());
	}

	@Override
	public String getDisplayName() {
		return getName() != null ? getName() : "(ohne Name)";
	}

}
