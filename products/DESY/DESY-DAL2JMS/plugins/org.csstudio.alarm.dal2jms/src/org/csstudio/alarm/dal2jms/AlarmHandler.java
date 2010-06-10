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
import org.csstudio.alarm.service.declaration.AlarmMessageKey;
import org.csstudio.alarm.service.declaration.IAlarmConnection;
import org.csstudio.alarm.service.declaration.IAlarmConnectionMonitor;
import org.csstudio.alarm.service.declaration.IAlarmListener;
import org.csstudio.alarm.service.declaration.IAlarmMessage;
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

    public void connect() throws AlarmConnectionException {
        // TODO (jpenning) use ldap or xml based configuration
        _alarmConnection.connectWithListener(newAlarmConnectionMonitor(),
                                             newAlarmListener(_jmsMessageService),
                                             "c:\\dal2jmsConfig.xml");
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
                LOG.debug(message.getMap().toString());

                if (isAlarmMessageOk(message)) {
                    jmsMessageService.sendAlarmMessage(message);
                } else {
                    LOG.error("Cannot convert alarm message to jms message: "
                            + message.getMap().toString());
                }
            }

            @Override
            public void stop() {
                // Nothing to do
            }

        };
    }

    // TODO (jpenning) move predicate to alarm message. and fix it.
    private boolean isAlarmMessageOk(@Nonnull final IAlarmMessage message) {
        boolean result = true;
        for (final AlarmMessageKey key : AlarmMessageKey.values()) {
            final String value = message.getString(key);
            result = result && (value != null);
            result = result && (!value.equals("noTimeStamp"));
            result = result && (!value.equals("Uninitialized"));
            result = result && (!value.equals("noMetaData"));
            if (!result) {
                break;
            }
        }
        return result;
    }

}
