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

package org.epics.css.dal.simulation;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.dal.DataExchangeException;
import org.csstudio.dal.RemoteException;
import org.csstudio.dal.Request;
import org.csstudio.dal.ResponseListener;
import org.csstudio.dal.SimpleProperty;
import org.csstudio.dal.context.ConnectionState;
import org.csstudio.dal.impl.RequestImpl;
import org.csstudio.dal.impl.ResponseImpl;
import org.csstudio.dal.proxy.AbstractProxyImpl;
import org.csstudio.dal.proxy.CommandProxy;
import org.csstudio.dal.proxy.DeviceProxy;
import org.csstudio.dal.proxy.DirectoryProxy;
import org.csstudio.dal.proxy.PropertyProxy;


/**
 * Simulation implementation of DeviceProxy.
 *
 * @author ikriznar
 */
public class DeviceProxyImpl extends AbstractProxyImpl<SimulatorPlug> implements DeviceProxy<SimulatorPlug>,
	DirectoryProxy<SimulatorPlug>
{
	protected Map<String, Object> characteristics = new HashMap<String, Object>();
	protected Map<String, DirectoryProxy<SimulatorPlug>> directoryProxies;
	protected Map<String, PropertyProxy<?,SimulatorPlug>> propertyProxies;
	protected Map<String, CommandProxy> commands = new HashMap<String, CommandProxy>();
	protected Map<String, Class<? extends SimpleProperty<?>>> propertyTypes = new HashMap<String, Class<? extends SimpleProperty<?>>>();

	/**
	 * Creates a new DeviceProxyImpl object.
	 *
	 * @param name Proxy name
	 */
	public DeviceProxyImpl(String name, SimulatorPlug plug)
	{
		super(name,plug);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.DeviceProxy#getCommand(java.lang.String)
	 */
	public CommandProxy getCommand(String name) throws RemoteException
	{
		return commands.get(name);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.DeviceProxy#getDirectoryProxy(java.lang.String)
	 */
	public DirectoryProxy getDirectoryProxy(String name)
		throws RemoteException
	{
		if (directoryProxies == null) {
			directoryProxies = new HashMap<String, DirectoryProxy<SimulatorPlug>>(3);
		}

		DirectoryProxy<SimulatorPlug> p = directoryProxies.get(name);

		if (p != null) {
			return p;
		}

		p = (DirectoryProxy<SimulatorPlug>) plug.getDirectoryProxy(this.name + '/' + name);
		directoryProxies.put(name, p);

		return p;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.DeviceProxy#getPropertyProxy(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
    public PropertyProxy<?,SimulatorPlug> getPropertyProxy(String name)
		throws RemoteException
	{
		if (propertyProxies == null) {
			propertyProxies = new HashMap<String, PropertyProxy<?,SimulatorPlug>>(3);
		}

		PropertyProxy<?,SimulatorPlug> p = propertyProxies.get(name);

		if (p != null) {
			return p;
		}

		p = (PropertyProxy<?, SimulatorPlug>) plug.getPropertyProxy(this.name + '/' + name,
			    plug.getPropertyProxyImplementationClass(getPropertyType(name),null,name));
		//			    SimulatorUtilities.getPropertyProxyImplementationClass(
		//			        getPropertyType(name)));
		propertyProxies.put(name, p);

		return p;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.DirectoryProxy#getCharacteristic(java.lang.String)
	 */
	public Object getCharacteristic(String characteristicName)
		throws DataExchangeException
	{
		return characteristics.get(characteristicName);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.DirectoryProxy#getCharacteristicNames()
	 */
	public String[] getCharacteristicNames() throws DataExchangeException
	{
		return characteristics.keySet()
		.toArray(new String[characteristics.size()]);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.DirectoryProxy#getCharacteristics(java.lang.String[], org.epics.css.dal.ResponseListener)
	 */
	public Request<? extends Object> getCharacteristics(String[] characteristics,
	    ResponseListener<? extends Object> callback) throws DataExchangeException
	{
		RequestImpl<Object> r = new RequestImpl<Object>(this, (ResponseListener<Object>) callback);

		for (int i = 0; i < characteristics.length; i++) {
			Object value = this.characteristics.get(characteristics[i]);
			r.addResponse(new ResponseImpl<Object>(this, r, value, characteristics[i],
			        value != null, null, null, null, true));
		}

		return r;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.DirectoryProxy#getCommandNames()
	 */
	public String[] getCommandNames() throws DataExchangeException
	{
		return commands.keySet().toArray(new String[commands.size()]);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.DirectoryProxy#getPropertyNames()
	 */
	public String[] getPropertyNames()
	{
		return propertyTypes.keySet().toArray(new String[propertyTypes.size()]);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.DirectoryProxy#getPropertyType(String)
	 */
	public Class<?extends SimpleProperty<?>> getPropertyType(String propertyName)
	{
		return propertyTypes.get(propertyName);
	}

	public void destroy ()
	{
		if (connectionStateMachine.isConnected()) {
			setConnectionState(ConnectionState.DISCONNECTING);
		}
		super.destroy();
		PropertyProxy<?,SimulatorPlug> propProxies[] =  propertyProxies.values().toArray(new PropertyProxy[propertyProxies.values().size()]);
		propertyProxies.clear();
		DirectoryProxy dirProxies[] = directoryProxies.values().toArray(new DirectoryProxy[directoryProxies.values().size()]);
		directoryProxies.clear();
		propertyTypes.clear();
		commands.clear();
		for (PropertyProxy<?,SimulatorPlug> propProxy:propProxies) {
			if (propProxy!=null)
			{
				propProxy.destroy();
				plug.releaseProxy(propProxy);
			}
		}
		
		for (DirectoryProxy dirProxy:dirProxies) {
			if (dirProxy!=null)
			{
				dirProxy.destroy();
				plug.releaseProxy(dirProxy);
			}
		}
		if (connectionStateMachine.getConnectionState()==ConnectionState.DISCONNECTING) {
			setConnectionState(ConnectionState.DISCONNECTED);
		}
		setConnectionState(ConnectionState.DESTROYED);
	}

	public void refresh()
	{
		// Override in order to clean up cached values.
	}
} /* __oOo__ */


/* __oOo__ */
