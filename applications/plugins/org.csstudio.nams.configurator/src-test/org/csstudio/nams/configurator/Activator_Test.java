package org.csstudio.nams.configurator;

import junit.framework.TestCase;

import org.csstudio.ams.configurationStoreService.declaration.ConfigurationEditingStoreService;
import org.csstudio.ams.configurationStoreService.declaration.ConfigurationStoreService;
import org.csstudio.ams.service.logging.declaration.Logger;
import org.csstudio.nams.configurator.treeviewer.ConfigurationTreeView;
import org.easymock.EasyMock;

public class Activator_Test extends TestCase {
	public void testBundleStart() {
		// Service-Mocks vorbereiten...
		ConfigurationEditingStoreService editingStoreService = EasyMock
				.createMock(ConfigurationEditingStoreService.class);
		ConfigurationStoreService storeService = EasyMock
				.createMock(ConfigurationStoreService.class);
		Logger logger = EasyMock.createNiceMock(Logger.class);
		EasyMock.replay(editingStoreService, storeService, logger);

		// Activator starten...
		Activator activator = new Activator();
		activator.bundleStart(editingStoreService, storeService, logger);

		assertSame(editingStoreService, ConfigurationTreeView.getEditingStoreService());
		assertSame(storeService, ConfigurationTreeView.getConfigurationService());
		assertSame(logger, ConfigurationTreeView.getLogger());
	}
}
