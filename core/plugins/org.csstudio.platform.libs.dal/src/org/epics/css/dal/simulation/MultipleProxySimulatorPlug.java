package org.epics.css.dal.simulation;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.epics.css.dal.DataExchangeException;
import org.epics.css.dal.DoubleProperty;
import org.epics.css.dal.DynamicValueCondition;
import org.epics.css.dal.DynamicValueState;
import org.epics.css.dal.RemoteException;
import org.epics.css.dal.Request;
import org.epics.css.dal.ResponseListener;
import org.epics.css.dal.SimpleProperty;
import org.epics.css.dal.context.ConnectionException;
import org.epics.css.dal.context.ConnectionState;
import org.epics.css.dal.device.PowerSupply;
import org.epics.css.dal.impl.AbstractDeviceImpl;
import org.epics.css.dal.impl.DoublePropertyImpl;
import org.epics.css.dal.impl.RequestImpl;
import org.epics.css.dal.impl.ResponseImpl;
import org.epics.css.dal.proxy.AbstractProxyImpl;
import org.epics.css.dal.proxy.CommandProxy;
import org.epics.css.dal.proxy.DeviceProxy;
import org.epics.css.dal.proxy.DirectoryProxy;
import org.epics.css.dal.proxy.MonitorProxy;
import org.epics.css.dal.proxy.PropertyProxy;
import org.epics.css.dal.proxy.SyncPropertyProxy;
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
				e.printStackTrace();
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

		public DoublePropertyProxyImpl(String name) {
			super(name);
			// TODO Auto-generated constructor stub
		}
		
	}
	
	public static class SinglePropertyProxyImpl<E> extends AbstractProxyImpl implements 
		PropertyProxy<E>,SyncPropertyProxy<E> {

		protected ValueProvider<E> valueProvider = new MemoryValueProvider<E>();
		protected DynamicValueCondition condition = new DynamicValueCondition(EnumSet
			    .of(DynamicValueState.NORMAL), System.currentTimeMillis(), null);
		protected List<MonitorProxyI> monitors = new ArrayList<MonitorProxyI>(1);
		protected boolean isSettable = true;
		
		public SinglePropertyProxyImpl(String name) {
			super(name);
			delayedConnect(1000);
		}
		
		public void delayedConnect(long timeout)
		{
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

		public MonitorProxy createMonitor(ResponseListener<E> callback) throws RemoteException {
			MonitorProxyI<E> m = new MonitorProxyI<E>(this, callback);
			monitors.add(m);

			return m;
		}

		public DynamicValueCondition getCondition() {
			return condition;
		}

		public Request<E> getValueAsync(ResponseListener<E> callback) throws DataExchangeException {
			if (getConnectionState() != ConnectionState.CONNECTED) {
				throw new DataExchangeException(this, "Proxy not connected");
			}

			RequestImpl<E> r = new RequestImpl<E>(this, callback);
			r.addResponse(new ResponseImpl<E>(this, r, valueProvider.get(), "value",
			        true, null, condition, null, true));

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
			        condition, null, true));

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

			MonitorProxyImpl[] m = monitors.toArray(new MonitorProxyImpl[monitors
				    .size()]);

			for (int i = 0; i < m.length; i++) {
				m[i].fireValueChange();
			}
		}
	}
	
	public static class SingleDeviceProxyImpl extends AbstractProxyImpl implements 
			DeviceProxy {

		protected MultipleProxySimulatorPlug plug;
		protected Map<String, DirectoryProxy> directoryProxies;
		protected Map<String, PropertyProxy<?>> propertyProxies;
		protected Map<String, CommandProxy> commands = new HashMap<String, CommandProxy>();
		protected Map<String, Class<?extends SimpleProperty<?>>> propertyTypes = new HashMap<String, Class<? extends SimpleProperty<?>>>();
		
		public SingleDeviceProxyImpl(String name) {
			super(name);
			this.plug = MultipleProxySimulatorPlug.getInstance();
			delayedConnect(2000);
			
		}
				
		public void delayedConnect(long timeout)
		{
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

		public DirectoryProxy getDirectoryProxy(String name) throws RemoteException {
			if (directoryProxies == null) {
				directoryProxies = new HashMap<String, DirectoryProxy>(3);
			}

			DirectoryProxy p = directoryProxies.get(name);

			if (p != null) {
				return p;
			}

			p = plug.getDirectoryProxy(this.name + '/' + name);
			directoryProxies.put(name, p);

			return p;
		}

		public PropertyProxy<?> getPropertyProxy(String name) throws RemoteException {
			if (propertyProxies == null) {
				propertyProxies = new HashMap<String, PropertyProxy<?>>(3);
			}

			PropertyProxy<?> p = propertyProxies.get(name);

			if (p != null) {
				return p;
			}

			p = plug.getPropertyProxy(this.name + '/' + name,
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
	protected DirectoryProxy createNewDirectoryProxy(String uniqueName) {
		try {
			PropertyProxyImpl<?> proxy = new PropertyProxyImpl<Object>(uniqueName);
			return proxy;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected <TT extends PropertyProxy<?>> TT createNewPropertyProxy(String uniqueName, Class<TT> type) throws ConnectionException {
		try {
			if (type == DoublePropertyProxyImpl.class) {
				return type.cast(new DoublePropertyProxyImpl(uniqueName));
			}
			SinglePropertyProxyImpl p = new SinglePropertyProxyImpl(uniqueName);
			return type.cast(p);
		} catch (Exception e) {
			throw new ConnectionException(this,
			    "Failed to instantiate simulation proxy '" + uniqueName
			    + "' for type '" + type.getName() + "'.", e);
		}
	}
	
	@Override
	protected <T extends DeviceProxy> T createNewDeviceProxy(String uniqueName, Class<T> type) throws ConnectionException {
		try {
			SingleDeviceProxyImpl p = new SingleDeviceProxyImpl(uniqueName);
			return type.cast(p);
		} catch (Exception e) {
			throw new ConnectionException(this,
			    "Failed to instantiate simulation proxy '" + uniqueName
			    + "' for type '" + type.getName() + "'.", e);
		}
	}
}
