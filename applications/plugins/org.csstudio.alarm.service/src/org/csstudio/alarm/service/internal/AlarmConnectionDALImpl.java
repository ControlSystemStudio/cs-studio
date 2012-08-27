/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY. THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN
 * "../AS IS" BASIS. WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN
 * ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS
 * DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS
 * AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE,
 * SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE
 * THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
 * DISTRIBUTION OF THIS PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY
 * FIND A COPY AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM $Id: AlarmConnectionJMSImpl.java,v 1.4
 * 2010/04/28 07:58:00 jpenning Exp $
 */
package org.csstudio.alarm.service.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.AlarmConnectionException;
import org.csstudio.alarm.service.declaration.AlarmResource;
import org.csstudio.alarm.service.declaration.AlarmServiceException;
import org.csstudio.alarm.service.declaration.IAlarmConnection;
import org.csstudio.alarm.service.declaration.IAlarmConnectionMonitor;
import org.csstudio.alarm.service.declaration.IAlarmListener;
import org.csstudio.alarm.service.declaration.IAlarmService;
import org.csstudio.domain.common.collection.ChunkableCollection;
import org.csstudio.servicelocator.ServiceLocator;
import org.csstudio.dal.DynamicValueAdapter;
import org.csstudio.dal.DynamicValueEvent;
import org.csstudio.dal.SimpleProperty;
import org.csstudio.dal.StringProperty;
import org.csstudio.dal.simple.ConnectionParameters;
import org.csstudio.dal.simple.RemoteInfo;
import org.csstudio.dal.simple.SimpleDALBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cosylab.util.CommonException;

/**
 * This is the DAL based implementation of the AlarmConnection.
 *
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 21.04.2010
 */
public class AlarmConnectionDALImpl implements IAlarmConnection {

    private static final Logger LOG = LoggerFactory.getLogger(AlarmConnectionDALImpl.class);
    
    private static final String COULD_NOT_CREATE_DAL_CONNECTION = "Could not create DAL connection";
    private static final String COULD_NOT_DEREGISTER_DAL_CONNECTION = "Could not deregister DAL connection";
    
    private SimpleDALBroker _simpleDALBroker;
    
    private final Map<String, ListenerItem> _pv2listenerItem = new HashMap<String, AlarmConnectionDALImpl.ListenerItem>();
    
    // The listener is given once at connect
    private IAlarmListener _listener;
    
    /**
     * Constructor must be called only from the AlarmService.
     * @param simpleDALBroker
     */
    public AlarmConnectionDALImpl(@Nonnull final SimpleDALBroker simpleDALBroker) {
        _simpleDALBroker = simpleDALBroker;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canHandleTopics() {
        return false;
    }
    
    @Override
    public void connect(@Nonnull final IAlarmConnectionMonitor connectionMonitor,
                        @Nonnull final IAlarmListener listener,
                        @Nonnull final AlarmResource resource) throws AlarmConnectionException {
        LOG.info("Connecting to DAL.");
        
        _listener = listener;
        registerAllFromResource();
        
        // The DAL implementation sends connect here, because the DynamicValueListenerAdapter will not do so
        connectionMonitor.onConnect();
    }
    
    private void registerAllFromResource() throws AlarmConnectionException {
        Set<String> simpleNames = getPVNamesFromResource();
        LOG.debug("About to connect " + simpleNames.size() + " PVs");
        ChunkableCollection<String> chunkableCollection = new ChunkableCollection<String>(simpleNames, 100);
        for (Collection<String> chunkOfSimpleNames : chunkableCollection) {
            for (final String recordName : chunkOfSimpleNames) {
                LOG.trace("Connecting to " + recordName);
                registerPV(recordName);
            }
            LOG.debug("Wait for system to keep up");
            sleep(1000);
        }
        
    }

    private void sleep(final int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LOG.debug("sleep was interrupted");
            // so what?
        }
    }
    
    /**
     * This method encapsulates retrieval of data from ldap or an xml file.
     * It is protected so it may be overridden by a test, another option for tests is to mock the alarm service.
     * 
     * @return all the pv names from the initially given resource
     * @throws AlarmConnectionException
     */
    @Nonnull
    protected Set<String> getPVNamesFromResource() throws AlarmConnectionException {
        IAlarmService alarmService = ServiceLocator.getService(IAlarmService.class);
        try {
            return alarmService.getPvNames();
        } catch (AlarmServiceException e) {
            throw new AlarmConnectionException(e.getMessage(), e);
        }
    }
    
    @Override
    public void registerPV(@Nonnull final String pvName) {
        // A pv is only registered once
        if (!_pv2listenerItem.containsKey(pvName)) {
            final RemoteInfo remoteInfo = new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX + "EPICS",
                                                         pvName,
                                                         null,
                                                         null);
            @SuppressWarnings("synthetic-access")
            final ListenerItem item = new ListenerItem();
            // REVIEW (jpenning): hard coded type in connection parameter
            item._connectionParameters = new ConnectionParameters(remoteInfo, String.class);
            item._dynamicValueAdapter = new DynamicValueListenerAdapter<String, StringProperty>(_listener);
            // TODO (jpenning) use constants for parameterization of expert mode
            item._parameters = new HashMap<String, Object>();
            item._parameters.put("EPICSPlug.monitor.mask", 4); // EPICSPlug.PARAMETER_MONITOR_MASK = Monitor.ALARM
            
            try {
                // the same listener is used for all pvs
                _simpleDALBroker.registerListener(item._connectionParameters,
                                                  item._dynamicValueAdapter,
                                                  item._parameters);
                _pv2listenerItem.put(pvName, item);
            } catch (final InstantiationException e) {
                LOG.error(COULD_NOT_CREATE_DAL_CONNECTION, e);
            } catch (final CommonException e) {
                LOG.error(COULD_NOT_CREATE_DAL_CONNECTION, e);
            }
        }
    }
    
    @Override
    public void deregisterPV(@Nonnull final String pvName) {
        ListenerItem item = _pv2listenerItem.remove(pvName);
        if (item != null) {
            disconnectItem(item);
        } else {
            LOG.warn("Trying to deregister a pv named '" + pvName + "' which was not registered.");
        }
    }
    
    @Override
    public void reloadPVsFromResource() throws AlarmConnectionException {
        // calculate change sets: toBeRemoved = current - new, toBeConnected = new - current
        Set<String> currentPVs = new HashSet<String>(_pv2listenerItem.keySet());
        Set<String> newPVs = new HashSet<String>(getPVNamesFromResource());
        
        Set<String> toBeConnectedPvs = new HashSet<String>(newPVs);
        toBeConnectedPvs.removeAll(currentPVs);
        
        Set<String> toBeRemovedPvs =  new HashSet<String>(currentPVs);
        toBeRemovedPvs.removeAll(newPVs);
        
        deregister(toBeRemovedPvs);
        register(toBeConnectedPvs);
        
        // this was the old-fashioned brute-force method:
        //        deregisterAll();
        //        registerAllFromResource();
    }
    
    private void register(@Nonnull final Set<String> pvSet) {
        LOG.info("Registering " + pvSet.size() + " PVs.");
        for (String pvName : pvSet) {
            registerPV(pvName);
        }
        
    }
    
    private void deregister(@Nonnull final Set<String> pvSet) {
        LOG.info("Deregistering " + pvSet.size() + " PVs.");
        for (String pvName : pvSet) {
            ListenerItem item = _pv2listenerItem.get(pvName);
            disconnectItem(item);
            _pv2listenerItem.remove(pvName);
        }
    }
    
    private void deregisterAll() {
        LOG.info("Deregistering all PVs.");
        for (final ListenerItem item : _pv2listenerItem.values()) {
            disconnectItem(item);
        }
        _pv2listenerItem.clear();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect() {
        LOG.info("Disconnecting from DAL.");
        deregisterAll();
        _simpleDALBroker.releaseAll();
    }
    
    private void disconnectItem(@Nonnull final ListenerItem item) {
        try {
            _simpleDALBroker.deregisterListener(item._connectionParameters,
                                                item._dynamicValueAdapter,
                                                item._parameters);
        } catch (final InstantiationException e) {
            LOG.error(COULD_NOT_DEREGISTER_DAL_CONNECTION, e);
        } catch (final CommonException e) {
            LOG.error(COULD_NOT_DEREGISTER_DAL_CONNECTION, e);
        }
    }
    
    /**
     * Object-based adapter.
     * Adapts the IAlarmListener and the IAlarmConnectionMonitor
     * to the DynamicValueListener expected by DAL.
     */
    private static class DynamicValueListenerAdapter<T, P extends SimpleProperty<T>> extends
            DynamicValueAdapter<T, P> {
        private static final Logger LOG_INNER = LoggerFactory.getLogger(DynamicValueListenerAdapter.class);
        
        private final IAlarmListener _alarmListener;
        
        public DynamicValueListenerAdapter(@Nonnull final IAlarmListener alarmListener) {
            _alarmListener = alarmListener;
        }
        
        @Override
        public void conditionChange(@Nonnull final DynamicValueEvent<T, P> event) {
            logEvent("conditionChange", event);
        }
        
        @Override
        public void valueChanged(@CheckForNull final DynamicValueEvent<T, P> event) {
            logEvent("valueChanged", event);
            forwardEvent(event);
        }

        @Override
        public void valueUpdated(@CheckForNull final DynamicValueEvent<T, P> event) {
            logEvent("valueUpdated", event);
            forwardEvent(event);
        }
        
        private void logEvent(@Nonnull final String nameOfCallback,
                              @Nonnull final DynamicValueEvent<T, P> event) {
            try {
                LOG_INNER.trace("{} received {} for {} value {} / {}", new Object[] {
                        nameOfCallback, event.getCondition(), event.getProperty().getUniqueName(),
                        event.getValue(), event.getData().stringValue() });
            } catch (Exception e) {
                LOG_INNER.trace(nameOfCallback + " received but failed to retrieve data", e);
            }
        }

        private void forwardEvent(@Nonnull final DynamicValueEvent<T, P> event) {
            if (AlarmMessageDALImpl.canCreateAlarmMessageFrom(event.getProperty(), event.getData())) {
                _alarmListener.onMessage(AlarmMessageDALImpl.newAlarmMessage(event.getProperty(),
                                                                             event.getData()));
            } else {
                LOG_INNER.warn("Could not create alarm message for "
                        + event.getProperty().getUniqueName());
            }
        }
    }
    
    
    /**
     * These items are stored in a list for later disconnection.
     */
    // CHECKSTYLE:OFF
    private static final class ListenerItem {
        ConnectionParameters _connectionParameters;
        DynamicValueAdapter<String, StringProperty> _dynamicValueAdapter;
        Map<String, Object> _parameters;
    }
    // CHECKSTYLE:ON
    
}
