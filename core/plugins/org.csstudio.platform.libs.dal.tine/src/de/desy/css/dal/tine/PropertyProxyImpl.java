package de.desy.css.dal.tine;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.epics.css.dal.DataExchangeException;
import org.epics.css.dal.DynamicValueCondition;
import org.epics.css.dal.DynamicValueState;
import org.epics.css.dal.RemoteException;
import org.epics.css.dal.Request;
import org.epics.css.dal.ResponseListener;
import org.epics.css.dal.SimpleProperty;
import org.epics.css.dal.Timestamp;
import org.epics.css.dal.context.ConnectionState;
import org.epics.css.dal.impl.RequestImpl;
import org.epics.css.dal.impl.ResponseImpl;
import org.epics.css.dal.proxy.AbstractProxyImpl;
import org.epics.css.dal.proxy.DirectoryProxy;
import org.epics.css.dal.proxy.MonitorProxy;
import org.epics.css.dal.proxy.PropertyProxy;
import org.epics.css.dal.proxy.Proxy;
import org.epics.css.dal.proxy.ProxyEvent;
import org.epics.css.dal.proxy.ProxyListener;
import org.epics.css.dal.proxy.SyncPropertyProxy;

import de.desy.tine.client.TLink;
import de.desy.tine.dataUtils.TDataType;
import de.desy.tine.definitions.TAccess;
import de.desy.tine.definitions.TMode;

/**
 * Implementation of PropertyProxy for Tine DAL plugin.
 * 
 * @author Jaka Bobnar, Cosylab
 * 
 * @param <T>
 */
public abstract class PropertyProxyImpl<T> extends AbstractProxyImpl implements
		PropertyProxy<T>, SyncPropertyProxy<T>, DirectoryProxy {

	private Map<String, Object> characteristics;
	protected DynamicValueCondition condition = new DynamicValueCondition(
			EnumSet.of(DynamicValueState.NORMAL), System.currentTimeMillis(),
			null);
	private PropertyNameDissector dissector;
	private String deviceName;
	private int timeOut = 1000;
	private Set<MonitorProxyImpl> monitors = new HashSet<MonitorProxyImpl>(3);

	public PropertyProxyImpl(String name) {
		super(name);
		dissector = new PropertyNameDissector(name);
		deviceName = PropertyProxyUtilities.makeDeviceName(dissector);
		setConnectionState(ConnectionState.CONNECTED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.epics.css.dal.proxy.PropertyProxy#createMonitor(org.epics.css.dal.ResponseListener)
	 */
	public synchronized MonitorProxy createMonitor(ResponseListener callback)
			throws RemoteException {
		MonitorProxyImpl m = new MonitorProxyImpl(this, callback);
		monitors.add(m);
		m.initialize(getDataObject());
		return m;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.epics.css.dal.proxy.PropertyProxy#getCondition()
	 */
	public DynamicValueCondition getCondition() {
		return condition;
	}

	/**
	 * Intended for only within plug.
	 * 
	 * @param s
	 *            new condition state.
	 */
	protected synchronized void setCondition(DynamicValueCondition s) {
		if (condition.areStatesEqual(s))
			return;
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

		ProxyEvent<PropertyProxy> pe = new ProxyEvent<PropertyProxy>(this,
				condition, connectionState, null);
		for (int i = 0; i < l.length; i++) {
			try {
				l[i].dynamicValueConditionChange(pe);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.epics.css.dal.proxy.AbstractProxyImpl#addProxyListener(org.epics.css.dal.proxy.ProxyListener)
	 */
	public void addProxyListener(ProxyListener l) {
		super.addProxyListener(l);
		l.connectionStateChange(new ProxyEvent<Proxy>(this, condition,
				connectionState, null));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.epics.css.dal.proxy.PropertyProxy#getValueAsync(org.epics.css.dal.ResponseListener)
	 */
	public Request getValueAsync(ResponseListener callback)
			throws DataExchangeException {

		if (getConnectionState() != ConnectionState.CONNECTED)
			return null;
		try {
			Object data = getDataObject();
			TDataType dout = PropertyProxyUtilities.toTDataType(data,
					PropertyProxyUtilities.getObjectSize(data), true);
			// TDataType din = new TDataType();
			short access = TAccess.CA_READ;
			TLink tLink = new TLink(deviceName, dissector.getDeviceProperty(),
					dout, dout, access); // fixed by C1 WPS on August 17 2007
			short mode = TMode.CM_SINGLE;
			int handle = 0;
			TINERequestImpl request = new TINERequestImpl(this, callback, tLink);
			handle = tLink.attach(mode, request, timeOut);
			if (handle < 0) {
				tLink.close();
				throw new ConnectionFailed(tLink.getError(-handle));
			}
			return request;
		} catch (Exception e) {
			DynamicValueCondition condition = new DynamicValueCondition(EnumSet
					.of(DynamicValueState.ERROR), System.currentTimeMillis(),
					"Error initializing proxy");
			setCondition(condition);
			throw new DataExchangeException(this,
					"Exception on async getting value '" + name + "'.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.epics.css.dal.proxy.PropertyProxy#setValueAsync(java.lang.Object,
	 *      org.epics.css.dal.ResponseListener)
	 */
	public Request setValueAsync(T value, ResponseListener callback)
			throws DataExchangeException {
		// if not connected async call makes a mess and monitors needs to be
		// reset
		if (getConnectionState() != ConnectionState.CONNECTED || !isSettable())
			return null;
		try {
			Object data = setDataToObject(value);
			TDataType din = PropertyProxyUtilities.toTDataType(data,
					PropertyProxyUtilities.getObjectSize(data), true);
			TDataType dout = new TDataType();
			short access = TAccess.CA_READ | TAccess.CA_WRITE;
			TLink tLink = new TLink(deviceName, dissector.getDeviceProperty(),
					dout, din, access);
			short mode = TMode.CM_SINGLE;
			int handle = 0;
			TINERequestImpl request = new TINERequestImpl(this, callback, tLink);
			handle = tLink.attach(mode, request, timeOut);
			if (handle < 0) {
				tLink.close();
				throw new ConnectionFailed(tLink.getError(-handle));
			}
			return request;
		} catch (Exception e) {
			DynamicValueCondition condition = new DynamicValueCondition(EnumSet
					.of(DynamicValueState.ERROR), System.currentTimeMillis(),
					"Error initializing proxy");
			setCondition(condition);
			throw new DataExchangeException(this,
					"Exception on async setting value '" + name + "'.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.epics.css.dal.proxy.SyncPropertyProxy#getValueSync()
	 */
	public T getValueSync() {
		TDataType dout = PropertyProxyUtilities.toTDataType(getDataObject(),
				PropertyProxyUtilities.getObjectSize(getDataObject()), true);
		TLink tl = new TLink(deviceName, dissector.getDeviceProperty(), dout,
				dout, TAccess.CA_READ); // fixed by C1 WPS on August 17 2007
		try {
			int statusCode = tl.execute(timeOut);
			if (statusCode > 0)
				setCondition(new DynamicValueCondition(EnumSet
						.of(DynamicValueState.ERROR), System
						.currentTimeMillis(), tl.getLastError()));
			else
				setCondition(new DynamicValueCondition(EnumSet
						.of(DynamicValueState.NORMAL), System
						.currentTimeMillis(), null));
			dout = tl.getOutputDataObject();
			tl.cancel();
		} catch (Exception e) {
			setCondition(new DynamicValueCondition(EnumSet
					.of(DynamicValueState.ERROR), System.currentTimeMillis(),
					tl.getLastError()));
		}
		return extractData(dout);
	}

	/**
	 * Extracts data of type T from the TDataType and returns the data.
	 * 
	 * @param out
	 * @return
	 */
	protected abstract T extractData(TDataType out);

	/**
	 * Sets the data to an object which can be sent to Tine. This object is
	 * usually an array of certain type.
	 * 
	 * @param data
	 * @return
	 */
	protected abstract Object setDataToObject(T data);

	/**
	 * Returns the data object which receives data from Tine. This object is
	 * usually an array. This object is encapsulated to TDataType and sent to
	 * TLink.
	 * 
	 * @return
	 */
	protected abstract Object getDataObject();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.epics.css.dal.proxy.SyncPropertyProxy#setValueSync(java.lang.Object)
	 */
	public void setValueSync(T value) {
		if (!isSettable())
			return;
		Object data = setDataToObject(value);
		TDataType din = PropertyProxyUtilities.toTDataType(data,
				PropertyProxyUtilities.getObjectSize(data), true);

		TLink tl = new TLink(deviceName, dissector.getDeviceProperty(),
				new TDataType(), din,
				(short) (TAccess.CA_READ | TAccess.CA_WRITE));

		try {
			int statusCode = tl.execute(timeOut);
			if (statusCode > 0)
				setCondition(new DynamicValueCondition(EnumSet
						.of(DynamicValueState.ERROR), System
						.currentTimeMillis(), tl.getLastError()));
			else
				setCondition(new DynamicValueCondition(EnumSet
						.of(DynamicValueState.NORMAL), System
						.currentTimeMillis(), null));
			tl.cancel();
		} catch (Exception e) {
			setCondition(new DynamicValueCondition(EnumSet
					.of(DynamicValueState.ERROR), System.currentTimeMillis(),
					tl.getLastError()));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.epics.css.dal.proxy.PropertyProxy#isSettable()
	 */
	public boolean isSettable() {
		return (Boolean) getCharacteristic("editable");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.epics.css.dal.proxy.DirectoryProxy#getCharacteristic(java.lang.String)
	 */
	public Object getCharacteristic(String characteristicName) {

		return getCharacteristics().get(characteristicName);

	}

	private synchronized Map<String, Object> getCharacteristics() {
		if (characteristics == null) {
			try {
				characteristics = PropertyProxyUtilities.getCharacteristics(
						dissector, getNumericType());
			} catch (ConnectionFailed e) {
				setCondition(new DynamicValueCondition(EnumSet
						.of(DynamicValueState.ERROR), System
						.currentTimeMillis(), null));
				e.printStackTrace();
				return new HashMap<String, Object>();
			}
		}
		return characteristics;
	}

	/**
	 * Returns number type of value. Eg if it si array of doubles, then double
	 * is return.
	 * 
	 * @return number type of value
	 */
	protected abstract Class getNumericType();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.epics.css.dal.proxy.DirectoryProxy#getCharacteristicNames()
	 */
	public synchronized String[] getCharacteristicNames() {
		Set<String> set = getCharacteristics().keySet();
		String[] names = new String[set.size()];
		return set.toArray(names);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.epics.css.dal.proxy.DirectoryProxy#getCharacteristics(java.lang.String[],
	 *      org.epics.css.dal.ResponseListener)
	 */
	public Request getCharacteristics(String[] characteristics,
			ResponseListener callback) {
		RequestImpl r = new RequestImpl(this, callback);
		for (int i = 0; i < characteristics.length; i++) {
			r.addResponse(new ResponseImpl(this, r, getCharacteristics().get(
					characteristics[i]), characteristics[i], true, null,
					condition, new Timestamp(), true));
		}
		return r;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.epics.css.dal.proxy.DirectoryProxy#getCommandNames()
	 */
	public String[] getCommandNames() {
		// property does not support commands
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.epics.css.dal.proxy.DirectoryProxy#getPropertyNames()
	 */
	public String[] getPropertyNames() {
		throw new UnsupportedOperationException("This is not device proxy.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.epics.css.dal.proxy.DirectoryProxy#getPropertyType(java.lang.String)
	 */
	public Class<? extends SimpleProperty> getPropertyType(String propertyName) {
		throw new UnsupportedOperationException("This is not device proxy.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.epics.css.dal.proxy.DirectoryProxy#refresh()
	 */
	public synchronized void refresh() {
		characteristics = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.epics.css.dal.proxy.AbstractProxyImpl#setConnectionState(org.epics.css.dal.context.ConnectionState)
	 */
	@Override
	protected synchronized void setConnectionState(ConnectionState s) {
		if (getConnectionState().equals(s)) {
			return;
		}
		super.setConnectionState(s);
		EnumSet<DynamicValueState> set = null;
		if (s == ConnectionState.CONNECTED) {
			set = EnumSet.of(DynamicValueState.NORMAL);
		} else if (s == ConnectionState.DISCONNECTED) {
			set = EnumSet.of(DynamicValueState.LINK_NOT_AVAILABLE);
		} else if (s == ConnectionState.DESTROYED) {
			set = EnumSet.of(DynamicValueState.LINK_NOT_AVAILABLE);
		} else if (s == ConnectionState.CONNECTION_FAILED) {
			set = EnumSet.of(DynamicValueState.ERROR);
		} else if (s == ConnectionState.CONNECTION_LOST) {
			set = EnumSet.of(DynamicValueState.ERROR);
		} else {
			set = EnumSet.of(DynamicValueState.NORMAL);
		}
		setCondition(new DynamicValueCondition(set, 0,
				"Connection state changed"));
	}

	String getDeviceName() {
		return deviceName;
	}

	PropertyNameDissector getDissector() {
		return dissector;
	}

	synchronized void removeMonitor(MonitorProxyImpl m) {
		monitors.remove(m);
	}

	/**
	 * Destroy all monitors.
	 */
	private void destroyMonitors() {
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
	 * (non-Javadoc)
	 * 
	 * @see org.epics.css.dal.proxy.AbstractProxyImpl#destroy()
	 */
	@Override
	public synchronized void destroy() {
		super.destroy();

		// destroy all monitors
		destroyMonitors();
	}
}
