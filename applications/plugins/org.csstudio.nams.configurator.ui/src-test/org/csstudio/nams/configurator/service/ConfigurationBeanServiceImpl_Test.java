package org.csstudio.nams.configurator.service;

import junit.framework.TestCase;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.Configuration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConfigurationBeanServiceImpl_Test extends TestCase {

	private LocalStoreConfigurationService confService;
	private Configuration testDaten;

	@Before
	public void setUp() throws Exception {
		testDaten = null;
		
		confService = EasyMock.createMock(LocalStoreConfigurationService.class);
		EasyMock.replay(confService);
	}

	@After
	public void tearDown() throws Exception {
		EasyMock.verify(confService);
		confService = null;
	}

	@Test
	public void testConfigurationBeanServiceImpl() {
		fail("Diesen Test implementieren, wenn der Conf-Service aufgeräumt ist, also das Configuration ein reines Material!");
		
		ConfigurationBeanService service = new ConfigurationBeanServiceImpl(confService);
		
		
	}

}
