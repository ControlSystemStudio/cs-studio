package org.csstudio.nams.configurator.beans;

import java.util.Date;

public class User2GroupBean extends AbstractConfigurationBean<User2GroupBean> {

	/**
	 * iUserGroupRef NUMBER(11) NOT NULL, iUserRef NUMBER(11) NOT NULL, iPos
	 * NUMBER(11) NOT NULL, Benchrichtigungsreihenfolge sActive NUMBER(6),
	 * Gruppenzugeh�rigkeit aktiv?(0 - Inactive, 1 - Active) cActiveReason
	 * VARCHAR2(128), Grund/Ursache der An/Abmeldung tTimeChange NUMBER(14),
	 * Zeitstempel der letzten �nderung des Datensatzes PRIMARY
	 * KEY(iUserGroupRef,iUserRef)
	 */
	public enum PropertyNames {
		active, activeReason, lastChange

	}

	boolean active;
	String activeReason = "";
	// FIXME we shall not use Date
	Date lastChange = new Date();
	private AlarmbearbeiterBean userBean;

	// public enum PropertyNames {
	// position, active, activeReason, lastChange
	// }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime * 0 + (active ? 1231 : 1237);
		result = prime * result
				+ ((activeReason == null) ? 0 : activeReason.hashCode());
		result = prime * result
				+ ((lastChange == null) ? 0 : lastChange.hashCode());
		result = prime * result
				+ ((userBean == null) ? 0 : userBean.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		final User2GroupBean other = (User2GroupBean) obj;
		if (active != other.active)
			return false;
		if (activeReason == null) {
			if (other.activeReason != null)
				return false;
		} else if (!activeReason.equals(other.activeReason))
			return false;
		if (lastChange == null) {
			if (other.lastChange != null)
				return false;
		} else if (!lastChange.equals(other.lastChange))
			return false;
		if (userBean == null) {
			if (other.userBean != null)
				return false;
		} else if (!userBean.equals(other.userBean))
			return false;
		return true;
	}

	public User2GroupBean(AlarmbearbeiterBean userBean) {
		this.userBean = userBean;
	}
	/**
	 * Just for getClone()
	 */
	public User2GroupBean(){
		
	}

	@Override
	protected void doUpdateState(User2GroupBean bean) {
		userBean = bean.getUserBean();
		setActive(bean.isActive());
		setActiveReason(bean.getActiveReason());
		setLastChange(bean.getLastChange());
	}

	public String getDisplayName() {
		return null;
	}

	public int getID() {
		return 0;
	}

	public void setID(int id) {
	}

	public String getUserName() {
		return userBean.getName();
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		boolean oldValue = this.active;
		this.active = active;
		pcs.firePropertyChange(PropertyNames.active.name(), oldValue, active);
	}

	public String getActiveReason() {
		return activeReason;
	}

	public void setActiveReason(String activeReason) {
		String oldValue = this.activeReason;
		this.activeReason = activeReason;
		pcs.firePropertyChange(PropertyNames.activeReason.name(), oldValue,
				activeReason);
	}

	public Date getLastChange() {
		return lastChange;
	}

	public void setLastChange(Date lastChange) {
		Date oldValue = this.lastChange;
		this.lastChange = lastChange;
		pcs.firePropertyChange(PropertyNames.lastChange.name(), oldValue,
				lastChange);
	}

	public AlarmbearbeiterBean getUserBean() {
		return userBean;
	}
}
