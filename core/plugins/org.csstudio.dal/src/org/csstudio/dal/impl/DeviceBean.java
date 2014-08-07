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

package org.csstudio.dal.impl;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.csstudio.dal.DynamicValueProperty;
import org.csstudio.dal.RemoteException;
import org.csstudio.dal.SimpleProperty;
import org.csstudio.dal.context.AbstractApplicationContext;
import org.csstudio.dal.context.Connectable;
import org.csstudio.dal.context.ConnectionException;
import org.csstudio.dal.context.ConnectionListener;
import org.csstudio.dal.context.ConnectionState;
import org.csstudio.dal.context.DeviceFamily;
import org.csstudio.dal.context.LinkBlocker;
import org.csstudio.dal.context.PropertyContext;
import org.csstudio.dal.proxy.AbstractPlug;
import org.csstudio.dal.proxy.DirectoryProxy;
import org.csstudio.dal.proxy.PropertyProxy;
import org.csstudio.dal.proxy.Proxy;
import org.csstudio.dal.simple.RemoteInfo;
import org.csstudio.dal.spi.DefaultDeviceFactoryService;
import org.csstudio.dal.spi.DeviceFactory;
import org.csstudio.dal.spi.Plugs;

import com.cosylab.util.CommonException;


/**
 * <code>DeviceBean</code> is implementation of <code>AbstractDevice</code>
 * whih implements <code>ContextBean</code> and <code>Connectable</code>
 * interface. This means that in contrast to  AbstractDevice this device is not
 * only created by factory, but can be also created as JavaBean: with
 * contructor without parameters and initialized later.
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 */
public class DeviceBean extends AbstractDeviceImpl implements Connectable
{
	protected AbstractApplicationContext ctx;
	protected RemoteInfo rinfo;
	protected DeviceFactory deviceFactory;
	private boolean autoConnect = true;
	
	/**
	     * Creates new named instance of device bean.
	     */
	public DeviceBean()
	{
		super(null,null);
	}

	/**
	     * Creates new named instance of device bean.
	     * @param name device name
	     */
	public DeviceBean(String name, DeviceFamily deviceFamily)
	{
		super(name,deviceFamily);
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.context.Connectable#addConnectionListener(org.csstudio.dal.context.ConnectionListener)
	 */
	public void addConnectionListener(ConnectionListener l)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented yet.");
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.context.Connectable#asyncConnect()
	 */
	public synchronized void asyncConnect()
		throws ConnectionException, IllegalStateException
	{
		checkInitialized();

		if (!connectionStateMachine.isTransitionAllowed(ConnectionState.CONNECTING)) {
			throw new IllegalStateException("Connectable '" + getUniqueName()
			    + "' is not in DISCONNECTED or READY state but in "
			    + getConnectionState() + ".");
		}

		setConnectionState(ConnectionState.CONNECTING);
		deviceFactory.reconnectDevice(this);
		
		reinitializePropertyProxies();
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.context.Connectable#connect()
	 */
	public void connect() throws ConnectionException, IllegalStateException
	{
		asyncConnect();
		LinkBlocker.blockUntillConnected(this,
		    Plugs.getConnectionTimeout(ctx.getConfiguration(),30000), true);
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.context.Connectable#destroy()
	 */
	public void destroy()
	{
		if (getConnectionState() == ConnectionState.DESTROYED) return; // TODO throw an exception?
		
		if (deviceFactory != null && deviceFactory.getDeviceFamily() != null)
			deviceFactory.getDeviceFamily().destroy(this);
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.context.Connectable#disconnect()
	 */
	public void disconnect()
	{
		if (!connectionStateMachine.isTransitionAllowed(ConnectionState.DISCONNECTING)) {
				throw new IllegalStateException("Connectable '" + getUniqueName()
				    + "' is not in CONNECTED or CONNECTING state but in "
				    + getConnectionState() + ".");
			}
		
		setConnectionState(ConnectionState.DISCONNECTING);
		
		Proxy[] proxy= releaseProxy(false);

		if (proxy != null && proxy[0]!=null) {
			((AbstractPlug)deviceFactory.getPlug()).releaseProxy(proxy[0]);
		}
		if (proxy != null && proxy[1]!=null && proxy[1]!=proxy[0]) {
			((AbstractPlug)deviceFactory.getPlug()).releaseProxy(proxy[1]);
		}

		setConnectionState(ConnectionState.DISCONNECTED);
	}
	
	//TODO override refresh method to also call refresh on all properties? like Abean.refresh?

	/* (non-Javadoc)
	 * @see org.csstudio.dal.context.Connectable#getRemoteInfo()
	 */
	public org.csstudio.dal.simple.RemoteInfo getRemoteInfo()
	{
		return rinfo;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.context.Connectable#removeConnectionListener(org.csstudio.dal.context.ConnectionListener)
	 */
	public void removeConnectionListener(ConnectionListener l)
	{
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.context.Connectable#setRemoteInfo(org.csstudio.dal.context.RemoteInfo)
	 */
	public void setRemoteInfo(org.csstudio.dal.simple.RemoteInfo rinfo) throws IllegalArgumentException
	{
		if (getConnectionState() != ConnectionState.DISCONNECTED
		    && getConnectionState() != ConnectionState.READY
		    && getConnectionState() != ConnectionState.INITIAL) {
			throw new IllegalStateException("Connectable '" + getUniqueName()
			    + "' is not in DISCONNECTED, INITIAL or READY state but in "
			    + getConnectionState() + ".");
		}

		if (rinfo == null) {
			this.rinfo = null;
			this.name = null;
			setConnectionState(ConnectionState.INITIAL);

			return;
		}

		this.rinfo = rinfo;
		this.name = rinfo.getRemoteName();
		
		if (ctx!=null && deviceFactory!=null && rinfo!=null) {
			setConnectionState(ConnectionState.READY);
		}
		
		tryConnect();
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.context.ContextBean#getApplicationContext()
	 */
	public AbstractApplicationContext getApplicationContext()
	{
		return ctx;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.context.ContextBean#initialize(org.csstudio.dal.context.AbstractApplicationContext)
	 */
	public void initialize(AbstractApplicationContext ctx)
		throws CommonException
	{
		// TODO: at some point this initialization must be changed. 
		// device factory should be obtained only after RemoteInfo is set and correct plug type is known, 
		// now it is used default plug type from ctx
		deviceFactory = DefaultDeviceFactoryService.getDeviceFactoryService()
			.getDeviceFactory(ctx, null);

		if (deviceFactory == null) {
			throw new CommonException(this, "Failed to create device factory");
		}

		this.ctx = ctx;
		
		if (ctx!=null && deviceFactory!=null && rinfo!=null) {
			setConnectionState(ConnectionState.READY);
		}
		tryConnect();
	}
	
	/**
	 * Performs initialization the other way around.
	 */
	public void initialize(DeviceFactory devFact){
		this.deviceFactory = devFact;
		this.ctx = devFact.getApplicationContext();

		if (ctx!=null && deviceFactory!=null && rinfo!=null) {
			setConnectionState(ConnectionState.READY);
		}
		
		tryConnect();
	}

	/**
	 * Checks if device is properly initialized.
	 *
	 * @throws IllegalStateException if it is not initialized
	 *
	 * @see #initialize(AbstractApplicationContext)
	 */
	protected void checkInitialized() throws IllegalStateException
	{
		if (deviceFactory == null) {
			throw new IllegalStateException("Device '" + getUniqueName()
			    + "' is not initialized.");
		}
	}
	
	/**
	 * Reinitializes all already existing properties. Usually only happens when device is initialized
	 * second time. 
	 */
	protected void reinitializePropertyProxies(){
		if (properties != null){
			String[] pns = properties.keySet().toArray(new String[0]);
			for (int i=0; i<pns.length; i++){
				try {
					PropertyProxy pp = deviceProxy.getPropertyProxy(pns[i]);
					DirectoryProxy dp = deviceProxy.getDirectoryProxy(pns[i]);
		
					DynamicValuePropertyImpl<?> prop = ((DynamicValuePropertyImpl<?>)getProperty(pns[i]));
					prop.initialize(pp, dp);
					prop.refresh();
				} catch (Exception e) {
					Logger.getLogger(DeviceBean.class).warn("Problem on re-initializing property " + pns[i]+".",e);
				}
			}
		}
	}
	
	@Override
	protected DynamicValueProperty<?> createProperty(String name)
	throws RemoteException, IllegalAccessException, InstantiationException,
		InvocationTargetException, NoSuchMethodException
	{
		Class<? extends SimpleProperty<?>> type = getPropertyType(name);
	
		// Creates property implementation
		Class<?> impClass = PropertyUtilities.getImplementationClass(type);
		DynamicValuePropertyImpl<?> property = (DynamicValuePropertyImpl<?>)impClass.getConstructor(String.class,
			    PropertyContext.class).newInstance(name, this);
	
		if (deviceProxy != null){
			PropertyProxy pp = deviceProxy.getPropertyProxy(name);
			DirectoryProxy dp = deviceProxy.getDirectoryProxy(name);
	
			property.initialize(pp, dp);
		}
		property.addPropertyChangeListener(propertyInterceptor);
		
//		if (deviceProxy != null){
//			if (property.getConnectionState() != ConnectionState.CONNECTED) {
//				Logger.getLogger(DeviceBean.class.getName()).info("Property '" + name +"' is not connected. Waiting for connection to be established...");
//			}
//			LinkBlocker.blockUntillConnected(property,Plugs.getConnectionTimeout(null, 30000) * 2, true);
//		}
	
		return property;
	}
	
	protected Class<? extends SimpleProperty<?>> getPropertyType(String name){
		return (Class<? extends SimpleProperty<?>>)DynamicValueProperty.class;
	}
	
	/**
	 * Sets connection state and fires event.
	 *
	 * @param s new state
	 */
	protected void setConnectionState(ConnectionState s)
	{
		super.setConnectionState(s);

		// TODO: should fire event here to connection listeners
	}
	
	/**
	 * @see {@link #setAutoConnect(boolean)} 
	 */
	public boolean isAutoConnect() {
		return autoConnect;
	}

	/**
	 * If autoConnect is true, Device is automatically connected when all requirements
	 * are provided (RemoteInfo, DeviceFactory). Default value is <code>true</code>.
	 */
	public void setAutoConnect(boolean autoConnect) {
		this.autoConnect = autoConnect;
		tryConnect();
	}
	
	protected void tryConnect(){
		if (autoConnect && (getConnectionState() == ConnectionState.READY  || getConnectionState() == ConnectionState.DISCONNECTED ) && deviceFactory != null)
			try {
				asyncConnect();
			} catch (Exception e) {
				deviceFactory.getPlug().getLogger().info("'"+getUniqueName()+"' failed to autoconnect.",e);
			}
	}

}

/* __oOo__ */
