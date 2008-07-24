package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.NewAMSConfigurationElementDTO;

/**
 * drop table AMS_UserGroup_User; create table AMS_UserGroup_User (
 * iUserGroupRef NUMBER(11) NOT NULL, iUserRef NUMBER(11) NOT NULL, iPos
 * NUMBER(11) NOT NULL, Benchrichtigungsreihenfolge sActive NUMBER(6),
 * Gruppenzugeh�rigkeit aktiv?(0 - Inactive, 1 - Active) cActiveReason
 * VARCHAR2(128), Grund/Ursache der An/Abmeldung tTimeChange NUMBER(14),
 * Zeitstempel der letzten �nderung des Datensatzes PRIMARY
 * KEY(iUserGroupRef,iUserRef) );
 */
@Entity
@Table(name = "AMS_USERGROUP_USER")
public class User2UserGroupDTO implements NewAMSConfigurationElementDTO {

	@EmbeddedId
	private User2UserGroupDTO_PK user2UserGroupPK;

	@Column(name = "iPos")
	private int position;

	@Column(name = "sActive")
	private short active;

	@Column(name = "cActiveReason")
	private String activeReason;

	@Column(name = "tTimeChange")
	private long lastchange;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + active;
		result = prime * result
				+ ((activeReason == null) ? 0 : activeReason.hashCode());
		result = prime * result + (int) (lastchange ^ (lastchange >>> 32));
		result = prime * result + position;
		result = prime
				* result
				+ ((user2UserGroupPK == null) ? 0 : user2UserGroupPK.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final User2UserGroupDTO other = (User2UserGroupDTO) obj;
		if (active != other.active)
			return false;
		if (activeReason == null) {
			if (other.activeReason != null)
				return false;
		} else if (!activeReason.equals(other.activeReason))
			return false;
		if (lastchange != other.lastchange)
			return false;
		if (position != other.position)
			return false;
		if (user2UserGroupPK == null) {
			if (other.user2UserGroupPK != null)
				return false;
		} else if (!user2UserGroupPK.equals(other.user2UserGroupPK))
			return false;
		return true;
	}

	public User2UserGroupDTO_PK getUser2UserGroupPK() {
		return user2UserGroupPK;
	}

	public void setUser2UserGroupPK(User2UserGroupDTO_PK user2UserGroupPK) {
		this.user2UserGroupPK = user2UserGroupPK;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public short getActive() {
		return active;
	}

	public void setActive(short active) {
		this.active = active;
	}

	public String getActiveReason() {
		return activeReason;
	}

	public void setActiveReason(String activeReason) {
		this.activeReason = activeReason;
	}

	@Transient
	public boolean isActive() {
		return active == 0 ? false : true;
	}
	@Transient
	public void setActive(boolean value) {
		active = value ? (short) 1 : (short) 0;
	}

	public long getLastchange() {
		return lastchange;
	}

	public void setLastchange(long lastchange) {
		this.lastchange = lastchange;
	}

	public String getUniqueHumanReadableName() {
		return toString();
	}

	public boolean isInCategory(int categoryDBId) {
		return false;
	}

}
