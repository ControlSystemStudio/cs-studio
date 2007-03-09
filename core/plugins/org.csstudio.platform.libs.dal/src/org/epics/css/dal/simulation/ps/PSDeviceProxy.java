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

package org.epics.css.dal.simulation.ps;

import org.epics.css.dal.SimpleProperty;
import org.epics.css.dal.context.ConnectionState;
import org.epics.css.dal.proxy.DeviceProxy;
import org.epics.css.dal.proxy.DirectoryProxy;
import org.epics.css.dal.proxy.PropertyProxy;
import org.epics.css.dal.simulation.CommandProxyImpl;
import org.epics.css.dal.simulation.DeviceProxyImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 *
 * <code>PSDeviceProxy</code> simulation device proxy implementation
 * for the PowerSupply.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 * @version $Id$
 *
 * @since VERSION
 */
public class PSDeviceProxy extends DeviceProxyImpl implements DeviceProxy,
	DirectoryProxy
{
	private Map<String, Class<?extends PropertyProxy>> propertyProxyTypes;

	/**
	 * Creates a new DeviceProxyImpl object.
	 *
	 * @param name Proxy name
	 */
	public PSDeviceProxy(String name)
	{
		super(name);
	}

	public void initalizeCommands(CommandProxyImpl[] ifcComm)
	{
		for (CommandProxyImpl com : ifcComm) {
			commands.put(com.getName(), com);
		}
	}

	public void initalizeProperties(String[] propertyNames,
	    Class<?extends SimpleProperty>[] propTypes,
	    Class<?extends PropertyProxy>[] propProxyTypes)
	{
		if (propertyProxies == null) {
			propertyProxies = new HashMap<String, PropertyProxy>(propertyNames.length
				    + 1);
		}

		if (propertyProxyTypes == null) {
			propertyProxyTypes = new HashMap<String, Class<?extends PropertyProxy>>(propertyNames.length
				    + 1);
		}

		if (directoryProxies == null) {
			directoryProxies = new HashMap<String, DirectoryProxy>(propertyNames.length
				    + 1);
		}

		if (propProxyTypes == null) {
			for (int i = 0; i < propertyNames.length; i++) {
				String s = propertyNames[i];
				propertyTypes.put(s, propTypes[i]);
				propertyProxies.put(s, null);
				directoryProxies.put(s, null);
			}
		} else {
			for (int i = 0; i < propertyNames.length; i++) {
				String s = propertyNames[i];
				propertyTypes.put(s, propTypes[i]);
				propertyProxies.put(s, null);
				directoryProxies.put(s, null);
				propertyProxyTypes.put(s, propProxyTypes[i]);
			}
		}

		delayedConnect(2000);
	}

	public void delayedConnect(long timeout)
	{
		if (timeout > 0) {
			Timer t = new Timer();
			setConnectionState(ConnectionState.CONNECTING);
			t.schedule(new TimerTask() {
					@Override
					public void run()
					{
						setConnectionState(ConnectionState.CONNECTED);
					}
				}, timeout);
		} else {
			setConnectionState(ConnectionState.CONNECTED);
		}
	}
} /* __oOo__ */


/* __oOo__ */
