package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Dieses Daten-Transfer-Objekt stellt hält die Konfiguration einer AMS_UserGroup.
 * 
 * Das Create-Statement für die Datenbank hat folgendes Aussehen:
 * 
 * <pre>
 *  create table AMS_UserGroup
 *  (
 *  iUserGroupId	INT NOT NULL,
 *  iGroupRef		INT default -1 NOT NULL,
 *  cUserGroupName	VARCHAR(128),
 *  sMinGroupMember	SMALLINT,
 *  iTimeOutSec		INT,
 *  sActive			SMALLINT default 1,
 *  PRIMARY KEY (iUserGroupId)
 *  );
 * </pre>
 */
@Entity
@Table(name = "AMS_UserGroup")
public class AlarmbearbeiterGruppenDTO {
	@Id @GeneratedValue
	@Column(name="iUserGroupId", nullable=false, unique=true)
	private int userGroupId;
	
	@Column(name="iGroupRef", nullable=false)
	private int groupRef = -1;
	
	@Column(name="cUserGroupName", length=128)
	private String userGroupName;
	
	@Column(name="sMinGroupMember")
	private short minGroupMember;
	
	@Column(name="iTimeOutSec")
	private int timeOutSec;
	
	@Column(name="sActive")
	private short active = 1;

	/**
	 * @return the userGroupId
	 */
	@SuppressWarnings("unused")
	private int getUserGroupId() {
		return userGroupId;
	}

	/**
	 * @param userGroupId the userGroupId to set
	 */
	@SuppressWarnings("unused")
	private void setUserGroupId(int userGroupId) {
		this.userGroupId = userGroupId;
	}

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
	 * @return the userGroupName
	 */
	@SuppressWarnings("unused")
	private String getUserGroupName() {
		return userGroupName;
	}

	/**
	 * @param userGroupName the userGroupName to set
	 */
	@SuppressWarnings("unused")
	private void setUserGroupName(String userGroupName) {
		this.userGroupName = userGroupName;
	}

	/**
	 * @return the minGroupMember
	 */
	@SuppressWarnings("unused")
	private short getMinGroupMember() {
		return minGroupMember;
	}

	/**
	 * @param minGroupMember the minGroupMember to set
	 */
	@SuppressWarnings("unused")
	private void setMinGroupMember(short minGroupMember) {
		this.minGroupMember = minGroupMember;
	}

	/**
	 * @return the timeOutSec
	 */
	@SuppressWarnings("unused")
	private int getTimeOutSec() {
		return timeOutSec;
	}

	/**
	 * @param timeOutSec the timeOutSec to set
	 */
	@SuppressWarnings("unused")
	private void setTimeOutSec(int timeOutSec) {
		this.timeOutSec = timeOutSec;
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
	
	
}
