package org.csstudio.nams.service.configurationaccess.localstore;

import java.util.List;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.NewAMSConfigurationElementDTO;
import org.csstudio.nams.service.configurationaccess.localstore.internalDTOs.filterConditionSpecifics.HasManuallyJoinedElements;

/**
 * A simple mapper to access the database.
 */
public interface Mapper {
	/**
	 * Stores or updates given element into database. Also saves or update
	 * manually joined elements if given element is instance of
	 * {@link HasManuallyJoinedElements}.
	 */
	public void save(NewAMSConfigurationElementDTO element) throws Throwable;

	/**
	 * Deletes a given element in the database. Also deletes manually joined
	 * elements if given element is instance of {@link HasManuallyJoinedElements}.
	 */
	public void delete(NewAMSConfigurationElementDTO element) throws Throwable;

	/**
	 * Loads all elements of given type from database. Also loads manually
	 * joined elements if given element is instance of {@link HasManuallyJoinedElements}.
	 */
	public <T extends NewAMSConfigurationElementDTO> List<T> loadAll(
			Class<T> clasz) throws Throwable;
}