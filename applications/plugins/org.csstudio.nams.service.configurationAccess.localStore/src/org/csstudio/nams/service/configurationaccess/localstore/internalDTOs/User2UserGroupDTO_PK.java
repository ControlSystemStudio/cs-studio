
package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class User2UserGroupDTO_PK implements Serializable {
	private static final long serialVersionUID = 5698295964009594216L;

	@Column(name = "iUserGroupRef")
	private int iUserGroupRef;

	@Column(name = "iUserRef", nullable = false)
	private int iUserRef;

	public User2UserGroupDTO_PK() {
	    // Nothing to do
	}

	public User2UserGroupDTO_PK(final int userGroupRef, final int userRef) {
		super();
		this.iUserGroupRef = userGroupRef;
		this.iUserRef = userRef;
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
		final User2UserGroupDTO_PK other = (User2UserGroupDTO_PK) obj;
		if (this.iUserGroupRef != other.iUserGroupRef) {
			return false;
		}
		if (this.iUserRef != other.iUserRef) {
			return false;
		}
		return true;
	}

	public int getIUserGroupRef() {
		return this.iUserGroupRef;
	}

	public int getIUserRef() {
		return this.iUserRef;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.iUserGroupRef;
		result = prime * result + this.iUserRef;
		return result;
	}

	public void setIUserGroupRef(final int userGroupRef) {
		this.iUserGroupRef = userGroupRef;
	}

	public void setIUserRef(final int userRef) {
		this.iUserRef = userRef;
	}
}
