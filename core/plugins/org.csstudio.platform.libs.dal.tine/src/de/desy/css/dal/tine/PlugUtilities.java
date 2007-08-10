package de.desy.css.dal.tine;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.epics.css.dal.spi.DeviceFactoryService;
import org.epics.css.dal.spi.Plugs;
import org.epics.css.dal.spi.PropertyFactoryService;

public class PlugUtilities {
	
	public static void configureTINEPlug(Properties p)
	{
		String[] s = Plugs.getPlugNames(p);
		Set<String> set = new HashSet<String>(Arrays.asList(s));

		if (!set.contains(TINEPlug.PLUG_TYPE)) {
			set.add(TINEPlug.PLUG_TYPE);

			StringBuffer sb = new StringBuffer();

			for (Iterator iter = set.iterator(); iter.hasNext();) {
				if (sb.length() > 0) {
					sb.append(',');
				}

				sb.append(iter.next());
			}

			p.put(Plugs.PLUGS, sb.toString());
		}

		p.put(Plugs.PLUGS_DEFAULT, TINEPlug.PLUG_TYPE);
		p.setProperty(DeviceFactoryService.DEFAULT_APPLICATION_CONTEXT,TINEApplicationContext.class.getName());
		p.setProperty(PropertyFactoryService.DEFAULT_FACTORY_IMPL, PropertyFactoryImpl.class.getName());
		p.put(Plugs.PLUG_PROPERTY_FACTORY_CLASS + TINEPlug.PLUG_TYPE, PropertyFactoryImpl.class.getName());
		System.getProperties().setProperty(DeviceFactoryService.DEFAULT_APPLICATION_CONTEXT,TINEApplicationContext.class.getName());
	}
}
