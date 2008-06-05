package org.csstudio.nams.service.configurationaccess.localstore.declaration;

public interface LocalStoreConfigurationService {

	/**
	 * Retrieves the current syncronize-state. This method is only used to
	 * enable a syncronisation support between the new decision-department-
	 * (earlier: FilterManager) and the deprecated but still in use
	 * Distributor-application; it is not recommend to use this flag for other
	 * purposes.
	 * 
	 * @return The current state flag.
	 * @throws StorageError
	 *             If a configuration error is present and this database is not
	 *             accessible.
	 * @throws StorageException
	 *             If a database exception occurred or database is unexpected
	 *             unreachable.
	 * @throws InconsistentConfiguration
	 *             If the stored configuration is inconsistent.
	 */
	public SyncronizeFlagDTO getCurrentSyncronizeFlagState()
			throws StorageError, StorageException, InconsistentConfiguration;

	/**
	 * Loads the entire configuration from the Database. ConfigurationDTO will
	 * be an object that contain a consitent set of all configuration elements
	 * loaded by exactly one transaction.
	 * 
	 * @return The entire configuration.
	 * @throws StorageError
	 *             If a configuration error is present and this database is not
	 *             accessible.
	 * @throws StorageException
	 *             If a database exception occurred or database is unexpected
	 *             unreachable.
	 * @throws InconsistentConfiguration
	 *             If the stored configuration is inconsistent.
	 */
	public ConfigurationDTO getEntireConfiguration() throws StorageError,
			StorageException, InconsistentConfiguration;

	/**
	 * @deprecated Just for testing the functionality - not for production use!
	 */
	@Deprecated
	public TopicDTO getTopicConfigurations(
			TopicConfigurationId topicConfigurationDatabaseId);

	/**
	 * Saves a new current syncronize-state. This method is only used to enable
	 * a syncronisation support between the new decision-department- (earlier:
	 * FilterManager) and the deprecated but still in use
	 * Distributor-application; it is not recommend to use this flag for other
	 * purposes.
	 * 
	 * @param currentState
	 *            The new state to save.
	 * @throws StorageError
	 *             If a configuration error is present and this database is not
	 *             accessible.
	 * @throws StorageException
	 *             If a database exception occurred or database is unexpected
	 *             unreachable.
	 * @throws UnknownConfigurationElementError
	 *             If the flag-object to be stored is not known by this
	 *             configuration database..
	 */
	public void saveCurrentSyncronizeFlagState(SyncronizeFlagDTO currentState)
			throws StorageError, StorageException,
			UnknownConfigurationElementError;
}
