package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.csstudio.nams.common.fachwert.RubrikTypeEnum;

/**
 * Dieses Daten-Transfer-Objekt hält eine AMS_Group.
 * 
 * Das Create-Statement für die Datenbank hat folgendes Aussehen:
 * 
 * <pre>
 * create table AMS_Groups						/* logische GUI Baumstruktur
 * (
 *	iGroupId		NUMBER(11) NOT NULL,		
 *	cGroupName		VARCHAR2(128),
 *	sType			NUMBER(6),			/* 1 - User, 2 - UserGroup, 3 - FilterCond, 4 - Filter, 5 - Topic 
 * 	PRIMARY KEY (iGroupId)
 * );
 * </pre>
 */

@Entity
@Table(name = "AMS_Groups")
public class RubrikDTO {

		@Id
		@GeneratedValue(strategy=GenerationType.AUTO)
		@Column(name = "iGroupId", nullable=false, unique=true)
		private int iGroupId;

		@Column(name = "cGroupName", length=128)
		private String cGroupName;

		@Column(name = "sType", length=6)
		private short sType;

		public String getCGroupName() {
			return cGroupName;
		}

		public void setCGroupName(String groupName) {
			cGroupName = groupName;
		}

		// For Hibernate only
		protected short getSType() {
			return sType;
		}
		
		// For Application
		public RubrikTypeEnum getType() {
			return RubrikTypeEnum.valueOf(sType);
		}

		// For Hibernate only
		protected void setSType(short type) {
			sType = type;
		}
		
		// For Application
		public void setType(RubrikTypeEnum type) {
			sType = (short) type.ordinal();
		}

		public int getIGroupId() {
			return iGroupId;
		}

		protected void setIGroupId(int groupId) {
			iGroupId = groupId;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + iGroupId;
			result = prime * result
					+ ((cGroupName == null) ? 0 : cGroupName.hashCode());
			result = prime * result + sType;
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
			final RubrikDTO other = (RubrikDTO) obj;
			if (iGroupId != other.iGroupId)
				return false;
			if (cGroupName == null) {
				if (other.cGroupName != null)
					return false;
			} else if (!cGroupName.equals(other.cGroupName))
				return false;
			if (sType != other.sType)
				return false;
			return true;
		}
	
}
