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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.jms.JMSException;
import javax.jms.MapMessage;

import org.apache.log4j.Logger;
import org.csstudio.alarm.dal2jms.JmsMessage.JmsMessageType;
import org.csstudio.alarm.service.declaration.AlarmConnectionException;
import org.csstudio.alarm.service.declaration.AlarmMessageKey;
import org.csstudio.alarm.service.declaration.IAlarmConnection;
import org.csstudio.alarm.service.declaration.IAlarmConnectionMonitor;
import org.csstudio.alarm.service.declaration.IAlarmListener;
import org.csstudio.alarm.service.declaration.IAlarmMessage;
import org.csstudio.alarm.service.declaration.IAlarmService;
import org.csstudio.platform.logging.CentralLogger;

/**
 * The alarm handler listens to DAL messages and forwards them to the JMS-based alarm system.
 * It is used in a headless application and allows the processing of alarm messages from IOCs which
 * are not connected to
 *
 * @author jhatje
 * @author $Author$
 * @version $Revision$
 * @since 07.05.2010
 */
public final class AlarmHandler {

    private static final Logger LOG = CentralLogger.getInstance().getLogger(AlarmHandler.class);

    private IAlarmConnection _connection = null;

    public AlarmHandler() {

        final IAlarmService alarmService = Activator.getDefault().getAlarmService();
        if (alarmService == null) {
            LOG.error("Alarm service must not be null. Connection cannot be established.");
            return;
        }

        _connection = alarmService.newAlarmConnection();
        if (_connection == null) {
            LOG.error("Alarm service returned null for connection.");
            return;
        }

        try {
            _connection.connectWithListener(createAlarmConnectionMonitor(),
                                            createAlarmListener(),
                                            "c:\\dal2jmsConfig.xml");
        } catch (final AlarmConnectionException e) {
            LOG.error("Error. Could not connect.", e);
        }
    }

    @Nonnull
    private IAlarmConnectionMonitor createAlarmConnectionMonitor() {
        return new IAlarmConnectionMonitor() {

            @Override
            public void onDisconnect() {
                // Nothing to do
            }

            @Override
            public void onConnect() {
                // Nothing to do
            }
        };
    }

    @Nonnull
    private IAlarmListener createAlarmListener() {
        return new IAlarmListener() {

            @Override
            public void onMessage(@Nonnull final IAlarmMessage message) {
                try {
                    LOG.debug(message.getMap().toString());

                    final MapMessage mapMessage = getMapMessage(message);
                    if (mapMessage != null) {
                        JmsMessage.INSTANCE.sendMessage(JmsMessageType.JMS_MESSAGE_TYPE_ALARM, mapMessage);
                    } else {
                        LOG.debug("INVALID message !");
                    }
                } catch (final JMSException e) {
                    LOG.error("Error while creating mapMessage", e);
                }
            }

            @Override
            public void stop() {
                // Nothing to do
            }

        };
    }

    @CheckForNull
    private MapMessage getMapMessage(@Nonnull final IAlarmMessage message) throws JMSException {

        final MapMessage result = JmsMessage.INSTANCE.createJmsSession().createMapMessage();

        for (final AlarmMessageKey key : AlarmMessageKey.values()) {
            /*
             * if the value is noTimeStamp or Uninitialized or noMetaData
             * return null -> do NOT create message !
             */
            final String value = message.getString(key);
            if ((value != null) && !value.equals("noTimeStamp") && !value.equals("Uninitialized")
                    && !value.equals("noMetaData")) {
                result.setString(key.name(), value);
            } else {
                return null;
            }
        }
        return result;
    }

}
