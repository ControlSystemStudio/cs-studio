
/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */

package org.csstudio.ams.application.monitor.internal;

import org.csstudio.ams.application.monitor.AmsMonitorActivator;
import org.csstudio.domain.desy.preferences.AbstractPreference;

/**
 * @author mmoeller
 * @version 1.0
 * @since 10.04.2012
 */
public class AmsMonitorPreference<T> extends AbstractPreference<T> {

    public static final AmsMonitorPreference<String> XMPP_SERVER =
            new AmsMonitorPreference<String>("xmppServer", "krynfs.desy.de");
    
    public static final AmsMonitorPreference<String> XMPP_USER =
            new AmsMonitorPreference<String>("xmppUser", "ams-system-monitor");

    public static final AmsMonitorPreference<String> XMPP_PASSWORD =
            new AmsMonitorPreference<String>("xmppPassword", "ams");

    public static final AmsMonitorPreference<String> JMS_PUBLISHER_URL =
            new AmsMonitorPreference<String>("jmsPublisherUrl", "tcp://localhost:62616");

    public static final AmsMonitorPreference<String> JMS_PUBLISHER_TOPIC_ALARM =
            new AmsMonitorPreference<String>("jmsPublisherTopicAlarm", "ALARM");

    public static final AmsMonitorPreference<String> JMS_CONSUMER_URL1 =
            new AmsMonitorPreference<String>("jmsConsumerUrl1", "tcp://localhost:62616");

    public static final AmsMonitorPreference<String> JMS_CONSUMER_URL2 =
            new AmsMonitorPreference<String>("jmsConsumerUrl2", "tcp://localhost:64616");

    public static final AmsMonitorPreference<String> JMS_CONSUMER_TOPIC_MONITOR =
            new AmsMonitorPreference<String>("jmsConsumerTopicMonitor", "T_AMS_SYSTEM_MONITOR");

    public static final AmsMonitorPreference<Long> AMS_CHECK_WAIT_TIME =
            new AmsMonitorPreference<Long>("amsCheckWaitTime", 10L);

    public static final AmsMonitorPreference<Long> DELIVERY_WORKER_CHECK_WAIT_TIME =
            new AmsMonitorPreference<Long>("deliveryWorkerCheckWaitTime", 90L);

    public static final AmsMonitorPreference<Integer> MAX_ALLOWED_WORKER_ERROR =
            new AmsMonitorPreference<Integer>("maxAllowedWorkerError", 3);

    public static final AmsMonitorPreference<Integer> MAX_ALLOWED_WORKER_WARN =
            new AmsMonitorPreference<Integer>("maxAllowedWorkerWarn", 3);

    public static final AmsMonitorPreference<Integer> MAX_ALLOWED_AMS_ERROR =
            new AmsMonitorPreference<Integer>("maxAllowedAmsError", 3);

    public static final AmsMonitorPreference<String> JMX_HOST_1 =
            new AmsMonitorPreference<String>("jmxHostname1", "krykjmsa.desy.de");
    
    public static final AmsMonitorPreference<Integer> JMX_PORT_1 =
            new AmsMonitorPreference<Integer>("jmxPort1", 1199);

    public static final AmsMonitorPreference<String> JMX_HOST_2 =
            new AmsMonitorPreference<String>("jmxHostname2", "krykjmsb.desy.de");
    
    public static final AmsMonitorPreference<Integer> JMX_PORT_2 =
            new AmsMonitorPreference<Integer>("jmxPort2", 1199);

    public static final AmsMonitorPreference<String> AMS_HOST =
            new AmsMonitorPreference<String>("amsHost", "KRYKAMS");

    public static final AmsMonitorPreference<String> AMS_USER =
            new AmsMonitorPreference<String>("amsUser", "applic");

    public static final AmsMonitorPreference<String> AMS_PROCESS_LIST =
            new AmsMonitorPreference<String>("amsProcessList", "");

    public static final AmsMonitorPreference<String> RESTART_MAIL_LIST =
            new AmsMonitorPreference<String>("restartMailList", "");

    public static final AmsMonitorPreference<Long> RESTART_WAIT_TIME =
            new AmsMonitorPreference<Long>("restartWaitTime", 5000L);

    public static final AmsMonitorPreference<String> ALARM_MAIL_SERVER =
            new AmsMonitorPreference<String>("mailServer", "");

    public static final AmsMonitorPreference<String> ALARM_MAIL_SENDER =
            new AmsMonitorPreference<String>("mailSender", "");
    
    public static final AmsMonitorPreference<String> ALARM_MAIL_DOMAIN_PART =
            new AmsMonitorPreference<String>("mailDomainPart", "");

    public static final AmsMonitorPreference<String> ALARM_MAIL_LOCAL_PART =
            new AmsMonitorPreference<String>("mailLocalPart", "");

    public static final AmsMonitorPreference<String> ALARM_MAIL_SUBJECT =
            new AmsMonitorPreference<String>("mailSubject", "");

    public static final AmsMonitorPreference<String> ALARM_AMS_GROUP =
            new AmsMonitorPreference<String>("amsGroup", "");

    public static final AmsMonitorPreference<String> ALARM_SMS_EMERGENCY_NUMBER =
            new AmsMonitorPreference<String>("emergencyNumber", "");

    public static final AmsMonitorPreference<String> ALARM_EMERGENCY_MAIL =
            new AmsMonitorPreference<String>("emergencyMail", "");

    public static final AmsMonitorPreference<Boolean> ALARM_SMS_USE_OAS =
            new AmsMonitorPreference<Boolean>("useOAS", false);

    public static final AmsMonitorPreference<Long> SMS_CHECK_INTERVAL =
            new AmsMonitorPreference<Long>("smsCheckInterval", 15L);

    private AmsMonitorPreference(final String keyAsString, final T defaultValue) {
        super(keyAsString, defaultValue);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Class<? extends AbstractPreference<T>> getClassType() {
        return (Class<? extends AbstractPreference<T>>) AmsMonitorPreference.class;
    }

    @Override
    public String getPluginID() {
        return AmsMonitorActivator.PLUGIN_ID;
    }
}
