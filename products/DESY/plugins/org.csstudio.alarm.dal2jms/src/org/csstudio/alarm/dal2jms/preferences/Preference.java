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

import org.csstudio.alarm.dal2jms.Activator;
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
public final class Preference<T> extends AbstractPreference<T> {

    public static final Preference<Integer> JMS_TIME_TO_LIVE_ALARMS =
        new Preference<Integer>("TimeToLiveAlarms", 3600000);
    public static final Preference<String> JMS_ALARM_TOPIC_NAME =
        new Preference<String>("AlarmTopicName", "ALARM");
    public static final Preference<String> XMPP_DAL2JMS_SERVER_NAME =
        new Preference<String>("XmppServerName", "krynfs.desy.de");
    public static final Preference<String> XMPP_DAL2JMS_USER_NAME =
        new Preference<String>("XmppUserName", "dal2jms");
    public static final Preference<String> XMPP_DAL2JMS_PASSWORD =
        new Preference<String>("XmppPassword", "dal2jms");
    public static final Preference<String> ALARM_CONFIG_XML_FILE_NAME =
        new Preference<String>("AlarmConfigXMLFileName", "resource/dal2jmsConfig.xml");

    private Preference(@Nonnull final String keyAsString, @Nonnull final T defaultValue) {
        super(keyAsString, defaultValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPluginID() {
        return Activator.PLUGIN_ID;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Class<? extends AbstractPreference<T>> getClassType() {
        return (Class<? extends AbstractPreference<T>>) Preference.class;
    }

}
