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
 * create table AMSFilterCondConj4FilterFCJoin (
 *    iFilterConditionID           NUMBER(11) NOT NULL,
 *    iFilterConditionRef			NUMBER(11) NOT NULL
 * );
 * </pre>
 * 
 * @author gs, mz
 * @see JunctorConditionForFilterTreeDTO
 * 
 */
@Entity
@Table(name = "AMSFilterCondConj4FilterFCJoin")
public class JunctorConditionForFilterTreeConditionJoinDTO implements
		NewAMSConfigurationElementDTO {
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
//	/**
//	 * Der Join selber ist der Primärschlüssel.
//	 */ // TODO Dieser Datensatz sollte keine ID haben!
//	@Embeddable
//	static class JoinPK implements Serializable {
//		/**
//		 * The serialize id,
//		 */
//		private static final long serialVersionUID = 2331180922158582062L;
//
//		/**
//		 * Die {@link JunctorConditionForFilterTreeDTO} zu der dieses Join
//		 * gehört.
//		 */
//		@Column(name = "iFilterConditionID", nullable = false)
//		int iFilterConditionID;
//
//		/**
//		 * Die "zugejointe" Bedingung.
//		 */
//		@Column(name = "iFilterConditionRef", nullable = false)
//		int iFilterConditionRef;
//
//		@Override
//		public int hashCode() {
//			final int prime = 31;
//			int result = 1;
//			result = prime * result + iFilterConditionID;
//			result = prime * result + iFilterConditionRef;
//			return result;
//		}
//
//		@Override
//		public boolean equals(final Object obj) {
//			if (this == obj)
//				return true;
//			if (obj == null)
//				return false;
//			if (!(obj instanceof JoinPK))
//				return false;
//			final JoinPK other = (JoinPK) obj;
//			if (iFilterConditionID != other.iFilterConditionID)
//				return false;
//			if (iFilterConditionRef != other.iFilterConditionRef)
//				return false;
//			return true;
//		}
//
//		@Override
//		public String toString() {
//			return this.getClass().getSimpleName() + ": Join von "
//					+ this.iFilterConditionID + " zu "
//					+ this.iFilterConditionRef;
//		}
//	}

	public JunctorConditionForFilterTreeConditionJoinDTO() {
		// Required by bean-convention.
	}

	public JunctorConditionForFilterTreeConditionJoinDTO(
			JunctorConditionForFilterTreeDTO filterCondition,
			FilterConditionDTO joinedCondition) {
		Contract.requireNotNull("filterCondition", filterCondition);
		Contract.requireNotNull("joinedCondition", joinedCondition);
		Contract
				.require(
						(filterCondition.getIFilterConditionID() != joinedCondition
								.getIFilterConditionID()) || filterCondition.getIFilterConditionID() == 0,
						"filterCondition.getIFilterConditionID() != joinedCondition.getIFilterConditionID() || filterCondition.getIFilterConditionID() == 0");

//		id = new JoinPK();
		iFilterConditionID = filterCondition.getIFilterConditionID();
		iFilterConditionRef = joinedCondition.getIFilterConditionID();
	}

//	@EmbeddedId
//	private JoinPK id;

	/**
	 * Setzt die FC der dieses Join gehört.
	 * 
	 * @param condition
	 *            Die "parent" FC, nicht null.
	 */
	public void setJoinParent(final JunctorConditionForFilterTreeDTO condition) {
		Contract.requireNotNull("condition", condition);

		this.iFilterConditionID = condition.getIFilterConditionID();
	}

	/**
	 * ONLY TO BE USED FOR MAPPING PURPOSES.
	 * 
	 * Returns the database id of the join parent condition.
	 */
	public int getJoinParentsDatabaseId() {
		return this.iFilterConditionID;
	}

	/**
	 * Setzt die "zugejointe" FC.
	 * 
	 * @param condition
	 *            Die "zugejointe" FC, nicht null.
	 */
	public void setJoinedCondition(final FilterConditionDTO condition) {
		Contract.requireNotNull("condition", condition);

		this.iFilterConditionRef = condition.getIFilterConditionID();
	}

	/**
	 * ONLY TO BE USED FOR MAPPING PURPOSES.
	 * 
	 * Returns the database id of the joined conditon condition.
	 */
	public int getJoinedConditionsDatabaseId() {
		return this.iFilterConditionRef;
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
		result = prime * result + iFilterConditionID;
		result = prime * result + iFilterConditionRef;
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
