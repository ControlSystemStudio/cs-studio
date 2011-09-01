
package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.csstudio.nams.common.contract.Contract;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.NewAMSConfigurationElementDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorCondForFilterTreeDTO;

/**
 * Representiert eine JOIN-Zeile für {@link JunctorCondForFilterTreeDTO}-Operanden.
 * 
 * <pre>
 * create table AMSFilterCondConj4FilterFCJoin (
 *    iFilterConditionID           NUMBER(11) NOT NULL,
 *    iFilterConditionRef			NUMBER(11) NOT NULL
 * );
 * </pre>
 * 
 * @author gs, mz
 * @see JunctorCondForFilterTreeDTO
 * 
 */
@Entity
@Table(name = "AMS_FILTERCOND_FILTERCOND")
public class JunctorConditionForFilterTreeConditionJoinDTO implements
		NewAMSConfigurationElementDTO {
	// /**
	// * Die {@link JunctorConditionForFilterTreeDTO} zu der dieses Join
	// * gehört.
	// */
	// @Column(name = "iFilterConditionID", nullable = false)
	// int iFilterConditionID;
	//
	// /**
	// * Die "zugejointe" Bedingung.
	// */
	// @Column(name = "iFilterConditionRef", nullable = false)
	// int iFilterConditionRef;
	/**
	 * Der Join selber ist der Primärschlüssel.
	 */
	// TODO Dieser Datensatz sollte keine ID haben!
	@Embeddable
	static class JoinPK implements Serializable {
		/**
		 * The serialize id,
		 */
		private static final long serialVersionUID = 2331180922158582062L;

		/**
		 * Die {@link JunctorCondForFilterTreeDTO} zu der dieses Join
		 * gehört.
		 */
		@Column(name = "iFilterConditionID", nullable = false)
		int iFilterConditionID;

		/**
		 * Die "zugejointe" Bedingung.
		 */
		@Column(name = "iFilterConditionRef", nullable = false)
		int iFilterConditionRef;

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
			if (this.iFilterConditionID != other.iFilterConditionID) {
				return false;
			}
			if (this.iFilterConditionRef != other.iFilterConditionRef) {
				return false;
			}
			return true;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + this.iFilterConditionID;
			result = prime * result + this.iFilterConditionRef;
			return result;
		}

		@Override
		public String toString() {
			return this.getClass().getSimpleName() + ": Join von "
					+ this.iFilterConditionID + " zu "
					+ this.iFilterConditionRef;
		}
	}

	@EmbeddedId
	private JoinPK id;

	public JunctorConditionForFilterTreeConditionJoinDTO() {
		// Required by bean-convention.
	}

	public JunctorConditionForFilterTreeConditionJoinDTO(
			final JunctorCondForFilterTreeDTO filterCondition,
			final FilterConditionDTO joinedCondition) {
		Contract.requireNotNull("filterCondition", filterCondition);
		Contract.requireNotNull("joinedCondition", joinedCondition);
		Contract
				.require(
						(filterCondition.getIFilterConditionID() != joinedCondition
								.getIFilterConditionID())
								|| (filterCondition.getIFilterConditionID() == 0),
						"filterCondition.getIFilterConditionID() != joinedCondition.getIFilterConditionID() || filterCondition.getIFilterConditionID() == 0");

		this.id = new JoinPK();
		this.id.iFilterConditionID = filterCondition.getIFilterConditionID();
		this.id.iFilterConditionRef = joinedCondition.getIFilterConditionID();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof JunctorConditionForFilterTreeConditionJoinDTO)) {
			return false;
		}
		final JunctorConditionForFilterTreeConditionJoinDTO other = (JunctorConditionForFilterTreeConditionJoinDTO) obj;
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		return true;
	}

	/**
	 * ONLY TO BE USED FOR MAPPING PURPOSES.
	 * 
	 * Returns the database id of the joined conditon condition.
	 */
	public int getJoinedConditionsDatabaseId() {
		return this.id.iFilterConditionRef;
	}

	/**
	 * ONLY TO BE USED FOR MAPPING PURPOSES.
	 * 
	 * Returns the database id of the join parent condition.
	 */
	public int getJoinParentsDatabaseId() {
		return this.id.iFilterConditionID;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getUniqueHumanReadableName() {
		/*- Da diese Joins nicht angezeigt werden, gibt es auch keine besondere
		    Darstellung. */
		return this.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isInCategory(final int categoryDBId) {
		/*- Hier gibt es keine categorys. */
		return false;
	}

	@Override
	public String toString() {
		return this.id.toString();
	}
}
