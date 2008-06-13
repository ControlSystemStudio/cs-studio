package org.csstudio.nams.configurator;

import junit.framework.TestCase;

import org.csstudio.nams.configurator.treeviewer.ConfigurationTreeView;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.easymock.EasyMock;

public class Activator_Test extends TestCase {
	public void testBundleStart() throws Exception {
		// Service-Mocks vorbereiten...
//		ConfigurationEditingStoreService editingStoreService = EasyMock
//				.createMock(ConfigurationEditingStoreService.class);
//		ConfigurationStoreService storeService = EasyMock
//				.createMock(ConfigurationStoreService.class);
		Logger logger = EasyMock.createNiceMock(Logger.class);
		EasyMock.replay(/*editingStoreService, storeService,*/ logger);

		// Activator starten...
		ConfiguratorActivator activator = new ConfiguratorActivator();
		activator.bundleStart(/*editingStoreService, storeService,*/ logger);

//		assertSame(editingStoreService, ConfigurationTreeView.getEditingStoreService());
//		assertSame(storeService, ConfigurationTreeView.getConfigurationService());
		assertSame(logger, ConfigurationTreeView.getLogger());
		
		activator.stopBundle(logger);
	}
}
