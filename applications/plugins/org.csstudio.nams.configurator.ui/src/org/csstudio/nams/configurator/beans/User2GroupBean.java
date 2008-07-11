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
		position, active, activeReason, lastChange

	}

	int position;
	boolean active;
	String activeReason = "";
	// FIXME we shall not use Date
	Date lastChange;
	private final AlarmbearbeiterBean userBean;
	private final AlarmbearbeiterGruppenBean groupBean;

	// public enum PropertyNames {
	// position, active, activeReason, lastChange
	// }

	public User2GroupBean(AlarmbearbeiterBean userBean,
			AlarmbearbeiterGruppenBean groupBean, int position) {
		this.userBean = userBean;
		this.groupBean = groupBean;
		this.position = position;
	}

	@Override
	protected void doUpdateState(User2GroupBean bean) {
		userBean.updateState(bean.getUserBean());
		groupBean.updateState(bean.getGroupBean());
		setPosition(bean.getPosition());
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

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		int oldValue = this.position;
		this.position = position;
		pcs.firePropertyChange(PropertyNames.position.name(), oldValue,
				position);
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

	public AlarmbearbeiterGruppenBean getGroupBean() {
		return groupBean;
	}
}
