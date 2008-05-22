package org.csstudio.nams.configurator.treeviewer.model.treecomponents;

import java.beans.PropertyChangeSupport;

import org.csstudio.nams.configurator.treeviewer.model.ConfigurationBean;

public class AlarmbearbeiterBean extends ConfigurationBean {

	public static enum PreferedAlarmType {
		NONE, EMAIL, SMS, VOICE;
	}

	public static enum AlarmbearbeiterBeanPropertyNames {
		userID, confirmCode, name, email, mobilePhone, phone, statusCode, active, preferedAlarmType

	}

	private int userID;// PRIMARY KEY
	private String name;
	private String email;
	private String mobilePhone;
	private String phone;
	private String statusCode;
	private String confirmCode;
	private boolean isActive;
	private PreferedAlarmType preferedAlarmType;

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
		propertyChangeSupport.firePropertyChange(
				AlarmbearbeiterBeanPropertyNames.userID.name(), oldValue,
				getUserID());
	}

	public String getConfirmCode() {
		return confirmCode;
	}

	public void setConfirmCode(String confirmCode) {
		String oldValue = getConfirmCode();
		this.confirmCode = confirmCode;
		propertyChangeSupport.firePropertyChange(
				AlarmbearbeiterBeanPropertyNames.confirmCode.name(), oldValue,
				getConfirmCode());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		String oldValue = getName();
		this.name = name;
		propertyChangeSupport.firePropertyChange(
				AlarmbearbeiterBeanPropertyNames.name.name(), oldValue,
				getName());
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		String oldValue = getEmail();
		this.email = email;
		propertyChangeSupport.firePropertyChange(
				AlarmbearbeiterBeanPropertyNames.email.name(), oldValue,
				getEmail());
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		String oldValue = getMobilePhone();
		this.mobilePhone = mobilePhone;
		propertyChangeSupport.firePropertyChange(
				AlarmbearbeiterBeanPropertyNames.mobilePhone.name(), oldValue,
				getMobilePhone());
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		String oldValue = getPhone();
		this.phone = phone;
		propertyChangeSupport.firePropertyChange(
				AlarmbearbeiterBeanPropertyNames.phone.name(), oldValue,
				getPhone());
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		String oldValue = getStatusCode();
		this.statusCode = statusCode;
		propertyChangeSupport.firePropertyChange(
				AlarmbearbeiterBeanPropertyNames.statusCode.name(), oldValue,
				getStatusCode());
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		boolean oldValue = isActive();
		this.isActive = isActive;
		propertyChangeSupport.firePropertyChange(
				AlarmbearbeiterBeanPropertyNames.active.name(), oldValue,
				isActive());
	}

	public PreferedAlarmType getPreferedAlarmType() {
		return preferedAlarmType;
	}

	public void setPreferedAlarmType(PreferedAlarmType preferedAlarmType) {
		PreferedAlarmType oldValue = getPreferedAlarmType();
		this.preferedAlarmType = preferedAlarmType;
		propertyChangeSupport.firePropertyChange(
				AlarmbearbeiterBeanPropertyNames.preferedAlarmType.name(),
				oldValue, getPreferedAlarmType());
	}

	@Override
	public String getDisplayName() {
		return getName() != null ? getName() : "(ohne Name)";
	}
}
