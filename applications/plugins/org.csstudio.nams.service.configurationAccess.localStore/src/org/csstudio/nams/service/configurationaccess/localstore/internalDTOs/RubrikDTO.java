
package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.csstudio.nams.common.fachwert.RubrikTypeEnum;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.NewAMSConfigurationElementDTO;

/**
 * Dieses Daten-Transfer-Objekt hält eine AMS_Group.
 * 
 * Das Create-Statement für die Datenbank hat folgendes Aussehen:
 * 
 * <pre>
 * create table AMS_Groups						/* logische GUI Baumstruktur
 * (
 * iGroupId		NUMBER(11) NOT NULL,		
 * cGroupName		VARCHAR2(128),
 * sType			NUMBER(6),			/* 1 - User, 2 - UserGroup, 3 - FilterCond, 4 - Filter, 5 - Topic 
 * 	PRIMARY KEY (iGroupId)
 * );
 * </pre>
 */

@Entity
@SequenceGenerator(name="rubrik_id", sequenceName="AMS_Groups_ID", allocationSize=1)
@Table(name = "AMS_Groups")
public class RubrikDTO implements NewAMSConfigurationElementDTO {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="rubrik_id")
	@Column(name = "iGroupId", nullable = false, unique = true)
	private int iGroupId;

	@Column(name = "cGroupName", length = 128)
	private String cGroupName;

	@Column(name = "sType", length = 6)
	private short sType;

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
		final RubrikDTO other = (RubrikDTO) obj;
		if (this.iGroupId != other.iGroupId) {
			return false;
		}
		if (this.cGroupName == null) {
			if (other.cGroupName != null) {
				return false;
			}
		} else if (!this.cGroupName.equals(other.cGroupName)) {
			return false;
		}
		if (this.sType != other.sType) {
			return false;
		}
		return true;
	}

	public String getCGroupName() {
		return this.cGroupName;
	}

	public int getIGroupId() {
		return this.iGroupId;
	}

	// For Application
	public RubrikTypeEnum getType() {
		return RubrikTypeEnum.valueOf(this.sType);
	}

	public String getUniqueHumanReadableName() {
		return this.getCGroupName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.iGroupId;
		result = prime * result
				+ ((this.cGroupName == null) ? 0 : this.cGroupName.hashCode());
		result = prime * result + this.sType;
		return result;
	}

	public boolean isInCategory(final int categoryDBId) {
		return false;
	}

	public void setCGroupName(final String groupName) {
		this.cGroupName = groupName;
	}

	// For Application
	public void setType(final RubrikTypeEnum type) {
		this.sType = type.getShortFor();
	}

	// For Hibernate only
	protected short getSType() {
		return this.sType;
	}

	protected void setIGroupId(final int groupId) {
		this.iGroupId = groupId;
	}

	// For Hibernate only
	protected void setSType(final short type) {
		this.sType = type;
	}

}
