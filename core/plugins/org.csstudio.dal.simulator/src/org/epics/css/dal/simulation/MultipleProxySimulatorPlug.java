package org.epics.css.dal.simulation;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.csstudio.dal.DataExchangeException;
import org.csstudio.dal.DoubleProperty;
import org.csstudio.dal.RemoteException;
import org.csstudio.dal.Request;
import org.csstudio.dal.ResponseListener;
import org.csstudio.dal.SimpleProperty;
import org.csstudio.dal.context.ConnectionException;
import org.csstudio.dal.context.ConnectionState;
import org.csstudio.dal.device.PowerSupply;
import org.csstudio.dal.impl.AbstractDeviceImpl;
import org.csstudio.dal.impl.DoublePropertyImpl;
import org.csstudio.dal.impl.RequestImpl;
import org.csstudio.dal.impl.ResponseImpl;
import org.csstudio.dal.proxy.AbstractPropertyProxyImpl;
import org.csstudio.dal.proxy.AbstractProxyImpl;
import org.csstudio.dal.proxy.CommandProxy;
import org.csstudio.dal.proxy.DeviceProxy;
import org.csstudio.dal.proxy.DirectoryProxy;
import org.csstudio.dal.proxy.MonitorProxy;
import org.csstudio.dal.proxy.PropertyProxy;
import org.csstudio.dal.proxy.SyncPropertyProxy;
import org.epics.css.dal.simulation.ps.PowerSupplyImpl;

/**
 * An instance of simulator plug which provides separate Directory and Property Proxies.
 * <p><code>For testing purposes only</code></p>
 * 
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class MultipleProxySimulatorPlug extends SimulatorPlug {

	public static class MonitorProxyI<T> extends RequestImpl<T> implements MonitorProxy,
		Runnable{


		protected SinglePropertyProxyImpl<T> proxy;
		protected long timerTrigger = 1000;
		protected boolean heartbeat = true;
		protected TimerTask task;
		protected boolean destroyed = false;

		
		public MonitorProxyI(SinglePropertyProxyImpl<T> source, ResponseListener<T> l) {
			super(source, l);
			this.proxy = source;
			proxy.addMonitor(this);
			resetTimer();
		}

		public void reInitialize(SinglePropertyProxyImpl<T> proxy) throws RemoteException
		{
			this.source = proxy;
			this.proxy = proxy;
		}

		/* (non-Javadoc)
		 * @see org.epics.css.dal.proxy.MonitorProxy#getRequest()
		 */
		public Request<T> getRequest()
		{
			return this;
		}

		/* (non-Javadoc)
		 * @see org.epics.css.dal.SimpleMonitor#getTimerTrigger()
		 */
		public long getTimerTrigger() throws DataExchangeException
		{
			return timerTrigger;
		}

		/* (non-Javadoc)
		 * @see org.epics.css.dal.SimpleMonitor#setTimerTrigger(long)
		 */
		public void setTimerTrigger(long trigger)
			throws DataExchangeException, UnsupportedOperationException
		{
			timerTrigger = trigger;
			resetTimer();
		}

		/* (non-Javadoc)
		 * @see org.epics.css.dal.SimpleMonitor#setHeartbeat(boolean)
		 */
		public void setHeartbeat(boolean heartbeat)
			throws DataExchangeException, UnsupportedOperationException
		{
			this.heartbeat = heartbeat;
			resetTimer();
		}

		/* (non-Javadoc)
		 * @see org.epics.css.dal.SimpleMonitor#isHeartbeat()
		 */
		public boolean isHeartbeat()
		{
			return heartbeat;
		}

		/* (non-Javadoc)
		 * @see org.epics.css.dal.SimpleMonitor#getDefaultTimerTrigger()
		 */
		public long getDefaultTimerTrigger() throws DataExchangeException
		{
			return 1000;
		}

		/* (non-Javadoc)
		 * @see org.epics.css.dal.SimpleMonitor#isDefault()
		 */
		public boolean isDefault()
		{
			return true;
		}

		private void fireValueEvent()
		{
			try {
				ResponseImpl<T> r = new ResponseImpl<T>(proxy, this,
					    proxy.getValueSync(), "value", true, null,
					    proxy.getCondition(), null, false);
				addResponse(r);
			} catch (DataExchangeException e) {
				Logger.getLogger(this.getClass()).warn("Simulator error.", e);
			}
		}

		/**
		 * Fires value change event if monitor is not in heartbeat mode.
		 */
		public void fireValueChange()
		{
			if (!heartbeat) {
				fireValueEvent();
			}
		}

		/**
		 * Run method executed at schedulet time intervals.
		 */
		public void run()
		{
			fireValueEvent();
		}

		private synchronized void resetTimer()
		{
			if (task != null) {
				task.cancel();
			}

			if (heartbeat) {
				task = MultipleProxySimulatorPlug.getInstance().schedule(this, timerTrigger);
			}
		}

		/* (non-Javadoc)
		 * @see org.epics.css.dal.SimpleMonitor#destroy()
		 */
		public void destroy()
		{
			if (task != null) {
				task.cancel();
			}

			destroyed = true;
			
			proxy.removeMonitor(this);
		}

		/* (non-Javadoc)
		 * @see org.epics.css.dal.SimpleMonitor#isDestroyed()
		 */
		public boolean isDestroyed()
		{
			return destroyed;
		}

		public void refresh()
		{
			// Override in order to clean up cached values.
		}
		
	}
	
	public static class DoublePropertyProxyImpl extends SinglePropertyProxyImpl<Double> {

		public DoublePropertyProxyImpl(String name, MultipleProxySimulatorPlug plug) {
			super(name,plug);
			// TODO Auto-generated constructor stub
		}
		
	}
	
	public static class SinglePropertyProxyImpl<E> extends AbstractPropertyProxyImpl<E, MultipleProxySimulatorPlug, MonitorProxyI<E>> implements 
		PropertyProxy<E,MultipleProxySimulatorPlug>,SyncPropertyProxy<E,MultipleProxySimulatorPlug> {

		protected ValueProvider<E> valueProvider = new MemoryValueProvider<E>();
		protected boolean isSettable = true;
		
		public SinglePropertyProxyImpl(String name, MultipleProxySimulatorPlug plug) {
			super(name,plug);
			setConnectionState(ConnectionState.READY);
			delayedConnect(1000);
		}
		
		public void refresh() {
			// TODO Auto-generated method stub
		}
		public Request<? extends Object> getCharacteristics(
				String[] characteristics,
				ResponseListener<? extends Object> callback)
				throws DataExchangeException {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		protected Object processCharacteristicAfterCache(Object value,
				String characteristicName) {
			return value;
		}
		
		public String[] getCharacteristicNames() throws DataExchangeException {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		protected Object processCharacteristicBeforeCache(Object value,
				String characteristicName) {
			return value;
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

		public MonitorProxy createMonitor(ResponseListener<E> callback, Map<String,Object> p) throws RemoteException {
			MonitorProxyI<E> m = new MonitorProxyI<E>(this, callback);
			return m;
		}

		public Request<E> getValueAsync(ResponseListener<E> callback) throws DataExchangeException {
			if (getConnectionState() != ConnectionState.CONNECTED) {
				throw new DataExchangeException(this, "Proxy not connected");
			}

			RequestImpl<E> r = new RequestImpl<E>(this, callback);
			r.addResponse(new ResponseImpl<E>(this, r, valueProvider.get(), "value",
			        true, null, getCondition(), null, true));

			return r;
		}

		public boolean isSettable() {
			return isSettable;
		}

		public Request<E> setValueAsync(E value, ResponseListener<E> callback) throws DataExchangeException {
			if (getConnectionState() != ConnectionState.CONNECTED) {
				throw new DataExchangeException(this, "Proxy not connected");
			}

			setValueSync(value);

			RequestImpl<E> r = new RequestImpl<E>(this, callback);
			r.addResponse(new ResponseImpl<E>(this, r, value, "", true, null,
			        getCondition(), null, true));

			return r;
		}

		public E getValueSync() throws DataExchangeException {
			if (getConnectionState() != ConnectionState.CONNECTED) {
				throw new DataExchangeException(this, "Proxy not connected");
			}

			return (E)valueProvider.get();
		}

		public void setValueSync(E value) throws DataExchangeException {
			if (getConnectionState() != ConnectionState.CONNECTED) {
				throw new DataExchangeException(this, "Proxy not connected");
			}

			valueProvider.set(value);

			MonitorProxyImpl<?>[] m = getMonitors().toArray(new MonitorProxyImpl[getMonitors().size()]);

			for (int i = 0; i < m.length; i++) {
				m[i].fireValueChange();
			}
		}
	}
	
	public static class SingleDeviceProxyImpl extends AbstractProxyImpl<MultipleProxySimulatorPlug> implements 
			DeviceProxy<MultipleProxySimulatorPlug> {

		protected MultipleProxySimulatorPlug plug;
		protected Map<String, DirectoryProxy<MultipleProxySimulatorPlug>> directoryProxies;
		protected Map<String, PropertyProxy<?,MultipleProxySimulatorPlug>> propertyProxies;
		protected Map<String, CommandProxy> commands = new HashMap<String, CommandProxy>();
		protected Map<String, Class<?extends SimpleProperty<?>>> propertyTypes = new HashMap<String, Class<? extends SimpleProperty<?>>>();
		
		public SingleDeviceProxyImpl(String name, MultipleProxySimulatorPlug plug) {
			super(name,plug);
			this.plug = MultipleProxySimulatorPlug.getInstance();
			setConnectionState(ConnectionState.READY);
			delayedConnect(2000);
			
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

		public CommandProxy getCommand(String name) throws RemoteException {
			return commands.get(name);
		}

		@SuppressWarnings("unchecked")
		public DirectoryProxy<MultipleProxySimulatorPlug> getDirectoryProxy(String name) throws RemoteException {
			if (directoryProxies == null) {
				directoryProxies = new HashMap<String, DirectoryProxy<MultipleProxySimulatorPlug>>(3);
			}

			DirectoryProxy<MultipleProxySimulatorPlug> p = directoryProxies.get(name);

			if (p != null) {
				return p;
			}

			p = (DirectoryProxy<MultipleProxySimulatorPlug>) plug.getDirectoryProxy(this.name + '/' + name);
			directoryProxies.put(name, p);

			return p;
		}

		@SuppressWarnings("unchecked")
		public PropertyProxy<?,MultipleProxySimulatorPlug> getPropertyProxy(String name) throws RemoteException {
			if (propertyProxies == null) {
				propertyProxies = new HashMap<String, PropertyProxy<?,MultipleProxySimulatorPlug>>(3);
			}

			PropertyProxy<?,MultipleProxySimulatorPlug> p = propertyProxies.get(name);

			if (p != null) {
				return p;
			}

			p = (PropertyProxy<?, MultipleProxySimulatorPlug>) plug.getPropertyProxy(this.name + '/' + name,
				   plug.getPropertyProxyImplementationClass(getPropertyType(name),null,name));
			//			    SimulatorUtilities.getPropertyProxyImplementationClass(
			//			        getPropertyType(name)));
			propertyProxies.put(name, p);

			return p;
		}

		public void refresh() {
			// TODO Auto-generated method stub
			
		}		
		
		public Class<?extends SimpleProperty<?>> getPropertyType(String propertyName)
		{
			return propertyTypes.get(propertyName);
		}
	}
	
	private static MultipleProxySimulatorPlug instance;

	public static final MultipleProxySimulatorPlug getInstance()
	{
		return getInstance((Properties)null);
	}

	public static final synchronized MultipleProxySimulatorPlug getInstance(Properties conf)
	{
		if (instance == null) {
			instance = new MultipleProxySimulatorPlug(conf);
		}

		return instance;
	}
	
	protected MultipleProxySimulatorPlug(Properties configuration) {
		super(configuration);
		registerPropertyProxyImplementationClass(DoublePropertyImpl.class, DoublePropertyProxyImpl.class);
		registerPropertyProxyImplementationClass(DoubleProperty.class, DoublePropertyProxyImpl.class);
		registerDeviceImplementationClass(PowerSupply.class, PowerSupplyImpl.class);
		registerDeviceProxyImplementationClass(PowerSupply.class, SingleDeviceProxyImpl.class);
		registerDeviceProxyImplementationClass(PowerSupplyImpl.class, SingleDeviceProxyImpl.class);
		registerDeviceProxyImplementationClass(AbstractDeviceImpl.class, SingleDeviceProxyImpl.class);
	}
	
	@Override
	protected DirectoryProxy<MultipleProxySimulatorPlug> createNewDirectoryProxy(String uniqueName) {
		try {
			PropertyProxyImpl<?> proxy = new PropertyProxyImpl<Object>(uniqueName,this,Object.class);
			return (DirectoryProxy)proxy;
			
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).warn("Simulator error.", e);
		}
		return null;
	}
	
	@Override
	protected <TT extends PropertyProxy<?,?>> TT createNewPropertyProxy(String uniqueName, Class<TT> type) throws ConnectionException {
		try {
			if (type == DoublePropertyProxyImpl.class) {
				return type.cast(new DoublePropertyProxyImpl(uniqueName,this));
			}
			SinglePropertyProxyImpl p = new SinglePropertyProxyImpl(uniqueName,this);
			return type.cast(p);
		} catch (Exception e) {
			throw new ConnectionException(this,
			    "Failed to instantiate simulation proxy '" + uniqueName
			    + "' for type '" + type.getName() + "'.", e);
		}
	}
	
	@Override
	protected <T extends DeviceProxy<?>> T createNewDeviceProxy(String uniqueName, Class<T> type) throws ConnectionException {
		try {
			SingleDeviceProxyImpl p = new SingleDeviceProxyImpl(uniqueName,this);
			return type.cast(p);
		} catch (Exception e) {
			throw new ConnectionException(this,
			    "Failed to instantiate simulation proxy '" + uniqueName
			    + "' for type '" + type.getName() + "'.", e);
		}
	}
}
