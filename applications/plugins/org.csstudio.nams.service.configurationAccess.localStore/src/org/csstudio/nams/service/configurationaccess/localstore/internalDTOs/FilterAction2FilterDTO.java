
package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterActionDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.FilterDTO;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.NewAMSConfigurationElementDTO;

@Entity
@Table(name = "AMS_FILTER_FILTERACTION")
public class FilterAction2FilterDTO implements NewAMSConfigurationElementDTO {

	@Embeddable
	public static class JoinPK implements Serializable {
		private static final long serialVersionUID = -9143559160527659819L;

		@Column(name = "IFILTERREF")
		int iFilterRef;

		@Column(name = "IFILTERACTIONREF")
		int iFilterActionRef;

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof JoinPK)) {
				return false;
			}
			final JoinPK other = (JoinPK) obj;
			if (this.iFilterActionRef != other.iFilterActionRef) {
				return false;
			}
			if (this.iFilterRef != other.iFilterRef) {
				return false;
			}
			return true;
		}

		public int getIFilterActionRef() {
			return this.iFilterActionRef;
		}

		public int getIFilterRef() {
			return this.iFilterRef;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + this.iFilterActionRef;
			result = prime * result + this.iFilterRef;
			return result;
		}

		@Override
		public String toString() {
			return "Id des Join von FilterAction: " + this.iFilterActionRef
					+ " zu Filter: " + this.iFilterRef;
		}
	}

	@EmbeddedId
	JoinPK id;

	@Column(name = "IPOS")
	int iPos;

	public FilterAction2FilterDTO() {
		
	}
	
	public FilterAction2FilterDTO(FilterActionDTO filterAction,
			FilterDTO filterDTO, int pos) {
		this.id = new JoinPK();
		this.id.iFilterActionRef = filterAction.getIFilterActionID();
		this.id.iFilterRef = filterDTO.getIFilterID();
		this.iPos = pos;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof FilterAction2FilterDTO)) {
			return false;
		}
		final FilterAction2FilterDTO other = (FilterAction2FilterDTO) obj;
		if (this.iPos != other.iPos) {
			return false;
		}
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		return true;
	}

	public JoinPK getId() {
		return this.id;
	}

	public int getIPos() {
		return this.iPos;
	}

	public String getUniqueHumanReadableName() {
		return this.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.iPos;
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		return result;
	}

	public boolean isInCategory(final int categoryDBId) {
		return false;
	}

	public void setIPos(final int pos) {
		this.iPos = pos;
	}

	@Override
	public String toString() {
		return "Join: " + this.id.toString() + ", Pos: " + this.iPos;
	}

}
