package org.epics.css.dal.tango;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.epics.css.dal.spi.DeviceFactoryService;
import org.epics.css.dal.spi.Plugs;
import org.epics.css.dal.spi.PropertyFactoryService;

/**
 * 
 * <code>PlugUtilities</code> provides some utility methods for the Tango 
 * DAL plug.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class PlugUtilities {
	
	/**
	 * Initializes the plug properties for tango.
	 * 
	 * @param p the properties object, which is being modified by this method
	 */
	public static void configureTangoPlug(Properties p)
	{
		String[] s = Plugs.getPlugNames(p);
		Set<String> set = new HashSet<String>(Arrays.asList(s));

		if (!set.contains(TangoPropertyPlug.PLUG_TYPE)) {
			set.add(TangoPropertyPlug.PLUG_TYPE);

			StringBuffer sb = new StringBuffer();
			
			Iterator<String> iter = set.iterator();
			while(iter.hasNext()) {
				if (sb.length() > 0) {
					sb.append(',');
				}
				sb.append(iter.next());
			}

			p.put(Plugs.PLUGS, sb.toString());
		}

		p.put(Plugs.PLUGS_DEFAULT, TangoPropertyPlug.PLUG_TYPE);
		p.setProperty(DeviceFactoryService.DEFAULT_APPLICATION_CONTEXT,TangoApplicationContext.class.getName());
		p.setProperty(PropertyFactoryService.DEFAULT_FACTORY_IMPL, PropertyFactoryImpl.class.getName());
		p.put(Plugs.PLUG_PROPERTY_FACTORY_CLASS + TangoPropertyPlug.PLUG_TYPE, PropertyFactoryImpl.class.getName());
		System.getProperties().setProperty(DeviceFactoryService.DEFAULT_APPLICATION_CONTEXT,TangoApplicationContext.class.getName());
	}
}
