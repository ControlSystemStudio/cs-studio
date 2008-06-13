package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.AlarmbearbeiterZuAlarmbearbeiterGruppenDTO;
import org.hibernate.annotations.CollectionOfElements;

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

	
	@OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	@JoinColumn(name="iUserGroupRef", referencedColumnName="iUserGroupId")
	@CollectionOfElements(fetch=FetchType.EAGER, targetElement=AlarmbearbeiterZuAlarmbearbeiterGruppenDTO.class)
	private Collection<AlarmbearbeiterZuAlarmbearbeiterGruppenDTO> conditionsAggregators;
	
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
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(this.getClass().getSimpleName());
		builder.append(": ");
		builder.append("iUserGroupId: ");
		builder.append(userGroupId);
		builder.append(", iGroupRef: ");
		builder.append(groupRef);
		builder.append(", cName: ");
		builder.append(this.userGroupName);
		builder.append(", linked Alarmbearbeiter: ");
		builder.append(conditionsAggregators.toString());
		return builder.toString();
	}

	/**
	 * @return the conditionsAggregators
	 */
	private Collection<AlarmbearbeiterZuAlarmbearbeiterGruppenDTO> getConditionsAggregators() {
		return conditionsAggregators;
	}

	/**
	 * @param conditionsAggregators the conditionsAggregators to set
	 */
	private void setConditionsAggregators(
			Collection<AlarmbearbeiterZuAlarmbearbeiterGruppenDTO> conditionsAggregators) {
		this.conditionsAggregators = conditionsAggregators;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + active;
		result = prime
				* result
				+ ((conditionsAggregators == null) ? 0 : conditionsAggregators
						.hashCode());
		result = prime * result + groupRef;
		result = prime * result + minGroupMember;
		result = prime * result + timeOutSec;
		result = prime * result + userGroupId;
		result = prime * result
				+ ((userGroupName == null) ? 0 : userGroupName.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
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
		if (conditionsAggregators == null) {
			if (other.conditionsAggregators != null)
				return false;
		} else if (!conditionsAggregators.equals(other.conditionsAggregators))
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
	
	
}
