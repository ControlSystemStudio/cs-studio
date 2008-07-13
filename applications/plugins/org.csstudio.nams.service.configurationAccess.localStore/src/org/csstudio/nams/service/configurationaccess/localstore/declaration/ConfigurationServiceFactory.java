package org.csstudio.nams.service.configurationaccess.localstore.declaration;

public interface ConfigurationServiceFactory {
	
	/**
	 * @deprecated Besser:
	 * Der Service ist nach der Erstellung vollstaenig initialisiert, sonst wirft bereits die Factory eine exception.
	 * Reimgegeben wird nur: Der User, das Password, der Connection String und ein Element von {@link DatabaseType}.
	 * public LocalStoreConfigurationService getConfigurationService(String connectionURL, String username, String password, DatabaseType dbType)
	 */
	@Deprecated
	public LocalStoreConfigurationService getConfigurationService(String connectionDriver, String connectionURL, String dialect, String username, String password);
}
