package org.csstudio.nams.service.history.impl.confstore;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.csstudio.nams.service.history.declaration.HistoryService;
import org.csstudio.nams.service.history.extensionPoint.HistoryServiceFactory;

public class HistoryServiceFactoryImpl implements HistoryServiceFactory {

	private static LocalStoreConfigurationService localStoreConfigurationService;

	public HistoryServiceFactoryImpl() {
		// TODO Auto-generated constructor stub
	}

	public HistoryService createService() {
		// TODO Auto-generated method stub
		return new HistoryServiceImpl(localStoreConfigurationService);
	}
	
	public static void injectLocalStoreConfigurationService(LocalStoreConfigurationService localStoreConfigurationService) {
		HistoryServiceFactoryImpl.localStoreConfigurationService = localStoreConfigurationService;
	}

}
