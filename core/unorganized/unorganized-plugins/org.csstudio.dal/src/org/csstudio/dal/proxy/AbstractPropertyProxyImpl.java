/**
 *
 */
package org.csstudio.dal.proxy;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.csstudio.dal.CharacteristicInfo;
import org.csstudio.dal.DataExchangeException;
import org.csstudio.dal.DynamicValueCondition;
import org.csstudio.dal.DynamicValueState;
import org.csstudio.dal.RemoteException;
import org.csstudio.dal.Request;
import org.csstudio.dal.Response;
import org.csstudio.dal.ResponseListener;
import org.csstudio.dal.SimpleProperty;
import org.csstudio.dal.context.ConnectionState;
import org.csstudio.dal.impl.PropertyUtilities;
import org.csstudio.dal.impl.RequestImpl;
import org.csstudio.dal.impl.ResponseImpl;
import org.csstudio.dal.simple.impl.DynamicValueConditionConverterUtil;

/**
 * Abstract implementation of PropertyProxy, as help for plug implementation.
 *
 * @author ikriznar
 *
 */
public abstract class AbstractPropertyProxyImpl<T,P extends AbstractPlug,M extends MonitorProxy>
    extends AbstractProxyImpl<P>
    implements PropertyProxy<T,P> /*, DirectoryProxy<P> implements also some directory methods although does not import it*/
{

    private DynamicValueCondition condition= new DynamicValueCondition(DynamicValueState.NORMAL);
    private Response<T> lastValueResponse;
    private Map<String, Object> characteristics;
    private List<M> monitors;


    private boolean liveDataNotSet = true;
    private boolean metaDataNotSet = true;

    public AbstractPropertyProxyImpl(String name, P plug) {
        super(name,plug);
    }

    /**
     * Internal storage for monitors.
     * @return characteristics cache
     */
    protected List<M> getMonitors() {
        if (monitors == null) {
            synchronized (this) {
                if (monitors==null) {
                    monitors = newMonitorsList();
                }
            }
        }
        return monitors;
    }

    protected boolean isMonitorListCreated() {
        return monitors!=null;
    }

    protected boolean isCharacteristicsCacheCreated() {
        return characteristics!=null;
    }

    /**
     * Creates new instance of list that is used for storing monitors.
     * Plug implementation may override to provide own list implementation.
     * @return new instance of list that is used for storing monitors
     */
    private List<M> newMonitorsList() {
        return new ArrayList<M>(2);
    }

    /**
     * Destroy all monitors.
     */
    protected void destroyMonitors()
    {
        if (monitors!=null) {
            MonitorProxy[] array;
            synchronized (monitors) {
                array = new MonitorProxy[monitors.size()];
                monitors.toArray(array);
            }
            // destroy all
            for (int i = 0; i < array.length; i++)
                array[i].destroy();
        }

    }

    /**
     * Add monitor.
     * @param monitor monitor to be added.
     */
    public void addMonitor(M monitor)
    {
        synchronized (getMonitors()) {
            if (!monitors.contains(monitor)) monitors.add(monitor);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addProxyListener(ProxyListener<?> l) {
        super.addProxyListener(l);

        @SuppressWarnings("rawtypes")
        ProxyEvent e = new ProxyEvent(this, getCondition(),
                getConnectionState(), null);

        try {
            l.dynamicValueConditionChange(e);
        } catch (Exception ex) {
            Logger.getLogger(this.getClass()).error("Failed to forward listener.", ex);
        }

    }

    /**
     * Remove monitor.
     * @param monitor monitor to be removed.
     */
    public void removeMonitor(M monitor)
    {
        if (monitors==null) {
            return;
        }
        synchronized (monitors) {
            monitors.remove(monitor);
        }
    }

    /**
     * Internal cache for proxy characteristics.
     * @return characteristics cache
     */
    protected Map<String, Object> getCharacteristics() {
        if (characteristics == null) {
            synchronized (this) {
                if (characteristics==null) {
                    characteristics = newCharacteristicsCache();
                }
            }
        }
        return characteristics;
    }

    /**
     * Returns new characteristics cache map. Called only once when cache is created.
     * Plug implementation may override this to implement own creation and initialization.
     * @return new characteristics cache map
     */
    protected Map<String, Object> newCharacteristicsCache() {
        return new HashMap<String, Object>(16);
    }

    @Override
    protected void handleConnectionState(ConnectionState s) {
        /* we update condition and fire update before connection update,
         * may not be perfect, but at least assures synchronization betwean
         * two of them in proxy and property implementation.
         */
        setCondition(connectionStateMachine.deriveUpdatedCondition(getCondition()));
        super.handleConnectionState(s);
    }

    /**
     * Fires new condition event.
     */
    protected void fireCondition() {
        if (this.proxyListeners == null) {
            return;
        }

        @SuppressWarnings("unchecked")
        ProxyListener<T>[] l= (ProxyListener<T>[])this.proxyListeners.toArray();

        ProxyEvent<PropertyProxy<T,?>> pe= new ProxyEvent<PropertyProxy<T,?>>((PropertyProxy<T,P>)this,this.condition,this.connectionStateMachine.getConnectionState(),null);
        for (int i = 0; i < l.length; i++) {
            try {
                l[i].dynamicValueConditionChange(pe);
            } catch (Exception e) {
                Logger.getLogger(this.getClass()).warn("Event handler error, continuing.", e);
            }
        }

        if (metaDataNotSet && condition.containsAnyOfStates(DynamicValueState.HAS_METADATA)) {
            metaDataNotSet=false;
            if (connectionStateMachine.requestOperationalState(getCondition().getStates())) {
                fireConnectionState(ConnectionState.OPERATIONAL,null);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.dal.proxy.PropertyProxy#getCondition()
     */
    public DynamicValueCondition getCondition() {
        return this.condition;
    }

    /**
     * Intended for only within plug.
     * @param s new condition state.
     */
    public synchronized void setCondition(DynamicValueCondition s) {
        if (s == null || this.condition == s || this.condition.areStatesEqual(s)) {
            return;
        }
        this.condition=s;
        fireCondition();
    }

    public void updateConditionWith(String message, DynamicValueState... states) {
        if (states == null) {
            return;
        }

        DynamicValueCondition c= new DynamicValueCondition(DynamicValueState.deriveSetWithStates(getCondition().getStates(), states),null, message);

        setCondition(c);
    }

    public Response<T> getLatestValueResponse() {
        return lastValueResponse;
    }

    public void updateValueReponse(Response<T> r) {
        lastValueResponse=r;

        if (liveDataNotSet && lastValueResponse!=null) {
            liveDataNotSet=false;
            getCondition().getStates().remove(DynamicValueState.NO_VALUE);
            updateConditionWith(null, DynamicValueState.HAS_LIVE_DATA);
            if (connectionStateMachine.requestOperationalState(getCondition().getStates())) {
                fireConnectionState(ConnectionState.OPERATIONAL,null);
            }
        }
    }

    /**
     * Returns some value for those characteristics, which does not require remote connection
     * but can provide value from proxy within.
     * @param name the characteristic name
     * @return the proxy local characteristic value
     */
    public Object getLocalProxyCharacteristic(String name) {

        if (name == null) {
            return null;
        }

        if (name.equals(CharacteristicInfo.C_SEVERITY.getName())) {
            return condition;
        }
        if (name.equals(CharacteristicInfo.C_STATUS.getName())) {
            return DynamicValueConditionConverterUtil.extractStatusInfo(condition);
        }
        if (name.equals(CharacteristicInfo.C_TIMESTAMP.getName())) {
            return DynamicValueConditionConverterUtil.extractTimestampInfo(condition);
        }

        return null;
    }

    /**
     * Default implementation trying to help getting characteristic value.
     */
    public Object getCharacteristic(String characteristicName)
            throws DataExchangeException {

        if (characteristicName==null) {
            return null;
        }

        // get system characteristic
        Object value= getLocalProxyCharacteristic(characteristicName);

        value = processCharacteristicBeforeCache(value, characteristicName);

        // get characteristic from cache
        if (value==null && characteristics!=null) {
            synchronized (characteristics) {
                value= characteristics.get(characteristicName);
            }
        }

        value = processCharacteristicAfterCache(value, characteristicName);

        if (value==null && (this instanceof DirectoryProxy)) {
            value= PropertyUtilities.verifyCharacteristic((DirectoryProxy<?>)this, characteristicName, value);
        }

        return value;
    }

    /**
     * Plug implementation should implement here processing characteristic value after has not been found in
     * characteristic cache.
     *
     * Plus should here implement remote requesting of characteristic. Simulator should simply
     * provide some value, if it is not already in cache.
     *
     * @param value the value establishes so far
     * @param characteristicName the name of requested characteristic
     * @return new value or just provided value
     */
    protected abstract Object processCharacteristicAfterCache(Object value,
            String characteristicName);

    /**
     * Plug implementation should implement here processing characteristic value before it is taken from
     * characteristic cache.
     *
     * If plug has nothing to do, then should simply return provided value.
     *
     * @param value the value establishes so far
     * @param characteristicName the name of requested characteristic
     * @return new value or just provided value
     */
    protected abstract Object processCharacteristicBeforeCache(Object value,
            String characteristicName);


    @Override
    public void destroy() {

        destroyMonitors();

        super.destroy();

    }

    /**
     * Fires new characteristics changed event
     */

    protected void fireCharacteristicsChanged(PropertyChangeEvent ev)
    {
        if (proxyListeners == null)
            return;

        ProxyListener<?>[] l = (ProxyListener[])proxyListeners.toArray();

        for (int i = 0; i < l.length; i++) {
            try {
                l[i].characteristicsChange(ev);
            } catch (Exception e) {
                Logger.getLogger(this.getClass()).warn("Simulator error.", e);
            }
        }

    }

    /*
     * Dummy methods because of DirectiryProxy, not needed or used in PropertyProxy.
     */

    public String[] getCommandNames() throws DataExchangeException {
        return null;
    }
    public Class<? extends SimpleProperty<?>> getPropertyType(
            String propertyName) throws RemoteException {
        return null;
    }
    public String[] getPropertyNames() throws RemoteException {
        return null;
    }


    @SuppressWarnings("unchecked")
    public Request<?> getCharacteristics(final String[] characteristics,
            ResponseListener<?> callback) throws DataExchangeException {

        RequestImpl<Object> r = new RequestImpl<Object>(this, (ResponseListener<Object>) callback);

        handleCharacteristicsReponses(characteristics, (ResponseListener<Object>) callback, r);

        return r;
    }

    /**
     * Handles firing responses with characteristics.
     * Plug implementation may override this method to handles this operation asynchronously.
     * Default implementation calls synchronous method.
     * @param characteristics
     * @param callback
     * @param request
     */
    protected void handleCharacteristicsReponses(final String[] characteristics,
            final ResponseListener<Object> callback,
            final RequestImpl<Object> request)
    {
        handleCharacteristicsReponsesSync(characteristics, callback, request);
    }

    /**
     * Handles getting and firing characteristics responses in synchronous way.
     * @param characteristics
     * @param callback
     * @param request
     */
    protected void handleCharacteristicsReponsesSync(final String[] characteristics,
            final ResponseListener<Object> callback,
            final RequestImpl<Object> request)
    {

        for (int i = 0; i < characteristics.length; i++) {
            Object value;
            try {
                value= getCharacteristic(characteristics[i]);

                request.addResponse(new ResponseImpl<Object>(this, request,    value, characteristics[i],
                        value != null, null, getCondition(), null, i+1 == characteristics.length));

            } catch (DataExchangeException e) {

                request.addResponse(new ResponseImpl<Object>(this, request,    null, characteristics[i],
                        false, e, getCondition(), null, i+1 == characteristics.length));

            }
        }
    }

    /**
     * Sets new characteristics value and fires property change event to proxy listeners
     * with new characteristic value. Note that adding value directly to getCharacteristics()
     * object does not fire property change event.
     *
     * @param chName characteristic name
     * @param newValue new value to be stores in characteristic cache
     * @return true if characteristics value in property has changed by this operation
     */
    public boolean updateCharacteristic(String chName, Object newValue)
    {
        if (chName==null || (newValue==null && characteristics==null)) {
            return false;
        }
        Object old = getCharacteristics().put(chName, newValue);
        if (newValue!=null) {
            if (newValue.equals(old)) {
                return false;
            }
        } else if (old==null) {
            return false;
        }
        fireCharacteristicsChanged(new PropertyChangeEvent(this,chName,old,newValue));
        return true;
    }


}
