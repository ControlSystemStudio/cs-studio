package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Dieses Daten-Transfer-Objekt stellt hält die Konfiguration einer AMS_USER.
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
@Table(name="AMS_User")
public class AlarmbearbeiterDTO {
	
	@Id @GeneratedValue
	@Column(name="iUserId", nullable=false, unique=true)
    private int userId;
	
	@Column(name="iGroupRef", nullable=false)
    private int groupRef = -1;
	
	@Column(name="cUserName", length=128)
	private String userName;
	
	@Column(name="cEmail", length=128)
	private String email;
	
	@Column(name="cMobilePhone", length=64)
	private String mobilePhone;
	
	@Column(name="cPhone", length=64)
	private String phone;
	
	@Column(name="cStatusCode", length=32)
	private String statusCode;
	
	@Column(name="cConfirmCode", length=32)
	private String confirmCode;
	
	@Column(name="sActive")
	private short active;
	

	/**
	 * @return the groupRef
	 */
	@SuppressWarnings("unused")
	private int getGroupRef() {
		return groupRef;
	}

	/**
	 * @param groupRef the groupRef to set
	 */
	@SuppressWarnings("unused")
	private void setGroupRef(int groupRef) {
		this.groupRef = groupRef;
	}

	/**
	 * @return the userName
	 */
	@SuppressWarnings("unused")
	private String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	@SuppressWarnings("unused")
	private void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the email
	 */
	@SuppressWarnings("unused")
	private String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	@SuppressWarnings("unused")
	private void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the mobilePhone
	 */
	@SuppressWarnings("unused")
	private String getMobilePhone() {
		return mobilePhone;
	}

	/**
	 * @param mobilePhone the mobilePhone to set
	 */
	@SuppressWarnings("unused")
	private void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	/**
	 * @return the phone
	 */
	@SuppressWarnings("unused")
	private String getPhone() {
		return phone;
	}

	/**
	 * @param phone the phone to set
	 */
	@SuppressWarnings("unused")
	private void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the statusCode
	 */
	@SuppressWarnings("unused")
	private String getStatusCode() {
		return statusCode;
	}

	/**
	 * @param statusCode the statusCode to set
	 */
	@SuppressWarnings("unused")
	private void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * @return the confirmCode
	 */
	@SuppressWarnings("unused")
	private String getConfirmCode() {
		return confirmCode;
	}

	/**
	 * @param confirmCode the confirmCode to set
	 */
	@SuppressWarnings("unused")
	private void setConfirmCode(String confirmCode) {
		this.confirmCode = confirmCode;
	}

	/**
	 * @return the active
	 */
	@SuppressWarnings("unused")
	private short getActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	@SuppressWarnings("unused")
	private void setActive(short active) {
		this.active = active;
	}

	/**
	 * @return the userId
	 */
	@SuppressWarnings("unused")
	private int getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	@SuppressWarnings("unused")
	private void setUserId(int userId) {
		this.userId = userId;
	}
	
	
}
