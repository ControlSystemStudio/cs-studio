
package org.csstudio.nams.configurator.beans;

import java.util.LinkedList;
import java.util.List;

import org.csstudio.nams.configurator.Messages;

public class AlarmbearbeiterGruppenBean extends
		AbstractConfigurationBean<AlarmbearbeiterGruppenBean> implements
		IReceiverBean {

	public static enum PropertyNames {
		groupID, name, minGroupMember, timeOutSec, active, users
	}

	private int groupID;// PRIMARY KEY
	private String name;
	private short minGroupMember;
	private int timeOutSec;
	private boolean isActive;
	private List<User2GroupBean> users = new LinkedList<User2GroupBean>();

	public AlarmbearbeiterGruppenBean() {
		this.groupID = -1;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final AlarmbearbeiterGruppenBean other = (AlarmbearbeiterGruppenBean) obj;
		if (this.groupID != other.groupID) {
			return false;
		}
		if (this.isActive != other.isActive) {
			return false;
		}
		if (this.minGroupMember != other.minGroupMember) {
			return false;
		}
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		if (this.timeOutSec != other.timeOutSec) {
			return false;
		}
		if (this.users == null) {
			if (other.users != null) {
				return false;
			}
		} else if (!this.users.equals(other.users)) {
			return false;
		}
		return true;
	}

	@Override
    public String getDisplayName() {
		return this.getName() == null ? Messages.AlarmbearbeiterGruppenBean_without_name : this.getName();
	}

	public int getGroupID() {
		return this.groupID;
	}

	@Override
    public int getID() {
		return this.getGroupID();
	}

	public short getMinGroupMember() {
		return this.minGroupMember;
	}

	public String getName() {
		return this.name;
	}

	public int getTimeOutSec() {
		return this.timeOutSec;
	}

	public List<User2GroupBean> getUsers() {
		return new LinkedList<User2GroupBean>(this.users);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + this.groupID;
		result = prime * result + (this.isActive ? 1231 : 1237);
		result = prime * result + this.minGroupMember;
		result = prime * result
				+ ((this.name == null) ? 0 : this.name.hashCode());
		result = prime * result + this.timeOutSec;
		result = prime * result
				+ ((this.users == null) ? 0 : this.users.hashCode());
		return result;
	}

	public boolean isActive() {
		return this.isActive;
	}

	public void setActive(final boolean isActive) {
		final boolean oldValue = this.isActive();
		this.isActive = isActive;
		this.pcs.firePropertyChange(PropertyNames.active.name(), oldValue, this
				.isActive());
	}

	public void setGroupID(final int groupID) {
		final int oldValue = this.getGroupID();
		this.groupID = groupID;

		this.pcs.firePropertyChange(PropertyNames.groupID.name(), oldValue,
				groupID);
	}

	@Override
    public void setID(final int id) {
		this.setGroupID(id);
	}

	public void setMinGroupMember(final short minGroupMember) {
		final short oldValue = minGroupMember;
		this.minGroupMember = minGroupMember;
		this.pcs.firePropertyChange(PropertyNames.minGroupMember.name(),
				oldValue, minGroupMember);
	}

	public void setName(final String name) {
		final String oldValue = this.getName();
		this.name = name;
		this.pcs.firePropertyChange(PropertyNames.name.name(), oldValue, this
				.getName());
	}

	public void setTimeOutSec(final int timeOutSec) {
		final int oldValue = this.timeOutSec;
		this.timeOutSec = timeOutSec;
		this.pcs.firePropertyChange(PropertyNames.timeOutSec.name(), oldValue,
				timeOutSec);
	}

	public void setUsers(final List<User2GroupBean> u) {
		final List<User2GroupBean> oldValue = this.users;
		// this.users.clear();
		// this.users.addAll(users);
		this.users = u;
		this.pcs
				.firePropertyChange(PropertyNames.users.name(), oldValue, u);
	}

	@Override
	public String toString() {
		return this.getDisplayName();
	}

	@Override
	protected void doUpdateState(final AlarmbearbeiterGruppenBean bean) {
		this.setActive(bean.isActive());
		this.setGroupID(bean.getGroupID());
		this.setMinGroupMember(bean.getMinGroupMember());
		this.setName(bean.getName());
		this.setTimeOutSec(bean.getTimeOutSec());
		final List<User2GroupBean> newList = new LinkedList<User2GroupBean>();
		for (final User2GroupBean mapBean : bean.getUsers()) {
			newList.add(mapBean.getClone());
		}
		this.setUsers(newList);
	}

	@Override
    public void setDisplayName(String name) {
		this.setName(name);
	}

}
