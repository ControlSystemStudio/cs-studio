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

package org.epics.css.dal.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.epics.css.dal.DataAccess;
import org.epics.css.dal.DataExchangeException;
import org.epics.css.dal.DynamicValueAdapter;
import org.epics.css.dal.DynamicValueCondition;
import org.epics.css.dal.DynamicValueEvent;
import org.epics.css.dal.DynamicValueListener;
import org.epics.css.dal.DynamicValueMonitor;
import org.epics.css.dal.IllegalViewException;
import org.epics.css.dal.RemoteException;
import org.epics.css.dal.SimpleMonitor;
import org.epics.css.dal.SimpleProperty;
import org.epics.css.dal.StringAccess;
import org.epics.css.dal.Timestamp;
import org.epics.css.dal.context.ConnectionState;
import org.epics.css.dal.context.Identifier;
import org.epics.css.dal.context.IdentifierUtilities;
import org.epics.css.dal.proxy.DirectoryProxy;
import org.epics.css.dal.proxy.MonitorProxy;
import org.epics.css.dal.proxy.PropertyProxy;

import com.cosylab.util.ListenerList;


/**
 * Simple property implementation
 *
 * @author Igor Kriznar (igor.kriznarATcosylab.com)
 *
 */
public class SimplePropertyImpl<T> extends DataAccessImpl<T>
	implements SimpleProperty<T>
{
	/*    protected Hashtable<Class<? extends DataAccess>,Class<? extends DataAccess> > dataAccessTypes =
	        new Hashtable<Class<? extends DataAccess>,Class<? extends DataAccess> >();
	        */
	protected Hashtable<Class<? extends DataAccess<?>>,Class<? extends DataAccess<?>>> dataAccessTypes = new Hashtable<Class<? extends DataAccess<?>>,Class<? extends DataAccess<?>>>();
	protected DirectoryProxy directoryProxy;
	protected MonitorProxyWrapper<T, ? extends SimpleProperty<T>> defaultMonitor;
	protected List<SimpleMonitor> monitors = new ArrayList<SimpleMonitor>();
	protected ListenerList propertyListener = new ListenerList(PropertyChangeListener.class);
	protected String name;
	protected Identifier identifier;
	protected DynamicValueCondition condition;

	protected Timestamp lastValueChangeTimestamp;
	protected Timestamp lastValueUpdateTimestamp;
	protected boolean lastValueSuccess=true;

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
	 * @see org.epics.css.dal.SimpleProperty#getAccessTypes()
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
	 * @see org.epics.css.dal.SimpleProperty#getCondition()
	 */
	public DynamicValueCondition getCondition()
	{
		return condition != null ? condition : proxy.getCondition();
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.SimpleProperty#getDataAccess(java.lang.Class)
	 */
	public <D extends DataAccess<?>> D getDataAccess(Class<D> type)
		throws IllegalViewException
	{
		if (type.isAssignableFrom(this.getClass())) {
			return type.cast(this);
		}

		Class<? extends DataAccess<?>> implClass = (Class<? extends DataAccess<?>>)dataAccessTypes.get(type);

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
	 * @see org.epics.css.dal.SimpleProperty#getDefaultDataAccess()
	 */
	public DataAccess<T> getDefaultDataAccess()
	{
		return this;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.SimpleProperty#getDescription()
	 */
	public String getDescription() throws DataExchangeException
	{
		if (directoryProxy.getConnectionState() != ConnectionState.CONNECTED)
			throw new DataExchangeException(this,"Directory proxy is not connected");

		return (String)directoryProxy.getCharacteristic(C_DESCRIPTION);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.SimpleProperty#getUniqueName()
	 */
	public String getUniqueName()
	{
		return proxy.getUniqueName();
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.SimpleProperty#isTimelag()
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
	 * @see org.epics.css.dal.SimpleProperty#isTimeout()
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
	 * @see org.epics.css.dal.CharacteristicContext#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(PropertyChangeListener l)
	{
		propertyListener.add(l);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.CharacteristicContext#getCharacteristic(java.lang.String)
	 */
	public Object getCharacteristic(String name) throws DataExchangeException
	{
		if (directoryProxy.getConnectionState() != ConnectionState.CONNECTED)
			throw new DataExchangeException(this,"Directory proxy is not connected");
		return directoryProxy.getCharacteristic(name);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.CharacteristicContext#getCharacteristicNames()
	 */
	public String[] getCharacteristicNames() throws DataExchangeException
	{
		if (directoryProxy.getConnectionState() != ConnectionState.CONNECTED)
			throw new DataExchangeException(this,"Directory proxy is not connected");
		return directoryProxy.getCharacteristicNames();
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.CharacteristicContext#getCharacteristics(java.lang.String[])
	 */
	public Map<String,Object> getCharacteristics(String[] names) throws DataExchangeException
	{
		if (directoryProxy.getConnectionState() != ConnectionState.CONNECTED)
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
	 * @see org.epics.css.dal.CharacteristicContext#getPropertyChangeListeners()
	 */
	public PropertyChangeListener[] getPropertyChangeListeners()
	{
		return (PropertyChangeListener[])propertyListener.toArray();
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.CharacteristicContext#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void removePropertyChangeListener(PropertyChangeListener l)
	{
		propertyListener.remove(l);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.ValueUpdateable#createNewMonitor(org.epics.css.dal.DynamicValueListener)
	 */
	public <E extends SimpleProperty<T>> DynamicValueMonitor createNewMonitor(DynamicValueListener<T, E> listener)
		throws RemoteException
	{
		MonitorProxyWrapper<T, E> mpw = new MonitorProxyWrapper<T, E>((E) this, listener);
		MonitorProxy mp = null;
		mp = proxy.createMonitor(mpw);
		mpw.initialize(mp);
		monitors.add(mp);

		return mpw;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.ValueUpdateable#getDefaultMonitor()
	 */
	public synchronized DynamicValueMonitor getDefaultMonitor()
	{
		if (defaultMonitor == null && proxy!=null && proxy.getConnectionState()==ConnectionState.CONNECTED) {
			defaultMonitor = new MonitorProxyWrapper<T, SimpleProperty<T>>(this, dvListeners);

			try {
				MonitorProxy mp = proxy.createMonitor(defaultMonitor);
				defaultMonitor.initialize(mp);
				monitors.add(mp);
			} catch (Exception e) {
				e.printStackTrace();
				if (defaultMonitor!=null) {
					defaultMonitor.destroy();
					defaultMonitor=null;
				}
			}
		}

		return defaultMonitor;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.DataAccess#addDynamicValueListener(org.epics.css.dal.DynamicValueListener)
	 */
	public <P extends SimpleProperty<T>> void addDynamicValueListener(DynamicValueListener<T, P> l)
	{
		super.addDynamicValueListener(l);

		if (defaultMonitor == null) {
			getDefaultMonitor();
		}
		
		DynamicValueEvent<T, P> e = new DynamicValueEvent<T, P>(
				this, (P)this, lastValue, getCondition(),
				lastValueUpdateTimestamp, "Initial update.");
		l.conditionChange(e);
		if (lastValue != null) {
			l.valueChanged(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.DataAccess#removeDynamicValueListener(org.epics.css.dal.DynamicValueListener)
	 */
	public <P extends SimpleProperty<T>> void removeDynamicValueListener(DynamicValueListener<T, P> l)
	{
		super.removeDynamicValueListener(l);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.ValueUpdateable#getLatestReceivedValueAsObject()
	 */
	public Object getLatestReceivedValueAsObject()
	{
		return lastValue;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.ValueUpdateable#getLatestValueChangeTimestamp()
	 */
	public Timestamp getLatestValueChangeTimestamp()
	{
		return lastValueChangeTimestamp;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.ValueUpdateable#getLatestValueSuccess()
	 */
	public boolean getLatestValueSuccess()
	{
		return lastValueSuccess;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.ValueUpdateable#getLatestValueUpdateTimestamp()
	 */
	public Timestamp getLatestValueUpdateTimestamp()
	{
		return lastValueUpdateTimestamp;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.impl.DataAccessImpl#initialize(org.epics.css.dal.proxy.PropertyProxy)
	 */
	public void initialize(PropertyProxy<T> proxy, DirectoryProxy dirProxy)
	{
		super.initialize(proxy);
		this.directoryProxy = dirProxy;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.SimpleProperty#getName()
	 */
	public String getName()
	{
		if (name == null) {
			return proxy.getUniqueName();
		}

		return name;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.Identifiable#getIdentifier()
	 */
	public Identifier getIdentifier()
	{
		if (identifier == null) {
			identifier = IdentifierUtilities.createIdentifier(this);
		}

		return identifier;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.context.Identifiable#isDebug()
	 */
	public boolean isDebug()
	{
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
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * Returns the DirectoryProxy which describes characteristics of the remote
	 * connection associated with this property.
	 * 
	 * @return the directory proxy
	 */
	public DirectoryProxy getDirectoryProxy() {
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
} 