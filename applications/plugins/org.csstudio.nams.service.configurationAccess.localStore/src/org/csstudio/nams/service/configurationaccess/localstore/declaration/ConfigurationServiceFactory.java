package org.csstudio.nams.service.configurationaccess.localstore.declaration;

public interface ConfigurationServiceFactory {
	public LocalStoreConfigurationService getConfigurationService(String connectionDriver, String connectionURL, String dialect, String username, String password);
}
