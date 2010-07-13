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

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.alarm.service.declaration.AlarmConnectionException;
import org.csstudio.alarm.service.declaration.AlarmPreference;
import org.csstudio.alarm.service.declaration.IAlarmConfigurationService;
import org.csstudio.alarm.service.declaration.IAlarmConnection;
import org.csstudio.alarm.service.declaration.IAlarmConnectionMonitor;
import org.csstudio.alarm.service.declaration.IAlarmListener;
import org.csstudio.alarm.service.declaration.IAlarmResource;
import org.csstudio.alarm.service.declaration.LdapEpicsAlarmcfgConfiguration;
import org.csstudio.dal.DalPlugin;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.treemodel.ContentModel;
import org.csstudio.utility.treemodel.CreateContentModelException;
import org.epics.css.dal.DoubleProperty;
import org.epics.css.dal.DynamicValueAdapter;
import org.epics.css.dal.DynamicValueEvent;
import org.epics.css.dal.SimpleProperty;
import org.epics.css.dal.simple.ConnectionParameters;
import org.epics.css.dal.simple.RemoteInfo;

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

    private static final Logger LOG = CentralLogger.getInstance()
            .getLogger(AlarmConnectionDALImpl.class);

    private static final String COULD_NOT_RETRIEVE_INITIAL_CONTENT_MODEL = "Could not retrieve initial content model";
    private static final String COULD_NOT_CREATE_DAL_CONNECTION = "Could not create DAL connection";
    private static final String COULD_NOT_DEREGISTER_DAL_CONNECTION = "Could not deregister DAL connection";

    private final List<ListenerItem> _listenerItems = new ArrayList<ListenerItem>();
    private final IAlarmConfigurationService _alarmConfigService;

    /**
     * Constructor must be called only from the AlarmService.
     *
     * @param alarmConfigService .
     */
    public AlarmConnectionDALImpl(@Nonnull final IAlarmConfigurationService alarmConfigService) {
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
        LOG.debug("Disconnecting from DAL.");
        for (final ListenerItem item : _listenerItems) {
            try {
                DalPlugin.getDefault().getSimpleDALBroker()
                        .deregisterListener(item._connectionParameters,
                                            item._dynamicValueAdapter,
                                            item._parameters);
            } catch (final InstantiationException e) {
                LOG.error(COULD_NOT_DEREGISTER_DAL_CONNECTION, e);
            } catch (final CommonException e) {
                LOG.error(COULD_NOT_DEREGISTER_DAL_CONNECTION, e);
            }
        }
        _listenerItems.clear();

    }

    @Override
    public void connectWithListenerForResource(@Nonnull final IAlarmConnectionMonitor connectionMonitor,
                                               @Nonnull final IAlarmListener listener,
                                               @Nonnull final IAlarmResource resource) throws AlarmConnectionException {
        LOG.info("Connecting to DAL for resource " + resource + ".");

        try {
            ContentModel<LdapEpicsAlarmcfgConfiguration> model = null;
            if (AlarmPreference.ALARMSERVICE_CONFIG_VIA_LDAP.getValue()) {
                model = _alarmConfigService.retrieveInitialContentModel(resource.getFacilities());
            } else {
                model = _alarmConfigService.retrieveInitialContentModelFromFile(resource.getFilepath());
            }

            for (final String recordName : model
                    .getSimpleNames(LdapEpicsAlarmcfgConfiguration.RECORD)) {
                LOG.debug("Connecting to " + recordName);
                connectToPV(connectionMonitor, listener, recordName);
            }

            // The DAL implementation sends connect here, because the DynamicValueListenerAdapter will not do so
            connectionMonitor.onConnect();
        } catch (final CreateContentModelException e) {
            LOG.error(COULD_NOT_RETRIEVE_INITIAL_CONTENT_MODEL, e);
            throw new AlarmConnectionException(COULD_NOT_RETRIEVE_INITIAL_CONTENT_MODEL, e);
        } catch (final FileNotFoundException e) {
            LOG.error("Resource " + resource.getFilepath() + " could not be found.", e);
            throw new AlarmConnectionException("Resource " + resource.getFilepath() + " could not be found.", e);
        }

    }

    private void connectToPV(@Nonnull final IAlarmConnectionMonitor connectionMonitor,
                             @Nonnull final IAlarmListener listener,
                             @Nonnull final String pvName) {

        final RemoteInfo remoteInfo = new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX + "EPICS",
                                                     pvName,
                                                     null,
                                                     null);

        final ListenerItem item = new ListenerItem();
        // TODO (jpenning) Review: hard coded Double.class in connection parameter
        item._connectionParameters = new ConnectionParameters(remoteInfo, Double.class);
        item._dynamicValueAdapter = new DynamicValueListenerAdapter<Double, DoubleProperty>(listener,
                                                                                            connectionMonitor);
        // TODO (jpenning) use constants for parameterization of expert mode
        item._parameters = new HashMap<String, Object>();
        item._parameters.put("EPICSPlug.monitor.mask", 4); // EPICSPlug.PARAMETER_MONITOR_MASK = Monitor.ALARM

        try {
            DalPlugin.getDefault().getSimpleDALBroker()
                    .registerListener(item._connectionParameters,
                                      item._dynamicValueAdapter,
                                      item._parameters);
            _listenerItems.add(item);
        } catch (final InstantiationException e) {
            LOG.error(COULD_NOT_CREATE_DAL_CONNECTION, e);
        } catch (final CommonException e) {
            LOG.error(COULD_NOT_CREATE_DAL_CONNECTION, e);
        }
    }

    /**
     * Object-based adapter.
     * Adapts the IAlarmListener and the IAlarmConnectionMonitor
     * to the DynamicValueListener expected by DAL.
     */
    private static class DynamicValueListenerAdapter<T, P extends SimpleProperty<T>> extends
            DynamicValueAdapter<T, P> {
        private static final Logger LOG_INNER = CentralLogger.getInstance()
                .getLogger(AlarmConnectionDALImpl.DynamicValueListenerAdapter.class);

        private final IAlarmListener _alarmListener;

        public DynamicValueListenerAdapter(@Nonnull final IAlarmListener alarmListener,
                                           @Nonnull final IAlarmConnectionMonitor alarmConnectionMonitor) {
            // The alarmConnectionMonitor is not used by the DynamicValueListenerAdapter, instead the connect is sent
            // directly after connectWithListenerForResource()
            _alarmListener = alarmListener;
        }

        @Override
        public void conditionChange(@CheckForNull final DynamicValueEvent<T, P> event) {
            // Currently we are not interested in conditionChange-Events
//            LOG_INNER.debug("conditionChange received " + event.getCondition() + " for "
//                    + event.getProperty().getUniqueName());
        }

        @Override
        public void valueChanged(@CheckForNull final DynamicValueEvent<T, P> event) {
            if (event != null) {
                LOG_INNER.debug("valueChanged received " + event.getCondition() + " for "
                        + event.getProperty().getUniqueName());
                if (AlarmMessageDALImpl.canCreateAlarmMessageFrom(event.getProperty(), event
                        .getData())) {
                    _alarmListener.onMessage(AlarmMessageDALImpl.newAlarmMessage(event
                            .getProperty(), event.getData()));
                } else {
                    LOG_INNER.warn("Could not create alarm message for "
                            + event.getProperty().getUniqueName());
                }
            } // else ignore
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
