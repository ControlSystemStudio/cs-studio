package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.User2UserGroupDTO;

/**
 * Dieses Daten-Transfer-Objekt stellt hält die Konfiguration einer
 * AMS_UserGroup.
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

	// HAT funktuioniert, ist Vorlage für auto-mapping.
	// @OneToMany
	// @JoinTable(name="AMS_UserGroup_User",
	// joinColumns=@JoinColumn(name="iUserGroupRef"),
	// inverseJoinColumns=@JoinColumn(name="iUserRef"))
	// public Set<AlarmbearbeiterDTO> bearbeiter;
	//	
	// public void setBearbeiter(Set<AlarmbearbeiterDTO> bearbeiter) {
	// this.bearbeiter = bearbeiter;
	// }
	//	
	// public Set<AlarmbearbeiterDTO> getBearbeiter() {
	// return bearbeiter;
	// }

	@Id
	@GeneratedValue
	@Column(name = "iUserGroupId", nullable = false, unique = true)
	private int userGroupId;

	@Column(name = "iGroupRef", nullable = false)
	private int groupRef = -1;

	@Column(name = "cUserGroupName", length = 128)
	private String userGroupName;

	@Column(name = "sMinGroupMember")
	private short minGroupMember;

	@Column(name = "iTimeOutSec")
	private int timeOutSec;

	@Column(name = "sActive")
	private short active = 1;

	/**
	 * Dieses Feld wird nachträglich manuelle gestzt!! Um Object-Identität zu gewährleisten.
	 */
	@Transient
	private Set<User2UserGroupDTO> alarmbearbeiterDieserGruppe = new HashSet<User2UserGroupDTO>();

	/**
	 * @return the userGroupId
	 */
	public int getUserGroupId() {
		return userGroupId;
	}

	/**
	 * @param userGroupId
	 *            the userGroupId to set
	 */
	public void setUserGroupId(int userGroupId) {
		this.userGroupId = userGroupId;
	}

	/**
	 * @return the groupRef
	 */
	public int getGroupRef() {
		return groupRef;
	}

	/**
	 * @param groupRef
	 *            the groupRef to set
	 */
	@SuppressWarnings("unused")
	public void setGroupRef(int groupRef) {
		this.groupRef = groupRef;
	}

	/**
	 * @return the userGroupName
	 */
	public String getUserGroupName() {
		return userGroupName;
	}

	/**
	 * @param userGroupName
	 *            the userGroupName to set
	 */
	@SuppressWarnings("unused")
	public void setUserGroupName(String userGroupName) {
		this.userGroupName = userGroupName;
	}

	/**
	 * @return the minGroupMember
	 */
	public short getMinGroupMember() {
		return minGroupMember;
	}

	/**
	 * @param minGroupMember
	 *            the minGroupMember to set
	 */
	@SuppressWarnings("unused")
	public void setMinGroupMember(short minGroupMember) {
		this.minGroupMember = minGroupMember;
	}

	/**
	 * @return the timeOutSec
	 */
	public int getTimeOutSec() {
		return timeOutSec;
	}

	/**
	 * @param timeOutSec
	 *            the timeOutSec to set
	 */
	@SuppressWarnings("unused")
	public void setTimeOutSec(int timeOutSec) {
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
	 * @param active
	 *            the active to set
	 */
	@SuppressWarnings("unused")
	private void setActive(short active) {
		this.active = active;
	}

	public boolean isActive() {
		return getActive() == 1;
	}

	@SuppressWarnings("unused")
	public void setActive(boolean active) {
		setActive((short) (active ? 1 : 0));
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(this.getClass()
				.getSimpleName());
		builder.append(": ");
		builder.append("iUserGroupId: ");
		builder.append(userGroupId);
		builder.append(", iGroupRef: ");
		builder.append(groupRef);
		builder.append(", cName: ");
		builder.append(this.userGroupName);
		if( alarmbearbeiterDieserGruppe != null ) 
		{
			builder.append(", alarmbearbeiter: ");
			builder.append(alarmbearbeiterDieserGruppe.toString());
		}
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + active;
		result = prime * result;
		result = prime * result + groupRef;
		result = prime * result + minGroupMember;
		result = prime * result + timeOutSec;
		result = prime * result + userGroupId;
		result = prime * result
				+ ((userGroupName == null) ? 0 : userGroupName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof AlarmbearbeiterGruppenDTO))
			return false;
		final AlarmbearbeiterGruppenDTO other = (AlarmbearbeiterGruppenDTO) obj;
		if (active != other.active)
			return false;
		if (groupRef != other.groupRef)
			return false;
		if (minGroupMember != other.minGroupMember)
			return false;
		if (timeOutSec != other.timeOutSec)
			return false;
		if (userGroupId != other.userGroupId)
			return false;
		if (userGroupName == null) {
			if (other.userGroupName != null)
				return false;
		} else if (!userGroupName.equals(other.userGroupName))
			return false;
		return true;
	}

	/**
	 * Liefert alle zugehörigen Alarmbearbeiter.
	 */
	public Set<User2UserGroupDTO> gibZugehoerigeAlarmbearbeiter() {
		return Collections.unmodifiableSet(this.alarmbearbeiterDieserGruppe);
	}

    public void alarmbearbeiterZuordnen(User2UserGroupDTO map) {
    	System.out.println(map + " wurde " + this + " zugeordnet");
    	alarmbearbeiterDieserGruppe.add(map);
	}
    public void setAlarmbearbeiter(List<User2UserGroupDTO> list){
    	alarmbearbeiterDieserGruppe = new HashSet<User2UserGroupDTO>(list);
    }
}
