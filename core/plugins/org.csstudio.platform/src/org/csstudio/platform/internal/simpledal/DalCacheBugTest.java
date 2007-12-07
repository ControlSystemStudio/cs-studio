package org.csstudio.platform.internal.simpledal;

import org.epics.css.dal.DoubleProperty;
import org.epics.css.dal.StringProperty;
import org.epics.css.dal.impl.DefaultApplicationContext;
import org.epics.css.dal.spi.DefaultPropertyFactoryService;
import org.epics.css.dal.spi.LinkPolicy;
import org.epics.css.dal.spi.PropertyFactory;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class DalCacheBugTest {

	private PropertyFactory _propertyFactory;
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		// set system properties
		System.setProperty("dal.plugs", "EPICS");
		System.setProperty("dal.plugs.default", "EPICS");
		System.setProperty("dal.propertyfactory.EPICS",
				"org.epics.css.dal.epics.PropertyFactoryImpl");

		System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list",
				"YES");
		System.setProperty(
				"com.cosylab.epics.caj.CAJContext.connection_timeout", "30.0");
		System.setProperty("com.cosylab.epics.caj.CAJContext.beacon_period",
				"15.0");
		System.setProperty("com.cosylab.epics.caj.CAJContext.repeater_port",
				"5065");
		System.setProperty("com.cosylab.epics.caj.CAJContext.server_port",
				"5064");
		System.setProperty("com.cosylab.epics.caj.CAJContext.max_array_bytes",
				"16384");

		// get the property _factory
		_propertyFactory = DefaultPropertyFactoryService.getPropertyFactoryService()
				.getPropertyFactory(new DefaultApplicationContext("Test"),
						LinkPolicy.ASYNC_LINK_POLICY, "EPICS");

	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCacheBug() throws Exception {
		String pv = "Random:1";
		
		// get the pv as DoubleProperty
		DoubleProperty doubleProperty = _propertyFactory.getProperty(pv, DoubleProperty.class, null);
		assertNotNull(doubleProperty);
		assertTrue(doubleProperty instanceof DoubleProperty);

		// get the same pv as StringProperty
		StringProperty stringProperty = _propertyFactory.getProperty(pv, StringProperty.class, null);
		assertNotNull(stringProperty);
		assertTrue(stringProperty instanceof StringProperty);
		
		
		// ergo -> ClassCastException
	}
}
