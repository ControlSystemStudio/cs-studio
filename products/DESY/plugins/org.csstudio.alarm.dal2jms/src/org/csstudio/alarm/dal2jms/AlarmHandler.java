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


import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.alarm.service.declaration.AlarmConnectionException;
import org.csstudio.alarm.service.declaration.IAlarmConnection;
import org.csstudio.alarm.service.declaration.IAlarmConnectionMonitor;
import org.csstudio.alarm.service.declaration.IAlarmListener;
import org.csstudio.alarm.service.declaration.IAlarmMessage;
import org.csstudio.alarm.service.declaration.IAlarmResource;
import org.csstudio.platform.logging.CentralLogger;

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

    private static final Logger LOG = CentralLogger.getInstance().getLogger(AlarmHandler.class);

    private final IAlarmConnection _alarmConnection;
    // Local service to handle jms communication
    private final JmsMessageService _jmsMessageService;

    /**
     * Constructor.
     * @param alarmConnection
     * @param jmsMessageService
     */
    public AlarmHandler(@Nonnull final IAlarmConnection alarmConnection,
                        @Nonnull final JmsMessageService jmsMessageService) {
        _alarmConnection = alarmConnection;
        _jmsMessageService = jmsMessageService;
    }

    public void connect(@Nonnull final String fileName) throws AlarmConnectionException {
        final IAlarmResource alarmResource = Activator.getDefault().getAlarmService()
                .createAlarmResource(null, fileName);
        _alarmConnection.connect(newAlarmConnectionMonitor(),
                                                        newAlarmListener(_jmsMessageService),
                                                        alarmResource);
    }

    // dal2jms currently provides no action on connection state changes, they are only logged.
    @Nonnull
    private IAlarmConnectionMonitor newAlarmConnectionMonitor() {
        return new IAlarmConnectionMonitor() {

            @Override
            public void onDisconnect() {
                LOG.debug("dal2jms received onDisconnect");
            }

            @Override
            public void onConnect() {
                LOG.debug("dal2jms received onConnect");
            }
        };
    }

    @Nonnull
    private IAlarmListener newAlarmListener(@Nonnull final JmsMessageService jmsMessageService) {
        return new IAlarmListener() {

            @Override
            public void onMessage(@Nonnull final IAlarmMessage message) {
//                LOG.debug(message.getMap().toString());
                jmsMessageService.sendAlarmMessage(message);
            }

            @Override
            public void stop() {
                // Nothing to do
            }

        };
    }

}
