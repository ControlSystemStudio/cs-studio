package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics;

import java.util.Collection;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.hibernate.Session;

/**
 * Dieses FC wird verwendet, um andere Condtions im Filter zu verneinen. Diese
 * FC wird allerdings nicht in der Auflistung der FCs im Konfigurationswerkzeug
 * angezeigt!
 * 
 * <pre>
 * create table AMSFilterNegationCond4Filter (
 *    iFilterConditionRef			NUMBER(11) NOT NULL,
 *    iNegatedFCRef                 NUMBER(11) NOT NULL
 * );
 * </pre>
 * 
 * @author gs, mz
 */
@Entity
@PrimaryKeyJoinColumn(name = "iFilterConditionRef", referencedColumnName = "iFilterConditionID")
@Table(name = "AMSFilterNegationCond4Filter")
public class NegationConditionForFilterTreeDTO extends FilterConditionDTO implements HasJoinedElements<FilterConditionDTO> {
	
	@EmbeddedId
	private NegationConditionForFilterTreeDTO_PK id;

	@Transient
	private FilterConditionDTO negatedFilterCondition;

	/**
	 * Returns the negated FilterCondition.
	 * 
	 * @return The filter condition, not null.
	 */
	public FilterConditionDTO getNegatedFilterCondition() {
		return negatedFilterCondition;
	}

	/**
	 * TO BE USED FOR MAPPING PURPOSES!
	 * 
	 * Sets the negated condition.
	 */
	public void setNegatedFilterCondition(
			FilterConditionDTO negatedFilterCondition) {
		this.negatedFilterCondition = negatedFilterCondition;
		this.id.setINegatedFCRef( negatedFilterCondition.getIFilterConditionID() );
	}
	
	/**
	 * TO BE USED BY {@link LocalStoreConfigurationService} ONLY!
	 * 
	 * Returns the database id of negated condition.
	 */
	public int getINegatedFCRef() {
		return this.id.getINegatedFCRef();
	}

	public void deleteJoinLinkData(Session session) throws Throwable {
		if (this.negatedFilterCondition instanceof HasJoinedElements) {
			((HasJoinedElements<?>)this.negatedFilterCondition).deleteJoinLinkData(session);
		}
	}

	@SuppressWarnings("unchecked")
	public void loadJoinData(Session session,
			Collection<FilterConditionDTO> allJoinedElements) throws Throwable {
		if (this.negatedFilterCondition instanceof HasJoinedElements) {
			((HasJoinedElements<FilterConditionDTO>)this.negatedFilterCondition).loadJoinData(session, allJoinedElements);
		}
	}

	public void storeJoinLinkData(Session session) throws Throwable {
		if (this.negatedFilterCondition instanceof HasJoinedElements) {
			((HasJoinedElements<?>)this.negatedFilterCondition).storeJoinLinkData(session);
		}
	}
}
