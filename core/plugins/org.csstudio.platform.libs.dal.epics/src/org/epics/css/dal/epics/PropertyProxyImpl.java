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

package org.epics.css.dal.epics;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatus;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.Channel;
import gov.aps.jca.TimeoutException;
import gov.aps.jca.dbr.CTRL;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_STS_String;
import gov.aps.jca.dbr.LABELS;
import gov.aps.jca.dbr.PRECISION;
import gov.aps.jca.dbr.STS;
import gov.aps.jca.dbr.Severity;
import gov.aps.jca.dbr.Status;
import gov.aps.jca.dbr.TIME;
import gov.aps.jca.event.ConnectionEvent;
import gov.aps.jca.event.ConnectionListener;
import gov.aps.jca.event.GetEvent;
import gov.aps.jca.event.GetListener;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.epics.css.dal.DataExchangeException;
import org.epics.css.dal.DynamicValueCondition;
import org.epics.css.dal.DynamicValueState;
import org.epics.css.dal.EnumPropertyCharacteristics;
import org.epics.css.dal.NumericPropertyCharacteristics;
import org.epics.css.dal.PatternPropertyCharacteristics;
import org.epics.css.dal.PropertyCharacteristics;
import org.epics.css.dal.RemoteException;
import org.epics.css.dal.Request;
import org.epics.css.dal.ResponseListener;
import org.epics.css.dal.SequencePropertyCharacteristics;
import org.epics.css.dal.SimpleProperty;
import org.epics.css.dal.Timestamp;
import org.epics.css.dal.context.ConnectionState;
import org.epics.css.dal.impl.PropertyUtilities;
import org.epics.css.dal.impl.RequestImpl;
import org.epics.css.dal.impl.ResponseImpl;
import org.epics.css.dal.proxy.AbstractProxyImpl;
import org.epics.css.dal.proxy.DirectoryProxy;
import org.epics.css.dal.proxy.MonitorProxy;
import org.epics.css.dal.proxy.PropertyProxy;
import org.epics.css.dal.proxy.ProxyEvent;
import org.epics.css.dal.proxy.ProxyListener;
import org.epics.css.dal.proxy.SyncPropertyProxy;

import com.cosylab.epics.caj.CAJChannel;
import com.cosylab.util.BitCondition;

/**
 * Simulations implementations of proxy.
 * 
 * @author ikriznar
 * 
 */
public class PropertyProxyImpl<T> extends AbstractProxyImpl implements
		PropertyProxy<T>, SyncPropertyProxy<T>, DirectoryProxy,
		ConnectionListener, GetListener {

	public static final String EPICS_WARNING_MAX = "HI";
	public static final String EPICS_WARNING_MIN = "LO";
	public static final String EPICS_ALARM_MAX = "HIHI";
	public static final String EPICS_ALARM_MIN = "LOLO";
	public static final String EPICS_MAX = "DRVH";
	public static final String EPICS_MIN = "DRVL";
	public static final String EPICS_OPR_MAX = "HOPR";
	public static final String EPICS_OPR_MIN = "LOPR";
	public static final String EPICS_UNITS = "EGU";
	
	
	/** C_CONDITION_WHEN_CLEARED characteristic for pattern channel */
	public static BitCondition[] patternWhenCleared = new BitCondition[] {
			BitCondition.UNUSED, BitCondition.UNUSED, BitCondition.UNUSED,
			BitCondition.UNUSED, BitCondition.UNUSED, BitCondition.UNUSED,
			BitCondition.UNUSED, BitCondition.UNUSED, BitCondition.UNUSED,
			BitCondition.UNUSED, BitCondition.UNUSED, BitCondition.UNUSED,
			BitCondition.UNUSED, BitCondition.UNUSED, BitCondition.UNUSED,
			BitCondition.UNUSED
		};

	/** C_CONDITION_WHEN_SET characteristic for pattern channel */
	public static BitCondition[] patternWhenSet = new BitCondition[] {
			BitCondition.OK, BitCondition.OK, BitCondition.OK, 
			BitCondition.OK, BitCondition.OK, BitCondition.OK, 
			BitCondition.OK, BitCondition.OK, BitCondition.OK,
			BitCondition.OK, BitCondition.OK, BitCondition.OK, 
			BitCondition.OK, BitCondition.OK, BitCondition.OK,
			BitCondition.OK
		};

	/** C_BIT_DESCRIPTION characteristic for pattern channel */
	public static String[] patternBitDescription = new String[] {
			"bit 0", "bit 1", "bit 2", "bit 3", "bit 4", "bit 5", "bit 6",
			"bit 7", "bit 8", "bit 9", "bit 10", "bit 11", "bit 12", "bit 13",
			"bit 14", "bit 15"
		};

	/** C_BIT_MASK characteristic for pattern channel */
	public static BitSet patternBitMask = new BitSet(16);
	{
		patternBitMask.set(0, 16);
	}

	protected Channel channel;

	protected DynamicValueCondition condition;

	protected List<MonitorProxyImpl> monitors = new ArrayList<MonitorProxyImpl>(1);

	protected DynamicValueState dbrState = DynamicValueState.NORMAL;

	protected DynamicValueState connState = DynamicValueState.LINK_NOT_AVAILABLE;

	protected String condDesc;

	protected EPICSPlug plug;
	
	protected Map<String, Object> characteristics = new CharacteristicsMap();
	
	protected DBRType type;
	
	protected Class<T> dataType;

	protected int elementCount;
	
	private ThreadPoolExecutor executor;
	
	private class CharacteristicsMap extends HashMap<String,Object>
	{
		private static final long serialVersionUID = 4768445313261300685L;

		public Object put (String key, Object value)
		{
			Object obj = super.put(key, value);
			if (value!=null) {
				if (value.equals(obj)) {
					return obj;
				}
			} else if (obj==null) {
				return obj;
			}
			fireCharacteristicsChanged(new PropertyChangeEvent(PropertyProxyImpl.this,key,obj,value));
			return obj;
		}
	}
	/**
	 * Create a new proprty instance (channel).
	 * @param plug plug hosting this property.
	 * @param name name of the property.
	 * @param dataType java data type to work with.
	 * @param type channel type to work with.
	 * @throws RemoteException thrown on failure.
	 */
	public PropertyProxyImpl(EPICSPlug plug, String name, Class<T> dataType, DBRType type) throws RemoteException {
		super(name);
		this.plug = plug;

		if (type.getValue() >= DBR_STS_String.TYPE.getValue())
			throw new IllegalArgumentException("type must be value-only type");
		
		synchronized (this) {
			this.type = type;
			this.dataType = dataType;
			condition = new DynamicValueCondition(EnumSet.of(DynamicValueState.LINK_NOT_AVAILABLE), 0, null);
			// create channel
			try {
				this.channel = plug.getContext().createChannel(name, this);
			} catch (Throwable th) {
				throw new RemoteException(this, "Failed create CA channel", th);
			}
		}

		
	}

	/*
	 * @see org.epics.css.dal.proxy.AbstractProxyImpl#destroy()
	 */
	@Override
	public synchronized void destroy() {
		super.destroy();

		// destroy all monitors
		destroyMonitors();
		
		try {
			channel.removeConnectionListener(this);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// destory channel
		channel.dispose();
		
		setConnectionState(ConnectionState.DESTROYED);
	}

	/**
	 * Add monitor.
	 * @param monitor monitor to be added.
	 */
	void addMonitor(MonitorProxyImpl monitor)
	{
		synchronized (monitors) {
			monitors.add(monitor);
		}
	}
	
	/**
	 * Remove monitor.
	 * @param monitor monitor to be removed.
	 */
	void removeMonitor(MonitorProxyImpl monitor)
	{
		synchronized (monitors) {
			monitors.remove(monitor);
		}
	}

	/**
	 * Destroy all monitors.
	 */
	private void destroyMonitors()
	{
		MonitorProxyImpl[] array;
		synchronized (monitors) {
			array = new MonitorProxyImpl[monitors.size()];
			monitors.toArray(array);
		}
		
		// destroy all
		for (int i = 0; i < array.length; i++)
			array[i].destroy();
	}
	
	/*
	 * @see org.epics.css.dal.proxy.PropertyProxy#getValueAsync(org.epics.css.dal.ResponseListener)
	 */
	public Request<T> getValueAsync(ResponseListener<T> callback)
			throws DataExchangeException {
		GetRequest<T> r = new GetRequest<T>(this, callback);
		try {
			channel.get(type, channel.getElementCount(), r);
			plug.flushIO();
		} catch (Exception e) {
			r.addResponse(new ResponseImpl<T>(this, r, null, "value", false, e,
					condition, null, true));
		}
		return r;
	}

	/*
	 * @see org.epics.css.dal.proxy.PropertyProxy#setValueAsync(T, org.epics.css.dal.ResponseListener)
	 */
	public Request<T> setValueAsync(T value, ResponseListener<T> callback)
			throws DataExchangeException {
		PutRequest<T> r = new PutRequest<T>(this, callback, value);
		try {
			Object o = PlugUtilities.toDBRValue(value);
			if (channel instanceof CAJChannel) 
				((CAJChannel) channel).put(PlugUtilities.toDBRType(value.getClass()), Array.getLength(o), o, r);
			else {
				// TODO workaround until Channel supports put(DBRType, int, Object, PutListener)
				PlugUtilities.put(channel, o, r);
			}
			plug.flushIO();
		} catch (Exception e) {
			r.addResponse(new ResponseImpl<T>(this, r, value, "value", false, e,
					condition, null, true));
		}
		return r;
	}

	/**
	 * Get listener implementation to implement sync. get.  
	 */
	private class GetListenerImpl implements GetListener {
		volatile GetEvent event = null;

		public synchronized void getCompleted(GetEvent ev) {
			event = ev;
			this.notifyAll();
		}
	}
	/**
	 * Connection listener implementation to implement sync. get.  
	 */
	private class ConnectionListenerImpl implements ConnectionListener {
		volatile ConnectionEvent event= null;

		public synchronized void connectionChanged(ConnectionEvent arg0) {
			event=arg0;
			this.notifyAll();
		}

	}
	
	/*
	 * @see org.epics.css.dal.proxy.SyncPropertyProxy#getValueSync()
	 */
	public T getValueSync() throws DataExchangeException {
		try 
		{

			GetListenerImpl listener = new GetListenerImpl();
	         synchronized (listener) {
				channel.get(type, channel.getElementCount(), listener);
				plug.flushIO();

				try {
					listener.wait((long) (plug.getTimeout() * 1000));
				} catch (InterruptedException e) {
					// noop
				}
			}

			final GetEvent event = listener.event;
			if (event == null)
				throw new TimeoutException("Get timeout.");

			// status check
			if (event.getStatus() != CAStatus.NORMAL)
				throw new CAStatusException(event.getStatus(), "Get failed.");

			// sanity check
			if (event.getDBR() == null)
				throw new DataExchangeException(this, "Get failed.");
			
			return PlugUtilities.toJavaValue(event.getDBR(), dataType);
		} catch (CAException e) {
			throw new DataExchangeException(this, "Get failed.", e);
		} catch (TimeoutException e) {
			throw new DataExchangeException(this, "Get failed with timeout.", e);
		}
	}

	/*
	 * @see org.epics.css.dal.proxy.SyncPropertyProxy#setValueSync(java.lang.Object)
	 */
	public void setValueSync(Object value) throws DataExchangeException {
		try {
			Object o = PlugUtilities.toDBRValue(value);
			if (channel instanceof CAJChannel) 
				((CAJChannel) channel).put(PlugUtilities.toDBRType(value.getClass()), Array.getLength(o), o);
			else {
				// TODO workaround until Channel supports put(DBRType, int, Object)
				PlugUtilities.put(channel, o);
			}
			// put does not affect on pendIO
			plug.flushIO();
		} catch (CAException e) {
			throw new DataExchangeException(this, "Set failed.", e);
		}
	}


	/*
	 * @see org.epics.css.dal.proxy.PropertyProxy#isSettable()
	 */
	public boolean isSettable() {
		return channel.getWriteAccess();
	}

	/*
	 * @see org.epics.css.dal.proxy.PropertyProxy#createMonitor(org.epics.css.dal.ResponseListener)
	 */
	public synchronized MonitorProxy createMonitor(ResponseListener<T> callback)
			throws RemoteException {

		if (getConnectionState() == ConnectionState.DESTROYED)
			throw new RemoteException(this, "Proxy destroyed.");
		
		try {
			MonitorProxyImpl<T> m = new MonitorProxyImpl<T>(plug, this, callback);
			monitors.add(m);
			return m;
		} catch (Throwable th) {
			throw new RemoteException(this, "Failed to create new monitor.", th);
		}
	}

	/*
	 * @see org.epics.css.dal.proxy.PropertyProxy#getCondition()
	 */
	public DynamicValueCondition getCondition() {
		return condition;
	}

	/**
	 * Intended for only within plug.
	 * 
	 * @param s new condition state.
	 */
	protected void setCondition(DynamicValueCondition s) {
		condition = s;
		fireCondition();
	}

	
	protected void fireCharacteristicsChanged(PropertyChangeEvent ev)
	{
		ProxyListener[] l = (ProxyListener[])proxyListeners.toArray();

		for (int i = 0; i < l.length; i++) {
			try {
				l[i].characteristicsChange(ev);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Fires new condition event.
	 */
	protected void fireCondition() {
		if (proxyListeners == null) {
			return;
		}

		ProxyListener<T>[] l = (ProxyListener<T>[]) proxyListeners.toArray();
		ProxyEvent<PropertyProxy<T>> ev = new ProxyEvent<PropertyProxy<T>>(this,
				condition, connectionState, null);

		for (int i = 0; i < l.length; i++) {
			try {
				l[i].dynamicValueConditionChange(ev);
			} catch (Throwable th) {
				th.printStackTrace();
			}
		}
	}

	/*
	 * @see DirectoryProxy#getCommandNames()
	 */
	public String[] getCommandNames() throws DataExchangeException {
		throw new UnsupportedOperationException("Property does not support commands.");
	}

	/*
	 * @see DirectoryProxy#getCommandParameterTypes(String)
	 */
	public Class[] getCommandParameterTypes(String commandName) throws DataExchangeException {
		throw new UnsupportedOperationException("Property does not support commands.");
	}

	/*
	 * @see org.epics.css.dal.proxy.DirectoryProxy#getPropertyNames()
	 */
	public String[] getPropertyNames() {
		throw new UnsupportedOperationException("This is not device proxy.");
	}

	/*
	 * @see org.epics.css.dal.proxy.DirectoryProxy#getPropertyType(java.lang.String)
	 */
	public Class<? extends SimpleProperty<?>> getPropertyType(String propertyName) {
		throw new UnsupportedOperationException("This is not device proxy.");
	}

	/**
	 * Characteristics async get listener.
	 * @see gov.aps.jca.event.GetListener#getCompleted(gov.aps.jca.event.GetEvent)
	 */
	public  void getCompleted(GetEvent ev) {
		
		if (ev.getStatus() == CAStatus.NORMAL)
			createCharacteristics(ev.getDBR());
		else
			createDefaultCharacteristics();
			
	}

	/**
	 * Creates default characteristics.
	 */
	protected void createDefaultCharacteristics() {
		createDefaultCharacteristics(true);
	}

	/**
	 * Creates default characteristics.
	 */
	protected void createDefaultCharacteristics(boolean notify) {
		synchronized (characteristics) {
			characteristics.put(PropertyCharacteristics.C_DESCRIPTION, "EPICS Channel '" + name + "'");
			characteristics.put(PropertyCharacteristics.C_DISPLAY_NAME, name);
			characteristics.put(PropertyCharacteristics.C_POSITION, new Double(0));
			characteristics.put(PropertyCharacteristics.C_PROPERTY_TYPE, "property");
			characteristics.put(NumericPropertyCharacteristics.C_SCALE_TYPE, "linear");

			characteristics.put(SequencePropertyCharacteristics.C_SEQUENCE_LENGTH, new Integer(elementCount));

			if (channel!=null) {
				DBRType ft= channel.getFieldType();
				characteristics.put("fieldType",ft);
				
				if (ft.isENUM()) {
				characteristics.put(NumericPropertyCharacteristics.C_RESOLUTION, 0xF);
				} else if (ft.isBYTE()) {
					characteristics.put(NumericPropertyCharacteristics.C_RESOLUTION, 0x8);
				} else if (ft.isSHORT()) {
					characteristics.put(NumericPropertyCharacteristics.C_RESOLUTION, 0xFF);
				} else {
					characteristics.put(NumericPropertyCharacteristics.C_RESOLUTION, 0xFFFF);
				}
				
			} else {
				characteristics.put(NumericPropertyCharacteristics.C_RESOLUTION, 0xFFFF);
			}


			//characteristics.put(NumericPropertyCharacteristics.C_SCALE_TYPE, );

			characteristics.put(PatternPropertyCharacteristics.C_CONDITION_WHEN_SET, patternWhenSet);
			characteristics.put(PatternPropertyCharacteristics.C_CONDITION_WHEN_CLEARED, patternWhenCleared);

			characteristics.put(PatternPropertyCharacteristics.C_BIT_MASK, patternBitMask);
			characteristics.put(PatternPropertyCharacteristics.C_BIT_DESCRIPTIONS, patternBitDescription);
			
			if (notify)
				characteristics.notifyAll();
		}
	}

	/**
	 * Creates characteristics from given DBR.
	 * @param dbr DBR containign characteristics.
	 */
	protected void createCharacteristics(DBR dbr)
	{
		synchronized (characteristics) {
			createDefaultCharacteristics(false);
	

			if (dbr.isCTRL())
			{
				CTRL gr = (CTRL)dbr;
				characteristics.put(NumericPropertyCharacteristics.C_UNITS, gr.getUnits());
				characteristics.put(EPICS_UNITS, gr.getUnits());
				
				// Integer -> Long needed here
				if (dbr.isINT())
				{
					characteristics.put(NumericPropertyCharacteristics.C_MINIMUM, new Long(gr.getLowerCtrlLimit().longValue()));
					characteristics.put(NumericPropertyCharacteristics.C_MAXIMUM, new Long(gr.getUpperCtrlLimit().longValue()));

					characteristics.put(NumericPropertyCharacteristics.C_GRAPH_MIN, new Long(gr.getLowerDispLimit().longValue()));
					characteristics.put(NumericPropertyCharacteristics.C_GRAPH_MAX, new Long(gr.getUpperDispLimit().longValue()));
					
					characteristics.put(NumericPropertyCharacteristics.C_WARNING_MAX, new Long(gr.getUpperWarningLimit().longValue()));
					characteristics.put(NumericPropertyCharacteristics.C_WARNING_MIN, new Long(gr.getLowerWarningLimit().longValue()));
					
					characteristics.put(NumericPropertyCharacteristics.C_ALARM_MAX, new Long(gr.getUpperAlarmLimit().longValue()));
					characteristics.put(NumericPropertyCharacteristics.C_ALARM_MIN, new Long(gr.getLowerAlarmLimit().longValue()));
										
				}
				else
				{
					characteristics.put(NumericPropertyCharacteristics.C_MINIMUM, gr.getLowerCtrlLimit());
					characteristics.put(NumericPropertyCharacteristics.C_MAXIMUM, gr.getUpperCtrlLimit());

					characteristics.put(NumericPropertyCharacteristics.C_GRAPH_MIN, gr.getLowerDispLimit());
					characteristics.put(NumericPropertyCharacteristics.C_GRAPH_MAX, gr.getUpperDispLimit());
					
					characteristics.put(NumericPropertyCharacteristics.C_WARNING_MAX, gr.getUpperWarningLimit());
					characteristics.put(NumericPropertyCharacteristics.C_WARNING_MIN, gr.getLowerWarningLimit());
					
					characteristics.put(NumericPropertyCharacteristics.C_ALARM_MAX, gr.getUpperAlarmLimit());
					characteristics.put(NumericPropertyCharacteristics.C_ALARM_MIN, gr.getLowerAlarmLimit());
				}
				
				characteristics.put(EPICS_MIN, characteristics.get(NumericPropertyCharacteristics.C_MINIMUM));
				characteristics.put(EPICS_MAX, characteristics.get(NumericPropertyCharacteristics.C_MAXIMUM));
				
				characteristics.put(EPICS_OPR_MIN, characteristics.get(NumericPropertyCharacteristics.C_GRAPH_MIN));
				characteristics.put(EPICS_OPR_MAX, characteristics.get(NumericPropertyCharacteristics.C_GRAPH_MAX));
				
				characteristics.put(EPICS_WARNING_MAX, characteristics.get(NumericPropertyCharacteristics.C_WARNING_MAX));
				characteristics.put(EPICS_WARNING_MIN, characteristics.get(NumericPropertyCharacteristics.C_WARNING_MIN));
				
				characteristics.put(EPICS_ALARM_MAX, characteristics.get(NumericPropertyCharacteristics.C_ALARM_MAX));
				characteristics.put(EPICS_ALARM_MIN, characteristics.get(NumericPropertyCharacteristics.C_ALARM_MIN));
				
			} else {
				characteristics.put(NumericPropertyCharacteristics.C_UNITS, "N/A");
			}
			
			if (dbr.isPRECSION())
			{
				short precision = ((PRECISION)dbr).getPrecision();
				characteristics.put(NumericPropertyCharacteristics.C_FORMAT, "%."  + precision + "f");
			}
			else if (dbr.isSTRING())
				characteristics.put(NumericPropertyCharacteristics.C_FORMAT, "%s");
			else
				characteristics.put(NumericPropertyCharacteristics.C_FORMAT, "%d");
			
			if (dbr.isLABELS())
			{
				String[] labels = ((LABELS)dbr).getLabels();
				characteristics.put(EnumPropertyCharacteristics.C_ENUM_DESCRIPTIONS, labels);

				characteristics.put(PatternPropertyCharacteristics.C_BIT_DESCRIPTIONS, labels);

				// create array of values (Long values)
				Object[] values = new Object[labels.length];
				for (int i = 0; i < values.length; i++)
					values[i] = new Long(i);
				
				characteristics.put(EnumPropertyCharacteristics.C_ENUM_VALUES, values);

			}
			
			createSpecificCharacteristics(dbr);

			characteristics.notifyAll();
		}
	}
	
	protected void createSpecificCharacteristics(DBR dbr) {
		// specific prosy implementation may override this and provide own characteristic initialization
	}

	/**
	 * Initiate characteristics search.
	 */
	protected void initializeCharacteristics()
	{
		if (channel.getConnectionState() != Channel.CONNECTED)
			return;
		
		elementCount = channel.getElementCount();
		
		// convert to CTRL value
		final int CTRL_OFFSET = 28;
		DBRType ctrlType = DBRType.forValue(type.getValue() + CTRL_OFFSET);
		characteristicsRequestTimestamp = System.currentTimeMillis();		
		try {
			channel.get(ctrlType, 1, this);
			plug.flushIO();
		} catch (Throwable th) {
			createDefaultCharacteristics();
		}
	}
	
	protected static final long CHARACTERISTICS_TIMEOUT = 5000;
	protected long characteristicsRequestTimestamp = System.currentTimeMillis();
	
	/*
	 * @see DirectoryProxy#getCharacteristicNames()
	 */
	public String[] getCharacteristicNames() throws DataExchangeException {
		synchronized (characteristics)
		{
			// characteristics not iniialized yet... wait
			if (characteristics.size() == 0)
			{
				initializeCharacteristics();
				long timeToWait = CHARACTERISTICS_TIMEOUT - (System.currentTimeMillis() - characteristicsRequestTimestamp);
				if (timeToWait > 0)
				{
					try {
						characteristics.wait(timeToWait);
					} catch (InterruptedException e) {
						// noop
					}
				}
				
				// nothing yet... create default ones
				if (characteristics.size() == 0)
					createDefaultCharacteristics();
			}
			
			// get names
			String[] names = new String[characteristics.size()];
			characteristics.keySet().toArray(names);
			return names;
		}
	}

	/*
	 * @see DirectoryProxy#getCharacteristic(String)
	 */
	public Object getCharacteristic(String characteristicName)
			throws DataExchangeException {
		if (characteristicName == null)
			return null;
		synchronized (characteristics)
		{
			// characteristics not iniialized yet... wait
			if (characteristics.size() == 0)
			{
				initializeCharacteristics();
				long timeToWait = CHARACTERISTICS_TIMEOUT - (System.currentTimeMillis() - characteristicsRequestTimestamp);
				if (timeToWait > 0)
				{
					try {
						characteristics.wait(timeToWait);
					} catch (InterruptedException e) {
						// noop
					}
				}
				
				// nothing yet... create default ones
				if (characteristics.size() == 0)
					createDefaultCharacteristics();
			}
			
			Object ch = characteristics.get(characteristicName);
			if (ch == null && characteristicName.length() <= 4) {
				ch = getCharacteristicFromField(characteristicName);
				characteristics.put(characteristicName, ch);
			}
			return PropertyUtilities.verifyCharacteristic(this, characteristicName, ch);
		}
	}
	
	private Object getCharacteristicFromField(String characteristicName) {
		if (channel.getConnectionState() != Channel.CONNECTED)
			return null;
		
		GetListenerImpl listener = new GetListenerImpl();
        synchronized (listener) {
        	try {
        		CAJChannel ch=null;
        		ConnectionListenerImpl conn= new ConnectionListenerImpl();
        		synchronized (conn) {
    				ch = (CAJChannel)plug.getContext().createChannel(name+"."+characteristicName,conn);
    				try {
    					conn.wait((long) (plug.getTimeout() * 1000));
    				} catch (InterruptedException e) {
    					// noop
    				}
				}
				ch.get(1, listener);
				plug.flushIO();
				try {
					listener.wait((long) (plug.getTimeout() * 1000));
				} catch (InterruptedException e) {
					// noop
				}
				ch.dispose();
			} catch (IllegalStateException e1) {
				e1.printStackTrace();
			} catch (CAException e1) {
				e1.printStackTrace();
			}			
		}

		final GetEvent event = listener.event;
		if (event == null || event.getStatus() != CAStatus.NORMAL || event.getDBR() == null) {
			return null;
		}		
				
		return event.getDBR().getValue();
	}
	
	
	/*
	 * @see DirectoryProxy#getCharacteristics(String[], ResponseListener)
	 */
	public Request<? extends Object> getCharacteristics(final String[] characteristics,
			ResponseListener<? extends Object> callback) throws DataExchangeException {
		final RequestImpl<Object> r = new RequestImpl<Object>(this, (ResponseListener<Object>) callback);
		Runnable getCharsAsync = new Runnable () {

			public void run() {
				for (int i = 0; i < characteristics.length; i++) {
					Object value;
					try {
						value= PropertyUtilities.verifyCharacteristic(PropertyProxyImpl.this, characteristics[i], getCharacteristic(characteristics[i]));
						r.addResponse(new ResponseImpl<Object>(PropertyProxyImpl.this, r,	value, characteristics[i],
								value != null, null, condition, null, true));

					} catch (DataExchangeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		};
		execute(getCharsAsync);
		
		return r;
	}
	
	/**
	 * Convert DBR to Java value.
	 * @param dbr DBR to convert.
	 * @return converted Java value.
	 */
	public final T toJavaValue(DBR dbr) {
		return PlugUtilities.toJavaValue(dbr, dataType);
	}

	/**
	 * Get CA channel.
	 * @return channel.
	 */
	protected Channel getChannel() {
		return channel;
	}

	/**
	 * Get DBR type (used to query data).
	 * @return DBR type.
	 */
	protected DBRType getType() {
		return type;
	}

	/**
	 * Update conditions.
	 * @param dbr status DBR.
	 */
	void updateConditionWithDBRStatus(STS dbr) {

		Status st = dbr.getStatus();
		Severity se = dbr.getSeverity();

		condDesc = st.getName();
		
		DynamicValueState dbrState = this.dbrState;
		
		if (se == Severity.NO_ALARM) {
			dbrState = DynamicValueState.NORMAL;
		} else if (se == Severity.MINOR_ALARM) {
			dbrState = DynamicValueState.WARNING;
		} else if (se == Severity.MAJOR_ALARM) {
			dbrState = DynamicValueState.ALARM;
		} else if (se == Severity.INVALID_ALARM) {
			dbrState = DynamicValueState.ERROR;
		}

		Timestamp timestamp = null;
		if (dbr instanceof TIME) {
			timestamp = PlugUtilities.convertTimestamp(((TIME) dbr).getTimeStamp());
		}

		checkStates(dbrState, connState, timestamp);
	}

	/*
	 * @see gov.aps.jca.event.ConnectionListener#connectionChanged(gov.aps.jca.event.ConnectionEvent)
	 */
	public synchronized void connectionChanged(ConnectionEvent event) {
		Runnable connChangedRunnable = new Runnable () {

			public void run() {
//				 Maps JCA states to DAL states
				gov.aps.jca.Channel.ConnectionState c= channel.getConnectionState();
				if (c==null) {
					System.err.println(PropertyProxyImpl.class.getName()+": JCA connection state for "+channel.getName()+" is NULL, connection event ignored!");
					return;
				} 
				if (c == gov.aps.jca.Channel.ConnectionState.CLOSED) {
					setConnectionState(ConnectionState.DESTROYED);
				} else if (c == gov.aps.jca.Channel.ConnectionState.CONNECTED) {
					boolean init= getConnectionState()==ConnectionState.CONNECTION_LOST;
					setConnectionState(ConnectionState.CONNECTED);
					if (init) {
						initializeCharacteristics();
					}
				} else if (c == gov.aps.jca.Channel.ConnectionState.DISCONNECTED) {
					setConnectionState(ConnectionState.CONNECTION_LOST);
				} else if (c == gov.aps.jca.Channel.ConnectionState.NEVER_CONNECTED) {
					setConnectionState(ConnectionState.CONNECTING);
				}		
			}
			
		};
		
		if (getPlug().getMaxThreads() == 0) {
			execute(connChangedRunnable);
		}
		else if (!getExecutor().isShutdown()) {
			execute(connChangedRunnable);
		}
	}

	/*
	 * @see org.epics.css.dal.proxy.AbstractProxyImpl#setConnectionState(org.epics.css.dal.context.ConnectionState)
	 */
	@Override
	protected void setConnectionState(ConnectionState s) {
		super.setConnectionState(s);
		DynamicValueState connState = this.connState;
		if (s == ConnectionState.CONNECTED) {
			connState = DynamicValueState.NORMAL;
			checkStates(dbrState, connState, null);
		} else if (s == ConnectionState.DISCONNECTED) {
			connState = DynamicValueState.LINK_NOT_AVAILABLE;
			checkStates(dbrState, connState, null);
		} else if (s == ConnectionState.CONNECTION_LOST) {
			connState = DynamicValueState.LINK_NOT_AVAILABLE;
			checkStates(dbrState, connState, null);
		} else if (s == ConnectionState.DESTROYED) {
			connState = DynamicValueState.LINK_NOT_AVAILABLE;
			checkStates(dbrState, connState, null);
			if (getPlug().getMaxThreads() != 0 && !getPlug().isUseCommonExecutor()) {
				getExecutor().shutdown();
		        try {
		            if (!getExecutor().awaitTermination(1, TimeUnit.SECONDS))
		                getExecutor().shutdownNow();
		        } catch (InterruptedException ie) {  }
			}
		}
	}

	/**
	 * Check states.
	 * @param timestamp
	 */
	private void checkStates(DynamicValueState dbrState, DynamicValueState connState, Timestamp timestamp) {
		
		// noop check (state already reported)
		if (this.dbrState == dbrState && this.connState == connState
				&& equal(condDesc, condition.getDescription())) {
			return;
		}

		this.dbrState = dbrState;
		this.connState = connState;
		
		EnumSet<DynamicValueState> en = null;

		if (dbrState == connState && connState == DynamicValueState.NORMAL) {
			en = EnumSet.of(DynamicValueState.NORMAL);
		} else {
			if (dbrState != DynamicValueState.NORMAL) {
				en = EnumSet.of(dbrState);
			}
			if (connState != DynamicValueState.NORMAL) {
				if (en == null) {
					en = EnumSet.of(connState);
				} else {
					en.add(connState);
				}
			}
		}

		setCondition(new DynamicValueCondition(en, timestamp, condDesc));

	}

	/*
	 * @see org.epics.css.dal.proxy.DirectoryProxy#refresh()
	 */
	public void refresh() {
		// TODO should this be sync, since initializCharacetistics is async
		initializeCharacteristics();
	}

	/**
	 * Get <code>EPICSPlug</code> instance.
	 * @return <code>EPICSPlug</code> instance.
	 */
	public EPICSPlug getPlug()
	{
		return plug;
	}

	/**
	 * Executes a <code>Runnable</code>. The <code>Runnable</code> is run in the same thread if
	 * {@link EPICSPlug#PROPERTY_MAX_THREADS} is equal to 0. Otherwise it is delegated to the
	 * <code>Executor</code> ({@link #getExecutor()}).
	 * 
	 * @param r the <code>Runnable</code> to run
	 */
	protected void execute(Runnable r) {
		if (getPlug().getMaxThreads() > 0) {
			getExecutor().execute(r);
		}
		else {
			r.run();
		}
	}
	
	/**
	 * This method should be called only if {@link EPICSPlug#PROPERTY_MAX_THREADS} is
	 * a number greater than 0. 
	 * <p>
	 * If {@link EPICSPlug#PROPERTY_USE_COMMON_EXECUTOR} is set to <code>true</code> the 
	 * <code>Executor</code> from {@link EPICSPlug#getExecutor()} is returned. Otherwise
	 * a new </code>ThreadPoolExecutor</code> is created.
	 * </p>
	 * 
	 * @return the executor
	 * @throws IllegalStateException if maximum number of threads defined by {@link EPICSPlug}
	 * is equal to 0.
	 */
	private ThreadPoolExecutor getExecutor() {
		if (executor==null) {
			synchronized (this) {
				if (getPlug().getMaxThreads() == 0) throw new IllegalStateException("Maximum number of threads must be greater than 0.");
				if (getPlug().isUseCommonExecutor()) executor = getPlug().getExecutor();
				else {
					executor= new ThreadPoolExecutor(getPlug().getCoreThreads(),getPlug().getMaxThreads(),Long.MAX_VALUE, TimeUnit.NANOSECONDS,
			                new ArrayBlockingQueue<Runnable>(getPlug().getMaxThreads()));
					executor.prestartAllCoreThreads();
				}				
			}
		}
		return executor;
	}
	
	private static boolean equal(String s1, String s2) {
		if (s1 == null || s2 == null) {
			if (s1 == s2) {
				return true;
			}
			return false;
		}
		return s1.equals(s2);
	}
	
}
