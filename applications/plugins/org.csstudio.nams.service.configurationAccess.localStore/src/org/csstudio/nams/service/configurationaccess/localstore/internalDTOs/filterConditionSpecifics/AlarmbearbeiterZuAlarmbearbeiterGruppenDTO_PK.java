package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class AlarmbearbeiterZuAlarmbearbeiterGruppenDTO_PK implements Serializable {

	private static final long serialVersionUID = -3151854763238278823L;

	@Column(name="IUSERGROUPREF")
	private int userGroupRef;
	
	@Column(name="IUSERREF", nullable=false)
	private int userRef;
	
//	private int pos;

	public AlarmbearbeiterZuAlarmbearbeiterGruppenDTO_PK() {
		
	}
	
	public AlarmbearbeiterZuAlarmbearbeiterGruppenDTO_PK(int userGroupRef,
			int userRef
//			, int pos
			) {
		super();
		this.userGroupRef = userGroupRef;
		this.userRef = userRef;
//		this.pos = pos;
	}

	public int getUserGroupRef() {
		return userGroupRef;
	}

	public void setUserGroupRef(int userGroupRef) {
		this.userGroupRef = userGroupRef;
	}

	public int getUserRef() {
		return userRef;
	}

	public void setUserRef(int userRef) {
		this.userRef = userRef;
	}

//	public int getPos() {
//		return pos;
//	}
//
//	public void setPos(int pos) {
//		this.pos = pos;
//	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
//		result = prime * result + pos;
		result = prime * result + userGroupRef;
		result = prime * result + userRef;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof AlarmbearbeiterZuAlarmbearbeiterGruppenDTO_PK))
			return false;
		final AlarmbearbeiterZuAlarmbearbeiterGruppenDTO_PK other = (AlarmbearbeiterZuAlarmbearbeiterGruppenDTO_PK) obj;
//		if (pos != other.pos)
//			return false;
		if (userGroupRef != other.userGroupRef)
			return false;
		if (userRef != other.userRef)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(this.getClass().getSimpleName());
		builder.append(": IUSERGROUPREF: ");
		builder.append(userGroupRef);
		builder.append(", IUSERREF: ");
		builder.append(userRef);
//		builder.append(", IPOS");
//		builder.append(pos);
		return builder.toString();
	}
}
