/**
 * 
 */
package org.csstudio.platform.libs.dal.tests.plugin;


import junit.framework.TestCase;

import org.csstudio.dal.CssApplicationContext;
import org.csstudio.dal.DalPlugin;
import org.csstudio.dal.PlugRegistry;
import org.csstudio.platform.libs.dal.simulator.Activator;
import org.epics.css.dal.CharacteristicInfo;
import org.epics.css.dal.simple.RemoteInfo;
import org.epics.css.dal.simple.SimpleDALBroker;
import org.epics.css.dal.simulation.PropertyFactoryImpl;
import org.epics.css.dal.simulation.SimulatorPlug;
import org.epics.css.dal.spi.LinkPolicy;
import org.epics.css.dal.spi.PropertyFactory;
import org.epics.css.dal.spi.PropertyFactoryService;
import org.junit.After;
import org.junit.Before;

import com.cosylab.util.CommonException;

/**
 * @author cosy
 *
 */
public class DalPluginTest extends TestCase {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	public void testSimulatorPlugin() {
		
		String simulator= "Simulator";
		
		assertEquals(SimulatorPlug.PLUG_TYPE, simulator);
		
		DalPlugin plugin = DalPlugin.getDefault();
		
		assertNotNull(plugin);
		
		assertNotNull(plugin.getApplicationContext());
		assertEquals(CssApplicationContext.class, plugin.getApplicationContext().getClass());
		CssApplicationContext ctx= (CssApplicationContext)plugin.getApplicationContext();
		
		assertNotNull(ctx);
		
		SimpleDALBroker broker= plugin.getSimpleDALBroker();
		
		assertNotNull(broker);
		
		PlugRegistry reg= PlugRegistry.getInstance();
		
		assertNotNull(reg);
		
		assertTrue(reg.isRegistered(simulator));
		
		PropertyFactoryService service= reg.getPropertyFactoryService(simulator);
		
		assertNotNull(service);
		assertEquals(Activator.class, service.getClass());
		
		PropertyFactory fac= plugin.getPropertyFactory(ctx, LinkPolicy.SYNC_LINK_POLICY, simulator);
		
		assertNotNull(fac);
		assertEquals(PropertyFactoryImpl.class, fac.getClass());
		assertEquals(ctx,fac.getApplicationContext());
		assertEquals(simulator, fac.getPlugType());	
		
		
		try {
			Object value= broker.getValue(new RemoteInfo(simulator, "DUMMY"));
			Object units= broker.getValue(new RemoteInfo(simulator, "DUMMY", CharacteristicInfo.C_UNITS.getName(), null));
			
			System.out.println("DUMMY has '"+value+"' of type '"+units+"'.");
			
			assertNotNull(value);
			assertNotNull(units);
			
		} catch (Exception e) {
			fail(e.toString());
		}
		
	}
}
