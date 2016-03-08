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

package org.csstudio.dal.tine;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.csstudio.dal.DataExchangeException;
import org.csstudio.dal.DynamicValueCondition;
import org.csstudio.dal.DynamicValueState;
import org.csstudio.dal.RemoteException;
import org.csstudio.dal.Request;
import org.csstudio.dal.ResponseListener;
import org.csstudio.dal.context.ConnectionState;
import org.csstudio.dal.proxy.AbstractPropertyProxyImpl;
import org.csstudio.dal.proxy.DirectoryProxy;
import org.csstudio.dal.proxy.MonitorProxy;
import org.csstudio.dal.proxy.PropertyProxy;
import org.csstudio.dal.proxy.SyncPropertyProxy;

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
public abstract class PropertyProxyImpl<T>
    extends AbstractPropertyProxyImpl<T,TINEPlug,MonitorProxyImpl<T>>
    implements PropertyProxy<T,TINEPlug>, SyncPropertyProxy<T,TINEPlug>, DirectoryProxy<TINEPlug>
{

    private PropertyNameDissector dissector;
    private String deviceName;
    private int timeOut = 1000;
    protected TFormat dataFormat;

    public PropertyProxyImpl(String name, TINEPlug plug) {
        super(name, plug);
        this.dissector = new PropertyNameDissector(name);
        this.deviceName = PropertyProxyUtilities.makeDeviceName(this.dissector);
        setConnectionState(ConnectionState.CONNECTED);
        initializeCharacteristics();
        try {
            this.dataFormat= (TFormat)getCharacteristic("dataFormat");
        } catch (DataExchangeException e) {
            Logger.getLogger(this.getClass()).error("Initializing data format failed.", e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.proxy.PropertyProxy#createMonitor(org.csstudio.dal.ResponseListener)
     */
    public synchronized MonitorProxy createMonitor(ResponseListener<T> callback, Map<String,Object> param) throws RemoteException {
        MonitorProxyImpl<T> m = new MonitorProxyImpl<T>(this,callback);
        m.initialize(getDataObject());
        return m;
    }


    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.proxy.PropertyProxy#getValueAsync(org.csstudio.dal.ResponseListener)
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
            DynamicValueCondition condition = new DynamicValueCondition(EnumSet.of(DynamicValueState.ERROR),null,"Error initializing proxy");
            setCondition(condition);
            throw new DataExchangeException(this,"Exception on async getting value '"+this.name+"'.",e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.proxy.PropertyProxy#setValueAsync(java.lang.Object, org.csstudio.dal.ResponseListener)
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
            DynamicValueCondition condition = new DynamicValueCondition(EnumSet.of(DynamicValueState.ERROR),null,"Error initializing proxy");
            setCondition(condition);
            throw new DataExchangeException(this,"Exception on async setting value '"+this.name+"'.",e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.proxy.SyncPropertyProxy#getValueSync()
     */
    public T getValueSync() {
        TDataType dout = PropertyProxyUtilities.toTDataType(getDataObject(),PropertyProxyUtilities.getObjectSize(getDataObject()),true);
        TDataType din = PropertyProxyUtilities.toTDataType(getDataObject(),PropertyProxyUtilities.getObjectSize(getDataObject()),false);
        TLink tl = new TLink(this.deviceName,this.dissector.getDeviceProperty(),dout,din,TAccess.CA_READ);
        try
        {
            int statusCode = tl.execute(this.timeOut);
            if (statusCode > 0) {
                setCondition(new DynamicValueCondition(EnumSet.of(DynamicValueState.ERROR),null,tl.getLastError()));
            } else {
                setCondition(new DynamicValueCondition(EnumSet.of(DynamicValueState.NORMAL)));
            }
            dout = tl.getOutputDataObject();
            tl.cancel();
        } catch (Exception e) {
            setCondition(new DynamicValueCondition(EnumSet.of(DynamicValueState.ERROR),null, tl.getLastError()));
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
     * @see org.csstudio.dal.proxy.SyncPropertyProxy#setValueSync(java.lang.Object)
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
                setCondition(new DynamicValueCondition(EnumSet.of(DynamicValueState.ERROR),null,tl.getLastError()));
            } else {
                setCondition(new DynamicValueCondition(EnumSet.of(DynamicValueState.NORMAL)));
            }
            tl.cancel();
        }
        catch (Exception e)
        {
            setCondition(new DynamicValueCondition(EnumSet.of(DynamicValueState.ERROR),null, tl.getLastError()));
        }
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.proxy.PropertyProxy#isSettable()
     */
    public boolean isSettable() {
        try {
            return (Boolean) getCharacteristic("editable");
        } catch (DataExchangeException e) {
            Logger.getLogger(this.getClass()).error("Characteristics failed.", e);
            return false;
        }
    }

    private void initializeCharacteristics() {
        try {
            getCharacteristics().putAll(TINEPlug.getInstance().getCharacteristics(this.dissector.getRemoteName(),getNumericType()));

            // notify DAL that metadata has been initialized
            updateConditionWith(null, DynamicValueState.HAS_METADATA);
//                characteristics = PropertyProxyUtilities.getCharacteristics(dissector,getNumericType());
        } catch (ConnectionFailed e) {
            setCondition(new DynamicValueCondition(EnumSet.of(DynamicValueState.ERROR)));
            Logger.getLogger(this.getClass()).error("Characteristics failed.", e);
        }
    }

    /**
     * Returns number type of value. Eg if it si array of doubles, then double is return.
     * @return number type of value
     */
    protected abstract Class getNumericType();

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.proxy.DirectoryProxy#getCharacteristicNames()
     */
    public synchronized String[] getCharacteristicNames() {
        Set<String> set = getCharacteristics().keySet();
        String[] names = new String[set.size()];
        return set.toArray(names);
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.proxy.DirectoryProxy#refresh()
     */
    public synchronized void refresh() {
        initializeCharacteristics();
    }

    String getDeviceName() {
        return this.deviceName;
    }

    PropertyNameDissector getDissector() {
        return this.dissector;
    }

    @Override
    protected Object processCharacteristicAfterCache(Object value,
            String characteristicName) {
        return value;
    }

    @Override
    protected Object processCharacteristicBeforeCache(Object value,
            String characteristicName) {
        return value;
    }

}
