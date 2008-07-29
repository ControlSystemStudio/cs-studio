package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.NewAMSConfigurationElementDTO;

@Entity
@Table(name="AMS_FILTER_FILTERACTION")
public class FilterAction2FilterDTO implements NewAMSConfigurationElementDTO {

	@Embeddable
	public static class JoinPK implements Serializable {
		private static final long serialVersionUID = -9143559160527659819L;

		@Column(name="IFILTERREF")
		int iFilterRef;
		
		@Column(name="IFILTERACTIONREF")
		int iFilterActionRef;

		public int getIFilterActionRef() {
			return iFilterActionRef;
		}
		
		public int getIFilterRef() {
			return iFilterRef;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + iFilterActionRef;
			result = prime * result + iFilterRef;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof JoinPK))
				return false;
			final JoinPK other = (JoinPK) obj;
			if (iFilterActionRef != other.iFilterActionRef)
				return false;
			if (iFilterRef != other.iFilterRef)
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "Id des Join von FilterAction: " + iFilterActionRef + " zu Filter: " + iFilterRef;
		}
	}
	
	@EmbeddedId
	JoinPK id;
	
	@Column(name="IPOS")
	int iPos;
	
	public JoinPK getId() {
		return id;
	}
	
	public void setIPos(int pos) {
		iPos = pos;
	}
	
	public int getIPos() {
		return iPos;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + iPos;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof FilterAction2FilterDTO))
			return false;
		final FilterAction2FilterDTO other = (FilterAction2FilterDTO) obj;
		if (iPos != other.iPos)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Join: " + id.toString() + ", Pos: " + iPos;
	}
	
	public String getUniqueHumanReadableName() {
		return toString();
	}

	public boolean isInCategory(int categoryDBId) {
		return false;
	}

}
