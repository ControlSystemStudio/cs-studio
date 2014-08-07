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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.csstudio.dal.DataAccess;
import org.csstudio.dal.DataExchangeException;
import org.csstudio.dal.DynamicValueCondition;
import org.csstudio.dal.DynamicValueEvent;
import org.csstudio.dal.DynamicValueListener;
import org.csstudio.dal.DynamicValueMonitor;
import org.csstudio.dal.IllegalViewException;
import org.csstudio.dal.RemoteException;
import org.csstudio.dal.SimpleProperty;
import org.csstudio.dal.StringAccess;
import org.csstudio.dal.Timestamp;
import org.csstudio.dal.context.Identifier;
import org.csstudio.dal.context.IdentifierUtilities;
import org.csstudio.dal.proxy.DirectoryProxy;
import org.csstudio.dal.proxy.MonitorProxy;
import org.csstudio.dal.proxy.PropertyProxy;
import org.csstudio.dal.proxy.Proxy;
import org.csstudio.dal.simple.ChannelListener;
import org.csstudio.dal.simple.impl.ChannelListenerNotifier;

import com.cosylab.util.ListenerList;


/**
 * Simple property implementation
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 *
 */
public abstract class SimplePropertyImpl<T> extends DataAccessImpl<T>
	implements SimpleProperty<T>
{
	/*    protected Hashtable<Class<? extends DataAccess>,Class<? extends DataAccess> > dataAccessTypes =
	        new Hashtable<Class<? extends DataAccess>,Class<? extends DataAccess> >();
	        */
	protected Hashtable<Class<? extends DataAccess<?>>,Class<? extends DataAccess<?>>> dataAccessTypes = new Hashtable<Class<? extends DataAccess<?>>,Class<? extends DataAccess<?>>>();
	protected DirectoryProxy<?> directoryProxy;
	protected MonitorProxyWrapper<T, ? extends SimpleProperty<T>> defaultMonitor;
	protected Set<MonitorProxyWrapper<T, ? extends SimpleProperty<T>>> monitors = new HashSet<MonitorProxyWrapper<T, ? extends SimpleProperty<T>>>();
	protected ListenerList propertyListener = new ListenerList(PropertyChangeListener.class);
	protected String name;
	protected Identifier identifier;
	protected DynamicValueCondition condition;

	protected Timestamp lastValueChangeTimestamp;
	protected Timestamp lastValueUpdateTimestamp;
	protected boolean lastValueSuccess=true;

	// AnyDataChannel methods...
	private ChannelListenerNotifier chListeners;
	/**
	 * Creates a new SimplePropertyImpl object.
	 *
	 * @param valClass value datatype class
	 * @param name property name
	 */
	public SimplePropertyImpl(Class<T> valClass, String name)
	{
		super(valClass);
		dataAccessTypes.put(StringAccess.class, StringDataAccessWrapper.class);

		this.name = name;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.SimpleProperty#getAccessTypes()
	 */
	@SuppressWarnings("unchecked")
	public Class<? extends DataAccess<?>>[] getAccessTypes()
	{
		Class<? extends DataAccess<?>>[] daTypes = (Class<? extends DataAccess<?>>[])new Class<?>[dataAccessTypes
			.size()];
		dataAccessTypes.keySet().toArray(daTypes);

		return daTypes;
	}

	public void addDataAccessType(Class<? extends DataAccess<?>> dataAccessType,
	    Class<? extends AbstractDataAccessWrapper<?>> implementation)
	{
		dataAccessTypes.put(dataAccessType, implementation);
	}

	public void removeDataAccessType(Class<?extends DataAccess<?>> dataAccessType)
	{
		dataAccessTypes.remove(dataAccessType);
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.SimpleProperty#getCondition()
	 */
	public DynamicValueCondition getCondition()
	{
		if (condition == null && proxy == null) throw new IllegalStateException("Proxy is null");
		return condition != null ? condition : proxy.getCondition();
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.SimpleProperty#getDataAccess(java.lang.Class)
	 */
	public <D extends DataAccess<?>> D getDataAccess(Class<D> type)
		throws IllegalViewException
	{
		if (type.isAssignableFrom(this.getClass())) {
			return type.cast(this);
		}

		Class<? extends DataAccess<?>> implClass = dataAccessTypes.get(type);

		if (implClass != null) {
			try {
				return (D)implClass.getConstructor(DataAccess.class)
				.newInstance(this);
			} catch (Exception e) {
				throw new IllegalViewException(this,
				    "Unable to instantiate: " + implClass.toString());
			}
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.SimpleProperty#getDefaultDataAccess()
	 */
	public DataAccess<T> getDefaultDataAccess()
	{
		return this;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.SimpleProperty#getDescription()
	 */
	public String getDescription() throws DataExchangeException
	{
		if (directoryProxy == null || !directoryProxy.getConnectionState().isConnected())
			throw new DataExchangeException(this,"Directory proxy is not connected");

		return (String)directoryProxy.getCharacteristic(C_DESCRIPTION);
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.SimpleProperty#getUniqueName()
	 */
	public String getUniqueName()
	{
		if (proxy == null) throw new IllegalStateException("Proxy is null");
		return proxy.getUniqueName();
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.SimpleProperty#isTimelag()
	 */
	public boolean isTimelag()
	{
		if (condition != null) {
			return condition.isTimelag();
		} else if (defaultMonitor != null) {
			return defaultMonitor.isTimelag();
		} else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.SimpleProperty#isTimeout()
	 */
	public boolean isTimeout()
	{
		if (condition != null) {
			return condition.isTimeout();
		} else if (defaultMonitor != null) {
			return defaultMonitor.isTimeout();
		} else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.CharacteristicContext#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(PropertyChangeListener l)
	{
		propertyListener.add(l);
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.CharacteristicContext#getCharacteristic(java.lang.String)
	 */
	public Object getCharacteristic(String name) throws DataExchangeException
	{
		if (directoryProxy == null || !directoryProxy.getConnectionState().isConnected())
			throw new DataExchangeException(this,"Directory proxy is not connected");
		return directoryProxy.getCharacteristic(name);
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.CharacteristicContext#getCharacteristicNames()
	 */
	public String[] getCharacteristicNames() throws DataExchangeException
	{
		if (directoryProxy == null || !directoryProxy.getConnectionState().isConnected())
			throw new DataExchangeException(this,"Directory proxy is not connected");
		return directoryProxy.getCharacteristicNames();
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.CharacteristicContext#getCharacteristics(java.lang.String[])
	 */
	public Map<String,Object> getCharacteristics(String[] names) throws DataExchangeException
	{
		if (directoryProxy == null || !directoryProxy.getConnectionState().isConnected())
			throw new DataExchangeException(this,"Directory proxy is not connected");

		// TODO: implement wrapper listener
		//w= new Wrapper...(names);
		//dirProxy.getCharacteristics(names, );
		//return w.getCharacteristics();

		// TODO : this is dummy implementation, improve it
		Map<String,Object> ch = new HashMap<String,Object>(names.length + 1);

		for (int i = 0; i < names.length; i++) {
			Object o = directoryProxy.getCharacteristic(names[i]);

			if (o != null) {
				ch.put(names[i], o);
			}
		}

		return ch;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.CharacteristicContext#getPropertyChangeListeners()
	 */
	public PropertyChangeListener[] getPropertyChangeListeners()
	{
		return (PropertyChangeListener[])propertyListener.toArray();
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.CharacteristicContext#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void removePropertyChangeListener(PropertyChangeListener l)
	{
		propertyListener.remove(l);
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.ValueUpdateable#createNewMonitor(org.csstudio.dal.DynamicValueListener)
	 */
	public <E extends SimpleProperty<T>> DynamicValueMonitor createNewMonitor(DynamicValueListener<T, E> listener)
		throws RemoteException
	{
		if (proxy == null) throw new IllegalStateException("Proxy is null");
		
		MonitorProxyWrapper<T, E> mpw = new MonitorProxyWrapper<T, E>((E) this, listener);
		MonitorProxy mp = null;
		mp = proxy.createMonitor(mpw,null);
		mpw.initialize(mp);
		synchronized (monitors) {
			monitors.add(mpw);
		}

		return mpw;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.ValueUpdateable#getDefaultMonitor()
	 */
	public synchronized DynamicValueMonitor getDefaultMonitor()
	{
		if (defaultMonitor == null && proxy!=null && proxy.getConnectionState().isConnected()) {
			defaultMonitor = new MonitorProxyWrapper<T, SimpleProperty<T>>(this, getDvListeners());

			try {
				MonitorProxy mp = proxy.createMonitor(defaultMonitor,null);
				defaultMonitor.initialize(mp);
				synchronized (monitors) {
					monitors.add(defaultMonitor);
				}
			} catch (Exception e) {
				Logger.getLogger(this.getClass()).fatal("Failed to create default monitor: "+e.getMessage(), e);
				if (defaultMonitor!=null) {
					defaultMonitor.destroy();
					defaultMonitor=null;
				}
			}
		}

		return defaultMonitor;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.DataAccess#addDynamicValueListener(org.csstudio.dal.DynamicValueListener)
	 */
	public <P extends SimpleProperty<T>> void addDynamicValueListener(DynamicValueListener<T, P> l)
	{
		super.addDynamicValueListener(l);

		if (defaultMonitor == null) {
			getDefaultMonitor();
		}
		
		if (proxy != null){
			DynamicValueEvent<T, P> e = new DynamicValueEvent<T, P>(
					this, (P)this, lastValue, getCondition(),
					lastValueUpdateTimestamp, "Initial update.");
			l.conditionChange(e);
			if (lastValue != null && lastValueSuccess) {
				l.valueChanged(e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.DataAccess#removeDynamicValueListener(org.csstudio.dal.DynamicValueListener)
	 */
	public <P extends SimpleProperty<T>> void removeDynamicValueListener(DynamicValueListener<T, P> l)
	{
		super.removeDynamicValueListener(l);
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.ValueUpdateable#getLatestValueChangeTimestamp()
	 */
	public Timestamp getLatestValueChangeTimestamp()
	{
		return lastValueChangeTimestamp;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.ValueUpdateable#getLatestValueSuccess()
	 */
	public boolean getLatestValueSuccess()
	{
		return lastValueSuccess;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.ValueUpdateable#getLatestValueUpdateTimestamp()
	 */
	public Timestamp getLatestValueUpdateTimestamp()
	{
		return lastValueUpdateTimestamp;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.impl.DataAccessImpl#initialize(org.csstudio.dal.proxy.PropertyProxy)
	 */
	public void initialize(PropertyProxy<T,?> proxy, DirectoryProxy<?> dirProxy)
	{
		super.initialize(proxy);
		this.directoryProxy = dirProxy;
		
		MonitorProxyWrapper<T,?>[] mm= getMonitorWrappers();  
		
		for (int i=0;i<mm.length;i++){
			try {
				MonitorProxy mp = proxy.createMonitor(mm[i],null);
				mm[i].initialize(mp);
			} catch (Exception e) {
				Logger.getLogger(SimplePropertyImpl.class).warn("Problem on re-initializing monitor on property"+getName()+".",e);
			}
		}
		// catch the identifier
		getIdentifier();
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.SimpleProperty#getName()
	 */
	public String getName()
	{
		if (name == null) {
			if (proxy == null) throw new IllegalStateException("Proxy is null");
			return proxy.getUniqueName();
		}

		return name;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.context.Identifiable#getIdentifier()
	 */
	public Identifier getIdentifier()
	{
		if (identifier == null) {
			identifier = IdentifierUtilities.createIdentifier(this);
		}

		return identifier;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.context.Identifiable#isDebug()
	 */
	public boolean isDebug()
	{
		if (proxy == null) throw new IllegalStateException("Proxy is null");
		return proxy.isDebug();
	}

	/**
	 * TODO must be fired when characteristics values change or characteristics
	 * are added or removed
	 * @param e
	 */
	protected void firePropertyChangeEvent(PropertyChangeEvent e)
	{
		PropertyChangeListener[] l= (PropertyChangeListener[])propertyListener.toArray();
		for (int i = 0; i < l.length; i++) {
			try {
				l[i].propertyChange(e);
			} catch (Exception ex) {
				Logger.getLogger(this.getClass()).error("Exception in event handler, continuing.", ex);
			}
		}
	}
	
	/**
	 * Returns the DirectoryProxy which describes characteristics of the remote
	 * connection associated with this property.
	 * 
	 * @return the directory proxy
	 */
	public DirectoryProxy<?> getDirectoryProxy() {
		return directoryProxy;
	}
	
	void updateLastValueCache(T lastValue, Timestamp lastUpdate, boolean sucess, boolean change) {
		if (lastValue!=null) {
			this.lastValue=lastValue;
		}
		if (change) {
			this.lastValueChangeTimestamp=lastUpdate;
		}
		this.lastValueUpdateTimestamp=lastUpdate;
		this.lastValueSuccess=true;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.dal.impl.DataAccessImpl#releaseProxy(boolean)
	 */
	@Override
	public Proxy<?>[] releaseProxy(boolean destroy) {
		MonitorProxyWrapper<T,?>[] mm= getMonitorWrappers();
		for (MonitorProxyWrapper<T,?> monitor : mm) {
			monitor.releaseProxy(destroy);
		}
		Proxy<?>[] tmp = new Proxy[]{super.releaseProxy(destroy)[0],directoryProxy};
		directoryProxy=null;
		if (destroy) {
			synchronized (monitors) {
				monitors.clear();
			}
			propertyListener.clear();
		}
		return tmp;
	}
	
	public ChannelListener[] getListeners() {
		return getChListeners().getChannelListeners();
	}
		
	private ChannelListenerNotifier getChListeners() {
		if (chListeners == null) {
			synchronized (this) {
				if (chListeners == null) {
					chListeners = new ChannelListenerNotifier(this);
				}
			}
		}
		return chListeners;
	}
	
	public void addListener(ChannelListener listener) {
		getChListeners().addChannelListener(listener);
	}
	
	public void removeListener(ChannelListener listener) {
		getChListeners().removeChannelListener(listener);
	}
	
	public DynamicValueMonitor[] getMonitors() {
		synchronized (monitors) {
			return monitors.toArray(new DynamicValueMonitor[monitors.size()]);
		}
	}
	
	@SuppressWarnings("unchecked")
	protected MonitorProxyWrapper<T,?>[] getMonitorWrappers() {
		synchronized (monitors) {
			return monitors.toArray(new MonitorProxyWrapper[monitors.size()]);
		}
	}
	
	void removeMonitor(MonitorProxyWrapper<?,?> mon) {
		synchronized (monitors) {
			monitors.remove(mon);
		}
	}
} 
