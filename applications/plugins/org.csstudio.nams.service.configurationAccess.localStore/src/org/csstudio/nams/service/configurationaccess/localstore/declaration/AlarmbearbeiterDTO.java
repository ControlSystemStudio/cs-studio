
package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
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
@SequenceGenerator(name="user_id", sequenceName="AMS_USER_ID", allocationSize=1)
@Table(name = "AMS_User")
public class AlarmbearbeiterDTO implements NewAMSConfigurationElementDTO {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id")
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
	public AlarmbearbeiterDTO(final int userId, final int groupRef,
			final String userName, final String email,
			final String mobilePhone, final String phone,
			final String statusCode, final String confirmCode,
			final boolean active) {
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

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AlarmbearbeiterDTO)) {
			return false;
		}
		final AlarmbearbeiterDTO other = (AlarmbearbeiterDTO) obj;
		if (this.active != other.active) {
			return false;
		}
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
		if (this.groupRef != other.groupRef) {
			return false;
		}
		if (this.mobilePhone == null) {
			if (other.mobilePhone != null) {
				return false;
			}
		} else if (!this.mobilePhone.equals(other.mobilePhone)) {
			return false;
		}
		if (this.phone == null) {
			if (other.phone != null) {
				return false;
			}
		} else if (!this.phone.equals(other.phone)) {
			return false;
		}
		if (this.statusCode == null) {
			if (other.statusCode != null) {
				return false;
			}
		} else if (!this.statusCode.equals(other.statusCode)) {
			return false;
		}
		if (this.userId != other.userId) {
			return false;
		}
		if (this.userName == null) {
			if (other.userName != null) {
				return false;
			}
		} else if (!this.userName.equals(other.userName)) {
			return false;
		}
		return true;
	}

	/**
	 * @return the confirmCode
	 * 
	 * TODO Fachwert???
	 */
	public String getConfirmCode() {
		return this.confirmCode;
	}

	/**
	 * @return the email
	 * 
	 * TODO Fachwert?????
	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * @return the groupRef
	 */
	public int getGroupRef() {
		return this.groupRef;
	}

	/**
	 * @return the mobilePhone
	 * 
	 * TODO Fachwert???
	 */
	public String getMobilePhone() {
		return this.mobilePhone;
	}

	/**
	 * @return the phone
	 * 
	 * TODO Fachwert???
	 */
	public String getPhone() {
		return this.phone;
	}

	public PreferedAlarmType getPreferedAlarmType() {
		return PreferedAlarmType.getValueForId(this.preferedAlarmingType);
	}

	/**
	 * @return the statusCode
	 * 
	 * TODO Fachwert???
	 */
	public String getStatusCode() {
		return this.statusCode;
	}

	public String getUniqueHumanReadableName() {
		return this.getUserName();
	}

	/**
	 * @return the userId
	 */
	public int getUserId() {
		return this.userId;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return this.userName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.active;
		result = prime
				* result
				+ ((this.confirmCode == null) ? 0 : this.confirmCode.hashCode());
		result = prime * result
				+ ((this.email == null) ? 0 : this.email.hashCode());
		result = prime * result + this.groupRef;
		result = prime
				* result
				+ ((this.mobilePhone == null) ? 0 : this.mobilePhone.hashCode());
		result = prime * result
				+ ((this.phone == null) ? 0 : this.phone.hashCode());
		result = prime * result
				+ ((this.statusCode == null) ? 0 : this.statusCode.hashCode());
		result = prime * result + this.userId;
		result = prime * result
				+ ((this.userName == null) ? 0 : this.userName.hashCode());
		return result;
	}

	public boolean isActive() {
		return this.getActive() == 1;
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
	public boolean isInCategory(final int categoryDBId) {
		return this.groupRef == categoryDBId;
	}

	@SuppressWarnings("unused")
	public void setActive(final boolean active) {
		this.setActive((short) (active ? 1 : 0));
	}

	/**
	 * @param confirmCode
	 *            the confirmCode to set
	 */
	@SuppressWarnings("unused")
	public void setConfirmCode(final String confirmCode) {
		this.confirmCode = confirmCode;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	@SuppressWarnings("unused")
	public void setEmail(final String email) {
		this.email = email;
	}

	/**
	 * @param groupRef
	 *            the groupRef to set
	 */
	@SuppressWarnings("unused")
	public void setGroupRef(final int groupRef) {
		this.groupRef = groupRef;
	}

	/**
	 * @param mobilePhone
	 *            the mobilePhone to set
	 */
	@SuppressWarnings("unused")
	public void setMobilePhone(final String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	/**
	 * @param phone
	 *            the phone to set
	 */
	@SuppressWarnings("unused")
	public void setPhone(final String phone) {
		this.phone = phone;
	}

	public void setPreferedAlarmType(final PreferedAlarmType type) {
		this.preferedAlarmingType = (short) type.ordinal();
	}

	/**
	 * @param statusCode
	 *            the statusCode to set
	 */
	@SuppressWarnings("unused")
	public void setStatusCode(final String statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(final int userId) {
		this.userId = userId;
	}

	/**
	 * @param userName
	 *            the userName to set
	 */
	@SuppressWarnings("unused")
	public void setUserName(final String userName) {
		this.userName = userName;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder(this.getClass()
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
	 * @return the active
	 */
	private short getActive() {
		return this.active;
	}

	@SuppressWarnings("unused")
	private short getPreferedAlarmingType() {
		return this.preferedAlarmingType;
	}

	/**
	 * @param active
	 *            the active to set
	 */
	private void setActive(final short active) {
		this.active = active;
	}

	@SuppressWarnings("unused")
	private void setPreferedAlarmingType(final short preferedAlarmingType) {
		this.preferedAlarmingType = preferedAlarmingType;
	}
}
