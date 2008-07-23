package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics;

import java.util.Collection;

import javax.persistence.Column;
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
	
	@SuppressWarnings("unused")
	@Column(name = "iFilterConditionRef", nullable = false, updatable = false, insertable = false)
	private int iFilterConditionRef;

	@Column(name = "iNegatedFCRef", nullable = false)
	private int iNegatedFCRef;

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
		this.iNegatedFCRef = negatedFilterCondition.getIFilterConditionID();
	}
	
	/**
	 * TO BE USED BY {@link LocalStoreConfigurationService} ONLY!
	 * 
	 * Returns the database id of negated condition.
	 */
	public int getINegatedFCRef() {
		return iNegatedFCRef;
	}

	public void deleteJoinLinkData(Session session) throws Throwable {
		// TODO Auto-generated method stub
		
	}

	public void loadJoinData(Session session,
			Collection<FilterConditionDTO> allJoinedElements) throws Throwable {
		// TODO Auto-generated method stub
		
	}

	public void storeJoinLinkData(Session session) throws Throwable {
		// TODO Auto-generated method stub
		
	}
}
