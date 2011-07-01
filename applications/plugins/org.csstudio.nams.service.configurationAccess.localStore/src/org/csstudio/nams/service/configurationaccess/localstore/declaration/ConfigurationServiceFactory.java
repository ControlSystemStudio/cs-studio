
package org.csstudio.nams.service.configurationaccess.localstore.declaration;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.exceptions.StorageError;

/**
 * Fabrik für den {@link LocalStoreConfigurationService}.
 */
public interface ConfigurationServiceFactory {
	/**
	 * Liefert ein vollständig initialisierten
	 * {@link LocalStoreConfigurationService} für die angegebene
	 * Datenbankeinstellung.
	 * 
	 * @param connectionURL
	 *            Die JDBC-URL.
	 * @param dbType
	 *            Die Datenbank-Art.
	 * @param username
	 *            Der Benutzer-/Shema-Name.
	 * @param password
	 *            Das Zugriffspassword.
	 * @return Den passenden {@link LocalStoreConfigurationService}.
	 * 
	 * @throws StorageError
	 *             If mapping or connection data is invalid.
	 */
	public LocalStoreConfigurationService getConfigurationService(
			String connectionURL, DatabaseType dbType, String username,
			String password) throws StorageError;
}
