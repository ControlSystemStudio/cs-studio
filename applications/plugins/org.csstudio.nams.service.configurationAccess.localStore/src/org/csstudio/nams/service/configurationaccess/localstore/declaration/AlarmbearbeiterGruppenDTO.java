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
import javax.persistence.Id;
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
@Table(name = "AMS_UserGroup")
public class AlarmbearbeiterGruppenDTO implements NewAMSConfigurationElementDTO, HasManuallyJoinedElements {

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

	@Transient
	private List<User2UserGroupDTO> alarmbearbeiterEinstellungenDieserGruppe = new LinkedList<User2UserGroupDTO>();

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
		if( alarmbearbeiterEinstellungenDieserGruppe != null ) 
		{
			builder.append(", alarmbearbeiter: ");
			builder.append(alarmbearbeiterEinstellungenDieserGruppe.toString());
		}
		return builder.toString();
	}

	

	/**
	 * Liefert alle zugehörigen Alarmbearbeiter.
	 */
	public List<AlarmbearbeiterDTO> gibZugehoerigeAlarmbearbeiter() {
		List<AlarmbearbeiterDTO> result = new LinkedList<AlarmbearbeiterDTO>();
		for (User2UserGroupDTO user2group : this.alarmbearbeiterEinstellungenDieserGruppe) {
			result.add(user2group.getAlarmbearbeiter());
		}
		
		return result;
	}

	
	
//	public boolean isUserActiveInGroup(AlarmbearbeiterDTO user) {
//		Contract.require(gibZugehoerigeAlarmbearbeiter().contains(user), "gibZugehoerigeAlarmbearbeiter().contains(user)");
//		return alarmbearbeiterEinstellungenDieserGruppe.get(user);
//	}
//	
    
    public void alarmbearbeiterZuordnen(AlarmbearbeiterDTO alarmbearbeiterDTO, boolean isActive, String activeReason, Date lastChange) {
    	alarmbearbeiterEinstellungenDieserGruppe.add(new User2UserGroupDTO(this, alarmbearbeiterDTO, alarmbearbeiterEinstellungenDieserGruppe.size(), isActive, activeReason, lastChange));
    }
    
    public void alleAlarmbearbeiterEntfernen(){
    	alarmbearbeiterEinstellungenDieserGruppe.clear();
    }

	public String getUniqueHumanReadableName() {
		return getUserGroupName();
	}

	public boolean isInCategory(int categoryDBId) {
		return false;
	}

	public void deleteJoinLinkData(Mapper mapper) throws Throwable {
		final Collection<User2UserGroupDTO> joins = mapper.loadAll(
				User2UserGroupDTO.class, false);

		for (final User2UserGroupDTO join : joins) {
			if (join.getUser2UserGroupPK().getIUserGroupRef() == this.getUserGroupId()) {
				mapper.delete(join);
			}
		}
	}

	public void loadJoinData(Mapper mapper) throws Throwable {
		List<User2UserGroupDTO> joins = mapper.loadAll(User2UserGroupDTO.class, true);
		Collections.sort(joins, new Comparator<User2UserGroupDTO>() {
			public int compare(User2UserGroupDTO joinLeft, User2UserGroupDTO joinRight) {
				return joinRight.getPosition() - joinLeft.getPosition();
			}
		});
		
		alarmbearbeiterEinstellungenDieserGruppe.clear();
		
		for (User2UserGroupDTO join : joins) {
			if( join.getUser2UserGroupPK().getIUserGroupRef() == this.getUserGroupId() ) {
				alarmbearbeiterEinstellungenDieserGruppe.add(join);
			}
		}
	}

	public void storeJoinLinkData(Mapper mapper) throws Throwable {
		final Collection<User2UserGroupDTO> oldJoins = mapper.loadAll(User2UserGroupDTO.class, true);

		Map<User2UserGroupDTO_PK, User2UserGroupDTO> oldJoinsNachPk = new HashMap<User2UserGroupDTO_PK, User2UserGroupDTO>();
		for (User2UserGroupDTO oldJoin : oldJoins) {
			if (oldJoin.getUser2UserGroupPK().getIUserGroupRef() == this
					.getUserGroupId()) {
			oldJoinsNachPk.put(oldJoin.getUser2UserGroupPK(), oldJoin);
			}
		}
		
		for (final User2UserGroupDTO join : this.alarmbearbeiterEinstellungenDieserGruppe) {
			
				User2UserGroupDTO oldJoin = oldJoinsNachPk.get(join.getUser2UserGroupPK());
				// existiert der join noch
				if(oldJoin != null) {
					
					oldJoin.setActive(join.getActive());
					oldJoin.setActiveReason(join.getActiveReason());
					oldJoin.setPosition(join.getPosition());
					
					mapper.save(oldJoin);
					oldJoinsNachPk.remove(oldJoin.getUser2UserGroupPK());
				} else {
					mapper.save(join);
				}
			
		}
		
		for (User2UserGroupDTO oldJoin : oldJoinsNachPk.values()) {
			mapper.delete(oldJoin);
		}
	}

	@Deprecated
	public Collection<User2UserGroupDTO> gibZugehoerigeAlarmbearbeiterMapping() {
		return alarmbearbeiterEinstellungenDieserGruppe;
	}
}
