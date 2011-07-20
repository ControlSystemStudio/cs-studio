
package org.csstudio.nams.configurator.beans;

import org.csstudio.nams.configurator.Messages;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.PreferedAlarmType;

public class AlarmbearbeiterBean extends
		AbstractConfigurationBean<AlarmbearbeiterBean> implements IReceiverBean {

	public static enum PropertyNames {
		userID, confirmCode, name, email, mobilePhone, phone, statusCode, active, preferedAlarmType
	}

	private int userID;// PRIMARY KEY
	private String name = ""; //$NON-NLS-1$
	private String email = ""; //$NON-NLS-1$
	private String mobilePhone = ""; //$NON-NLS-1$
	private String phone = ""; //$NON-NLS-1$
	private String statusCode = ""; //$NON-NLS-1$
	private String confirmCode = ""; //$NON-NLS-1$
	private boolean isActive = false;
	private PreferedAlarmType preferedAlarmType = PreferedAlarmType.NONE;

	public AlarmbearbeiterBean() {
		this.userID = -1;
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
		final AlarmbearbeiterBean other = (AlarmbearbeiterBean) obj;
		if (this.confirmCode == null) {
			if (other.confirmCode != null) {
				return false;
			}
		} else if (!this.confirmCode.equals(other.confirmCode)) {
			return false;
		}
		if (this.email == null) {
			if (other.email != null) {
				return false;
			}
		} else if (!this.email.equals(other.email)) {
			return false;
		}
		if (this.isActive != other.isActive) {
			return false;
		}
		if (this.mobilePhone == null) {
			if (other.mobilePhone != null) {
				return false;
			}
		} else if (!this.mobilePhone.equals(other.mobilePhone)) {
			return false;
		}
		if (this.name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!this.name.equals(other.name)) {
			return false;
		}
		if (this.phone == null) {
			if (other.phone != null) {
				return false;
			}
		} else if (!this.phone.equals(other.phone)) {
			return false;
		}
		if (this.preferedAlarmType == null) {
			if (other.preferedAlarmType != null) {
				return false;
			}
		} else if (!this.preferedAlarmType.equals(other.preferedAlarmType)) {
			return false;
		}
		if (this.statusCode == null) {
			if (other.statusCode != null) {
				return false;
			}
		} else if (!this.statusCode.equals(other.statusCode)) {
			return false;
		}
		if (this.userID != other.userID) {
			return false;
		}
		return true;
	}

	public String getConfirmCode() {
		return this.confirmCode;
	}

	@Override
    public String getDisplayName() {
		return this.getName() != null ? this.getName() : Messages.AlarmbearbeiterBean_without_name;
	}

	public String getEmail() {
		return this.email;
	}

	@Override
    public int getID() {
		return this.getUserID();
	}

	public String getMobilePhone() {
		return this.mobilePhone;
	}

	public String getName() {
		return this.name;
	}

	public String getPhone() {
		return this.phone;
	}

	public PreferedAlarmType getPreferedAlarmType() {
		return this.preferedAlarmType;
	}

	public String getStatusCode() {
		return this.statusCode;
	}

	public int getUserID() {
		return this.userID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime
				* result
				+ ((this.confirmCode == null) ? 0 : this.confirmCode.hashCode());
		result = prime * result
				+ ((this.email == null) ? 0 : this.email.hashCode());
		result = prime * result + (this.isActive ? 1231 : 1237);
		result = prime
				* result
				+ ((this.mobilePhone == null) ? 0 : this.mobilePhone.hashCode());
		result = prime * result
				+ ((this.name == null) ? 0 : this.name.hashCode());
		result = prime * result
				+ ((this.phone == null) ? 0 : this.phone.hashCode());
		result = prime
				* result
				+ ((this.preferedAlarmType == null) ? 0
						: this.preferedAlarmType.hashCode());
		result = prime * result
				+ ((this.statusCode == null) ? 0 : this.statusCode.hashCode());
		result = prime * result + this.userID;
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

	public void setConfirmCode(final String confirmCode) {
		final String oldValue = this.getConfirmCode();
		this.confirmCode = (confirmCode != null) ? confirmCode : ""; //$NON-NLS-1$
		this.pcs.firePropertyChange(PropertyNames.confirmCode.name(), oldValue,
				this.getConfirmCode());
	}

	public void setEmail(final String email) {
		final String oldValue = this.getEmail();
		this.email = (email != null) ? email : ""; //$NON-NLS-1$
		this.pcs.firePropertyChange(PropertyNames.email.name(), oldValue, this
				.getEmail());
	}

	@Override
    public void setID(final int id) {
		this.setUserID(id);
	}

	public void setMobilePhone(final String mobilePhone) {
		final String oldValue = this.getMobilePhone();
		this.mobilePhone = (mobilePhone != null) ? mobilePhone : ""; //$NON-NLS-1$
		this.pcs.firePropertyChange(PropertyNames.mobilePhone.name(), oldValue,
				this.getMobilePhone());
	}

	public void setName(final String name) {
		final String oldValue = this.getName();
		this.name = (name != null) ? name : ""; //$NON-NLS-1$
		this.pcs.firePropertyChange(PropertyNames.name.name(), oldValue, this
				.getName());
	}

	public void setPhone(final String phone) {
		final String oldValue = this.getPhone();
		this.phone = (phone != null) ? phone : ""; //$NON-NLS-1$
		this.pcs.firePropertyChange(PropertyNames.phone.name(), oldValue, this
				.getPhone());
	}

	public void setPreferedAlarmType(final PreferedAlarmType preferedAlarmType) {
		final PreferedAlarmType oldValue = this.getPreferedAlarmType();
		this.preferedAlarmType = preferedAlarmType;
		this.pcs.firePropertyChange(PropertyNames.preferedAlarmType.name(),
				oldValue, this.getPreferedAlarmType());
	}

	public void setStatusCode(final String statusCode) {
		final String oldValue = this.getStatusCode();
		this.statusCode = (statusCode != null) ? statusCode : ""; //$NON-NLS-1$
		this.pcs.firePropertyChange(PropertyNames.statusCode.name(), oldValue,
				this.getStatusCode());
	}

	/**
	 * Shall not be used in user generated code. The UserID is an autogenerated
	 * value.
	 */
	public void setUserID(final int userID) {
		final int oldValue = this.getUserID();
		this.userID = userID;
		this.pcs.firePropertyChange(PropertyNames.userID.name(), oldValue, this
				.getUserID());
	}

	@Override
	public String toString() {
		return this.getDisplayName();
	}

	@Override
	protected void doUpdateState(final AlarmbearbeiterBean bean) {
		this.setUserID(bean.getUserID());
		this.setActive(bean.isActive);
		this.setConfirmCode(bean.getConfirmCode());
		this.setEmail(bean.getEmail());
		this.setMobilePhone(bean.getMobilePhone());
		this.setName(bean.getName());
		this.setPhone(bean.getPhone());
		this.setPreferedAlarmType(bean.getPreferedAlarmType());
		this.setStatusCode(bean.getStatusCode());
	}

	@Override
    public void setDisplayName(String name) {
		this.setName(name);
	}
}
