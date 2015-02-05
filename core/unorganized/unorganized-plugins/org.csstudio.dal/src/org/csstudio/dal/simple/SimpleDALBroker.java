/**
 *
 */
package org.csstudio.dal.simple;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.csstudio.dal.DynamicValueListener;
import org.csstudio.dal.DynamicValueMonitor;
import org.csstudio.dal.DynamicValueProperty;
import org.csstudio.dal.ExpertMonitor;
import org.csstudio.dal.RemoteException;
import org.csstudio.dal.Request;
import org.csstudio.dal.ResponseListener;
import org.csstudio.dal.Timestamp;
import org.csstudio.dal.context.AbstractApplicationContext;
import org.csstudio.dal.context.ConnectionException;
import org.csstudio.dal.context.LifecycleEvent;
import org.csstudio.dal.context.LifecycleListener;
import org.csstudio.dal.context.LinkBlocker;
import org.csstudio.dal.impl.DefaultApplicationContext;
import org.csstudio.dal.impl.RequestImpl;
import org.csstudio.dal.impl.ResponseImpl;
import org.csstudio.dal.spi.DefaultPropertyFactoryBroker;
import org.csstudio.dal.spi.LinkPolicy;
import org.csstudio.dal.spi.Plugs;
import org.csstudio.dal.spi.PropertyFactoryService;

import com.cosylab.util.CommonException;

/**
 * Simple DAL main object for access to DAL in request style.
 * DAL offers here functionality trough addressed synchronous and
 * asynchronous interface.
 *
 * @author ikriznar
 *
 */
public class SimpleDALBroker {

	private class PropertiesCleanupTask extends TimerTask {

		@Override
		public void run() {
			final ArrayList<String> toRemove = new ArrayList<String>();
			synchronized (properties) {
				PropertyHolder holder;
				for (final String key : properties.keySet()) {
					holder = properties.get(key);
					if (holder.expires > 0 && holder.expires < System.currentTimeMillis() && !holder.property.hasDynamicValueListeners()) {
						toRemove.add(key);
					}
				}
				for (final String key : toRemove) {
					holder = properties.remove(key);
					getFactory().destroy(holder.property);
				}
			}
		}

	}

	/**
	 * @author ikriznar
	 *
	 */
	public class PropertyHolder {
		public PropertyHolder(final DynamicValueProperty<?> p) {
			this.property=p;
		}
		DynamicValueProperty<?> property;
		/**
		 * Tells when broker garbage collector could check if the property is no longer used.
		 * If 0, then it is in active use.
		 * If 1 or more than it can be checked if already expired and is candidate for deletion.
		 */
		public long expires;

	}

	public class UninitializedPropertyHolder extends PropertyHolder {

		boolean propertyInitialized = false;
		RemoteException re;
		InstantiationException ie;

		public UninitializedPropertyHolder() {
			super(null);
			expires = -1;
		}
	}

	private static SimpleDALBroker broker;

	private static String createKey(final ConnectionParameters cparam, final String defaultPlugType) {
		final StringBuilder sb = new StringBuilder();
		if (cparam.getRemoteInfo().getPlugType()==null) {
			sb.append(defaultPlugType);
		} else {
			sb.append(cparam.getRemoteInfo().getPlugType());
		}
		sb.append(',');
		sb.append(cparam.getRemoteInfo().getRemoteName());
		sb.append(',');
		final DataFlavor df = cparam.getConnectionType();
		if (df == null) {
            sb.append("null");
        } else {
            sb.append(df.toString());
        }
		return sb.toString();
	}

	/**
	 * Creates singleton instance of SimpleDALBroker.
	 * This is same as calling {@link SimpleDALBroker#newInstance(AbstractApplicationContext)} with <code>null</code> as parameter.
	 * @return singleton instance of SimpleDALBroker
	 */
	public static SimpleDALBroker getInstance() {
		return newInstance(null, null);
	}

	/**
	 * Creates new instance of SimpleDALBroker or singelton instance of parameter is <code>null</code>.
	 * @param ctx application context or null
	 * @return if ctx is provided new instance, otherwise singelton
	 */
	public static SimpleDALBroker newInstance(final AbstractApplicationContext ctx) {

		final Object o= ctx.getApplicationProperty(Plugs.PROPERTY_FACTORY_SERVICE_IMPLEMENTATION);

		if (o instanceof PropertyFactoryService) {
			return newInstance(ctx, (PropertyFactoryService)o);
		}
		return newInstance(ctx, null);
	}

	/**
	 * Creates new instance of SimpleDALBroker or singelton instance of parameter is <code>null</code>.
	 * @param ctx application context or null
	 * @param service implementation of PropertyFactoryService which will be used to instantiate factories.
	 * @return if ctx is provided new instance, otherwise singelton
	 */
	public static SimpleDALBroker newInstance(final AbstractApplicationContext ctx, final PropertyFactoryService service) {
		if (ctx==null) {
			if (broker == null) {
				broker = new SimpleDALBroker(new DefaultApplicationContext("SimpleDALContext"));
			}
			return broker;
		}
		final SimpleDALBroker sdb= new SimpleDALBroker(ctx);
		if (service!=null) {
			sdb.getFactory().setPropertyFactoryService(service);
		}
		return sdb;
	}

	private final AbstractApplicationContext ctx;
	private final HashMap<String, PropertyHolder> properties;
	/**
	 * Time duration which property is alive after has been last used.
	 * After this time it could be deleted.
	 */
	private final long timeToLive = 60000;
	private DefaultPropertyFactoryBroker factory;

	private static final long CLEANUP_INTERVAL = 60000;
	private final Timer cleanupTimer;

	private SimpleDALBroker(final AbstractApplicationContext ctx) {
		this.ctx=ctx;
		properties= new HashMap<String, PropertyHolder>();
		cleanupTimer = new Timer("Cleanup Timer");
		cleanupTimer.scheduleAtFixedRate(new PropertiesCleanupTask(), CLEANUP_INTERVAL, CLEANUP_INTERVAL);

		ctx.addLifecycleListener(new LifecycleListener() {

			@Override
            public void destroyed(final LifecycleEvent event) {
				// TODO implement?
			}

			@Override
            public void destroying(final LifecycleEvent event) {
				// TODO implement?
				cleanupTimer.cancel();
			}

			@Override
            public void initialized(final LifecycleEvent event) {
				// not important
			}

			@Override
            public void initializing(final LifecycleEvent event) {
				// not important
			}
		});
	}

	private PropertyHolder getPropertyHolder(final ConnectionParameters cparam) throws InstantiationException, CommonException {
		return getPropertyHolder(cparam, System.currentTimeMillis()+timeToLive);
	}

	private PropertyHolder getPropertyHolder(final ConnectionParameters cparam, final long expires) throws InstantiationException, CommonException {

		PropertyHolder ph;
		UninitializedPropertyHolder uph = null;
		final String key = createKey(cparam,getFactory().getDefaultPlugType());

		synchronized (properties) {
			ph= properties.get(key);
			if (ph == null) {
				uph = new UninitializedPropertyHolder();
				properties.put(key, uph);
			}
		}

		if (ph==null) {
			DynamicValueProperty<?> property;
			try {
				if (cparam.getConnectionType() != null) {
					property = getFactory().
					getProperty(
							cparam.getRemoteInfo(),
							cparam.getConnectionType().getDALType(),
							null);
				}
				else {
					property = getFactory().
					getProperty(cparam.getRemoteInfo());
				}
			} catch (final RemoteException e) {
				uph.re = e;
				synchronized (properties) {
					properties.remove(key);
				}
				synchronized (uph) {
					uph.propertyInitialized = true;
					uph.notifyAll();
				}
				throw e;
			} catch (final InstantiationException e) {
				uph.ie = e;
				synchronized (properties) {
					properties.remove(key);
				}
				synchronized (uph) {
					uph.propertyInitialized = true;
					uph.notifyAll();
				}
				throw e;
			}
			ph= new PropertyHolder(property);
			synchronized (properties) {
				properties.put(key, ph);
			}
			synchronized (uph) {
				uph.propertyInitialized = true;
				uph.notifyAll();
			}
		}
		else if (ph instanceof UninitializedPropertyHolder) {
			uph = (UninitializedPropertyHolder) ph;
			synchronized (uph) {
				try {
					while (!uph.propertyInitialized) {
						uph.wait();
					}
				} catch (final InterruptedException e) {
					throw new CommonException(this, "Thread has been interrupted.", e);
				}
			}
			synchronized (properties) {
				ph= properties.get(key);
				if (ph == null) {
					if (uph.re != null) {
                        throw uph.re;
                    }
					if (uph.ie != null) {
                        throw uph.ie;
                    }
					throw new CommonException(this, "Internal error.");
				}
				if (ph instanceof UninitializedPropertyHolder) {
                    throw new CommonException(this, "Internal error.");
                }
			}
		}

		ph.expires = expires;
		return ph;
	}

	/**
	 * Utility method for JUnit testing.
	 * @return the size of properties map
	 */
	public int getPropertiesMapSize() {
		int size = -1;
		synchronized (properties) {
			size = properties.size();
		}
		return size;
	}

	private DefaultPropertyFactoryBroker getFactory() {
		if (factory == null) {
			factory = DefaultPropertyFactoryBroker.getInstance(ctx, LinkPolicy.ASYNC_LINK_POLICY);
		}
		return factory;
	}

	/**
	 * Returns remote value.
	 * Value is read and returned synchronously.
	 * The remote connection to remote object is closed not sooner
	 * then one minute and no later than two minutes after
	 * last time the connection was used.
	 *
	 * @param cparam connection parameter to remote value
	 * @return remote value
	 * @throws InstantiationException
	 * @throws CommonException
	 */
	public Object getValue(final ConnectionParameters cparam) throws InstantiationException, CommonException {
		final PropertyHolder ph= getPropertyHolder(cparam);
		blockUntillConnected(ph.property);
		if (cparam.getRemoteInfo().getCharacteristic()!=null) {
			return ph.property.getCharacteristic(cparam.getRemoteInfo().getCharacteristic());
		}
		return ph.property.getValue();
	}

	/**
	 * Returns remote value with synchronous call.
	 * The remote connection to remote object is closed not sooner
	 * then one minute and no later than two minutes after
	 * last time the connection was used.
	 *
	 * @param rinfo connection information to remote value
	 * @param type Java data type for expected remote value
	 * @return returned value already cast to requested data type, can be <code>null</code>
	 * @throws InstantiationException if error
	 * @throws CommonException if error
	 */
	public <T> T getValue(final RemoteInfo rinfo, final Class<T> type) throws InstantiationException, CommonException {
		final PropertyHolder ph= getPropertyHolder(new ConnectionParameters(rinfo, type));
		blockUntillConnected(ph.property);
		if (rinfo.getCharacteristic()!=null) {
			final Object o= ph.property.getCharacteristic(rinfo.getCharacteristic());
			if (o!=null) {
				return type.cast(o);
			}
			return null;
		}

		Object o=null;
		if (type == AnyData.class) {
			o= ph.property.getData();
		} else {
			o= ph.property.getValue();
		}
		if (o!=null) {
			return type.cast(o);
		}
		return null;
	}

	/**
	 * Return remote value with synchronous call.
	 * The remote connection to remote object is closed not sooner
	 * then one minute and no later than two minutes after
	 * last time the connection was used.
	 *
	 * @param rinfo connection information to remote value
	 * @return returned value, can be <code>null</code>
	 * @throws InstantiationException if error
	 * @throws CommonException if error
	 */
	public Object getValue(final RemoteInfo rinfo) throws InstantiationException, CommonException {
		final PropertyHolder ph= getPropertyHolder(new ConnectionParameters(rinfo));
		blockUntillConnected(ph.property);
		if (rinfo.getCharacteristic()!=null) {
			return ph.property.getCharacteristic(rinfo.getCharacteristic());
		}
		return ph.property.getValue();
	}

	/**
	 * Return remote value with synchronous call.
	 * The remote connection to remote object is closed not sooner
	 * then one minute and no later than two minutes after
	 * last time the connection was used.
	 *
	 * @param property name of remote property
	 * @return returned value, can be <code>null</code>
	 * @throws InstantiationException if error
	 * @throws CommonException if error
	 */
	public Object getValue(final String property) throws InstantiationException, CommonException {
		return getValue(RemoteInfo.fromString(property, RemoteInfo.DAL_TYPE_PREFIX + getFactory().getDefaultPlugType()));
	}

	/**
	 * Asynchronously requests remote value.
	 * The remote connection to remote object is closed not sooner
	 * then one minute and no later than two minutes after
	 * last time the connection was used.
	 *
	 * @param cparam complete connection parameters to remote value
	 * @param callback callback which will be notified when remote value is returned
	 * @return request object, which identifies and controls response returned to callback.
	 * @throws InstantiationException if error
	 * @throws CommonException if error
	 */
	public <T> Request<T> getValueAsync(final ConnectionParameters cparam, final ResponseListener<T> callback) throws InstantiationException, CommonException {
		final PropertyHolder ph= getPropertyHolder(cparam);
		blockUntillConnected(ph.property);
		if (cparam.getRemoteInfo().getCharacteristic()!=null) {
			return (Request<T>)ph.property.getCharacteristicAsynchronously(
					cparam.getRemoteInfo().getCharacteristic(),
					callback);
		}

		if (cparam.getDataType().getJavaType() == AnyData.class) {
			final RequestImpl<T> r= new RequestImpl<T>(ph.property, callback);
			r.addResponse(new ResponseImpl<T>(ph.property, r, (T)ph.property.getData(), "", true, null, null, new Timestamp(), true));
			return r;
		}
		return ((DynamicValueProperty<T>)ph.property).getAsynchronous(callback);
	}

	/**
	 * Sends new value to remote object.
	 * The remote connection to remote object is closed not sooner
	 * then one minute and no later than two minutes after
	 * last time the connection was used.
	 *
	 * @param rinfo connection information about remote entity
	 * @param value new value to be set
	 * @throws CommonException if fails
	 * @throws InstantiationException if fails
	 */
	public void setValue(final RemoteInfo rinfo, final Object value) throws InstantiationException, CommonException  {
		final PropertyHolder ph= getPropertyHolder(new ConnectionParameters(rinfo, value.getClass()));
		blockUntillConnected(ph.property);
		ph.property.setValueAsObject(value);
	}

	public <T> Request<T> setValueAsync(final ConnectionParameters cparam, final Object value, final ResponseListener<T> callback) throws Exception {
		final PropertyHolder ph = getPropertyHolder(cparam);
		blockUntillConnected(ph.property);
		return ((DynamicValueProperty<T>) ph.property).setAsynchronous((T)value, callback);
	}

	public void registerListener(final ConnectionParameters cparam, final ChannelListener listener) throws InstantiationException, CommonException {
		final PropertyHolder ph= getPropertyHolder(cparam, 0);
		if (cparam.getRemoteInfo().getCharacteristic()!=null) {
			return;
		}

		ph.property.addListener(listener);
	}

	public void deregisterListener(final ConnectionParameters cparam, final ChannelListener listener) throws InstantiationException, CommonException {
		final PropertyHolder ph= getPropertyHolder(cparam, 1);
		if (cparam.getRemoteInfo().getCharacteristic()!=null) {
			return;
		}

		ph.property.removeListener(listener);
	}

	public void registerListener(final ConnectionParameters cparam, final DynamicValueListener listener) throws InstantiationException, CommonException {
		final PropertyHolder ph= getPropertyHolder(cparam, 0);
		if (cparam.getRemoteInfo().getCharacteristic()!=null) {
			return;
		}

		ph.property.addDynamicValueListener(listener);
	}

	public void deregisterListener(final ConnectionParameters cparam, final DynamicValueListener listener) throws InstantiationException, CommonException {
		final PropertyHolder ph= getPropertyHolder(cparam, 1);
		if (cparam.getRemoteInfo().getCharacteristic()!=null) {
			return;
		}

		ph.property.removeDynamicValueListener(listener);
	}

	public void registerListener(final ConnectionParameters cparam, final PropertyChangeListener listener) throws InstantiationException, CommonException {
		final PropertyHolder ph= getPropertyHolder(cparam, 0);

		ph.property.addPropertyChangeListener(listener);
	}

	public void deregisterListener(final ConnectionParameters cparam, final PropertyChangeListener listener) throws InstantiationException, CommonException {
		final PropertyHolder ph= getPropertyHolder(cparam, 1);

		ph.property.removePropertyChangeListener(listener);
	}

	/**
	 * Registers listener to special ExpertMonitor, which can be created by plug specific parameters.
	 *
	 * @param cparam connection parameters for remote property
	 * @param listener listener which should receive value and status updated
	 * @param paremeters plug specific parameters intended for ExpertMonitor
	 * @throws InstantiationException if fails
	 * @throws CommonException if fails
	 */
	public void registerListener(final ConnectionParameters cparam, final DynamicValueListener listener, final Map<String,Object> parameters) throws InstantiationException, CommonException {
		final PropertyHolder ph= getPropertyHolder(cparam, 0);
		if (cparam.getRemoteInfo().getCharacteristic()!=null) {
			return;
		}

		if (parameters == null || parameters.size()==0) {
			ph.property.addDynamicValueListener(listener);
		} else {
			ph.property.createNewExpertMonitor(listener, parameters);
		}
	}

	/**
	 * Deregisters listener to special ExpertMonitor, which was created by plug specific parameters.
	 *
	 * @param cparam connection parameters for remote property
	 * @param listener listener which should receive value and status updated
	 * @param paremeters plug specific parameters which were used to create ExpertMonitor
	 * @throws InstantiationException if fails
	 * @throws CommonException if fails
	 */
	public void deregisterListener(final ConnectionParameters cparam, final DynamicValueListener listener, final Map<String,Object> parameters) throws InstantiationException, CommonException {
		final PropertyHolder ph= getPropertyHolder(cparam, 0);
		if (cparam.getRemoteInfo().getCharacteristic()!=null) {
			return;
		}

		if (parameters == null || parameters.size()==0) {
			ph.property.removeDynamicValueListener(listener);
		} else {
			final DynamicValueMonitor[] mon= ph.property.getMonitors();
			for (final DynamicValueMonitor m : mon) {
				if (m instanceof ExpertMonitor && parameters.equals(((ExpertMonitor)m).getParameters())) {
					m.destroy();
					return;
				}
			}
		}

	}

	private void blockUntillConnected(final DynamicValueProperty<?> property) throws ConnectionException {
		LinkBlocker.blockUntillConnected(property, Plugs.getConnectionTimeout(ctx.getConfiguration(), 30000) * 2, true);
	}

	/**
	 * Return default plug type, which is used for all remote names, which does not
	 * explicitly declare plug or connection type.
	 *
	 * <p>
	 * By default (if not set) plug type equals to Simulator.
	 * </p>
	 *
	 *  @return default plug type
	 */
	public String getDefaultPlugType() {
		return getFactory().getDefaultPlugType();
	}

	/**
	 * Sets default plug type, which is used for all remote names, which does not
	 * explicitly declare plug or connection type.
	 *
	 * <p>
	 * So far supported values are: EPICS, TINE, Simulator.
	 * By default (if not set) plug type equals to Simulator.
	 * </p>
	 *
	 * @param defautl plug type.
	 */
	public void setDefaultPlugType(final String plugType) {
		getFactory().setDefaultPlugType(plugType);
	}

	public void releaseAll() {
		getFactory().releaseAll();

		synchronized (properties) {
			properties.clear();
		}
	}

}
