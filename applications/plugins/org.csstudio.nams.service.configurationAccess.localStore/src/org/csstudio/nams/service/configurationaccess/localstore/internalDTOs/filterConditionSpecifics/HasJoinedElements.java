package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics;

import java.util.Collection;
import java.util.Set;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.NewAMSConfigurationElementDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;
import org.hibernate.Session;

/**
 * Indicates that the given Element has Joined-Elements to be load and stored
 * manually by the {@link LocalStoreConfigurationService}.
 */
public interface HasJoinedElements<T extends NewAMSConfigurationElementDTO> {

	/**
	 * ONLY USED FOR MAPPING PURPOSES!
	 * 
	 * For Example: This method is used to store the join-data for previously
	 * set {@link FilterConditionDTO}s (see:
	 * {@link JunctorConditionForFilterTreeDTO#setOperands(Set)}in class
	 * {@link JunctorConditionForFilterTreeDTO}. IMPORTANT: This method has to
	 * be called in a valid open transaction!
	 * 
	 * @param session
	 *            The session to store to; it is guaranteed that only
	 *            {@link JunctorConditionForFilterTreeDTO} will be stored and/or
	 *            deleted.
	 * @throws If
	 *             an error occurred
	 */
	public abstract void storeJoinData(Session session) throws Throwable;

	/**
	 * ONLY USED FOR MAPPING PURPOSES!
	 * 
	 * For Example: This method is used to load the join-data and set
	 * {@link NewAMSConfigurationElementDTO}s (see:
	 * {@link JunctorConditionForFilterTreeDTO#setOperands(Set)} in class
	 * {@link JunctorConditionForFilterTreeDTO}. IMPORTANT: This method has to
	 * be called in a valid open transaction!
	 * 
	 * @param session
	 *            The session to store to; it is guaranteed that only
	 *            {@link JunctorConditionForFilterTreeDTO} will be loaded and
	 *            nothing be deleted.
	 * @param allJoinedElements
	 *            All avail {@link NewAMSConfigurationElementDTO}; is is
	 *            guaranteed that no {@link NewAMSConfigurationElementDTO} will
	 *            be modified or deleted.
	 * @throws If
	 *             an error occurred
	 */
	public abstract void loadJoinData(Session session,
			Collection<T> allJoinedElements) throws Throwable;

}