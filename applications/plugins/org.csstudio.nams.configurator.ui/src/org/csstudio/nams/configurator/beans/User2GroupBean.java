
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
	String activeReason = ""; //$NON-NLS-1$
	// FIXME we shall not use Date
	Date lastChange = new Date();
	private AlarmbearbeiterBean userBean;

	// public enum PropertyNames {
	// position, active, activeReason, lastChange
	// }

	/**
	 * Just for getClone()
	 */
	public User2GroupBean() {
	    // Nothing to do
	}

	public User2GroupBean(final AlarmbearbeiterBean userBean) {
		this.userBean = userBean;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final User2GroupBean other = (User2GroupBean) obj;
		if (this.active != other.active) {
			return false;
		}
		if (this.activeReason == null) {
			if (other.activeReason != null) {
				return false;
			}
		} else if (!this.activeReason.equals(other.activeReason)) {
			return false;
		}
		if (this.lastChange == null) {
			if (other.lastChange != null) {
				return false;
			}
		} else if (!this.lastChange.equals(other.lastChange)) {
			return false;
		}
		if (this.userBean == null) {
			if (other.userBean != null) {
				return false;
			}
		} else if (!this.userBean.equals(other.userBean)) {
			return false;
		}
		return true;
	}

	public String getActiveReason() {
		return this.activeReason;
	}

	@Override
    public String getDisplayName() {
		return null;
	}

	@Override
    public int getID() {
		return 0;
	}

	public Date getLastChange() {
		return this.lastChange;
	}

	public AlarmbearbeiterBean getUserBean() {
		return this.userBean;
	}

	public String getUserName() {
		return this.userBean.getName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime * 0 + (this.active ? 1231 : 1237);
		result = prime
				* result
				+ ((this.activeReason == null) ? 0 : this.activeReason
						.hashCode());
		result = prime * result
				+ ((this.lastChange == null) ? 0 : this.lastChange.hashCode());
		result = prime * result
				+ ((this.userBean == null) ? 0 : this.userBean.hashCode());
		return result;
	}

	public boolean isActive() {
		return this.active;
	}

	public void setActive(final boolean active) {
		final boolean oldValue = this.active;
		this.active = active;
		this.pcs.firePropertyChange(PropertyNames.active.name(), oldValue,
				active);
	}

	public void setActiveReason(final String activeReason) {
		final String oldValue = this.activeReason;
		this.activeReason = (activeReason != null) ? activeReason : ""; //$NON-NLS-1$
		this.pcs.firePropertyChange(PropertyNames.activeReason.name(),
				oldValue, activeReason);
	}

	@Override
    public void setID(final int id) {
	    // Nothing to do
	}

	public void setLastChange(final Date lastChange) {
		final Date oldValue = this.lastChange;
		this.lastChange = lastChange;
		this.pcs.firePropertyChange(PropertyNames.lastChange.name(), oldValue,
				lastChange);
	}

	@Override
	protected void doUpdateState(final User2GroupBean bean) {
		this.userBean = bean.getUserBean();
		this.setActive(bean.isActive());
		this.setActiveReason(bean.getActiveReason());
		this.setLastChange(bean.getLastChange());
	}

	@Override
    public void setDisplayName(String name) {
		// Nothing to do here
	}
}
