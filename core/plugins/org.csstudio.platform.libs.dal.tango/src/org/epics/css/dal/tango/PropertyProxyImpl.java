package org.epics.css.dal.tango;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.epics.css.dal.AccessType;
import org.epics.css.dal.CharacteristicInfo;
import org.epics.css.dal.DataExchangeException;
import org.epics.css.dal.DynamicValueCondition;
import org.epics.css.dal.DynamicValueState;
import org.epics.css.dal.NumericPropertyCharacteristics;
import org.epics.css.dal.PropertyCharacteristics;
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
import org.epics.css.dal.proxy.ProxyEvent;
import org.epics.css.dal.proxy.ProxyListener;
import org.epics.css.dal.proxy.SyncPropertyProxy;
import org.epics.css.dal.simple.impl.DataUtil;

import fr.esrf.Tango.AttrWriteType;
import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.AttributeInfo;
import fr.esrf.TangoApi.DeviceAttribute;
import fr.esrf.TangoApi.DeviceProxy;

/**
 * 
 * <code>PropertyProxyImpl</code> is a base proeprty proxy implementation
 * for tango control system. It provides handling of read and write 
 * actions on a specific property as well as monitoring the values. 
 * The extendor of this class has to provide methods for transformation
 * of the specified value types to tango {@link DeviceAttribute}s and
 * vice versa. 
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 * @param <T> the type of values handled by this property 
 */
public abstract class PropertyProxyImpl<T> extends AbstractProxyImpl implements PropertyProxy<T>, SyncPropertyProxy<T>, DirectoryProxy {

	private DynamicValueCondition condition = new DynamicValueCondition(EnumSet.of(DynamicValueState.NORMAL),new Timestamp(System.currentTimeMillis(),0),null);
	private Map<String, Object> characteristics;
	
	private Set<MonitorProxyImpl<T>> monitors= new HashSet<MonitorProxyImpl<T>>(3);
	
	private DeviceProxy tangoProxy;
	private Class<?> type;
	private PropertyName propertyName;
	
	/**
	 * Constructs a new PropertyProxy implementation with the given 
	 * remote property name.
	 *  
	 * @param name the property name
	 * @param type the type of the property value (the characteristics are
	 * 			cast into this type)
	 */
	public PropertyProxyImpl(String propertyName, Class<?> type) {
		super(propertyName);
		this.propertyName = new PropertyName(propertyName);
		this.type = type;
	}
	
	/**
	 * Initializes this property proxy.
	 * 
	 * @param parentProxy the device proxy of the device that this property belongs to
	 */
	void initialize(DeviceProxy parentProxy) {
		this.tangoProxy = parentProxy;
		setConnectionState(ConnectionState.CONNECTED);
	}
		
	/**
	 * Returns the tango device proxy.
	 * 
	 * @return the device proxy.
	 */
	DeviceProxy getDeviceProxy() {
		return tangoProxy;
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.PropertyProxy#createMonitor(org.epics.css.dal.ResponseListener)
	 */
	public MonitorProxy createMonitor(ResponseListener<T> callback, Map<String,Object> param) throws RemoteException {
		synchronized (monitors) {
			MonitorProxyImpl<T> m = new MonitorProxyImpl<T>(this,callback);
			this.monitors.add(m);
			m.initialize();
			return m;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.AbstractProxyImpl#destroy()
	 */
	@Override
	public void destroy() {
		synchronized (monitors) {
			for (MonitorProxyImpl<T> m : monitors) {
				m.destroy();
			}
		}
		super.destroy();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.PropertyProxy#getCondition()
	 */
	public DynamicValueCondition getCondition() {
		return this.condition;
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.PropertyProxy#isSettable()
	 */
	public boolean isSettable() {
		Object obj = getCharacteristics().get("settable");
		if (obj != null) return (Boolean)obj;
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.PropertyProxy#getValueAsync(org.epics.css.dal.ResponseListener)
	 */
	public Request<T> getValueAsync(ResponseListener<T> callback) throws DataExchangeException {
		try {
			TangoRequestImpl<T> request = new TangoRequestImpl<T>(this, callback);
			this.tangoProxy.read_attribute_asynch(propertyName.getPropertyName(),request.getCallback());
			return request;
		} catch (DevFailed e) {
			DynamicValueCondition condition = new DynamicValueCondition(EnumSet.of(DynamicValueState.ERROR),new Timestamp(System.currentTimeMillis(),0),"Error initializing proxy");
			setCondition(condition);
			throw new DataExchangeException(this,"Cannot asynchronously read from device '" + this.tangoProxy.get_name() +"' property + '" + propertyName.getPropertyName() +"'.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.PropertyProxy#setValueAsync(java.lang.Object, org.epics.css.dal.ResponseListener)
	 */
	public Request<T> setValueAsync(T value, ResponseListener<T> callback) throws DataExchangeException {
		try {
			TangoRequestImpl<T> request = new TangoRequestImpl<T>(this, callback, value);
			DeviceAttribute attr = valueToDeviceAttribute(value);
			this.tangoProxy.write_attribute_asynch(attr,request.getCallback());
			return request;
		} catch (DevFailed e) {
			DynamicValueCondition condition = new DynamicValueCondition(EnumSet.of(DynamicValueState.ERROR),new Timestamp(System.currentTimeMillis(),0),"Error initializing proxy");
			setCondition(condition);
			throw new DataExchangeException(this,"Cannot asynchronously write to device '" + this.tangoProxy.get_name() +"' property + '" + propertyName.getPropertyName() +"'.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.SyncPropertyProxy#getValueSync()
	 */
	public T getValueSync() throws DataExchangeException {
		try {
			DeviceAttribute da = this.tangoProxy.read_attribute(propertyName.getPropertyName());
			return extractValue(da);
		} catch (DevFailed e) {
			DynamicValueCondition condition = new DynamicValueCondition(EnumSet.of(DynamicValueState.ERROR),new Timestamp(System.currentTimeMillis(),0),"Error initializing proxy");
			setCondition(condition);
			throw new DataExchangeException(this,"Cannot synchronously read from device '" + this.tangoProxy.get_name() +"' property + '" + propertyName.getPropertyName() +"'.", e);
		}
	}
		
	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.SyncPropertyProxy#setValueSync(java.lang.Object)
	 */
	public void setValueSync(T value) throws DataExchangeException {
		try {
			DeviceAttribute attr = valueToDeviceAttribute(value);
			this.tangoProxy.write_attribute(attr);
		} catch (DevFailed e) {
			DynamicValueCondition condition = new DynamicValueCondition(EnumSet.of(DynamicValueState.ERROR),new Timestamp(System.currentTimeMillis(),0),"Error initializing proxy");
			setCondition(condition);
			throw new DataExchangeException(this,"Cannot synchronously write to device '" + this.tangoProxy.get_name() +"' property + '" + propertyName.getPropertyName() +"'.", e);
		}
	}
	
	/**
	 * Transforms the device attribute to the type of data that this
	 * proxy knows. This method is used, when reading values from the 
	 * control system. The device attribute is obtained from the
	 * system, transformed to proper type by this method and returned to the
	 * called.
	 * 
	 * @param da the device attribute as read from the control system
	 * @return the data of the appropriate type handled by this proxy
	 * 
	 * @throws DevFailed if the extraction of value was not succesful
	 */
	protected abstract T extractValue(DeviceAttribute da) throws DevFailed ;

	/**
	 * Transforms the given value to device attribute as understood
	 * by the control system. This methos is used when writing values
	 * to the control system. The value as set by the local peer is 
	 * transformed to the device attribute, which is then sent to 
	 * the control system.
	 * 
	 * @param value the value to be transformed
	 * @return the device attribute holding the given value
	 */
	protected abstract DeviceAttribute valueToDeviceAttribute(T value);

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.DirectoryProxy#getCharacteristic(java.lang.String)
	 */
	public Object getCharacteristic(String characteristicName) throws DataExchangeException {
		return getCharacteristics().get(characteristicName);
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.DirectoryProxy#getCharacteristicNames()
	 */
	public String[] getCharacteristicNames() throws DataExchangeException {
		Set<String> set = getCharacteristics().keySet();
		String[] names = new String[set.size()];
		return set.toArray(names);
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.DirectoryProxy#getCharacteristics(java.lang.String[], org.epics.css.dal.ResponseListener)
	 */
	@SuppressWarnings("unchecked")
	public Request<? extends Object> getCharacteristics(String[] characteristics, ResponseListener<? extends Object> callback)
			throws DataExchangeException {
		RequestImpl<Object> r = new RequestImpl<Object>(this,(ResponseListener<Object>) callback);
		for (int i = 0; i < characteristics.length; i++) {
			r.addResponse(new ResponseImpl<Object>(this,r,getCharacteristics().get(characteristics[i]),characteristics[i],true,null,this.condition,new Timestamp(),true));
		}
		return r;
	}
	
	/**
	 * Reads the characteristics of this property from the control system.
	 * 
	 * @return the characteristics
	 */
	private synchronized Map<String, Object> getCharacteristics() {
		if (this.characteristics == null) {
			try {
				AttributeInfo ai = tangoProxy.get_attribute_info(propertyName.getPropertyName());
				if (ai != null) {
					this.characteristics = new HashMap<String,Object>();
					this.characteristics.put(PropertyCharacteristics.C_DISPLAY_NAME,ai.name);
					this.characteristics.put(PropertyCharacteristics.C_DESCRIPTION,ai.description);
					this.characteristics.put(PropertyCharacteristics.C_PROPERTY_TYPE,ai.data_type);
					this.characteristics.put(NumericPropertyCharacteristics.C_FORMAT,ai.format);
					Object alarmMax = ProxyUtilities.cast(ai.max_alarm,type); 
					this.characteristics.put(NumericPropertyCharacteristics.C_ALARM_MAX,alarmMax);
					this.characteristics.put(NumericPropertyCharacteristics.C_WARNING_MAX,alarmMax);
					Object alarmMin = ProxyUtilities.cast(ai.min_alarm,type);
					this.characteristics.put(NumericPropertyCharacteristics.C_ALARM_MIN,alarmMin);
					this.characteristics.put(NumericPropertyCharacteristics.C_WARNING_MIN,alarmMin);
					Object max = ProxyUtilities.cast(ai.max_value,type);
					this.characteristics.put(NumericPropertyCharacteristics.C_GRAPH_MAX,max);
					this.characteristics.put(NumericPropertyCharacteristics.C_MAXIMUM,max);
					Object min = ProxyUtilities.cast(ai.min_value,type);
					this.characteristics.put(NumericPropertyCharacteristics.C_GRAPH_MIN,min);
					this.characteristics.put(NumericPropertyCharacteristics.C_MINIMUM,min);
					this.characteristics.put(NumericPropertyCharacteristics.C_UNITS,ai.display_unit);
					AttrWriteType wt = ai.writable;
					this.characteristics.put("settable", wt == AttrWriteType.WRITE || wt == AttrWriteType.READ_WRITE);
					this.characteristics.put(PropertyCharacteristics.C_ACCESS_TYPE, AccessType.getAccess(wt == AttrWriteType.READ || wt == AttrWriteType.READ_WITH_WRITE || wt == AttrWriteType.READ_WRITE, wt == AttrWriteType.WRITE || wt == AttrWriteType.READ_WRITE || wt == AttrWriteType.READ_WITH_WRITE));
					this.characteristics.put(CharacteristicInfo.C_META_DATA.getName(),DataUtil.createMetaData(characteristics));
//					this.characteristics.put(CharacteristicInfo.C_META_DATA.getName(), DataUtil.createNumericMetaData(
//							((Number) min).doubleValue(), ((Number) max).doubleValue(), 
//							((Number) alarmMin).doubleValue(), ((Number) alarmMax).doubleValue(), 
//							((Number) alarmMin).doubleValue(), ((Number) alarmMax).doubleValue(), 
//							0, ai.display_unit));
					
				} else {
					throw new DevFailed();
				}
			} catch (DevFailed e) {
				setCondition(new DynamicValueCondition(EnumSet.of(DynamicValueState.ERROR),new Timestamp(System.currentTimeMillis(),0),null));
				TangoPropertyPlug.getInstance().getLogger().warn("Could not load characteristics",e);
				return new HashMap<String, Object>();
			}
		}
		return this.characteristics;
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.DirectoryProxy#getCommandNames()
	 */
	public String[] getCommandNames() throws DataExchangeException {
		throw new UnsupportedOperationException("This is a property proxy. Properties do not have commands.");
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.DirectoryProxy#getPropertyNames()
	 */
	public String[] getPropertyNames() throws RemoteException {
		throw new UnsupportedOperationException("This is a property proxy. Properties do not have properties.");
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.DirectoryProxy#getPropertyType(java.lang.String)
	 */
	public Class<? extends SimpleProperty<?>> getPropertyType(String propertyName) throws RemoteException {
		throw new UnsupportedOperationException("This is a property proxy. Properties do not have properties.");
	}

	/*
	 * (non-Javadoc)
	 * @see org.epics.css.dal.proxy.DirectoryProxy#refresh()
	 */
	public void refresh() {
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
		setCondition(new DynamicValueCondition(set,new Timestamp(System.currentTimeMillis(),0),"Connection state changed"));
	}	
	
	/**
	 * Intended for only within the plug.
	 * 
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
	@SuppressWarnings("unchecked")
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
				Logger.getLogger(this.getClass()).warn("Event handler error, continuing.", e);
			}
		}
	}
	
	/**
	 * Returns the property name object describing the unique name of this property.
	 * 
	 * @return the property name
	 */
	protected PropertyName getPropertyName() {
		return propertyName;
	}
}
