package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Dieses Daten-Transfer-Objekt stellt hält die Konfiguration einer
 * AMS_UserGroup_User. Dies ist ein JOIN mit zusätzlichen Daten.
 * 
 * Das Create-Statement für die Datenbank hat folgendes Aussehen:
 * 
 * <pre>
 *  create table AMS_UserGroup_User
 *  (
 *  iUserGroupRef	INT NOT NULL,
 *  iUserRef		INT NOT NULL,
 *  iPos			INT NOT NULL,
 *  sActive			SMALLINT,
 *  cActiveReason	VARCHAR(128),
 *  tTimeChange		BIGINT,
 *  PRIMARY KEY(iUserGroupRef,iUserRef,iPos)
 *  );
 * </pre>
 */
@Entity
@Table(name = "AMS_UserGroup_User")
public class AlarmbearbeiterGruppenZuAlarmbearbeiterDTO {
	@Id
	@Column(name="iUserGroupRef", nullable=false)
	private int userGroupRef;
	
	@Id
	@Column(name="iUserRef", nullable=false)
	private int userRef;
	
	@Id
	@Column(name="iPos", nullable=false)
	private int pos;
	
	@Column(name="sActive")
	private short active;
	
	@Column(name="cActiveReason", length=128)
	private String activeReason;
	
	@Column(name="tTimeChange")
	private long timeChange;

	/**
	 * @return the userGroupRef
	 */
	@SuppressWarnings("unused")
	private int getUserGroupRef() {
		return userGroupRef;
	}

	/**
	 * @param userGroupRef the userGroupRef to set
	 */
	@SuppressWarnings("unused")
	private void setUserGroupRef(int userGroupRef) {
		this.userGroupRef = userGroupRef;
	}

	/**
	 * @return the userRef
	 */
	@SuppressWarnings("unused")
	private int getUserRef() {
		return userRef;
	}

	/**
	 * @param userRef the userRef to set
	 */
	@SuppressWarnings("unused")
	private void setUserRef(int userRef) {
		this.userRef = userRef;
	}

	/**
	 * @return the pos
	 */
	@SuppressWarnings("unused")
	private int getPos() {
		return pos;
	}

	/**
	 * @param pos the pos to set
	 */
	@SuppressWarnings("unused")
	private void setPos(int pos) {
		this.pos = pos;
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
	 * @return the activeReason
	 */
	@SuppressWarnings("unused")
	private String getActiveReason() {
		return activeReason;
	}

	/**
	 * @param activeReason the activeReason to set
	 */
	@SuppressWarnings("unused")
	private void setActiveReason(String activeReason) {
		this.activeReason = activeReason;
	}

	/**
	 * @return the timeChange
	 */
	@SuppressWarnings("unused")
	private long getTimeChange() {
		return timeChange;
	}

	/**
	 * @param timeChange the timeChange to set
	 */
	@SuppressWarnings("unused")
	private void setTimeChange(long timeChange) {
		this.timeChange = timeChange;
	}

	
}
