
package org.csstudio.nams.service.history.impl.confstore;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.history.declaration.HistoryService;
import org.csstudio.nams.service.history.extensionPoint.HistoryServiceFactory;

public class HistoryServiceFactoryImpl implements HistoryServiceFactory {

	private static LocalStoreConfigurationService localStoreConfigurationService;

	public static void injectLocalStoreConfigurationService(
			final LocalStoreConfigurationService localStoreConfigurationService) {
		HistoryServiceFactoryImpl.localStoreConfigurationService = localStoreConfigurationService;
	}

	public HistoryServiceFactoryImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
    public HistoryService createService() {
		// TODO Auto-generated method stub
		return new HistoryServiceImpl(
				HistoryServiceFactoryImpl.localStoreConfigurationService);
	}
}
