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

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.csstudio.dal.SimpleProperty;
import org.csstudio.dal.context.ConnectionState;
import org.csstudio.dal.proxy.DeviceProxy;
import org.csstudio.dal.proxy.DirectoryProxy;
import org.csstudio.dal.proxy.PropertyProxy;
import org.epics.css.dal.simulation.CommandProxyImpl;
import org.epics.css.dal.simulation.DeviceProxyImpl;
import org.epics.css.dal.simulation.SimulatorPlug;
import org.epics.css.dal.simulation.SimulatorUtilities;


/**
 *
 * <code>PSDeviceProxy</code> simulation device proxy implementation
 * for the PowerSupply.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 * @since VERSION
 */
public class PSDeviceProxy extends DeviceProxyImpl implements DeviceProxy<SimulatorPlug>,
	DirectoryProxy<SimulatorPlug>
{
	private Map<String, Class<?extends PropertyProxy<?,SimulatorPlug>>> propertyProxyTypes;
	private long delay = 0;
	
	/**
	 * Creates a new DeviceProxyImpl object.
	 *
	 * @param name Proxy name
	 */
	public PSDeviceProxy(String name, SimulatorPlug plug)
	{
		this(name, plug, (Long)SimulatorUtilities.getConfiguration(SimulatorUtilities.CONNECTION_DELAY));
	}
	
	/**
	 * Creates a new DeviceProxyImpl object.
	 *
	 * @param name Proxy name
	 * @param connectionDelay
	 */
	public PSDeviceProxy(String name, SimulatorPlug plug, long connectionDelay)
	{
		super(name,plug);
		this.delay = connectionDelay;
	}

	public void initalizeCommands(CommandProxyImpl[] ifcComm)
	{
		for (CommandProxyImpl com : ifcComm) {
			commands.put(com.getName(), com);
		}
	}

	public void initalizeProperties(String[] propertyNames,
	    Class<?extends SimpleProperty<?>>[] propTypes,
	    Class<?extends PropertyProxy<?,SimulatorPlug>>[] propProxyTypes)
	{
		if (propertyProxies == null) {
			propertyProxies = new HashMap<String, PropertyProxy<?,SimulatorPlug>>(propertyNames.length
				    + 1);
		}

		if (propertyProxyTypes == null) {
			propertyProxyTypes = new HashMap<String, Class<? extends PropertyProxy<?,SimulatorPlug>>>(propertyNames.length
				    + 1);
		}

		if (directoryProxies == null) {
			directoryProxies = new HashMap<String, DirectoryProxy<SimulatorPlug>>(propertyNames.length
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

		delayedConnect(delay);
	}
	
	public void delayedConnect(long timeout)
	{
		setConnectionState(ConnectionState.CONNECTING);
		if (timeout > 0) {
			Timer t = new Timer();
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
