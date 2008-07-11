package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.csstudio.nams.common.contract.Contract;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.NewAMSConfigurationElementDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.JunctorConditionForFilterTreeDTO;

/**
 * Representiert eine JOIN-Zeile für {@link JunctorConditionForFilterTreeDTO}-Operanden.
 * 
 * <pre>
 * create table AMS_FilterCond_Conj_4_Filter_FilterCond_Join (
 *   iFilterConditionID             INT NOT NULL,
 *   iFilterConditionRef			INT NOT NULL,
 * );
 * </pre>
 * 
 * @author gs, mz
 * @see JunctorConditionForFilterTreeDTO
 * 
 */
@Entity
@Table(name = "AMS_FilterCond_Conj_4_Filter_FilterCond_Join")
public class JunctorConditionForFilterTreeConditionJoinDTO implements
		NewAMSConfigurationElementDTO {

	/**
	 * Der Join selber ist der Primärschlüssel.
	 */
	@Embeddable
	static class JoinPK implements Serializable {
		/**
		 * The serialize id,
		 */
		private static final long serialVersionUID = 2331180922158582062L;

		/**
		 * Die {@link JunctorConditionForFilterTreeDTO} zu der dieses Join
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
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + iFilterConditionID;
			result = prime * result + iFilterConditionRef;
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof JoinPK))
				return false;
			final JoinPK other = (JoinPK) obj;
			if (iFilterConditionID != other.iFilterConditionID)
				return false;
			if (iFilterConditionRef != other.iFilterConditionRef)
				return false;
			return true;
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

	/**
	 * Setzt die FC der dieses Join gehört.
	 * 
	 * @param condition
	 *            Die "parent" FC, nicht null.
	 */
	public void setJoinParent(final JunctorConditionForFilterTreeDTO condition) {
		Contract.requireNotNull("condition", condition);

		this.id.iFilterConditionID = condition.getIFilterConditionID();
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
	 * Setzt die "zugejointe" FC.
	 * 
	 * @param condition
	 *            Die "zugejointe" FC, nicht null.
	 */
	public void setJoinedCondition(final FilterConditionDTO condition) {
		Contract.requireNotNull("condition", condition);

		this.id.iFilterConditionRef = condition.getIFilterConditionID();
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
	 * {@inheritDoc}
	 */
	public String getUniqueHumanReadableName() {
		/*- Da diese Joins nicht angezeigt werden, gibt es auch keine besondere
		    Darstellung. */
		return this.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isInCategory(final int categoryDBId) {
		/*- Hier gibt es keine categorys. */
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof JunctorConditionForFilterTreeConditionJoinDTO))
			return false;
		final JunctorConditionForFilterTreeConditionJoinDTO other = (JunctorConditionForFilterTreeConditionJoinDTO) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return this.id.toString();
	}
}
