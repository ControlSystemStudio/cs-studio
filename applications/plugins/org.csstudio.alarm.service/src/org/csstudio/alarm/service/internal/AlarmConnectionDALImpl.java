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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.AlarmConnectionException;
import org.csstudio.alarm.service.declaration.AlarmPreference;
import org.csstudio.alarm.service.declaration.IAlarmConfigurationService;
import org.csstudio.alarm.service.declaration.IAlarmConnection;
import org.csstudio.alarm.service.declaration.IAlarmConnectionMonitor;
import org.csstudio.alarm.service.declaration.IAlarmListener;
import org.csstudio.alarm.service.declaration.IAlarmResource;
import org.csstudio.alarm.service.internal.localization.Messages;
import org.csstudio.utility.ldap.service.LdapServiceException;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration;
import org.csstudio.utility.treemodel.ContentModel;
import org.csstudio.utility.treemodel.CreateContentModelException;
import org.epics.css.dal.DynamicValueAdapter;
import org.epics.css.dal.DynamicValueEvent;
import org.epics.css.dal.SimpleProperty;
import org.epics.css.dal.StringProperty;
import org.epics.css.dal.simple.ConnectionParameters;
import org.epics.css.dal.simple.RemoteInfo;
import org.epics.css.dal.simple.SimpleDALBroker;
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

    private final IAlarmConfigurationService _alarmConfigService;
    private final SimpleDALBroker _simpleDALBroker;

    private final Map<String, ListenerItem> _pv2listenerItem = new HashMap<String, AlarmConnectionDALImpl.ListenerItem>();

    // The listener and resource are given once at connect
    private IAlarmListener _listener;
    private IAlarmResource _resource;

    /**
     * Constructor must be called only from the AlarmService.
     *
     * @param alarmConfigService
     * @param simpleDALBroker
     */
    public AlarmConnectionDALImpl(@Nonnull final IAlarmConfigurationService alarmConfigService, @Nonnull final SimpleDALBroker simpleDALBroker) {
        _alarmConfigService = alarmConfigService;
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
                        @Nonnull final IAlarmResource resource) throws AlarmConnectionException {
        LOG.info("Connecting to DAL for resource " + resource + ".");

        _listener = listener;
        _resource = resource;
        registerAllFromResource();

        // The DAL implementation sends connect here, because the DynamicValueListenerAdapter will not do so
        connectionMonitor.onConnect();
    }

    private void registerAllFromResource() throws AlarmConnectionException {
        final Set<String> simpleNames = getPVNamesFromResource();
        for (final String recordName : simpleNames) {
            LOG.debug("Connecting to " + recordName);
            registerPV(recordName);
        }
    }

    /**
     * This method encapsulates access to the framework. It may be overridden by a test so it is not necessary to run a plugin test.
     *
     * @return all the pv names from the initially given resource
     * @throws AlarmConnectionException
     */
    @Nonnull
    protected Set<String> getPVNamesFromResource() throws AlarmConnectionException {
        ContentModel<LdapEpicsAlarmcfgConfiguration> model = null;
        try {
            if (AlarmPreference.ALARMSERVICE_CONFIG_VIA_LDAP.getValue()) {
                model = _alarmConfigService.retrieveInitialContentModel(AlarmPreference.getFacilityNames());
            } else {
                model = _alarmConfigService.retrieveInitialContentModelFromFile(_resource
                        .getFilepath());
            }
        } catch (final CreateContentModelException e) {
            LOG.error("Could not create content model", e);
            throw new AlarmConnectionException(Messages.CouldNotCreateContentModel, e);
        } catch (final FileNotFoundException e) {
            LOG.error("Resource not found: " + _resource.getFilepath(), e);
            throw new AlarmConnectionException(Messages.ResourceNotFound + ": " + _resource.getFilepath(), e);
        } catch (final LdapServiceException e) {
            throw new AlarmConnectionException(Messages.LdapServiceError, e);
        }
        return model.getSimpleNames(LdapEpicsAlarmcfgConfiguration.RECORD);
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
        final ListenerItem item = _pv2listenerItem.remove(pvName);
        if (item != null) {
            disconnectItem(item);
        } else {
            LOG.warn("Trying to deregister a pv named '" + pvName + "' which was not registered.");
        }
    }

    @Override
    public void reloadPVsFromResource() throws AlarmConnectionException {
        deregisterAll();
        registerAllFromResource();
    }



    private void deregisterAll() {
        LOG.debug("Deregistering all PVs.");
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
        LOG.debug("Disconnecting from DAL.");
        deregisterAll();
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
        private static final Logger LOG_INNER = LoggerFactory
                .getLogger(AlarmConnectionDALImpl.DynamicValueListenerAdapter.class);

        private final IAlarmListener _alarmListener;

        public DynamicValueListenerAdapter(@Nonnull final IAlarmListener alarmListener
//                                           ,
//                                           @Nonnull final IAlarmConnectionMonitor alarmConnectionMonitor
                                           ) {
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
//                LOG_INNER.debug("valueChanged received " + event.getCondition() + " for "
//                        + event.getProperty().getUniqueName());
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
