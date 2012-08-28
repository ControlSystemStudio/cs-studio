/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.alarm.dal2jms;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.AlarmConnectionException;
import org.csstudio.alarm.service.declaration.AlarmMessageKey;
import org.csstudio.alarm.service.declaration.AlarmResource;
import org.csstudio.alarm.service.declaration.IAlarmConnection;
import org.csstudio.alarm.service.declaration.IAlarmConnectionMonitor;
import org.csstudio.alarm.service.declaration.IAlarmListener;
import org.csstudio.alarm.service.declaration.IAlarmMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The alarm handler listens to DAL messages and forwards them to the JMS-based alarm system.
 * It is used in a headless application and allows the processing of alarm messages from IOCs which
 * are not connected to an interconnection server.
 * It utilizes a connection monitor and a message listener.
 *
 * @author jhatje
 * @author $Author$
 * @version $Revision$
 * @since 07.05.2010
 */
final class AlarmHandler {
    
    private static final Logger LOG = LoggerFactory.getLogger(AlarmHandler.class);
    
    private final IAlarmConnection _alarmConnection;
    // Local service to handle jms communication
    private final JmsMessageService _jmsMessageService;
    private final MessageWorker _messageWorker;
    private final AtomicBoolean _started = new AtomicBoolean(false);
    
    /**
     * Constructor.
     * @param alarmConnection
     * @param jmsMessageService
     */
    public AlarmHandler(@Nonnull final IAlarmConnection alarmConnection,
                        @Nonnull final JmsMessageService jmsMessageService) {
        _alarmConnection = alarmConnection;
        _jmsMessageService = jmsMessageService;
        _messageWorker = new MessageWorker(jmsMessageService);
        
    }
    
    public void connect() throws AlarmConnectionException {
        _alarmConnection.connect(newAlarmConnectionMonitor(),
                                 newAlarmListener(_jmsMessageService),
                                 new AlarmResource());
        // connect waited some time to let the callback storm of the connections pass by
        // so now we may forward alarm messages which have a true meaning (not being artifacts of the connecting dal2jms-server)
        LOG.info("dal2jms finished connecting, now forwarding of alarm messages begins");
        _started.set(true);
    }
    
    public void reconnect() throws AlarmConnectionException {
        _alarmConnection.reloadPVsFromResource();
    }
    
    // dal2jms currently provides no action on connection state changes, they are logged only.
    @Nonnull
    private IAlarmConnectionMonitor newAlarmConnectionMonitor() {
        return new IAlarmConnectionMonitor() {
            
            @SuppressWarnings("synthetic-access")
            @Override
            public void onDisconnect() {
                LOG.info("dal2jms monitors the jms connection: onDisconnect received");
            }
            
            @SuppressWarnings("synthetic-access")
            @Override
            public void onConnect() {
                LOG.info("dal2jms monitors the jms connection: onConnect received");
            }
        };
    }
    
    @Nonnull
    private IAlarmListener newAlarmListener(@Nonnull final JmsMessageService jmsMessageService) {
        return new IAlarmListener() {
            
            @SuppressWarnings("synthetic-access")
            @Override
            public void onMessage(@Nonnull final IAlarmMessage message) {
                LOG.trace("dal2jms received onMessage: {}, value: {}", message, message.getString(AlarmMessageKey.VALUE));
                if (_started.get()) {
                    _messageWorker.enqueue(message);
                }
            }
            
            @Override
            @SuppressWarnings("synthetic-access")
            public void stop() {
                LOG.trace("dal2jms received stop");
                // Nothing to do
            }
            
        };
    }
    
}
