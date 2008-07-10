package org.csstudio.nams.service.configurationaccess.localstore;

import junit.framework.TestCase;

import org.csstudio.nams.service.configurationaccess.localstore.declaration.Configuration;
import org.csstudio.nams.service.configurationaccess.localstore.declaration.LocalStoreConfigurationService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This TestCase tests the factory and the service for a real database (Oracle).
 * The service will be configured and created by the factory and some service
 * interactions will be perforemed. Do not forgett to reset the databse before
 * any run of this integration test!
 * 
 * @author gs, mz
 */
public class ConfigurationServiceFactoryImpl_DatabaseIntegrationTest_RequiresOracle
		extends TestCase {

	private ConfigurationServiceFactoryImpl factory;

	@Before
	public void setUp() throws Exception {
		factory = new ConfigurationServiceFactoryImpl();
	}

	@After
	public void tearDown() throws Exception {
		factory = null;
	}

	@Test
	public void testFactoryAndServiceOnOracle() throws Throwable {
		assertNotNull(factory);
		
		LocalStoreConfigurationService service = factory.getConfigurationService(
				"oracle.jdbc.driver.OracleDriver",
				"jdbc:oracle:thin:@(DESCRIPTION =(ADDRESS = (PROTOCOL = TCP)(HOST = 134.100.7.235)(PORT = 1521))(LOAD_BALANCE = yes)(CONNECT_DATA =(SERVER = DEDICATED)(FAILOVER_MODE =(TYPE = NONE)(METHOD = BASIC)(RETRIES = 180)(DELAY = 5))))",
				"org.hibernate.dialect.Oracle10gDialect",
				"DESY", 
				"DESY");
		
		assertNotNull(service);
		
		Configuration entireConfiguration = service.getEntireConfiguration();
		
		assertNotNull(entireConfiguration);
	}
}
