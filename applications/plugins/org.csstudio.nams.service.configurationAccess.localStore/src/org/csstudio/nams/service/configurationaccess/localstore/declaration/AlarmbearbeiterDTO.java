package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.PreferedAlarmType;

/**
 * Dieses Daten-Transfer-Objekt enthaelt die Konfiguration eines AMS_USER.
 * 
 * Das Create-Statement für die Datenbank hat folgendes Aussehen:
 * 
 * <pre>
 *  create table AMS_User
 *  (
 *  iUserId			INT NOT NULL,
 *  iGroupRef		INT default -1 NOT NULL,
 *  cUserName		VARCHAR(128),
 *  cEmail			VARCHAR(128),
 *  cMobilePhone	VARCHAR(64),
 *  cPhone			VARCHAR(64),
 *  cStatusCode		VARCHAR(32),
 *  cConfirmCode	VARCHAR(32),
 *  sActive			SMALLINT,
 *  PRIMARY KEY (iUserId)
 *  );
 * </pre>
 */
@Entity
@Table(name = "AMS_User")
public class AlarmbearbeiterDTO implements NewAMSConfigurationElementDTO {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "iUserId", nullable = false, unique = true)
	private int userId;

	/**
	 * Kategorie
	 */
	@Column(name = "iGroupRef", nullable = false)
	private int groupRef = -1;

	@Column(name = "cUserName", length = 128)
	private String userName;

	@Column(name = "cEmail", length = 128)
	private String email;

	@Column(name = "cMobilePhone", length = 64)
	private String mobilePhone;

	@Column(name = "cPhone", length = 64)
	private String phone;

	@Column(name = "cStatusCode", length = 32)
	private String statusCode;

	@Column(name = "cConfirmCode", length = 32)
	private String confirmCode;

	@Column(name = "sActive")
	private short active;
	
	@Column(name = "SPREFERREDALARMINGTYPERR", length = 5)
	private short preferedAlarmingType;

	public AlarmbearbeiterDTO() {
	}

	/**
	 * This constructor is normally used by the configurator and by tests. It
	 * should not be used inside another NAMS application.
	 * 
	 * @param userId
	 * @param categoryRef
	 *            TODO Dieses ist die Rubrik-Id, besser wäre ein CategoryDTO!
	 * @param userName
	 * @param email
	 * @param mobilePhone
	 * @param phone
	 * @param statusCode
	 * @param confirmCode
	 * @param active
	 */
	public AlarmbearbeiterDTO(int userId, int groupRef, String userName,
			String email, String mobilePhone, String phone, String statusCode,
			String confirmCode, boolean active) {
		super();
		this.userId = userId;
		this.groupRef = groupRef;
		this.userName = userName;
		this.email = email;
		this.mobilePhone = mobilePhone;
		this.phone = phone;
		this.statusCode = statusCode;
		this.confirmCode = confirmCode;
		this.active = (short) (active == true ? 1 : 0);
	}

	/**
	 * @return the groupRef
	 */
	int getGroupRef() {
		return groupRef;
	}

	/**
	 * @param groupRef
	 *            the groupRef to set
	 */
	@SuppressWarnings("unused")
	private void setGroupRef(int groupRef) {
		this.groupRef = groupRef;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	@SuppressWarnings("unused")
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the email
	 * 
	 * TODO Fachwert?????
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	@SuppressWarnings("unused")
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the mobilePhone
	 * 
	 * TODO Fachwert???
	 */
	public String getMobilePhone() {
		return mobilePhone;
	}

	/**
	 * @param mobilePhone
	 *            the mobilePhone to set
	 */
	@SuppressWarnings("unused")
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	/**
	 * @return the phone
	 * 
	 * TODO Fachwert???
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone
	 *            the phone to set
	 */
	@SuppressWarnings("unused")
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the statusCode
	 * 
	 * TODO Fachwert???
	 */
	public String getStatusCode() {
		return statusCode;
	}

	/**
	 * @param statusCode
	 *            the statusCode to set
	 */
	@SuppressWarnings("unused")
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * @return the confirmCode
	 * 
	 * TODO Fachwert???
	 */
	public String getConfirmCode() {
		return confirmCode;
	}

	/**
	 * @param confirmCode
	 *            the confirmCode to set
	 */
	@SuppressWarnings("unused")
	public void setConfirmCode(String confirmCode) {
		this.confirmCode = confirmCode;
	}

	/**
	 * @return the active
	 */
	private short getActive() {
		return active;
	}

	public boolean isActive() {
		return getActive() == 1;
	}

	@SuppressWarnings("unused")
	public void setActive(boolean active) {
		setActive((short) (active ? 1 : 0));
	}

	/**
	 * @param active
	 *            the active to set
	 */
	private void setActive(short active) {
		this.active = active;
	}

	/**
	 * @return the userId
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + active;
		result = prime * result
				+ ((confirmCode == null) ? 0 : confirmCode.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + groupRef;
		result = prime * result
				+ ((mobilePhone == null) ? 0 : mobilePhone.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		result = prime * result
				+ ((statusCode == null) ? 0 : statusCode.hashCode());
		result = prime * result + userId;
		result = prime * result
				+ ((userName == null) ? 0 : userName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof AlarmbearbeiterDTO))
			return false;
		final AlarmbearbeiterDTO other = (AlarmbearbeiterDTO) obj;
		if (active != other.active)
			return false;
		if (confirmCode == null) {
			if (other.confirmCode != null)
				return false;
		} else if (!confirmCode.equals(other.confirmCode))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (groupRef != other.groupRef)
			return false;
		if (mobilePhone == null) {
			if (other.mobilePhone != null)
				return false;
		} else if (!mobilePhone.equals(other.mobilePhone))
			return false;
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		if (statusCode == null) {
			if (other.statusCode != null)
				return false;
		} else if (!statusCode.equals(other.statusCode))
			return false;
		if (userId != other.userId)
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(this.getClass()
				.getSimpleName());
		builder.append(": name: ");
		builder.append(this.userName);
		builder.append(", GroupRef: ");
		builder.append(this.groupRef);
		builder.append(", userId: ");
		builder.append(this.userId);
		builder.append(", active: ");
		builder.append(this.active);
		return builder.toString();
	}

	/**
	 * Prueft, ob dieser Alarmbearbeiter in der Rubrik mit dem angegebenen
	 * Datenbank-Rubrik-Primaerschluessel (GroupRef) enthalten ist.
	 * 
	 * TODO Besser hier ein CategoryDTO verwenden.
	 * 
	 * @param categoryDBId 
	 * @return
	 */
	public boolean isInCategory(int categoryDBId) {
		return this.groupRef == categoryDBId;
	}

	public String getUniqueHumanReadableName() {
		return getUserName();
	}

	@SuppressWarnings("unused")
	private short getPreferedAlarmingType() {
		return preferedAlarmingType;
	}

	@SuppressWarnings("unused")
	private void setPreferedAlarmingType(short preferedAlarmingType) {
		this.preferedAlarmingType = preferedAlarmingType;
	}
	
	public PreferedAlarmType getPreferedAlarmType(){
		return PreferedAlarmType.getValueForId(preferedAlarmingType);
	}
	public void setPreferedAlarmType(PreferedAlarmType type){
		preferedAlarmingType = (short) type.ordinal();
	}
}
