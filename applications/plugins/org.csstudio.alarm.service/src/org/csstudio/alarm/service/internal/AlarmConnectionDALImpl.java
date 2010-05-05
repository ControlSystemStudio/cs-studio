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
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.AlarmConnectionException;
import org.csstudio.alarm.service.declaration.IAlarmConnection;
import org.csstudio.alarm.service.declaration.IAlarmConnectionMonitor;
import org.csstudio.alarm.service.declaration.IAlarmListener;
import org.csstudio.alarm.service.declaration.IAlarmMessage;
import org.csstudio.dal.DalPlugin;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.model.LdapContentModel;
import org.csstudio.utility.ldap.model.Record;
import org.csstudio.utility.ldap.reader.LdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;
import org.epics.css.dal.simple.AnyData;
import org.epics.css.dal.simple.AnyDataChannel;
import org.epics.css.dal.simple.ChannelListener;
import org.epics.css.dal.simple.ConnectionParameters;
import org.epics.css.dal.simple.MetaData;
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
    private static final String COULD_NOT_RETRIEVE_FROM_LDAP = "Could not retrieve records from LDAP";
    private static final String COULD_NOT_CREATE_DAL_CONNECTION = "Could not create DAL connection";
    private static final String COULD_NOT_DEREGISTER_DAL_CONNECTION = "Could not deregister DAL connection";
    
    private final CentralLogger _log = CentralLogger.getInstance();
    
    private final List<ListenerItem> _listenerItems = new ArrayList<ListenerItem>();
    private final ILdapService _ldapService;
    
    /**
     * Constructor must be called only from the AlarmService.
     * 
     * @param ldapService .
     */
    AlarmConnectionDALImpl(final ILdapService ldapService) {
        _ldapService = ldapService;
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
        for (ListenerItem item : _listenerItems) {
            try {
                SimpleDALBroker.getInstance().deregisterListener(item._connectionParameters,
                                                                 item._channelListener);
            } catch (InstantiationException e) {
                _log.error(this, COULD_NOT_DEREGISTER_DAL_CONNECTION, e);
            } catch (CommonException e) {
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
    
    private void connectToPV(final IAlarmConnectionMonitor connectionMonitor,
                             final IAlarmListener listener,
                             final String pvName) {
        RemoteInfo remoteInfo = new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX + "EPICS",
                                               pvName,
                                               null,
                                               null);
        ListenerItem item = new ListenerItem();
        item._connectionParameters = new ConnectionParameters(remoteInfo, Double.class);
        item._channelListener = new ChannelListenerAdapter(listener, connectionMonitor);
        
        try {
            DalPlugin.getDefault().getSimpleDALBroker()
            .registerListener(item._connectionParameters, item._channelListener);
        } catch (InstantiationException e) {
            _log.error(this, COULD_NOT_CREATE_DAL_CONNECTION, e);
        } catch (CommonException e) {
            _log.error(this, COULD_NOT_CREATE_DAL_CONNECTION, e);
        }
        _listenerItems.add(item);
    }
    
    private void bla(final IAlarmConnectionMonitor connectionMonitor, final IAlarmListener listener) {
        LdapSearchResult result = null;
        try {
            result = _ldapService.retrieveRecordsForIOC("TEST", "berndTest");
        } catch (InterruptedException e) {
            _log.error(this, COULD_NOT_RETRIEVE_FROM_LDAP, e);
        }
        
        if (result != null) {
            final LdapContentModel model = new LdapContentModel(result);
            Set<Record> records = model.getRecords("berndTest");
            for (Record record : records) {
                _log.debug(this, record.getName());
                connectToPV(connectionMonitor, listener, record.getName());
            }
        }
    }
    
    /**
     * Object-based adapter.<br>
     * Adapts the IAlarmListener and the IAlarmConnectionMonitor to the ChannelListener expected by
     * DAL.
     */
    private static final class ChannelListenerAdapter implements ChannelListener {
        private final IAlarmListener _alarmListener;
        private final IAlarmConnectionMonitor _connectionMonitor;
        
        // TODO jp connection monitor ignored in DAL impl
        // TODO jp message analyzing unsufficient in DAL impl
        // TODO jp sync issue: may these methods be called by different threads?
        
        public ChannelListenerAdapter(final IAlarmListener alarmListener,
                                      final IAlarmConnectionMonitor connectionMonitor) {
            _alarmListener = alarmListener;
            _connectionMonitor = connectionMonitor;
        }
        
        @Override
        public void channelDataUpdate(final AnyDataChannel channel) {
            AnyData data = channel.getData();
            MetaData meta = data != null ? data.getMetaData() : null;
            
            IAlarmMessage message = new AlarmMessageDALImpl(data);
            _alarmListener.onMessage(message);
            
            CentralLogger.getInstance()
            .debug(this,
                   "Channel Data Update Received (" + (data != null ? data : "no value")
                   + "; " + (meta != null ? meta : "no metadata") + ")");
        }
        
        @Override
        public void channelStateUpdate(final AnyDataChannel channel) {
            CentralLogger.getInstance().debug(this,
                                              "Channel State Update Received: "
                                              + channel.getStateInfo());
        }
        
    }
    
    /**
     * These items are stored in a list for later disconnection.
     */
    private static final class ListenerItem {
        ConnectionParameters _connectionParameters;
        ChannelListener _channelListener;
    }
    
}
