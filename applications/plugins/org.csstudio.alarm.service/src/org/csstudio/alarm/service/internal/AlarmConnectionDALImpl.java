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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.AlarmConnectionException;
import org.csstudio.alarm.service.declaration.IAlarmConfigurationService;
import org.csstudio.alarm.service.declaration.IAlarmConnection;
import org.csstudio.alarm.service.declaration.IAlarmConnectionMonitor;
import org.csstudio.alarm.service.declaration.IAlarmListener;
import org.csstudio.alarm.service.declaration.LdapEpicsAlarmCfgObjectClass;
import org.csstudio.dal.DalPlugin;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.model.ContentModel;
import org.epics.css.dal.DoubleProperty;
import org.epics.css.dal.DynamicValueAdapter;
import org.epics.css.dal.DynamicValueEvent;
import org.epics.css.dal.SimpleProperty;
import org.epics.css.dal.simple.ConnectionParameters;
import org.epics.css.dal.simple.RemoteInfo;
import org.epics.css.dal.simple.SimpleDALBroker;

import com.cosylab.util.CommonException;

/**
 * This is the DAL based implementation of the AlarmConnection.
 *
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 21.04.2010
 */
public final class AlarmConnectionDALImpl implements IAlarmConnection {
    private static final String COULD_NOT_CREATE_DAL_CONNECTION = "Could not create DAL connection";
    private static final String COULD_NOT_DEREGISTER_DAL_CONNECTION = "Could not deregister DAL connection";

    private final CentralLogger _log = CentralLogger.getInstance();

    private final List<ListenerItem> _listenerItems = new ArrayList<ListenerItem>();
    private final IAlarmConfigurationService _alarmConfigService;

    /**
     * Constructor must be called only from the AlarmService.
     *
     * @param alarmConfigService .
     */
    AlarmConnectionDALImpl(@Nonnull final IAlarmConfigurationService alarmConfigService) {
        _alarmConfigService = alarmConfigService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canHandleTopics() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect() {
        _log.debug(this, "Disconnecting from DAL.");
        for (final ListenerItem item : _listenerItems) {
            try {
                SimpleDALBroker.getInstance().deregisterListener(item._connectionParameters,
                                                                 item._dynamicValueAdapter,
                                                                 item._parameters);
            } catch (final InstantiationException e) {
                _log.error(this, COULD_NOT_DEREGISTER_DAL_CONNECTION, e);
            } catch (final CommonException e) {
                _log.error(this, COULD_NOT_DEREGISTER_DAL_CONNECTION, e);
            }
        }
        _listenerItems.clear();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connectWithListener(@Nonnull final IAlarmConnectionMonitor connectionMonitor,
                                    @Nonnull final IAlarmListener listener) throws AlarmConnectionException {
        connectWithListenerForTopics(connectionMonitor, listener, new String[0]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connectWithListenerForTopics(@Nonnull final IAlarmConnectionMonitor connectionMonitor,
                                             @Nonnull final IAlarmListener listener,
                                             @Nonnull final String[] topics) throws AlarmConnectionException {
        _log.info(this, "Connecting to DAL for topics " + Arrays.toString(topics) + ".");

        // TODO jp error handling currently removed
        bla(connectionMonitor, listener);
    }

    private void connectToPV(@Nonnull final IAlarmConnectionMonitor connectionMonitor,
                             @Nonnull final IAlarmListener listener,
                             @Nonnull final String pvName) {

        final RemoteInfo remoteInfo = new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX + "EPICS",
                                                     pvName,
                                                     null,
                                                     null);

        final ListenerItem item = new ListenerItem();
        // TODO jp Review: hard coded Double.class in connection parameter
        item._connectionParameters = new ConnectionParameters(remoteInfo, Double.class);
        item._dynamicValueAdapter = new DynamicValueListenerAdapter<Double, DoubleProperty>(listener, connectionMonitor);
        // TODO jp use constants for parameterization
        item._parameters = new HashMap<String, Object>();
        // item._parameters.put("EPICSPlug.monitor.mask", 4); // EPICSPlug.PARAMETER_MONITOR_MASK = Monitor.ALARM

        try {
            DalPlugin.getDefault().getSimpleDALBroker()
                    .registerListener(item._connectionParameters,
                                      item._dynamicValueAdapter,
                                      item._parameters);
        } catch (InstantiationException e) {
            _log.error(this, COULD_NOT_CREATE_DAL_CONNECTION, e);
        } catch (CommonException e) {
            _log.error(this, COULD_NOT_CREATE_DAL_CONNECTION, e);
        }

        _listenerItems.add(item);
    }

    private void bla(@Nonnull final IAlarmConnectionMonitor connectionMonitor, @Nonnull final IAlarmListener listener) {

        // TODO jp the facilities must be given as parameter
        List<String> facilitiesAsString = new ArrayList<String>();
        facilitiesAsString.add("Test");
        final ContentModel<LdapEpicsAlarmCfgObjectClass> model = _alarmConfigService
                .retrieveInitialContentModel(facilitiesAsString);

        for (final String recordName : model.getSimpleNames(LdapEpicsAlarmCfgObjectClass.RECORD)) {
            _log.debug(this, "Connecting to " + recordName);
            connectToPV(connectionMonitor, listener, recordName);
        }
        //        connectToPV(connectionMonitor, listener, "alarmTest:RAMPA_calc");
        //        connectToPV(connectionMonitor, listener, "alarmTest:RAMPB_calc");
        //        connectToPV(connectionMonitor, listener, "alarmTest:RAMPC_calc");
    }

    /**
     * Object-based adapter.
     * Adapts the IAlarmListener and the IAlarmConnectionMonitor
     * to the DynamicValueListener expected by DAL.
     */
    private static class DynamicValueListenerAdapter<T, P extends SimpleProperty<T>> extends DynamicValueAdapter<T, P> {

        private final IAlarmListener _alarmListener;
        private final IAlarmConnectionMonitor _connectionMonitor;

        public DynamicValueListenerAdapter(@Nonnull final IAlarmListener alarmListener,
                                           @Nonnull final IAlarmConnectionMonitor alarmConnectionMonitor) {
            _alarmListener = alarmListener;
            _connectionMonitor = alarmConnectionMonitor;
        }

        @Override
        public void conditionChange(@Nonnull final DynamicValueEvent<T, P> event) {
            // TODO jp process state change correctly
            processEvent("conditionChange", event);
        }

        @Override
        public void valueChanged(@Nonnull final DynamicValueEvent<T, P> event) {
            processEvent("valueChanged", event);
        }

        private void processEvent(@Nonnull final String text, @Nonnull final DynamicValueEvent<T, P> event) {
            // TODO jp Review access to event
            CentralLogger.getInstance().debug(this,
                                              text + " received " + event.getCondition() + " for "
                                              + event.getProperty().getUniqueName());
            if (event.getProperty().isConnected()) {
                _alarmListener.onMessage(new AlarmMessageDALImpl(event.getData()));
            }
        }

    }

    /**
     * These items are stored in a list for later disconnection.
     */
    // CHECKSTYLE:OFF
    private static final class ListenerItem {
        ConnectionParameters _connectionParameters;
        DynamicValueAdapter<?, ?> _dynamicValueAdapter;
        Map<String, Object> _parameters;
    }
    // CHECKSTYLE:ON

}
