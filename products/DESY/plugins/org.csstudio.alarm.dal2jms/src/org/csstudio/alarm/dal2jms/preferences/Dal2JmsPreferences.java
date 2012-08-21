/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchroton,
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
package org.csstudio.alarm.dal2jms.preferences;

import javax.annotation.Nonnull;

import org.csstudio.alarm.dal2jms.Dal2JmsActivator;
import org.csstudio.domain.desy.preferences.AbstractPreference;

/**
 * Constant definitions for plug-in preferences
 *
 *
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 10.06.2010

 * @param <T> the type of the preference. It must match the type of the default value.
 */
public final class Dal2JmsPreferences<T> extends AbstractPreference<T> {

    public static final Dal2JmsPreferences<Integer> JMS_TIME_TO_LIVE_ALARMS =
        new Dal2JmsPreferences<Integer>("TimeToLiveAlarms", 3600000);
    public static final Dal2JmsPreferences<String> JMS_ALARM_DESTINATION_TOPIC_NAME =
        new Dal2JmsPreferences<String>("AlarmTopicName", "ALARM");
    public static final Dal2JmsPreferences<String> JMS_ACK_SOURCE_TOPIC_NAME =
        new Dal2JmsPreferences<String>("AckSourceTopicName", "ACK");
    public static final Dal2JmsPreferences<String> XMPP_DAL2JMS_SERVER_NAME =
        new Dal2JmsPreferences<String>("XmppServerName", "krynfs.desy.de");
    public static final Dal2JmsPreferences<String> XMPP_DAL2JMS_USER_NAME =
        new Dal2JmsPreferences<String>("XmppUserName", "dal2jms");
    public static final Dal2JmsPreferences<String> XMPP_DAL2JMS_PASSWORD =
        new Dal2JmsPreferences<String>("XmppPassword", "dal2jms");
    public static final Dal2JmsPreferences<String> SNAPSHOT_FILENAME =
        new Dal2JmsPreferences<String>("SnapshotFilename", "dal2jms.ser");
    public static final Dal2JmsPreferences<Integer> SNAPSHOT_INTERVAL_SECS =
        new Dal2JmsPreferences<Integer>("SnapshotIntervalSecs", 60);

    private Dal2JmsPreferences(@Nonnull final String keyAsString, @Nonnull final T defaultValue) {
        super(keyAsString, defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String getPluginID() {
        return Dal2JmsActivator.PLUGIN_ID;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    protected Class<? extends AbstractPreference<T>> getClassType() {
        return (Class<? extends AbstractPreference<T>>) Dal2JmsPreferences.class;
    }

}
