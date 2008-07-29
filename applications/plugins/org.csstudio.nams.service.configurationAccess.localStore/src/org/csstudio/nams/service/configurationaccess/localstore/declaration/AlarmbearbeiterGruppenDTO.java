package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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

import org.csstudio.nams.common.contract.Contract;
import org.csstudio.nams.service.configurationaccess.localstore.Mapper;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.User2UserGroupDTO;
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
	private List<AlarmbearbeiterDTO> alarmbearbeiterDieserGruppe = new LinkedList<AlarmbearbeiterDTO>();

	@Transient
	private Map<AlarmbearbeiterDTO, Boolean> alarmbearbeiterAktiv = new HashMap<AlarmbearbeiterDTO, Boolean>();

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
	public List<AlarmbearbeiterDTO> gibZugehoerigeAlarmbearbeiter() {
		return Collections.unmodifiableList(this.alarmbearbeiterDieserGruppe);
	}

	
	
	public boolean isUserActiveInGroup(AlarmbearbeiterDTO user) {
		Contract.require(gibZugehoerigeAlarmbearbeiter().contains(user), "gibZugehoerigeAlarmbearbeiter().contains(user)");
		return alarmbearbeiterAktiv.get(user);
	}
	
    public void alarmbearbeiterZuordnen(AlarmbearbeiterDTO map) {
    	alarmbearbeiterAktiv.put(map, map.isActive());
    	alarmbearbeiterDieserGruppe.add(map);
	}
    public void setAlarmbearbeiter(List<AlarmbearbeiterDTO> list){
    	alarmbearbeiterDieserGruppe = new LinkedList<AlarmbearbeiterDTO>(list);
    	alarmbearbeiterAktiv.clear();
    	for (AlarmbearbeiterDTO alarmbearbeiterDTO : alarmbearbeiterDieserGruppe) {
			alarmbearbeiterAktiv.put(alarmbearbeiterDTO, alarmbearbeiterDTO.isActive());
		}
    }

	public String getUniqueHumanReadableName() {
		return getUserGroupName();
	}

	public boolean isInCategory(int categoryDBId) {
		return false;
	}

	public void deleteJoinLinkData(Mapper mapper) throws Throwable {
		// TODO Auto-generated method stub
//		final Collection<User2UserGroupDTO> user2GroupMappings = loadAll(
//				session, User2UserGroupDTO.class);
//
//		for (final User2UserGroupDTO a : user2GroupMappings) {
//			if (a.getUser2UserGroupPK().getIUserGroupRef() == dto
//					.getUserGroupId()) {
//				deleteDTONoTransaction(session, a);
//			}
//		}
//		deleteDTONoTransaction(session, dto);
		
	}

	public void loadJoinData(Mapper mapper) throws Throwable {
		List<User2UserGroupDTO> joins = mapper.loadAll(User2UserGroupDTO.class, true);
		Collections.sort(joins, new Comparator<User2UserGroupDTO>() {
			public int compare(User2UserGroupDTO joinLeft, User2UserGroupDTO joinRight) {
				return joinRight.getPosition() - joinLeft.getPosition();
			}
		});
		
		List<AlarmbearbeiterDTO> alleAlarmbearbeiter = mapper.loadAll(AlarmbearbeiterDTO.class, true);
		
		Map<Integer, AlarmbearbeiterDTO> alarmbearbeiterNachSchluessel = new HashMap<Integer, AlarmbearbeiterDTO>();
		for (AlarmbearbeiterDTO alarmbearbeiter : alleAlarmbearbeiter) {
			alarmbearbeiterNachSchluessel.put(alarmbearbeiter.getUserId(), alarmbearbeiter);
		}
		
		alarmbearbeiterDieserGruppe.clear();
		
		for (User2UserGroupDTO join : joins) {
			if( join.getUser2UserGroupPK().getIUserGroupRef() == this.getUserGroupId() ) {
				int userRef = join.getUser2UserGroupPK().getIUserRef();
				AlarmbearbeiterDTO alarmbearbeiter = alarmbearbeiterNachSchluessel.get(userRef);
				assert alarmbearbeiter != null : "Alarmbearbeiter für ID " + userRef + " exisitiert";
				
				alarmbearbeiterDieserGruppe.add(alarmbearbeiter);
			}
		}
	}

	public void storeJoinLinkData(Mapper mapper) throws Throwable {
		final Collection<User2UserGroupDTO> user2GroupMappings = mapper.loadAll(User2UserGroupDTO.class, true);

		for (final User2UserGroupDTO a : user2GroupMappings) {
			if (a.getUser2UserGroupPK().getIUserGroupRef() == this
					.getUserGroupId()) {
				mapper.delete(a);
			}
		}
		// for all used FC
		// get the mappingDTO, if no DTO exists, create one
		final List<AlarmbearbeiterDTO> zugehoerigeAlarmbearbeiter = this
				.gibZugehoerigeAlarmbearbeiter();

		// save the used Mappings
		for (int position = 0; position < zugehoerigeAlarmbearbeiter.size(); position++) {
			AlarmbearbeiterDTO user = zugehoerigeAlarmbearbeiter.get(position);
			
			User2UserGroupDTO user2userGroup = new User2UserGroupDTO(this, user);
			user2userGroup.setActive(alarmbearbeiterAktiv.get(user));
			user2userGroup.setActiveReason("TODO!"); // FIXME Das hier muss noch gemacht werden!
			user2userGroup.setLastchange(0); // FIXME Das hier muss noch gemacht werden!
			user2userGroup.setPosition(position);
			mapper.save(user2userGroup);
		}
	}

	@Deprecated
	public Collection<User2UserGroupDTO> gibZugehoerigeAlarmbearbeiterMapping() {
		final List<AlarmbearbeiterDTO> zugehoerigeAlarmbearbeiter = this
		.gibZugehoerigeAlarmbearbeiter();
		
		final Collection<User2UserGroupDTO> result = new LinkedList<User2UserGroupDTO>();
		
		for (int position = 0; position < zugehoerigeAlarmbearbeiter.size(); position++) {
			AlarmbearbeiterDTO user = zugehoerigeAlarmbearbeiter.get(position);
			
			User2UserGroupDTO user2userGroup = new User2UserGroupDTO();
			user2userGroup.setActive(alarmbearbeiterAktiv.get(user));
			user2userGroup.setActiveReason("TODO!"); // FIXME Das hier muss noch gemacht werden!
			user2userGroup.setLastchange(0); // FIXME Das hier muss noch gemacht werden!
			user2userGroup.setPosition(position);
			result.add(user2userGroup);
		}
		return result;
	}
}
