
package org.csstudio.nams.service.configurationaccess.localstore;

import java.io.Serializable;
import java.util.List;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.NewAMSConfigurationElementDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.HasManuallyJoinedElements;

/**
 * A simple mapper to access the database.
 */
public interface Mapper {
	/**
	 * Deletes a given element in the database. Also deletes manually joined
	 * elements if given element is instance of
	 * {@link HasManuallyJoinedElements}.
	 */
	public void delete(NewAMSConfigurationElementDTO element) throws Throwable;

	/**
	 * Loads the element identified by given id from the database. Also loads
	 * manually joined elements if given element is instance of
	 * {@link HasManuallyJoinedElements}.
	 * 
	 * @param id
	 *            The id of element to find.
	 * @param clasz
	 *            Type of searched element.
	 * @param loadManuallyJoinedMappingsIfAvailable
	 *            Indicates if elements inherits
	 *            {@link HasManuallyJoinedElements} should be forced to load
	 *            their joins.
	 * @return The element identified by given id or null if none matching is
	 *         available.
	 */
	public <T extends NewAMSConfigurationElementDTO> T findForId(
			Class<T> clasz, Serializable id,
			boolean loadManuallyJoinedMappingsIfAvailable) throws Throwable;

	/**
	 * Loads all elements of given type from database. Also loads manually
	 * joined elements if given element is instance of
	 * {@link HasManuallyJoinedElements}.
	 * 
	 * @param clasz
	 *            Type of searched elements.
	 * @param loadManuallyJoinedMappingsIfAvailable
	 *            Indicates if elements inherits
	 *            {@link HasManuallyJoinedElements} should be forced to load
	 *            their joins.
	 */
	public <T extends NewAMSConfigurationElementDTO> List<T> loadAll(
			Class<T> clasz, boolean loadManuallyJoinedMappingsIfAvailable)
			throws Throwable;

	/**
	 * Stores or updates given element into database. Also saves or update
	 * manually joined elements if given element is instance of
	 * {@link HasManuallyJoinedElements}.
	 */
	public void save(NewAMSConfigurationElementDTO element) throws Throwable;
}