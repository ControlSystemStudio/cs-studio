
package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.nams.service.configurationaccess.localstore.Mapper;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.User2UserGroupDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.User2UserGroupDTO_PK;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.HasManuallyJoinedElements;

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
@SequenceGenerator(name="usergroup_id", sequenceName="AMS_UserGroup_ID", allocationSize=1)
@Table(name = "AMS_UserGroup")
public class AlarmbearbeiterGruppenDTO implements
		NewAMSConfigurationElementDTO, HasManuallyJoinedElements {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usergroup_id")
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

	@Transient
	private final List<User2UserGroupDTO> alarmbearbeiterEinstellungenDieserGruppe = new LinkedList<User2UserGroupDTO>();

	public void alarmbearbeiterZuordnen(
			final AlarmbearbeiterDTO alarmbearbeiterDTO,
			final boolean isActive, final String activeReason,
			final Date lastChange) {
		this.alarmbearbeiterEinstellungenDieserGruppe
				.add(new User2UserGroupDTO(this, alarmbearbeiterDTO,
						this.alarmbearbeiterEinstellungenDieserGruppe.size(),
						isActive, activeReason, lastChange));
	}

	public void alleAlarmbearbeiterEntfernen() {
		this.alarmbearbeiterEinstellungenDieserGruppe.clear();
	}

	public void deleteJoinLinkData(final Mapper mapper) throws Throwable {
		final Collection<User2UserGroupDTO> joins = mapper.loadAll(
				User2UserGroupDTO.class, false);

		for (final User2UserGroupDTO join : joins) {
			if (join.getUser2UserGroupPK().getIUserGroupRef() == this
					.getUserGroupId()) {
				mapper.delete(join);
			}
		}
	}

	/**
	 * @return the groupRef
	 */
	public int getGroupRef() {
		return this.groupRef;
	}

	/**
	 * @return the minGroupMember
	 */
	public short getMinGroupMember() {
		return this.minGroupMember;
	}

	/**
	 * @return the timeOutSec
	 */
	public int getTimeOutSec() {
		return this.timeOutSec;
	}

	public String getUniqueHumanReadableName() {
		return this.getUserGroupName();
	}

	/**
	 * @return the userGroupId
	 */
	public int getUserGroupId() {
		return this.userGroupId;
	}

	/**
	 * @return the userGroupName
	 */
	public String getUserGroupName() {
		return this.userGroupName;
	}

	/**
	 * Liefert alle zugehörigen Alarmbearbeiter.
	 */
	public List<AlarmbearbeiterDTO> gibZugehoerigeAlarmbearbeiter() {
		final List<AlarmbearbeiterDTO> result = new LinkedList<AlarmbearbeiterDTO>();
		for (final User2UserGroupDTO user2group : this.alarmbearbeiterEinstellungenDieserGruppe) {
			result.add(user2group.getAlarmbearbeiter());
		}

		return result;
	}

	@Deprecated
	public Collection<User2UserGroupDTO> gibZugehoerigeAlarmbearbeiterMapping() {
		return this.alarmbearbeiterEinstellungenDieserGruppe;
	}

	public boolean isActive() {
		return this.getActive() == 1;
	}

	public boolean isInCategory(final int categoryDBId) {
		return false;
	}

	public void loadJoinData(final Mapper mapper) throws Throwable {
		final List<User2UserGroupDTO> joins = mapper.loadAll(
				User2UserGroupDTO.class, true);
		Collections.sort(joins, new Comparator<User2UserGroupDTO>() {
			public int compare(final User2UserGroupDTO joinLeft,
					final User2UserGroupDTO joinRight) {
				return joinRight.getPosition() - joinLeft.getPosition();
			}
		});

		this.alarmbearbeiterEinstellungenDieserGruppe.clear();

		for (final User2UserGroupDTO join : joins) {
			if (join.getUser2UserGroupPK().getIUserGroupRef() == this
					.getUserGroupId()) {
				this.alarmbearbeiterEinstellungenDieserGruppe.add(join);
			}
		}
	}

	@SuppressWarnings("unused")
	public void setActive(final boolean active) {
		this.setActive((short) (active ? 1 : 0));
	}

	/**
	 * @param groupRef
	 *            the groupRef to set
	 */
	@SuppressWarnings("unused")
	public void setGroupRef(final int groupRef) {
		this.groupRef = groupRef;
	}

	// public boolean isUserActiveInGroup(AlarmbearbeiterDTO user) {
	// Contract.require(gibZugehoerigeAlarmbearbeiter().contains(user),
	// "gibZugehoerigeAlarmbearbeiter().contains(user)");
	// return alarmbearbeiterEinstellungenDieserGruppe.get(user);
	// }
	//	

	/**
	 * @param minGroupMember
	 *            the minGroupMember to set
	 */
	@SuppressWarnings("unused")
	public void setMinGroupMember(final short minGroupMember) {
		this.minGroupMember = minGroupMember;
	}

	/**
	 * @param timeOutSec
	 *            the timeOutSec to set
	 */
	@SuppressWarnings("unused")
	public void setTimeOutSec(final int timeOutSec) {
		this.timeOutSec = timeOutSec;
	}

	/**
	 * @param userGroupId
	 *            the userGroupId to set
	 */
	public void setUserGroupId(final int userGroupId) {
		this.userGroupId = userGroupId;
	}

	/**
	 * @param userGroupName
	 *            the userGroupName to set
	 */
	@SuppressWarnings("unused")
	public void setUserGroupName(final String userGroupName) {
		this.userGroupName = userGroupName;
	}

	public void storeJoinLinkData(final Mapper mapper) throws Throwable {
		final Collection<User2UserGroupDTO> oldJoins = mapper.loadAll(
				User2UserGroupDTO.class, false);

		final Map<User2UserGroupDTO_PK, User2UserGroupDTO> oldJoinsNachPk = new HashMap<User2UserGroupDTO_PK, User2UserGroupDTO>();
		for (final User2UserGroupDTO oldJoin : oldJoins) {
			if (oldJoin.getUser2UserGroupPK().getIUserGroupRef() == this
					.getUserGroupId()) {
				oldJoinsNachPk.put(oldJoin.getUser2UserGroupPK(), oldJoin);
			}
		}

		for (final User2UserGroupDTO join : this.alarmbearbeiterEinstellungenDieserGruppe) {

			final User2UserGroupDTO oldJoin = oldJoinsNachPk.get(join
					.getUser2UserGroupPK());
			// existiert der join noch
			if (oldJoin != null) {

				oldJoin.setActive(join.getActive());
				oldJoin.setActiveReason(join.getActiveReason());
				oldJoin.setPosition(join.getPosition());

				mapper.save(oldJoin);
				oldJoinsNachPk.remove(oldJoin.getUser2UserGroupPK());
			} else {
				join.getUser2UserGroupPK().setIUserGroupRef(this.getUserGroupId());
				mapper.save(join);
			}

		}

		for (final User2UserGroupDTO oldJoin : oldJoinsNachPk.values()) {
			mapper.delete(oldJoin);
		}
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder(this.getClass()
				.getSimpleName());
		builder.append(": ");
		builder.append("iUserGroupId: ");
		builder.append(this.userGroupId);
		builder.append(", iGroupRef: ");
		builder.append(this.groupRef);
		builder.append(", cName: ");
		builder.append(this.userGroupName);
		if (this.alarmbearbeiterEinstellungenDieserGruppe != null) {
			builder.append(", alarmbearbeiter: ");
			builder.append(this.alarmbearbeiterEinstellungenDieserGruppe
					.toString());
		}
		return builder.toString();
	}

	/**
	 * @return the active
	 */
	@SuppressWarnings("unused")
	private short getActive() {
		return this.active;
	}

	/**
	 * @param active
	 *            the active to set
	 */
	@SuppressWarnings("unused")
	private void setActive(final short active) {
		this.active = active;
	}
}
