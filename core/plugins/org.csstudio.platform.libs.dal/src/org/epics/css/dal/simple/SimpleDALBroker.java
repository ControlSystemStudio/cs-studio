/**
 *
 */
package org.epics.css.dal.simple;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.epics.css.dal.DynamicValueListener;
import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.RemoteException;
import org.epics.css.dal.Request;
import org.epics.css.dal.ResponseListener;
import org.epics.css.dal.Timestamp;
import org.epics.css.dal.context.AbstractApplicationContext;
import org.epics.css.dal.context.ConnectionException;
import org.epics.css.dal.context.LifecycleEvent;
import org.epics.css.dal.context.LifecycleListener;
import org.epics.css.dal.context.LinkBlocker;
import org.epics.css.dal.impl.DefaultApplicationContext;
import org.epics.css.dal.impl.RequestImpl;
import org.epics.css.dal.impl.ResponseImpl;
import org.epics.css.dal.spi.DefaultPropertyFactoryBroker;
import org.epics.css.dal.spi.LinkPolicy;
import org.epics.css.dal.spi.Plugs;

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
			final List<String> toRemove = new ArrayList<String>();
			synchronized (properties) {
				PropertyHolder holder;
				for (final String key : properties.keySet()) {
					holder = properties.get(key);
					if ((holder.expires > 0) && (holder.expires < System.currentTimeMillis()) && !holder.property.hasDynamicValueListeners()) {
						toRemove.add(key);
					}
				}
				for (final String key : toRemove) {
					holder = properties.remove(key);
					factory.destroy(holder.property);
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

	private static String createKey(final ConnectionParameters cparam) {
		final StringBuilder sb = new StringBuilder();
		sb.append(cparam.getRemoteInfo().getConnectionType()+",");
		sb.append(cparam.getRemoteInfo().getRemoteName()+",");
		final DataFlavor df = cparam.getConnectionType();
		if (df == null) {
            sb.append("null");
        } else {
            sb.append(df.toString());
        }
		return sb.toString();
	}

	public static SimpleDALBroker getInstance() {
		return newInstance(null);
	}

	public static SimpleDALBroker newInstance(final AbstractApplicationContext ctx) {
		if (ctx==null) {
			if (broker == null) {
				broker = new SimpleDALBroker(new DefaultApplicationContext("SimpleDALContext"));
			}
			return broker;
		}
		return new SimpleDALBroker(ctx);
	}


	private final AbstractApplicationContext _ctx;
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
		this._ctx=ctx;
		properties= new HashMap<String, PropertyHolder>();
		cleanupTimer = new Timer("Cleanup Timer");
		cleanupTimer.scheduleAtFixedRate(new PropertiesCleanupTask(), CLEANUP_INTERVAL, CLEANUP_INTERVAL);

		ctx.addLifecycleListener(new LifecycleListener() {

			public void destroyed(final LifecycleEvent event) {
				// TODO implement?
			}

			public void destroying(final LifecycleEvent event) {
				// TODO implement?
				cleanupTimer.cancel();
			}

			public void initialized(final LifecycleEvent event) {
				// not important
			}

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
		final String key = createKey(cparam);

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
			factory = DefaultPropertyFactoryBroker.getInstance();
			factory.initialize(_ctx, LinkPolicy.ASYNC_LINK_POLICY);
		}
		return factory;
	}

	/**
	 * Value is read and returned synchronously.
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

	public Object getValue(final RemoteInfo rinfo) throws InstantiationException, CommonException {
		final PropertyHolder ph= getPropertyHolder(new ConnectionParameters(rinfo));
		blockUntillConnected(ph.property);
		if (rinfo.getCharacteristic()!=null) {
			return ph.property.getCharacteristic(rinfo.getCharacteristic());
		}
		return ph.property.getValue();
	}

	public <T> Request<T> getValueAsync(final ConnectionParameters cparam, final ResponseListener<T> callback) throws InstantiationException, CommonException {
		final PropertyHolder ph= getPropertyHolder(cparam);
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

	public void setValue(final RemoteInfo rinfo, final Object value) throws Exception {
		final PropertyHolder ph= getPropertyHolder(new ConnectionParameters(rinfo, value.getClass()));
		blockUntillConnected(ph.property);
		ph.property.setValueAsObject(value);
	}

	public <T> Request<T> setValueAsync(final ConnectionParameters cparam, final Object value, final ResponseListener<T> callback) throws Exception {
		final PropertyHolder ph = getPropertyHolder(cparam);
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

	private void blockUntillConnected(final DynamicValueProperty<?> property) throws ConnectionException {
		LinkBlocker.blockUntillConnected(property, Plugs.getConnectionTimeout(_ctx.getConfiguration(), 30000) * 2, true);
	}
}
