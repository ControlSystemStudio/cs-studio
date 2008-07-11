package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class User2UserGroupDTO_PK implements Serializable{
	private static final long serialVersionUID = 5698295964009594216L;

	@Column(name="iFilterConditionRef")
	private int iUserGroupRef;
	
	@Column(name="iFilterRef", nullable=false)
	private int iUserRef;

	public int getIUserGroupRef() {
		return iUserGroupRef;
	}

	public void setIUserGroupRef(int userGroupRef) {
		iUserGroupRef = userGroupRef;
	}

	public int getIUserRef() {
		return iUserRef;
	}

	public void setIUserRef(int userRef) {
		iUserRef = userRef;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + iUserGroupRef;
		result = prime * result + iUserRef;
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
		final User2UserGroupDTO_PK other = (User2UserGroupDTO_PK) obj;
		if (iUserGroupRef != other.iUserGroupRef)
			return false;
		if (iUserRef != other.iUserRef)
			return false;
		return true;
	}
}
