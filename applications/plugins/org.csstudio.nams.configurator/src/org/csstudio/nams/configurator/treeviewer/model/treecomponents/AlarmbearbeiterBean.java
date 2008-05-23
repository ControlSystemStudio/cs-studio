package org.csstudio.nams.configurator.treeviewer.model.treecomponents;

import java.beans.PropertyChangeSupport;

public class AlarmbearbeiterBean extends
		AbstractConfigurationBean<AlarmbearbeiterBean> {

	public static enum PreferedAlarmType {
		NONE, EMAIL, SMS, VOICE;
	}

	public static enum PropertyNames {
		userID, confirmCode, name, email, mobilePhone, phone, statusCode, active, preferedAlarmType

	}

	private int userID;// PRIMARY KEY
	private String name = "";
	private String email = "";
	private String mobilePhone = "";
	private String phone = "";
	private String statusCode = "";
	private String confirmCode = "";
	private boolean isActive = false;
	private PreferedAlarmType preferedAlarmType = PreferedAlarmType.NONE;

	private PropertyChangeSupport propertyChangeSupport;

	public AlarmbearbeiterBean() {
		userID = -1;
		propertyChangeSupport = getPropertyChangeSupport();

	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		int oldValue = getUserID();
		this.userID = userID;
		propertyChangeSupport.firePropertyChange(PropertyNames.userID.name(),
				oldValue, getUserID());
	}

	public String getConfirmCode() {
		return confirmCode;
	}

	public void setConfirmCode(String confirmCode) {
		String oldValue = getConfirmCode();
		this.confirmCode = confirmCode;
		propertyChangeSupport.firePropertyChange(PropertyNames.confirmCode
				.name(), oldValue, getConfirmCode());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		String oldValue = getName();
		this.name = name;
		propertyChangeSupport.firePropertyChange(PropertyNames.name.name(),
				oldValue, getName());
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		String oldValue = getEmail();
		this.email = email;
		propertyChangeSupport.firePropertyChange(PropertyNames.email.name(),
				oldValue, getEmail());
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		String oldValue = getMobilePhone();
		this.mobilePhone = mobilePhone;
		propertyChangeSupport.firePropertyChange(PropertyNames.mobilePhone
				.name(), oldValue, getMobilePhone());
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		String oldValue = getPhone();
		this.phone = phone;
		propertyChangeSupport.firePropertyChange(PropertyNames.phone.name(),
				oldValue, getPhone());
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		String oldValue = getStatusCode();
		this.statusCode = statusCode;
		propertyChangeSupport.firePropertyChange(PropertyNames.statusCode
				.name(), oldValue, getStatusCode());
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		boolean oldValue = isActive();
		this.isActive = isActive;
		propertyChangeSupport.firePropertyChange(PropertyNames.active.name(),
				oldValue, isActive());
	}

	public PreferedAlarmType getPreferedAlarmType() {
		return preferedAlarmType;
	}

	public void setPreferedAlarmType(PreferedAlarmType preferedAlarmType) {
		PreferedAlarmType oldValue = getPreferedAlarmType();
		this.preferedAlarmType = preferedAlarmType;
		propertyChangeSupport.firePropertyChange(
				PropertyNames.preferedAlarmType.name(), oldValue,
				getPreferedAlarmType());
	}

	public void copyStateOf(AlarmbearbeiterBean otherBean) {
		throw new UnsupportedOperationException("not implemented yet.");
	}

	@Override
	public AlarmbearbeiterBean getClone() {

		AlarmbearbeiterBean bean = new AlarmbearbeiterBean();
		bean.setUserID(this.getUserID());
		bean.setActive(this.isActive);
		bean.setConfirmCode(this.getConfirmCode());
		bean.setEmail(this.getEmail());
		bean.setMobilePhone(this.getMobilePhone());
		bean.setName(this.getName());
		bean.setPhone(this.getPhone());
		bean.setPreferedAlarmType(this.getPreferedAlarmType());
		bean.setStatusCode(this.getStatusCode());

		return bean;
	}

	public String getDisplayName() {
		return getName() != null ? getName() : "(ohne Name)";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AlarmbearbeiterBean) {
			AlarmbearbeiterBean bean = (AlarmbearbeiterBean) obj;
			boolean isEqual = false;
			if (this.getUserID() == bean.getUserID()
					&& this.getConfirmCode().equals(bean.getConfirmCode())
					&& this.getEmail().equals(bean.getEmail())
					&& this.getMobilePhone().equals(bean.getMobilePhone())
					&& this.getName().equals(bean.getName())
					&& this.getPhone().equals(bean.getPhone())
					&& this.getPreferedAlarmType() == bean
							.getPreferedAlarmType()
					&& this.getStatusCode().equals(bean.getStatusCode())) {
				isEqual = true;

			}
			return isEqual;
		}
		return super.equals(obj);
	}

	@Override
	public void updateState(AlarmbearbeiterBean bean) {
		this.setUserID(bean.getUserID());
		this.setActive(bean.isActive);
		this.setConfirmCode(bean.getConfirmCode());
		this.setEmail(bean.getEmail());
		this.setMobilePhone(bean.getMobilePhone());
		this.setName(bean.getName());
		this.setPhone(bean.getPhone());
		this.setPreferedAlarmType(bean.getPreferedAlarmType());
		this.setStatusCode(this.getStatusCode());

	}
}
