package org.csstudio.nams.configurator.beans;

import java.beans.PropertyChangeSupport;

public class AlarmbearbeiterGruppenBean extends
		AbstractConfigurationBean<AlarmbearbeiterGruppenBean> {

	public static enum AlarmbearbeitergruppenBeanPropertyNames {
		groupID, name, minGroupMember, timeOutSec, active
	}

	private int groupID;// PRIMARY KEY
	private String name;
	private short minGroupMember;
	private int timeOutSec;
	private boolean isActive;
	private PropertyChangeSupport propertyChangeSupport;

	public AlarmbearbeiterGruppenBean() {
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

	public String getDisplayName() {
		return getName() == null ? "(ohne Name)" : getName();
	}

	public void copyStateOf(AlarmbearbeiterGruppenBean otherBean) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("not implemented yet.");
	}

	@Override
	public AlarmbearbeiterGruppenBean getClone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + groupID;
		result = prime * result + (isActive ? 1231 : 1237);
		result = prime * result + minGroupMember;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime
				* result
				+ ((propertyChangeSupport == null) ? 0 : propertyChangeSupport
						.hashCode());
		result = prime * result + timeOutSec;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AlarmbearbeiterGruppenBean other = (AlarmbearbeiterGruppenBean) obj;
		if (groupID != other.groupID)
			return false;
		if (isActive != other.isActive)
			return false;
		if (minGroupMember != other.minGroupMember)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (propertyChangeSupport == null) {
			if (other.propertyChangeSupport != null)
				return false;
		} else if (!propertyChangeSupport.equals(other.propertyChangeSupport))
			return false;
		if (timeOutSec != other.timeOutSec)
			return false;
		return true;
	}

	@Override
	public void updateState(AlarmbearbeiterGruppenBean bean) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getID() {
		return this.getGroupID();
	}
	
	@Override
	public String toString() {
		return getDisplayName();
	}

}
