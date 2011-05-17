/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchroton,
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
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.log4j.Logger;
import org.csstudio.alarm.dal2jms.preferences.Preference;
import org.csstudio.alarm.service.declaration.AlarmMessageKey;
import org.csstudio.alarm.service.declaration.IAlarmMessage;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.utility.jms.sharedconnection.SharedJmsConnections;

/**
 * Local service directly used by the alarm handler.
 *
 * Used for mapping from alarm message to jms message and for sending jms messages.
 *
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 09.06.2010
 */
class JmsMessageService {

    private static final Logger LOG = CentralLogger.getInstance()
            .getLogger(JmsMessageService.class);

    // Given as preference
    private final String _alarmTopicName;

    // Given as preference
    private final int _timeToLiveForAlarms;

    public JmsMessageService() {
        _timeToLiveForAlarms = Preference.JMS_TIME_TO_LIVE_ALARMS.getValue();
        _alarmTopicName = Preference.JMS_ALARM_TOPIC_NAME.getValue();
    }

    public void sendAlarmMessage(@Nonnull final IAlarmMessage alarmMessage) {
        Session session = null;
        try {
            session = newSession();
            MapMessage message = session.createMapMessage();
            copyAlarmMsgToJmsMsg(alarmMessage, message);
            sendViaMessageProducer(session, message);
        } catch (final JMSException jmse) {
            LOG.debug("dal2jms could not forward alarm message.", jmse);
        } finally {
            tryToCloseSession(session);
        }
    }

    private void sendViaMessageProducer(@Nonnull final Session session,
                                        @Nonnull final MapMessage message) throws JMSException {
        MessageProducer producer = null;
        try {
            producer = newMessageProducer(session);
            producer.send(message);
        } finally {
            tryToCloseMessageProducer(producer);
        }
    }

    @Nonnull
    private MessageProducer newMessageProducer(@Nonnull final Session session) throws JMSException {
        Destination destination = session.createTopic(_alarmTopicName);
        MessageProducer result = session.createProducer(destination);
        result.setDeliveryMode(DeliveryMode.PERSISTENT);
        result.setTimeToLive(_timeToLiveForAlarms);
        return result;
    }

    private void tryToCloseMessageProducer(@CheckForNull final MessageProducer messageProducer) {
        if (messageProducer != null) {
            try {
                messageProducer.close();
            } catch (final JMSException e) {
                LOG.warn("Failed to close message producer", e);
            }
        }
    }

    @Nonnull
    private Session newSession() throws JMSException {
        return SharedJmsConnections.sharedSenderConnection()
                .createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    private void tryToCloseSession(@CheckForNull final Session session) {
        if (session != null) {
            try {
                session.close();
            } catch (final JMSException e) {
                LOG.warn("Failed to close JMS session", e);
            }
        }
    }

    private void copyAlarmMsgToJmsMsg(@Nonnull final IAlarmMessage alarmMessage,
                                      @Nonnull final MapMessage message) throws JMSException {
        for (final AlarmMessageKey key : AlarmMessageKey.values()) {
            message.setString(key.name(), alarmMessage.getString(key));
        }
    }
}
