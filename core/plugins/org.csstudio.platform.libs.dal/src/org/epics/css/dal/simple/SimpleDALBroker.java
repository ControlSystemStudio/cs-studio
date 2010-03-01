/**
 * 
 */
package org.epics.css.dal.simple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
			ArrayList<ConnectionParameters> toRemove = new ArrayList<ConnectionParameters>();
			synchronized (properties) {
				PropertyHolder holder;
				for (Iterator<ConnectionParameters> iterator = properties.keySet().iterator(); iterator.hasNext();) {
					ConnectionParameters cp = (ConnectionParameters) iterator.next();
					holder = properties.get(cp);
					if (holder.expires > 0 && holder.expires < System.currentTimeMillis() && !holder.property.hasDynamicValueListeners()) {
						toRemove.add(cp);
					}
				}
				for (ConnectionParameters connectionParameters : toRemove) {
					holder = properties.remove(connectionParameters);
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
		public PropertyHolder(DynamicValueProperty<?> p) {
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

	private static SimpleDALBroker broker;



	public static SimpleDALBroker getInstance() {
		return newInstance(null);
	}
	
	public static SimpleDALBroker newInstance(AbstractApplicationContext ctx) {
		if (ctx==null) {
			if (broker == null) {
				broker = new SimpleDALBroker(new DefaultApplicationContext("SimpleDALContext"));
			}
			return broker;
		}
		return new SimpleDALBroker(ctx);
	}

	
	private AbstractApplicationContext ctx;
	private HashMap<ConnectionParameters, PropertyHolder> properties;
	/**
	 * Time duration which property is alive after has been last used. 
	 * After this time it could be deleted.
	 */
	private long timeToLive = 60000;
	private DefaultPropertyFactoryBroker factory;
	
	private static final long CLEANUP_INTERVAL = 60000;
	private Timer cleanupTimer;
	// TODO  SimpleDALBroker should be destroyed at one point and cleanupTask canceled...
	private PropertiesCleanupTask cleanupTask;
	
	private SimpleDALBroker(AbstractApplicationContext ctx) {
		this.ctx=ctx;
		properties= new HashMap<ConnectionParameters, PropertyHolder>();
		cleanupTimer = new Timer("Cleanup Timer");
		cleanupTimer.scheduleAtFixedRate(new PropertiesCleanupTask(), CLEANUP_INTERVAL, CLEANUP_INTERVAL);
		
		ctx.addLifecycleListener(new LifecycleListener() {

			public void destroyed(LifecycleEvent event) {
				// TODO implement
				System.out.println(">>> DESTROYED!");
			}

			public void destroying(LifecycleEvent event) {
				// TODO implement
				System.out.println(">>> DESTROYING!");
			}

			public void initialized(LifecycleEvent event) {
				// not important
			}

			public void initializing(LifecycleEvent event) {
				// not important
			}
		});
	}

	private PropertyHolder getPropertyHolder(ConnectionParameters cparam) throws RemoteException, InstantiationException {
		return getPropertyHolder(cparam, System.currentTimeMillis()+timeToLive);
	}
	
	private PropertyHolder getPropertyHolder(ConnectionParameters cparam, long expires) throws RemoteException, InstantiationException {
		
		PropertyHolder ph;
		
		synchronized (properties) {
			ph= properties.get(cparam);
		}
		
		if (ph==null) {
			DynamicValueProperty<?> property;
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
			ph= new PropertyHolder(property);
			synchronized (properties) {
				properties.put(cparam, ph);
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
			factory.initialize(ctx, LinkPolicy.ASYNC_LINK_POLICY);
		}
		return factory;
	}

	/**
	 * Value is read and returned synchronously.
	 * 
	 * @param cparam connection parameter to remote value
	 * @return remote value
	 * @throws RemoteException if value retrieval fails
	 * @throws InstantiationException 
	 */
	public Object getValue(ConnectionParameters cparam) throws RemoteException, InstantiationException {
		PropertyHolder ph= getPropertyHolder(cparam);
		blockUntillConnected(ph.property);
		if (cparam.getRemoteInfo().getCharacteristic()!=null) {
			return ph.property.getCharacteristic(cparam.getRemoteInfo().getCharacteristic());
		}
		return ph.property.getValue();
	}

	public <T> T getValue(RemoteInfo rinfo, Class<T> type) throws RemoteException, InstantiationException {
		PropertyHolder ph= getPropertyHolder(new ConnectionParameters(rinfo, type));
		blockUntillConnected(ph.property);
		if (rinfo.getCharacteristic()!=null) {
			Object o= ph.property.getCharacteristic(rinfo.getCharacteristic());
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
	
	public Object getValue(RemoteInfo rinfo) throws RemoteException, InstantiationException {
		PropertyHolder ph= getPropertyHolder(new ConnectionParameters(rinfo));
		blockUntillConnected(ph.property);
		if (rinfo.getCharacteristic()!=null) {
			return ph.property.getCharacteristic(rinfo.getCharacteristic());
		}
		return ph.property.getValue();
	}

	public <T> Request<T> getValueAsync(ConnectionParameters cparam, ResponseListener<T> callback) throws RemoteException, InstantiationException {
		PropertyHolder ph= getPropertyHolder(cparam);
		if (cparam.getRemoteInfo().getCharacteristic()!=null) {
			return (Request<T>)ph.property.getCharacteristicAsynchronously(
					cparam.getRemoteInfo().getCharacteristic(),
					callback);
		}

		if (cparam.getDataType().getJavaType() == AnyData.class) {
			RequestImpl<T> r= new RequestImpl<T>(ph.property, callback);
			r.addResponse(new ResponseImpl<T>(ph.property, r, (T)ph.property.getData(), "", true, null, null, new Timestamp(), true));
			return r;
		}
		return ((DynamicValueProperty<T>)ph.property).getAsynchronous((ResponseListener<T>)callback);
	}
	
	public void setValue(RemoteInfo rinfo, Object value) throws Exception {
		PropertyHolder ph= getPropertyHolder(new ConnectionParameters(rinfo, value.getClass()));
		blockUntillConnected(ph.property);
		ph.property.setValueAsObject(value);
	}
	
	public <T> Request<T> setValueAsync(ConnectionParameters cparam, Object value, ResponseListener<T> callback) throws Exception {
		PropertyHolder ph = getPropertyHolder(cparam);
		return ((DynamicValueProperty<T>) ph.property).setAsynchronous((T)value, callback);
	}
	
	public void registerListener(ConnectionParameters cparam, ChannelListener listener) throws RemoteException, InstantiationException {
		PropertyHolder ph= getPropertyHolder(cparam, 0);
		if (cparam.getRemoteInfo().getCharacteristic()!=null) {
			return;
		}
		
		ph.property.addListener(listener);
	}

	public void deregisterListener(ConnectionParameters cparam, ChannelListener listener) throws RemoteException, InstantiationException {
		PropertyHolder ph= getPropertyHolder(cparam, 1);
		if (cparam.getRemoteInfo().getCharacteristic()!=null) {
			return;
		}
		
		ph.property.removeListener(listener);
	}
	public void registerListener(ConnectionParameters cparam, DynamicValueListener listener) throws RemoteException, InstantiationException {
		PropertyHolder ph= getPropertyHolder(cparam, 0);
		if (cparam.getRemoteInfo().getCharacteristic()!=null) {
			return;
		}
		
		ph.property.addDynamicValueListener(listener);
	}

	public void deregisterListener(ConnectionParameters cparam, DynamicValueListener listener) throws RemoteException, InstantiationException {
		PropertyHolder ph= getPropertyHolder(cparam, 1);
		if (cparam.getRemoteInfo().getCharacteristic()!=null) {
			return;
		}
		
		ph.property.removeDynamicValueListener(listener);
	}
	
	private void blockUntillConnected(DynamicValueProperty<?> property) throws ConnectionException {
		LinkBlocker.blockUntillConnected(property, Plugs.getConnectionTimeout(ctx.getConfiguration(), 30000) * 2, true);
	}
}
