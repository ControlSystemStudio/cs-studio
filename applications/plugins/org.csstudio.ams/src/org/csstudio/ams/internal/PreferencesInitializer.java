
/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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

package org.csstudio.ams.internal;

import org.csstudio.ams.AmsActivator;
import org.csstudio.ams.AmsConstants;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * Preference initializer implemenation. This class initializes the preferences
 * with default values. New preference settings should be initialized in this
 * class, too.
 *
 * @author Alexander Will
 */
public final class PreferencesInitializer extends AbstractPreferenceInitializer {

    /**
     * {@inheritDoc}
     */
    @Override
    public final void initializeDefaultPreferences()
    {
        final IEclipsePreferences node = new DefaultScope().getNode(AmsActivator.PLUGIN_ID);

        // database settings
        node.put(AmsPreferenceKey.P_CONFIG_DATABASE_CONNECTION, "Oracle10g");
        node.put(AmsPreferenceKey.P_CONFIG_DATABASE_CONNECTION, "jdbc:oracle:thin:@(DESCRIPTION =" +
        "(ADDRESS = (PROTOCOL = TCP)(HOST = dbsrv01.desy.de)(PORT = 1521))" +
        "(ADDRESS = (PROTOCOL = TCP)(HOST = dbsrv02.desy.de)(PORT = 1521))" +
        "(ADDRESS = (PROTOCOL = TCP)(HOST = dbsrv03.desy.de)(PORT = 1521))" +
        "(LOAD_BALANCE = yes)" +
        "(CONNECT_DATA =" +
         "(SERVER = DEDICATED)" +
          "(SERVICE_NAME = desy_db.desy.de)" +
          "(FAILOVER_MODE =" +
            "(TYPE = NONE)" +
            "(METHOD = BASIC)" +
            "(RETRIES = 180)" +
            "(DELAY = 5)" +
          ")" +
        ")" +
        ")");
        node.put(AmsPreferenceKey.P_CONFIG_DATABASE_USER, "KRYKAMS");
        node.put(AmsPreferenceKey.P_CONFIG_DATABASE_PASSWORD, "krykams");
        
        node.put(AmsPreferenceKey.P_APP_DATABASE_TYPE, "Derby");
        node.put(AmsPreferenceKey.P_APP_DATABASE_CONNECTION, "jdbc:derby://krykderby.desy.de/amsdb;create=true");
        node.put(AmsPreferenceKey.P_APP_DATABASE_USER, "APP");
        node.put(AmsPreferenceKey.P_APP_DATABASE_PASSWORD, "APP");

        node.put(AmsPreferenceKey.P_CACHE_DATABASE_TYPE, "HSQLDB");
        node.put(AmsPreferenceKey.P_CACHE_DATABASE_CONNECTION, "jdbc:hsqldb:mem:memConfigDB");
        node.put(AmsPreferenceKey.P_CACHE_DATABASE_USER, "SA");
        node.put(AmsPreferenceKey.P_CACHE_DATABASE_PASSWORD, "");

        // filter key field of message
        node.put(AmsPreferenceKey.P_FILTER_KEYFIELDS,
                "TYPE" + ";" +
                "EVENTTIME" + ";" +
                "TEXT" + ";" +
                "USER" + ";" +
                "HOST" + ";" +
                "APPLICATION-ID" + ";" +
                "PROCESS-ID" + ";" +
                "NAME" + ";" +
                "CLASS" + ";" +
                "DOMAIN" + ";" +
                "FACILITY" + ";" +
                "LOCATION" + ";" +
                "SEVERITY" + ";" +
                "STATUS" + ";" +
                "VALUE" + ";" +
                "DESTINATION" + ";" +
                AmsConstants.MSGPROP_REINSERTED
        );

        // AMS management password
        node.put(AmsPreferenceKey.P_AMS_MANAGEMENT_PASSWORD, "");

        /* OpenJMS
        // jms communication - external
        node.put(SampleService.P_JMS_EXTERN_CONNECTION_FACTORY_CLASS, "org.exolab.jms.jndi.InitialContextFactory");
        node.put(SampleService.P_JMS_EXTERN_PROVIDER_URL, "rmi://krykelog.desy.de:1099/");
        node.put(SampleService.P_JMS_EXTERN_CONNECTION_FACTORY, "ConnectionFactory");
        // jms communication - ams internal
        node.put(SampleService.P_JMS_AMS_CONNECTION_FACTORY_CLASS, "org.exolab.jms.jndi.InitialContextFactory");
        node.put(SampleService.P_JMS_AMS_PROVIDER_URL, "rmi://krykelog.desy.de:1099/");
        node.put(SampleService.P_JMS_AMS_CONNECTION_FACTORY, "ConnectionFactory");
        // jms communication - free topics
        node.put(SampleService.P_JMS_FREE_TOPIC_CONNECTION_FACTORY_CLASS, "org.exolab.jms.jndi.InitialContextFactory");
        node.put(SampleService.P_JMS_FREE_TOPIC_CONNECTION_FACTORY, "ConnectionFactory");
        */

        /* ActiveMQ */
        // jms communication - external
        node.put(AmsPreferenceKey.P_JMS_EXTERN_CONNECTION_FACTORY_CLASS, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        node.put(AmsPreferenceKey.P_JMS_EXTERN_PROVIDER_URL_1, "failover:(tcp://krykjmsb.desy.de:64616)?maxReconnectDelay=5000");
        // ADDED BY: Markus Moeller, 02.08.2007
        node.put(AmsPreferenceKey.P_JMS_EXTERN_PROVIDER_URL_2, "failover:(tcp://krykjmsa.desy.de:62616)?maxReconnectDelay=5000");
        // ADDED BY: Markus Moeller, 13.08.2007
        node.put(AmsPreferenceKey.P_JMS_EXTERN_SENDER_PROVIDER_URL, "failover:(tcp://krykjmsb.desy.de:64616,tcp://krykjmsa.desy.de:62616)?maxReconnectDelay=5000");
        node.put(AmsPreferenceKey.P_JMS_EXTERN_CONNECTION_FACTORY, "ConnectionFactory");
        node.put(AmsPreferenceKey.P_JMS_EXTERN_CREATE_DURABLE, "false");

        // jms communication - ams internal
        node.put(AmsPreferenceKey.P_JMS_AMS_CONNECTION_FACTORY_CLASS, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        node.put(AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_1, "failover:(tcp://krykjmsb.desy.de:64616)?maxReconnectDelay=5000");
        // ADDED BY: Markus Moeller, 02.08.2007
        node.put(AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_2, "failover:(tcp://krykjmsa.desy.de:62616)?maxReconnectDelay=5000");
        // ADDED BY: Markus Moeller, 13.08.2007
        node.put(AmsPreferenceKey.P_JMS_AMS_SENDER_PROVIDER_URL, "failover:(tcp://krykjmsb.desy.de:64616,tcp://krykjmsa.desy.de:62616)?maxReconnectDelay=5000");
        node.put(AmsPreferenceKey.P_JMS_AMS_CONNECTION_FACTORY, "ConnectionFactory");
        node.put(AmsPreferenceKey.P_JMS_AMS_CREATE_DURABLE, "false");

        // jms communication - free topics
        node.put(AmsPreferenceKey.P_JMS_FREE_TOPIC_CONNECTION_FACTORY_CLASS, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        node.put(AmsPreferenceKey.P_JMS_FREE_TOPIC_CONNECTION_FACTORY, "ConnectionFactory");

        // external topics
        node.put(AmsPreferenceKey.P_JMS_EXT_TOPIC_ALARM, "ALARM");
        node.put(AmsPreferenceKey.P_JMS_EXT_TOPIC_ALARM_REINSERT, "ALARM");
        node.put(AmsPreferenceKey.P_JMS_EXT_TSUB_ALARM_FMR, "SUB_AMS_FILTERMANAGER");
        node.put(AmsPreferenceKey.P_JMS_EXT_TOPIC_COMMAND, "COMMAND");
        node.put(AmsPreferenceKey.P_JMS_EXT_TSUB_CMD_FMR_START_RELOAD, "SUB_AMS_CMD_FMR_START_RELOAD");
        node.put(AmsPreferenceKey.P_JMS_EXT_TOPIC_STATUSCHANGE, "T_AMS_STATUS_CHANGE");

        // ams internal topics
        node.put(AmsPreferenceKey.P_JMS_AMS_TOPIC_DISTRIBUTOR, "T_AMS_DISTRIBUTE");
        node.put(AmsPreferenceKey.P_JMS_AMS_TSUB_DISTRIBUTOR, "SUB_AMS_DISTRIBUTOR");
        node.put(AmsPreferenceKey.P_JMS_AMS_TOPIC_REPLY, "T_AMS_CON_REPLY");
        node.put(AmsPreferenceKey.P_JMS_AMS_TSUB_REPLY, "SUB_AMS_DISTRIBUTOR_REPLY");
        node.put(AmsPreferenceKey.P_JMS_AMS_TOPIC_MESSAGEMINDER, "T_AMS_MESSAGEMINDER");
        node.put(AmsPreferenceKey.P_JMS_AMS_TSUB_MESSAGEMINDER, "SUB_AMS_MESSAGEMINDER");

        node.put(AmsPreferenceKey.P_JMS_AMS_TOPIC_CONNECTOR_DEVICETEST, "T_AMS_CON_DEVICETEST");
        node.put(AmsPreferenceKey.P_JMS_AMS_TOPIC_SMS_CONNECTOR, "T_AMS_CON_SMS");
        node.put(AmsPreferenceKey.P_JMS_AMS_TOPIC_SMS_CONNECTOR_FORWARD, "false");
        node.put(AmsPreferenceKey.P_JMS_AMS_TSUB_SMS_CONNECTOR, "SUB_AMS_CON_SMS");
        node.put(AmsPreferenceKey.P_JMS_AMS_TSUB_SMS_CONNECTOR_DEVICETEST, "SUB_AMS_CON_SMS_DEVICETEST");
        node.put(AmsPreferenceKey.P_JMS_AMS_TOPIC_EMAIL_CONNECTOR, "T_AMS_CON_MAIL");
        node.put(AmsPreferenceKey.P_JMS_AMS_TOPIC_EMAIL_CONNECTOR_FORWARD, "false");
        node.put(AmsPreferenceKey.P_JMS_AMS_TSUB_EMAIL_CONNECTOR, "SUB_AMS_CON_MAIL");
        node.put(AmsPreferenceKey.P_JMS_AMS_TOPIC_VOICEMAIL_CONNECTOR, "T_AMS_CON_VOICEMAIL");
        node.put(AmsPreferenceKey.P_JMS_AMS_TOPIC_VOICEMAIL_CONNECTOR_FORWARD, "false");
        node.put(AmsPreferenceKey.P_JMS_AMS_TSUB_VOICEMAIL_CONNECTOR, "SUB_AMS_CON_VOICEMAIL");
        node.put(AmsPreferenceKey.P_JMS_AMS_TOPIC_JMS_CONNECTOR, "T_AMS_CON_JMS");
        node.put(AmsPreferenceKey.P_JMS_AMS_TOPIC_JMS_CONNECTOR_FORWARD, "true");
        node.put(AmsPreferenceKey.P_JMS_AMS_TSUB_JMS_CONNECTOR, "SUB_AMS_CON_JMS");

        node.put(AmsPreferenceKey.P_JMS_AMS_TOPIC_COMMAND, "COMMAND");
        node.put(AmsPreferenceKey.P_JMS_AMS_TSUB_CMD_FMR_RELOAD_END, "SUB_AMS_CMD_FMR_RELOAD_END");

        node.put(AmsPreferenceKey.P_JMS_AMS_TOPIC_MONITOR, "T_AMS_SYSTEM_MONITOR");
    }
}
