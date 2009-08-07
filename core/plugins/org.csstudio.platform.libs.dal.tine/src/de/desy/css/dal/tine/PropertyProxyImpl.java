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
import de.desy.tine.definitions.TFormat;
import de.desy.tine.definitions.TMode;

/**
 * Implementation of PropertyProxy for Tine DAL plugin.
 * 
 * @author Jaka Bobnar, Cosylab
 *
 * @param <T>
 */
public abstract class PropertyProxyImpl<T> extends AbstractProxyImpl implements PropertyProxy<T>, SyncPropertyProxy<T>, DirectoryProxy {
	
	private Map<String, Object> characteristics;
	protected DynamicValueCondition condition= new DynamicValueCondition(EnumSet.of(DynamicValueState.NORMAL),System.currentTimeMillis(),null);
	private PropertyNameDissector dissector;
	private String deviceName; 
	private int timeOut = 1000;
	private Set<MonitorProxyImpl> monitors= new HashSet<MonitorProxyImpl>(3);
	protected TFormat dataFormat;
	
	public PropertyProxyImpl(String name) {
		super(name);
		this.dissector = new PropertyNameDissector(name);
		this.deviceName = PropertyProxyUtilities.makeDeviceName(this.dissector);
		setConnectionState(ConnectionState.CONNECTED);
		getCharacteristics();
		this.dataFormat= (TFormat)getCharacteristic("dataFormat");
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.PropertyProxy#createMonitor(org.epics.css.dal.ResponseListener)
	 */
	public synchronized MonitorProxy createMonitor(ResponseListener<T> callback) throws RemoteException {
		MonitorProxyImpl<T> m = new MonitorProxyImpl<T>(this,callback);
		this.monitors.add(m);
		m.initialize(getDataObject());
		return m;
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.PropertyProxy#getCondition()
	 */
	public DynamicValueCondition getCondition() {
		return this.condition;
	}
	
	/**
	 * Intended for only within plug.
	 * @param s new condition state.
	 */
	protected synchronized void setCondition(DynamicValueCondition s) {
		if (this.condition.areStatesEqual(s)) {
			return;
		}
		this.condition=s;
		fireCondition();
	}
	
	/**
	 * Fires new condition event.
	 */
	protected void fireCondition() {
		if (this.proxyListeners == null) {
			return;
		}
		
		ProxyListener<T>[] l= (ProxyListener<T>[])this.proxyListeners.toArray();

		ProxyEvent<PropertyProxy<T>> pe= new ProxyEvent<PropertyProxy<T>>(this,this.condition,this.connectionState,null);
		for (int i = 0; i < l.length; i++) {
			try {
				l[i].dynamicValueConditionChange(pe);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.epics.css.dal.proxy.AbstractProxyImpl#addProxyListener(org.epics.css.dal.proxy.ProxyListener)
	 */
	@Override
	public void addProxyListener(ProxyListener<?> l) {
		super.addProxyListener(l);
		l.connectionStateChange(new ProxyEvent<Proxy>(this,this.condition,this.connectionState,null));
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.PropertyProxy#getValueAsync(org.epics.css.dal.ResponseListener)
	 */
	public Request<T> getValueAsync(ResponseListener<T> callback) throws DataExchangeException {
		if (getConnectionState() != ConnectionState.CONNECTED) {
			return null;
		}
		try {
			Object data = getDataObject();
	        TDataType dout = PropertyProxyUtilities.toTDataType(data,PropertyProxyUtilities.getObjectSize(data),true);
	        TDataType din = PropertyProxyUtilities.toTDataType(data,PropertyProxyUtilities.getObjectSize(data),false);
	        short access = TAccess.CA_READ;
	        TLink tLink = new TLink(this.deviceName,this.dissector.getDeviceProperty(),dout,din,access);
	        short mode = TMode.CM_SINGLE;
	        int handle = 0;
	        TINERequestImpl<T> request = new TINERequestImpl<T>(this, callback, tLink);
        	handle = tLink.attach(mode, request, this.timeOut);
        	if (handle < 0) {
        		tLink.close();
        		throw new ConnectionFailed(tLink.getError(-handle));
        	}
        	return request;
        } catch (Exception e) {
        	DynamicValueCondition condition = new DynamicValueCondition(EnumSet.of(DynamicValueState.ERROR),System.currentTimeMillis(),"Error initializing proxy");
			setCondition(condition);
			throw new DataExchangeException(this,"Exception on async getting value '"+this.name+"'.",e);
        }
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.PropertyProxy#setValueAsync(java.lang.Object, org.epics.css.dal.ResponseListener)
	 */
	public Request<T> setValueAsync(T value, ResponseListener<T> callback) throws DataExchangeException {
		//if not connected async call makes a mess and monitors needs to be reset
		if (getConnectionState() != ConnectionState.CONNECTED || !isSettable()) {
			return null;
		}
		try {
			Object data = convertDataToObject(value);
	        TDataType din = PropertyProxyUtilities.toTDataType(data,PropertyProxyUtilities.getObjectSize(data),true);
	        TDataType dout = PropertyProxyUtilities.toTDataType(data,PropertyProxyUtilities.getObjectSize(data),false);
	        short access = TAccess.CA_READ | TAccess.CA_WRITE;
	        TLink tLink = new TLink(this.deviceName,this.dissector.getDeviceProperty(),dout,din,access);
	        short mode = TMode.CM_SINGLE;
	        int handle = 0;
	        TINERequestImpl<T> request = new TINERequestImpl<T>(this, callback, tLink);
        	handle = tLink.attach(mode, request, this.timeOut);
        	
        	if (handle < 0) {
        		tLink.close();
        		throw new ConnectionFailed(tLink.getError(-handle));
        	}
        	return request;
        } catch (Exception e) {
        	DynamicValueCondition condition = new DynamicValueCondition(EnumSet.of(DynamicValueState.ERROR),System.currentTimeMillis(),"Error initializing proxy");
			setCondition(condition);
			throw new DataExchangeException(this,"Exception on async setting value '"+this.name+"'.",e);
        }
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.SyncPropertyProxy#getValueSync()
	 */
	public T getValueSync() {
		TDataType dout = PropertyProxyUtilities.toTDataType(getDataObject(),PropertyProxyUtilities.getObjectSize(getDataObject()),true);
		TDataType din = PropertyProxyUtilities.toTDataType(getDataObject(),PropertyProxyUtilities.getObjectSize(getDataObject()),false);
		TLink tl = new TLink(this.deviceName,this.dissector.getDeviceProperty(),dout,din,TAccess.CA_READ);
	    try
        {
		    int statusCode = tl.execute(this.timeOut);
			if (statusCode > 0) {
				setCondition(new DynamicValueCondition(EnumSet.of(DynamicValueState.ERROR),System.currentTimeMillis(),tl.getLastError()));
			} else {
				setCondition(new DynamicValueCondition(EnumSet.of(DynamicValueState.NORMAL),System.currentTimeMillis(),null));
			}
		    dout = tl.getOutputDataObject();
		    tl.cancel();
        } catch (Exception e) {
        	setCondition(new DynamicValueCondition(EnumSet.of(DynamicValueState.ERROR),System.currentTimeMillis(), tl.getLastError()));
        }
	    return extractData(dout);
	}
	
	/**
	 * Extracts data of type T from the TDataType and returns the data.
	 * @param out
	 * @return
	 * @throws DataExchangeException 
	 */
	protected abstract T extractData(TDataType out);
	
	/**
	 * Sets the data to an object which can be sent to Tine. This object is usually 
	 * an array of certain type.
	 * 
	 * @param data
	 * @return
	 */
	protected abstract Object convertDataToObject(T data);
	
	/**
	 * Returns the data object which receives data from Tine. This object is usually
	 * an array. This object is encapsulated to TDataType and sent to TLink.
	 * @return
	 */
	protected abstract Object getDataObject();
	
	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.SyncPropertyProxy#setValueSync(java.lang.Object)
	 */
	public void setValueSync(T value) {
		if (! isSettable()) {
			return;
		}
		Object data = convertDataToObject(value);
        TDataType din = PropertyProxyUtilities.toTDataType(data,PropertyProxyUtilities.getObjectSize(data),true);
        TDataType dout = PropertyProxyUtilities.toTDataType(data,PropertyProxyUtilities.getObjectSize(data),false);
        TLink tl = new TLink(this.deviceName,this.dissector.getDeviceProperty(),dout,din,(short)(TAccess.CA_READ | TAccess.CA_WRITE));
        
        try
        {
        	int statusCode = tl.execute(this.timeOut);
        	if (statusCode > 0) {
				setCondition(new DynamicValueCondition(EnumSet.of(DynamicValueState.ERROR),System.currentTimeMillis(),tl.getLastError()));
			} else {
				setCondition(new DynamicValueCondition(EnumSet.of(DynamicValueState.NORMAL),System.currentTimeMillis(),null));
			}
        	tl.cancel();
        }
        catch (Exception e)
        {
        	setCondition(new DynamicValueCondition(EnumSet.of(DynamicValueState.ERROR),System.currentTimeMillis(), tl.getLastError()));
        }
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.PropertyProxy#isSettable()
	 */
	public boolean isSettable() {
		return (Boolean) getCharacteristic("editable");
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.DirectoryProxy#getCharacteristic(java.lang.String)
	 */
	public Object getCharacteristic(String characteristicName) {
		
		return getCharacteristics().get(characteristicName);
		
	}
	
	private synchronized Map<String, Object> getCharacteristics() {
		if (this.characteristics == null) {
			try {
				this.characteristics = TINEPlug.getInstance().getCharacteristics(this.dissector.getRemoteName(),getNumericType());
//				characteristics = PropertyProxyUtilities.getCharacteristics(dissector,getNumericType());
			} catch (ConnectionFailed e) {
				setCondition(new DynamicValueCondition(EnumSet.of(DynamicValueState.ERROR),System.currentTimeMillis(),null));
				e.printStackTrace();
				return new HashMap<String, Object>();
			}
		}
		return this.characteristics;
	}

	/**
	 * Returns number type of value. Eg if it si array of doubles, then double is return.
	 * @return number type of value
	 */
	protected abstract Class getNumericType();

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.DirectoryProxy#getCharacteristicNames()
	 */
	public synchronized String[] getCharacteristicNames() {
		Set<String> set = getCharacteristics().keySet();
		String[] names = new String[set.size()];
		return set.toArray(names);
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.DirectoryProxy#getCharacteristics(java.lang.String[], org.epics.css.dal.ResponseListener)
	 */
	public Request<? extends Object> getCharacteristics(String[] characteristics, ResponseListener<? extends Object> callback) {
		RequestImpl<Object> r = new RequestImpl<Object>(this,(ResponseListener<Object>) callback);
		for (int i = 0; i < characteristics.length; i++) {
			r.addResponse(new ResponseImpl<Object>(this,r,getCharacteristics().get(characteristics[i]),characteristics[i],true,null,this.condition,new Timestamp(),true));
		}
		return r;
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.DirectoryProxy#getCommandNames()
	 */
	public String[] getCommandNames() {
		// property does not support commands
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.DirectoryProxy#getPropertyNames()
	 */
	public String[] getPropertyNames() {
		throw new UnsupportedOperationException("This is not device proxy.");
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.DirectoryProxy#getPropertyType(java.lang.String)
	 */
	public Class<? extends SimpleProperty<?>> getPropertyType(String propertyName) {
		throw new UnsupportedOperationException("This is not device proxy.");
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.DirectoryProxy#refresh()
	 */
	public synchronized void refresh() {
		this.characteristics = null;		
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.epics.css.dal.proxy.AbstractProxyImpl#setConnectionState(org.epics.css.dal.context.ConnectionState)
	 */
	@Override
	protected synchronized void setConnectionState(ConnectionState s) {
		if (getConnectionState().equals(s)) {
			return;
		}
		super.setConnectionState(s);
		EnumSet<DynamicValueState> set = null;
		if (s==ConnectionState.CONNECTED) {
			set = EnumSet.of(DynamicValueState.NORMAL);
		} else if (s==ConnectionState.DISCONNECTED) {
			set = EnumSet.of(DynamicValueState.LINK_NOT_AVAILABLE);
		} else if (s==ConnectionState.DESTROYED) {
			set = EnumSet.of(DynamicValueState.LINK_NOT_AVAILABLE);
		} else if (s==ConnectionState.CONNECTION_FAILED) {
			set = EnumSet.of(DynamicValueState.ERROR);
		} else if (s==ConnectionState.CONNECTION_LOST) {
			set = EnumSet.of(DynamicValueState.ERROR);
		} else {
			set = EnumSet.of(DynamicValueState.NORMAL);
		}
		setCondition(new DynamicValueCondition(set,0,"Connection state changed"));
	}	

	String getDeviceName() {
		return this.deviceName;
	}

	PropertyNameDissector getDissector() {
		return this.dissector;
	}
	
	synchronized void removeMonitor(MonitorProxyImpl m) {
		this.monitors.remove(m);
	}
	
	@Override
	public void destroy() {
		super.destroy();
		
		destroyMonitors();
	}
	
	protected synchronized void destroyMonitors() {
		MonitorProxyImpl[] array = this.monitors.toArray(new MonitorProxyImpl[this.monitors.size()]);

		// destroy all
		for (MonitorProxyImpl monitor : array) {
			monitor.destroy();
		}
	}
}
