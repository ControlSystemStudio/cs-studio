
package org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics;

import java.util.Set;

import org.csstudio.nams.service.configurationaccess.localstore.Mapper;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.Configuration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.NewAMSConfigurationElementDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.FilterConditionDTO;

/**
 * Indicates that the given Element has Joined-Elements to be load and stored
 * manually by the {@link LocalStoreConfigurationService}.
 * 
 * XXX Besser wäre die Hibernate session nicht reinzugeben, sondern über eine
 * Operation anzugeben, welche dto-typen die joined-link-Daten sichert, so
 * könnten Sammlungen dieser injeziert werden; die DTOs wären dann
 * "mapper-neutral" und testbar. punlic Class<?>[] getJoinLinkDataTypes();
 * public void storeJoinLinkData(Map<Class<?>>, NamsDTO> linkDataMapToStoreTo)
 * etc... Die Konfiguration anschließemd einheitich für alle ELemente
 * abstrahieren und im Service ausführen; der Typ {@link Configuration} ist dann
 * ein simpler Container
 */
public interface HasManuallyJoinedElements {

	/**
	 * ONLY USED FOR MAPPING PURPOSES!
	 * 
	 * Deletes all join-data (link/mapping; NOT the joined elements)
	 * 
	 * IMPORTANT: This method has to be called in a valid open transaction!
	 * 
	 * @param mapper
	 *            The Mapper to delete to; it is guaranteed that all join data
	 *            will be deleted.
	 * @throws If
	 *             an error occurred
	 */
	public abstract void deleteJoinLinkData(Mapper mapper) throws Throwable;

	/**
	 * ONLY USED FOR MAPPING PURPOSES!
	 * 
	 * For Example: This method is used to load the join-data and set
	 * {@link NewAMSConfigurationElementDTO}s (see:
	 * {@link JunctorCondForFilterTreeDTO#setOperands(Set)} in class
	 * {@link JunctorCondForFilterTreeDTO}. IMPORTANT: This method has to
	 * be called in a valid open transaction!
	 * 
	 * @param mapper
	 *            The session to store to; it is guaranteed that only
	 *            {@link JunctorCondForFilterTreeDTO} will be loaded and
	 *            nothing be deleted.
	 * @throws If
	 *             an error occurred
	 */
	public abstract void loadJoinData(Mapper mapper) throws Throwable;

	/**
	 * ONLY USED FOR MAPPING PURPOSES!
	 * 
	 * For Example: This method is used to store the join-data for previously
	 * set {@link FilterConditionDTO}s (see:
	 * {@link JunctorCondForFilterTreeDTO#setOperands(Set)}in class
	 * {@link JunctorCondForFilterTreeDTO}. IMPORTANT: This method has to
	 * be called in a valid open transaction!
	 * 
	 * @param mapper
	 *            The Mapper to store to; it is quaranted that only belonging
	 *            mappings are stored..
	 * @throws If
	 *             an error occurred
	 */
	public abstract void storeJoinLinkData(Mapper mapper) throws Throwable;
}