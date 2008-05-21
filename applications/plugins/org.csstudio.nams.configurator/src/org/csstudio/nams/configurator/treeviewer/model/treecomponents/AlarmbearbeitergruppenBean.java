package org.csstudio.nams.configurator.treeviewer.model.treecomponents;

import java.beans.PropertyChangeSupport;

import org.csstudio.nams.configurator.treeviewer.model.ObservableBean;

public class AlarmbearbeitergruppenBean extends ObservableBean{
	
	public static enum AlarmbearbeitergruppenBeanPropertyNames {
		userGroupID, name, minGroupMember, timeOutSec, active
			
	}
	
	private int userGroupID;// PRIMARY KEY
	private String name;
	private short minGroupMember;
	private int timeOutSec;
	private boolean isActive;
	private PropertyChangeSupport propertyChangeSupport;
	
	public AlarmbearbeitergruppenBean() {
		userGroupID = -1;
		propertyChangeSupport = getPropertyChangeSupport();
	}

	public int getUserGroupID() {
		return userGroupID;
	}

	public void setUserGroupID(int userGroupID) {
		int oldValue = getUserGroupID();
		this.userGroupID = userGroupID;
		propertyChangeSupport.firePropertyChange(AlarmbearbeitergruppenBeanPropertyNames.userGroupID.name(), oldValue, getUserGroupID());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		String oldValue = getName();
		this.name = name;
		propertyChangeSupport.firePropertyChange(AlarmbearbeitergruppenBeanPropertyNames.name.name(), oldValue, getName());
	}

	public short getMinGroupMember() {
		return minGroupMember;
	}

	public void setMinGroupMember(short minGroupMember) {
		short oldValue = getMinGroupMember();
		this.minGroupMember = minGroupMember;
		propertyChangeSupport.firePropertyChange(AlarmbearbeitergruppenBeanPropertyNames.minGroupMember.name(), oldValue, getMinGroupMember());
	}

	public int getTimeOutSec() {
		return timeOutSec;
	}

	public void setTimeOutSec(int timeOutSec) {
		int oldValue = getTimeOutSec();
		this.timeOutSec = timeOutSec;
		propertyChangeSupport.firePropertyChange(AlarmbearbeitergruppenBeanPropertyNames.timeOutSec.name(), oldValue, getTimeOutSec());
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		boolean oldValue = isActive();
		this.isActive = isActive;
		propertyChangeSupport.firePropertyChange(AlarmbearbeitergruppenBeanPropertyNames.active.name(), oldValue, isActive());
	}
	
}
