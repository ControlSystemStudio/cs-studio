package org.epics.css.dal.demo;

import org.epics.css.dal.DoubleProperty;
import org.epics.css.dal.epics.PlugUtilities;
import org.epics.css.dal.epics.test.DoubleChannelTest;
import org.epics.css.dal.impl.DefaultApplicationContext;
import org.epics.css.dal.spi.DefaultPropertyFactoryService;
import org.epics.css.dal.spi.LinkPolicy;
import org.epics.css.dal.spi.PropertyFactory;

public class SimpleDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			
			DefaultApplicationContext ctx= new DefaultApplicationContext("Simple Demo App");

			// This is the conveniente way, if we don't want to
			// make any reference to EPICS DAL implementation, 
			// then go further
			//PlugUtilities.configureEPICSPlug(ctx.getConfiguration());
			
			
			// Manual waz to initiate EPICS DAL plugin with confuiguzration strings
			
			ctx.getConfiguration().put("dal.plugs", "EPICS");
			ctx.getConfiguration().put("dal.plugs.default", "EPICS");
			ctx.getConfiguration().put("dal.propertyfactory.EPICS", "org.epics.css.dal.epics.PropertyFactoryImpl");

			// just to see the configuration in console we print
			System.out.println(ctx.getConfiguration());
			
			// Now we make singel DAL property connected to EPICS channel
			
			PropertyFactory factory= DefaultPropertyFactoryService.getPropertyFactoryService().getPropertyFactory(ctx,LinkPolicy.SYNC_LINK_POLICY);
			
			DoubleProperty p= factory.getProperty("MY:EPICS:CHANNEL",DoubleProperty.class,null);
			
			System.out.println(p.getUniqueName()+": "+p.getValue());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
