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
 * FIND A COPY AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM $Id: AlarmMessageJMSImpl.java,v 1.4
 * 2010/04/28 07:58:00 jpenning Exp $
 */
package org.csstudio.alarm.service.internal;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;

import org.apache.log4j.Logger;
import org.csstudio.alarm.service.declaration.AlarmMessageKey;
import org.csstudio.alarm.service.declaration.EventtimeUtil;
import org.csstudio.alarm.service.declaration.IAlarmMessage;
import org.csstudio.alarm.service.declaration.Severity;
import org.csstudio.platform.logging.CentralLogger;

/**
 * JMS based implementation of the message abstraction of the AlarmService
 * This is an immutable class.
 *
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 21.04.2010
 */
public class AlarmMessageJMSImpl implements IAlarmMessage {
    private static final Logger LOG = CentralLogger.getInstance()
            .getLogger(AlarmMessageJMSImpl.class);

    private final MapMessage _mapMessage;

    /**
     * Constructor.
     *
     * @param mapMessage this message will be evaluated by subsequent calls to getString
     */
    private AlarmMessageJMSImpl(@Nonnull final MapMessage mapMessage) {
        this._mapMessage = mapMessage;
    }

    public static boolean canCreateAlarmMessageFrom(@Nonnull final Message message) {
        // TODO (jpenning) define correctness of alarm message from JMS here
        return message instanceof MapMessage;
    }

    public static IAlarmMessage newAlarmMessage(@Nonnull final Message message) {
        assert canCreateAlarmMessageFrom(message) : "Alarm message cannot be created";
        return new AlarmMessageJMSImpl((MapMessage) message);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    private final String getString(@Nonnull final String key) {
        String result = "";
        try {
            result = _mapMessage.getString(key);
        } catch (JMSException e) {
            LOG.error("Error analyzing JMS message", e);
            // result is already empty string
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public final String getString(@Nonnull final AlarmMessageKey key) {
        return getString(key.getDefiningName());
    }

    @Override
    public final Map<String, String> getMap() {
        // TODO (jpenning) performance: cache the result map
        Map<String, String> result = new HashMap<String, String>();
        try {
            @SuppressWarnings("unchecked")
            Enumeration<String> mapNames = _mapMessage.getMapNames();
            while (mapNames.hasMoreElements()) {
                String key = mapNames.nextElement();
                result.put(key.toUpperCase(), _mapMessage.getString(key));
            }
        } catch (JMSException e) {
            LOG.error("Error creating map from JMS message", e);
        }
        return result;
    }

    @Override
    public final String toString() {
        return "JMS-AlarmMessage of type " + getString(AlarmMessageKey.TYPE) +
               " for " + getString(AlarmMessageKey.NAME) +
               ", Severity " + getSeverity() +
               ", Status " + getString(AlarmMessageKey.STATUS);
    }

    @Nonnull
    public Severity getSeverity()
    {
        return Severity.parseSeverity(getString(AlarmMessageKey.SEVERITY));
    }

    @Override
    @CheckForNull
    public Date getEventtime() {
        return EventtimeUtil.parseTimestamp(getString(AlarmMessageKey.EVENTTIME));
    }

    @Nonnull
    public Date getEventtimeOrCurrentTime() {
        Date result = getEventtime();
        if (result == null) {
            result = new Date(System.currentTimeMillis());
        }
        return result;
    }

    @Override
    public boolean isAcknowledgement() {
        final String ack = getString(AlarmMessageKey.ACK);
        return (ack != null) && ack.equals("TRUE");
    }

}
