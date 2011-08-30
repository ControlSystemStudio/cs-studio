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

import javax.annotation.Nonnull;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.csstudio.alarm.service.declaration.AlarmConnectionException;
import org.csstudio.alarm.service.declaration.IAlarmConnection;
import org.csstudio.alarm.service.declaration.IAlarmConnectionMonitor;
import org.csstudio.alarm.service.declaration.IAlarmListener;
import org.csstudio.alarm.service.declaration.IAlarmResource;
import org.csstudio.platform.utility.jms.IConnectionMonitor;
import org.csstudio.platform.utility.jms.sharedconnection.IMessageListenerSession;
import org.csstudio.platform.utility.jms.sharedconnection.SharedJmsConnections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the JMS based implementation of the AlarmConnection.
 *
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 21.04.2010
 */
public final class AlarmConnectionJMSImpl implements IAlarmConnection {
    private static final Logger LOG = LoggerFactory.getLogger(AlarmConnectionJMSImpl.class);

    private static final String COULD_NOT_CREATE_LISTENER_SESSION = "Could not create listener session using the shared JMS connections";

    private AlarmListenerAdapter _listener;
    private IMessageListenerSession _listenerSession;
    private AlarmConnectionMonitorAdapter _monitor;

    /**
     * Constructor must be called only from the AlarmService.
     */
    AlarmConnectionJMSImpl() {
        // EMPTY
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canHandleTopics() {
        return true;
    }

    @Override
    public void connect(@Nonnull final IAlarmConnectionMonitor connectionMonitor,
                                               @Nonnull final IAlarmListener listener,
                                               @Nonnull final IAlarmResource resource) throws AlarmConnectionException {
        LOG.info("Connecting to JMS for topics " + resource.getTopics() + ".");

        try {
            _listener = new AlarmListenerAdapter(listener);
            _listenerSession = SharedJmsConnections.startMessageListener(_listener,
                                                                         resource.getTopics().toArray(new String[0]),
                                                                         Session.AUTO_ACKNOWLEDGE);

            _monitor = new AlarmConnectionMonitorAdapter(connectionMonitor);
            _listenerSession.addMonitor(_monitor);
            if (_listenerSession.isActive()) {
                _monitor.onConnected();
            }
        } catch (final JMSException e) {
            LOG.error(COULD_NOT_CREATE_LISTENER_SESSION);
            throw new AlarmConnectionException(COULD_NOT_CREATE_LISTENER_SESSION, e);
        }
    }

    @Override
    public void registerPV(@Nonnull final String pvName) {
        // Nothing to do in the JMS implementation
    }

    @Override
    public void deregisterPV(@Nonnull final String pvName) {
        // Nothing to do in the JMS implementation
    }

    @Override
    public void reloadPVsFromResource() {
        // Nothing to do in the JMS implementation
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect() {
        LOG.info("Disconnecting from JMS.");

        // Remove the connection monitor, so it will not be called when the connection is closed.
        _listenerSession.removeMonitor(_monitor);

        _listenerSession.close();

        _listener.getAlarmListener().stop();
    }

    /**
     * Object based adapter. Adapts the IAlarmListener to the MessageListener expected by JMS.
     */
    private static final class AlarmListenerAdapter implements MessageListener {

        private final IAlarmListener _alarmListener;

        public AlarmListenerAdapter(@Nonnull final IAlarmListener alarmListener) {
            this._alarmListener = alarmListener;
        }

        @SuppressWarnings("synthetic-access")
        @Override
        public void onMessage(@Nonnull final Message message) {
            if (AlarmMessageJMSImpl.canCreateAlarmMessageFrom(message)) {
                _alarmListener.onMessage(AlarmMessageJMSImpl.newAlarmMessage(message));
            } else {
                LOG.warn("Could not create alarm message from " + message);
            }
        }

        @Nonnull
        public IAlarmListener getAlarmListener() {
            return _alarmListener;
        }

    }

    /**
     * Object based adapter. Adapts the IAlarmConnectionMonitor to the IConnectionMonitor expected
     * by JMS.
     */
    private static final class AlarmConnectionMonitorAdapter implements IConnectionMonitor {

        private final IAlarmConnectionMonitor _monitor;

        public AlarmConnectionMonitorAdapter(@Nonnull final IAlarmConnectionMonitor monitor) {
            this._monitor = monitor;
        }

        @Override
        public void onConnected() {
            _monitor.onConnect();
        }

        @Override
        public void onDisconnected() {
            _monitor.onDisconnect();
        }

    }

}
