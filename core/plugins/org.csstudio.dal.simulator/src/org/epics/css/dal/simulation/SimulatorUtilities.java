/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

/**
 *
 */
package org.epics.css.dal.simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;

import org.csstudio.dal.SimpleProperty;
import org.csstudio.dal.device.AbstractDevice;
import org.csstudio.dal.device.PowerSupply;
import org.csstudio.dal.proxy.DeviceProxy;
import org.csstudio.dal.proxy.PropertyProxy;
import org.csstudio.dal.spi.Plugs;
import org.epics.css.dal.simulation.ps.PSDeviceProxy;

import com.cosylab.naming.URIName;


/**
 * @author ikriznar
 *
 */
public final class SimulatorUtilities
{
	public static final String CONNECTION_DELAY = "connectionDelay";
	
	private static HashMap<String, Object> configurations;
	
	static {
		configurations  = new HashMap<String, Object>();
		configurations.put(CONNECTION_DELAY, new Long(0));
	}
	
	private SimulatorUtilities()
	{
		super();
	}

	/**
	 * Loads to properties configuration, which enables EPICS plug.
	 * @param p configuration
	 */
	public static void configureSimulatorPlug(Properties p)
	{
		Plugs.configureSimulatorPlug(p);
	}

	@SuppressWarnings("unchecked")
	public static Class<?extends PropertyProxy<?,?>> getPropertyProxyImplementationClass(
	    Class<?extends SimpleProperty<?>> propertyType, Class<?extends SimpleProperty<?>> implType)
	{
		
		if (propertyType!=null) {
			String n= propertyType.getName();
			n= n.substring(n.lastIndexOf('.')+1);
			n= "org.epics.css.dal.simulation."+n+"ProxyImpl";
			
			try {
				return (Class<?extends PropertyProxy<?,?>>)Class.forName(n);
			} catch (ClassNotFoundException e) {
				// noop
			}
		}
		
		if (implType!=null) {
			String n= implType.getName();
			n= n.substring(n.lastIndexOf('.')+1,n.length()-4);
			n= "org.epics.css.dal.simulation."+n+"ProxyImpl";
			
			try {
				return (Class<?extends PropertyProxy<?,?>>)Class.forName(n);
			} catch (ClassNotFoundException e) {
				// noop
			}
		}

		return DoublePropertyProxyImpl.class;
	}

	@SuppressWarnings("unchecked")
	public static Class<?extends DeviceProxy<?>> getDeviceProxyImplementationClass(
	    Class<?extends AbstractDevice> deviceType)
	{
		if (PowerSupply.class.isAssignableFrom(deviceType)) return (Class<? extends DeviceProxy<?>>) PSDeviceProxy.class;
		return (Class<? extends DeviceProxy<?>>) DeviceProxyImpl.class;
	}

	public static Object getCharacteristic(String characteristicName,
	    PropertyProxy<?,?> ppi)
	{
		DirContext ctx = SimulatorPlug.getInstance().getDefaultDirectory();

		try {
			URIName uri = new URIName(null, SimulatorPlug.DEFAULT_AUTHORITY,
				    ppi.getUniqueName(), null);
			Attributes attr = ctx.getAttributes(uri);
			Object characteristic = null;

			if (attr instanceof org.epics.css.dal.directory.Attributes) {
				org.epics.css.dal.directory.Attributes at = (org.epics.css.dal.directory.Attributes)attr;
				characteristic = at.getAttributeValue(characteristicName);
			} else if (attr != null) {
				characteristic = attr.get(characteristicName).get();
			}

			if (characteristic == null) {
				uri = new URIName(null, SimulatorPlug.DEFAULT_AUTHORITY,
					    ppi.getClass().getSimpleName(), null);
				attr = ctx.getAttributes(uri);

				if (attr instanceof org.epics.css.dal.directory.Attributes) {
					org.epics.css.dal.directory.Attributes at = (org.epics.css.dal.directory.Attributes)attr;
					characteristic = at.getAttributeValue(characteristicName);
				} else if (attr != null) {
					characteristic = attr.get(characteristicName).get();
				}
			}

			return characteristic;
		} catch (NamingException e) {
			throw new RuntimeException("Cannot instanitate URIName.", e);
		}
	}

	public static Object putCharacteristic(String characteristicName,
		    String propertyUniqueName, Object value)
	{
		DirContext ctx = SimulatorPlug.getInstance().getDefaultDirectory();

		try {
			URIName uri = new URIName(null, SimulatorPlug.DEFAULT_AUTHORITY,
				    propertyUniqueName, null);
			Attributes attr = ctx.getAttributes(uri);
			
			if (attr==null) {
				attr=new org.epics.css.dal.directory.Attributes();
				ctx.bind(uri, null, attr);
			}
			
			Object characteristic = null;

			if (attr instanceof org.epics.css.dal.directory.Attributes) {
				org.epics.css.dal.directory.Attributes at = (org.epics.css.dal.directory.Attributes)attr;
				characteristic = at.putAttributeValue(characteristicName, value);
			} else if (attr != null) {
				characteristic = attr.put(characteristicName, value);
			}


			return characteristic;
		} catch (NamingException e) {
			throw new RuntimeException("Cannot instanitate URIName.", e);
		}
	}

	public static String[] getCharacteristicNames(PropertyProxy<?,?> ppi)
	{
		DirContext ctx = SimulatorPlug.getInstance().getDefaultDirectory();

		try {
			URIName uri = new URIName(null, SimulatorPlug.DEFAULT_AUTHORITY,
				    ppi.getUniqueName(), null);
			Attributes attr = ctx.getAttributes(uri);

			if (attr == null) {
				uri = new URIName(null, SimulatorPlug.DEFAULT_AUTHORITY,
					    ppi.getClass().getSimpleName(), null);
				attr = ctx.getAttributes(uri);
			}

			NamingEnumeration<String> en = attr.getIDs();
			ArrayList<String> list = new ArrayList<String>();

			while (en.hasMore()) {
				list.add(en.next());
			}

			String[] names = new String[list.size()];

			return list.toArray(names);
		} catch (NamingException e) {
			throw new RuntimeException("Cannot instanitate URIName.", e);
		}
	}
	
	public static Object getConfiguration(String configName) {
		return configurations.get(configName);
	}
	
	public static void putConfiguration(String configName, Object config) {
		configurations.put(configName, config);
	}
	
}

/* __oOo__ */
