
package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.nams.service.configurationaccess.localstore.Mapper;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.AlarmbearbeiterGruppenDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.NewAMSConfigurationElementDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.InconsistentConfigurationException;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.HasManuallyJoinedElements;

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
public class User2UserGroupDTO implements NewAMSConfigurationElementDTO,
		HasManuallyJoinedElements {

	@Transient
	private AlarmbearbeiterDTO alarmbearbeiter;

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

	public User2UserGroupDTO() {
		this.alarmbearbeiter = null;
	}

	public User2UserGroupDTO(final AlarmbearbeiterGruppenDTO group,
			final AlarmbearbeiterDTO user) {
		super();
		this.user2UserGroupPK = new User2UserGroupDTO_PK(
				group.getUserGroupId(), user.getUserId());
		this.alarmbearbeiter = user;
	}

	public User2UserGroupDTO(final AlarmbearbeiterGruppenDTO group,
			final AlarmbearbeiterDTO user, final int position,
			final boolean active, final String activeReason,
			final Date lastChange) {
		super();
		this.position = position;
		this.user2UserGroupPK = new User2UserGroupDTO_PK(
				group.getUserGroupId(), user.getUserId());
		this.alarmbearbeiter = user;
		this.active = (short) (active ? 1 : 0);
		this.activeReason = activeReason;
		this.lastchange = lastChange.getTime();
	}

	public void deleteJoinLinkData(final Mapper mapper) throws Throwable {
		this.alarmbearbeiter = null;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final User2UserGroupDTO other = (User2UserGroupDTO) obj;
		if (this.active != other.active) {
			return false;
		}
		if (this.activeReason == null) {
			if (other.activeReason != null) {
				return false;
			}
		} else if (!this.activeReason.equals(other.activeReason)) {
			return false;
		}
		if (this.lastchange != other.lastchange) {
			return false;
		}
		if (this.position != other.position) {
			return false;
		}
		if (this.user2UserGroupPK == null) {
			if (other.user2UserGroupPK != null) {
				return false;
			}
		} else if (!this.user2UserGroupPK.equals(other.user2UserGroupPK)) {
			return false;
		}
		return true;
	}

	public short getActive() {
		return this.active;
	}

	public String getActiveReason() {
		return this.activeReason;
	}

	public AlarmbearbeiterDTO getAlarmbearbeiter() {
		return this.alarmbearbeiter;
	}

	public long getLastchange() {
		return this.lastchange;
	}

	public int getPosition() {
		return this.position;
	}

	public String getUniqueHumanReadableName() {
		return this.toString();
	}

	public User2UserGroupDTO_PK getUser2UserGroupPK() {
		return this.user2UserGroupPK;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.active;
		result = prime
				* result
				+ ((this.activeReason == null) ? 0 : this.activeReason
						.hashCode());
		result = prime * result
				+ (int) (this.lastchange ^ (this.lastchange >>> 32));
		result = prime * result + this.position;
		result = prime
				* result
				+ ((this.user2UserGroupPK == null) ? 0 : this.user2UserGroupPK
						.hashCode());
		return result;
	}

	@Transient
	public boolean isActive() {
		return this.active == 0 ? false : true;
	}

	public boolean isInCategory(final int categoryDBId) {
		return false;
	}

	public void loadJoinData(final Mapper mapper) throws Throwable {
		this.alarmbearbeiter = mapper.findForId(AlarmbearbeiterDTO.class, this
				.getUser2UserGroupPK().getIUserRef(), false);
		if (this.alarmbearbeiter == null) {
			throw new InconsistentConfigurationException(
					"Join zu unbekantem Alarmbearbeiter: Join-ID: "
							+ this.getUser2UserGroupPK().toString()
							+ ", referenzierte id: "
							+ this.getUser2UserGroupPK().getIUserRef());
		}
	}

	@Transient
	public void setActive(final boolean value) {
		this.active = value ? (short) 1 : (short) 0;
	}

	public void setActive(final short active) {
		this.active = active;
	}

	public void setActiveReason(final String activeReason) {
		this.activeReason = activeReason;
	}

	public void setLastchange(final long lastchange) {
		this.lastchange = lastchange;
	}

	public void setPosition(final int position) {
		this.position = position;
	}

	@Deprecated
	public void setUser2UserGroupPK(final User2UserGroupDTO_PK user2UserGroupPK) {
		this.user2UserGroupPK = user2UserGroupPK;
	}

	public void storeJoinLinkData(final Mapper mapper) throws Throwable {
		// Nothing to do.
	}

}
