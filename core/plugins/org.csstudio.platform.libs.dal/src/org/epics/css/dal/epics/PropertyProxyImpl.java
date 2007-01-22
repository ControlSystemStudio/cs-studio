/*
 * Copyright (c) 2006 by Cosylab d.o.o.
 *
 * The full license specifying the redistribution, modification, usage and other
 * rights and obligations is included with the distribution of this project in
 * the file license.html. If the license is not included you may find a copy at
 * http://www.cosylab.com/legal/abeans_license.htm or may write to Cosylab, d.o.o.
 *
 * THIS SOFTWARE IS PROVIDED AS-IS WITHOUT WARRANTY OF ANY KIND, NOT EVEN THE
 * IMPLIED WARRANTY OF MERCHANTABILITY. THE AUTHOR OF THIS SOFTWARE, ASSUMES
 * _NO_ RESPONSIBILITY FOR ANY CONSEQUENCE RESULTING FROM THE USE, MODIFICATION,
 * OR REDISTRIBUTION OF THIS SOFTWARE.
 */

package org.epics.css.dal.epics;

import gov.aps.jca.CAException;
import gov.aps.jca.CAStatus;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.epics.css.dal.context.ConnectionState;
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
public class PropertyProxyImpl extends AbstractProxyImpl implements
		PropertyProxy<Object>, SyncPropertyProxy<Object>, DirectoryProxy,
		ConnectionListener, GetListener {


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

	protected CAJChannel channel;

	protected DynamicValueCondition condition;

	protected List<MonitorProxyImpl> monitors = new ArrayList<MonitorProxyImpl>(1);

	protected DynamicValueState dbrState = DynamicValueState.NORMAL;

	protected DynamicValueState connState = DynamicValueState.LINK_NOT_AVAILABLE;

	protected String condDesc;

	protected EPICSPlug plug;
	
	protected Map<String, Object> characteristics = new HashMap<String, Object>();
	
	protected DBRType type;
	
	protected Class dataType;

	protected int elementCount;
	
	/**
	 * Create a new proprty instance (channel).
	 * @param plug plug hosting this property.
	 * @param name name of the property.
	 * @param dataType java data type to work with.
	 * @param type channel type to work with.
	 * @throws RemoteException thrown on failure.
	 */
	public PropertyProxyImpl(EPICSPlug plug, String name, Class dataType, DBRType type) throws RemoteException {
		super(name);
		this.plug = plug;

		if (type.getValue() >= DBR_STS_String.TYPE.getValue())
			throw new IllegalArgumentException("type must be value-only type");
		
		this.type = type;
		this.dataType = dataType;
		
		// create channel
		try {
			this.channel = (CAJChannel)plug.getContext().createChannel(name, this);
		} catch (Throwable th) {
			throw new RemoteException(this, "Failed create CA channel", th);
		}

		condition = new DynamicValueCondition(EnumSet.of(DynamicValueState.LINK_NOT_AVAILABLE), 0, null);
	}

	/*
	 * @see org.epics.css.dal.proxy.AbstractProxyImpl#destroy()
	 */
	@Override
	public synchronized void destroy() {
		super.destroy();

		// destroy all monitors
		destroyMonitors();
		
		// destory channel
		channel.dispose();
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
	public Request getValueAsync(ResponseListener callback)
			throws DataExchangeException {
		GetRequest r = new GetRequest(this, callback);
		try {
			channel.get(type, channel.getElementCount(), r);
			plug.flushIO();
		} catch (Exception e) {
			r.addResponse(new ResponseImpl(this, r, null, "value", false, e,
					condition, null, true));
		}
		return r;
	}

	/*
	 * @see org.epics.css.dal.proxy.PropertyProxy#setValueAsync(T, org.epics.css.dal.ResponseListener)
	 */
	public Request setValueAsync(Object value, ResponseListener callback)
			throws DataExchangeException {
		PutRequest r = new PutRequest(this, callback);
		try {
			Object o = PlugUtilities.toDBRValue(value);
			channel.put(PlugUtilities.toDBRType(value.getClass()), Array.getLength(o), o, r);
			plug.flushIO();
		} catch (Exception e) {
			e.printStackTrace();
			r.addResponse(new ResponseImpl(this, r, null, "value", false, e,
					condition, null, true));
		}
		return r;
	}

	/*
	 * @see org.epics.css.dal.proxy.SyncPropertyProxy#getValueSync()
	 */
	public Object getValueSync() throws DataExchangeException {
		try {
			DBR dbr = channel.get(type, channel.getElementCount());
			plug.pendIO();
			
			// error check (sync get does not report an error)
			if (dbr == null)
				throw new DataExchangeException(this, "Get failed.");
			
			return PlugUtilities.toJavaValue(dbr, dataType);
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
			channel.put(PlugUtilities.toDBRType(value.getClass()), Array.getLength(o), o);
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
	public synchronized MonitorProxy createMonitor(ResponseListener callback)
			throws RemoteException {

		if (getConnectionState() == ConnectionState.DESTROYED)
			throw new RemoteException(this, "Proxy destroyed.");
		
		try {
			MonitorProxyImpl m = new MonitorProxyImpl(plug, this, callback);
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

	/**
	 * Fires new condition event.
	 */
	protected void fireCondition() {
		if (proxyListeners == null) {
			return;
		}

		ProxyListener[] l = (ProxyListener[]) proxyListeners.toArray();
		ProxyEvent<PropertyProxy> ev = new ProxyEvent<PropertyProxy>(this,
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
	public Class<? extends SimpleProperty> getPropertyType(String propertyName) {
		throw new UnsupportedOperationException("This is not device proxy.");
	}

	/**
	 * Characteristics async get listener.
	 * @see gov.aps.jca.event.GetListener#getCompleted(gov.aps.jca.event.GetEvent)
	 */
	public void getCompleted(GetEvent ev) {
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

			characteristics.put(SequencePropertyCharacteristics.C_SEQUENCE_LENGTH, new Integer(elementCount));
			
			characteristics.put(NumericPropertyCharacteristics.C_RESOLUTION, 0xFFFF);

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

				characteristics.put(NumericPropertyCharacteristics.C_MINIMUM, gr.getLowerCtrlLimit());
				characteristics.put(NumericPropertyCharacteristics.C_MAXIMUM, gr.getUpperCtrlLimit());

				characteristics.put(NumericPropertyCharacteristics.C_GRAPH_MIN, gr.getLowerDispLimit());
				characteristics.put(NumericPropertyCharacteristics.C_GRAPH_MAX, gr.getUpperDispLimit());
				
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
				
				characteristics.put(EnumPropertyCharacteristics.C_ENUM_VALUE, values);
			}

			characteristics.notifyAll();
		}
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
		synchronized (characteristics)
		{
			// characteristics not iniialized yet... wait
			if (characteristics.size() == 0)
			{
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
			
			return characteristics.get(characteristicName);
		}
	}

	
	
	
	/*
	 * @see DirectoryProxy#getCharacteristics(String[], ResponseListener)
	 */
	public Request getCharacteristics(String[] characteristics,
			ResponseListener callback) throws DataExchangeException {
		RequestImpl r = new RequestImpl(this, callback);
		for (int i = 0; i < characteristics.length; i++) {
			r.addResponse(new ResponseImpl(this, r,
					getCharacteristic(characteristics[i]), characteristics[i],
					true, null, condition, null, true));
		}
		return r;
	}
	
	/**
	 * Convert DBR to Java value.
	 * @param dbr DBR to convert.
	 * @return converted Java value.
	 */
	public final Object toJavaValue(DBR dbr) {
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

		if (se == Severity.NO_ALARM) {
			dbrState = DynamicValueState.NORMAL;
		} else if (se == Severity.MINOR_ALARM) {
			dbrState = DynamicValueState.WARNING;
		} else if (se == Severity.MAJOR_ALARM) {
			dbrState = DynamicValueState.ALARM;
		} else if (se == Severity.INVALID_ALARM) {
			dbrState = DynamicValueState.ERROR;
		}

		long timestamp = 0;
		if (dbr instanceof TIME) {
			timestamp = PlugUtilities.toUTC(((TIME) dbr).getTimeStamp());
		}

		checkStates(timestamp);
	}

	/*
	 * @see gov.aps.jca.event.ConnectionListener#connectionChanged(gov.aps.jca.event.ConnectionEvent)
	 */
	public synchronized void connectionChanged(ConnectionEvent event) {
		// Maps JCA states to DAL states
		if (channel.getConnectionState() == gov.aps.jca.Channel.ConnectionState.CLOSED) {
			setConnectionState(ConnectionState.DESTROYED);
		} else if (channel.getConnectionState() == gov.aps.jca.Channel.ConnectionState.CONNECTED) {
			initializeCharacteristics();
			setConnectionState(ConnectionState.CONNECTED);
		} else if (channel.getConnectionState() == gov.aps.jca.Channel.ConnectionState.DISCONNECTED) {
			setConnectionState(ConnectionState.CONNECTION_LOST);
		} else if (channel.getConnectionState() == gov.aps.jca.Channel.ConnectionState.NEVER_CONNECTED) {
			setConnectionState(ConnectionState.CONNECTING);
		}
	}

	/*
	 * @see org.epics.css.dal.proxy.AbstractProxyImpl#setConnectionState(org.epics.css.dal.context.ConnectionState)
	 */
	@Override
	protected void setConnectionState(ConnectionState s) {
		super.setConnectionState(s);
		if (s == ConnectionState.CONNECTED) {
			connState = DynamicValueState.NORMAL;
			checkStates(0);
		} else if (s == ConnectionState.DISCONNECTED) {
			connState = DynamicValueState.LINK_NOT_AVAILABLE;
			checkStates(0);
		} else if (s == ConnectionState.DESTROYED) {
			connState = DynamicValueState.LINK_NOT_AVAILABLE;
			checkStates(0);
		}
	}

	/**
	 * Check states.
	 * @param timestamp
	 */
	private void checkStates(long timestamp) {
		
		// noop check (state already reported)
		if (condition.containsStates(dbrState, connState)
				&& condDesc == condition.getDescription()) {
			return;
		}

		// check timestamp
		if (timestamp == 0) {
			timestamp = System.currentTimeMillis();
		}

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
}
