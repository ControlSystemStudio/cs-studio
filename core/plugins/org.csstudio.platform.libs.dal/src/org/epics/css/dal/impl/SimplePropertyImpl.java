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

import com.cosylab.util.ListenerList;

import org.epics.css.dal.DataAccess;
import org.epics.css.dal.DataExchangeException;
import org.epics.css.dal.DoubleAccess;
import org.epics.css.dal.DynamicValueCondition;
import org.epics.css.dal.DynamicValueEvent;
import org.epics.css.dal.DynamicValueListener;
import org.epics.css.dal.DynamicValueMonitor;
import org.epics.css.dal.IllegalViewException;
import org.epics.css.dal.LongAccess;
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


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
	protected Hashtable dataAccessTypes = new Hashtable();
	protected DirectoryProxy directoryProxy;
	protected MonitorProxyWrapper defaultMonitor;
	protected List<SimpleMonitor> monitors = new ArrayList<SimpleMonitor>();
	protected ListenerList propertyListener = new ListenerList(PropertyChangeListener.class);
	protected String name;
	protected Identifier identifier;
	protected DynamicValueCondition condition;
	private DynamicValueChangedListener dvChangedListener = new DynamicValueChangedListener();

	private class DynamicValueChangedListener implements DynamicValueListener
	{
		private DynamicValueEvent lastValueChangedEvent;
		private DynamicValueEvent lastValueModificationEvent;

		public void valueUpdated(DynamicValueEvent event)
		{
			lastValueModificationEvent = event;
		}

		public void valueChanged(DynamicValueEvent event)
		{
			lastValueChangedEvent = event;
			lastValueModificationEvent = event;
		}

		public void timeoutStarts(DynamicValueEvent event)
		{
		}

		public void timeoutStops(DynamicValueEvent event)
		{
		}

		public void timelagStarts(DynamicValueEvent event)
		{
		}

		public void timelagStops(DynamicValueEvent event)
		{
		}

		public void errorResponse(DynamicValueEvent event)
		{
		}

		public void conditionChange(DynamicValueEvent event)
		{
		}

		public DynamicValueEvent getLastValueChangedEvent()
		{
			return lastValueChangedEvent;
		}

		public DynamicValueEvent getLastValueModificationEvent()
		{
			return lastValueModificationEvent;
		}
	}

	/**
	 * Creates a new SimplePropertyImpl object.
	 *
	 * @param valClass value datatype class
	 * @param name property name
	 */
	public SimplePropertyImpl(Class<T> valClass, String name)
	{
		super(valClass);
		dataAccessTypes.put(this.getClass().getSuperclass().getInterfaces()[0],
		    this);
		dataAccessTypes.put(StringAccess.class, StringDataAccessWrapper.class);

		this.name = name;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.SimpleProperty#getAccessTypes()
	 */
	@SuppressWarnings("unchecked")
	public Class<?extends DataAccess>[] getAccessTypes()
	{
		Class<?extends DataAccess>[] daTypes = (Class<?extends DataAccess>[])new Class<?>[dataAccessTypes
			.size()];
		dataAccessTypes.keySet().toArray(daTypes);

		return daTypes;
	}

	public void addDataAccessType(Class<?extends DataAccess> dataAccessType,
	    Class<?extends AbstractDataAccessWrapper> implementation)
	{
		dataAccessTypes.put(dataAccessType, implementation);
	}

	public void removeDataAccessType(Class<?extends DataAccess> dataAccessType)
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
	public <D extends DataAccess> D getDataAccess(Class<D> type)
		throws IllegalViewException
	{
		if (type.isAssignableFrom(this.getClass())) {
			return type.cast(this);
		}

		Class implClass = (Class)dataAccessTypes.get(type);

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
	public Map getCharacteristics(String[] names) throws DataExchangeException
	{
		if (directoryProxy.getConnectionState() != ConnectionState.CONNECTED)
			throw new DataExchangeException(this,"Directory proxy is not connected");

		// TODO: implement wrapper listener
		//w= new Wrapper...(names);
		//dirProxy.getCharacteristics(names, );
		//return w.getCharacteristics();

		// TODO : this is dummy implementation, improve it
		Map ch = new HashMap(names.length + 1);

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
	public DynamicValueMonitor createNewMonitor(DynamicValueListener listener)
		throws RemoteException
	{
		MonitorProxyWrapper mpw = new MonitorProxyWrapper(this, listener);
		MonitorProxy mp = null;
		mp = proxy.createMonitor(mpw);
		mpw.initialize(mp);
		monitors.add(mp);

		return mpw;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.ValueUpdateable#getDefaultMonitor()
	 */
	public DynamicValueMonitor getDefaultMonitor()
	{
		if (defaultMonitor == null) {
			defaultMonitor = new MonitorProxyWrapper(this, dvListeners);

			try {
				MonitorProxy mp = proxy.createMonitor(defaultMonitor);
				defaultMonitor.initialize(mp);
				monitors.add(mp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return defaultMonitor;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.DataAccess#addDynamicValueListener(org.epics.css.dal.DynamicValueListener)
	 */
	public void addDynamicValueListener(DynamicValueListener l)
	{
		super.addDynamicValueListener(l);

		if (defaultMonitor == null) {
			getDefaultMonitor();
		}
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.DataAccess#removeDynamicValueListener(org.epics.css.dal.DynamicValueListener)
	 */
	public void removeDynamicValueListener(DynamicValueListener l)
	{
		super.removeDynamicValueListener(l);
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.ValueUpdateable#getLatestReceivedValueAsObject()
	 */
	public Object getLatestReceivedValueAsObject()
	{
		return (Object)lastValue;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.ValueUpdateable#getLatestValueChangeTimestamp()
	 */
	public Timestamp getLatestValueChangeTimestamp()
	{
		if (dvChangedListener.getLastValueChangedEvent() == null) {
			return null;
		}

		return dvChangedListener.getLastValueChangedEvent().getTimestamp();
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.ValueUpdateable#getLatestValueSuccess()
	 */
	public boolean getLatestValueSuccess()
	{
		if (dvChangedListener.getLastValueModificationEvent() == null) {
			return true;
		}

		DynamicValueCondition condition = dvChangedListener.getLastValueModificationEvent()
			.getCondition();

		if (condition.isAlarm()) {
			return false;
		}

		if (condition.isError()) {
			return false;
		}

		if (condition.isLinkNotAvailable()) {
			return false;
		}

		if (condition.isWarning()) {
			return false;
		}

		if (condition.isNormal()) {
			return true;
		}

		return false;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.ValueUpdateable#getLatestValueUpdateTimestamp()
	 */
	public Timestamp getLatestValueUpdateTimestamp()
	{
		return lastTimestamp;
	}

	/* (non-Javadoc)
	 * @see org.epics.css.dal.impl.DataAccessImpl#initialize(org.epics.css.dal.proxy.PropertyProxy)
	 */
	public void initialize(PropertyProxy<T> proxy, DirectoryProxy dirProxy)
	{
		super.initialize(proxy);
		this.directoryProxy = dirProxy;
		super.addDynamicValueListener(dvChangedListener);
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
		for (int i = 0; i < propertyListener.size(); i++) {
			((PropertyChangeListener)propertyListener.get(i)).propertyChange(e);
		}
	}
} /* __oOo__ */


/* __oOo__ */
