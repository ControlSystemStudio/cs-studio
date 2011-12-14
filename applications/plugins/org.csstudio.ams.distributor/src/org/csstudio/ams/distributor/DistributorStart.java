
/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.ams.distributor;

import java.sql.SQLException;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.csstudio.ams.AmsActivator;
import org.csstudio.ams.Log;
import org.csstudio.ams.dbAccess.AmsConnectionFactory;
import org.csstudio.ams.distributor.preferences.DistributorPreferenceKey;
import org.csstudio.ams.internal.AmsPreferenceKey;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.preference.IPreferenceStore;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;

public class DistributorStart implements IApplication,
                                         IGenericServiceListener<ISessionService> {

    private static DistributorStart _instance = null;

    private ISessionService xmppService;

    private String managementPassword;

    private boolean stopped;
    private boolean restart;

    public DistributorStart() {
        _instance = this;
        xmppService = null;

        final IPreferencesService pref = Platform.getPreferencesService();
        managementPassword = pref.getString(AmsActivator.PLUGIN_ID, AmsPreferenceKey.P_AMS_MANAGEMENT_PASSWORD, "", null);
        if(managementPassword == null) {
            managementPassword = "";
        }
    }

    public void stop() {
        return;
    }

    public static DistributorStart getInstance() {
        return _instance;
    }

    public synchronized void setRestart() {
        restart = true;
        stopped = true;
        notify();
    }

    public synchronized void setShutdown() {
        restart = false;
        stopped = true;
        notify();
    }

    /**
     *
     * @return The password for management
     */
    public synchronized String getPassword()
    {
        return managementPassword;
    }

    /**
     *
     */
    public Object start(final IApplicationContext context) throws Exception {
        Log.log(this, Log.INFO, "Starting Distributor ...");

        DistributorPlugin.getDefault().addSessionServiceListener(this);

        DistributorPreferenceKey.showPreferences();
        final IPreferenceStore prefs = AmsActivator.getDefault().getPreferenceStore();

        java.sql.Connection localDatabaseConnection = null;
        java.sql.Connection masterDatabaseConnection = null;
        try {
            localDatabaseConnection = AmsConnectionFactory.getApplicationDB();
            masterDatabaseConnection = AmsConnectionFactory.getConfigurationDB();

            // Create a JMS sender connection
            final ConnectionFactory senderConnectionFactory = new ActiveMQConnectionFactory(prefs.getString(AmsPreferenceKey.P_JMS_AMS_SENDER_PROVIDER_URL));
            final Connection senderConnection = senderConnectionFactory.createConnection();
            senderConnection.start();

            // Create a sender session and destination for the command topic
            final Session commandSenderSession = senderConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            final String commandTopicName = prefs.getString(AmsPreferenceKey.P_JMS_AMS_TOPIC_COMMAND);
            final Topic commandTopic = commandSenderSession.createTopic(commandTopicName);
            final MessageProducer commandMessageProducer = commandSenderSession.createProducer(commandTopic);

            // Create the synchronizer object for the database synchronization
            final ConfigurationSynchronizer synchronizer =
                new ConfigurationSynchronizer(localDatabaseConnection, masterDatabaseConnection, commandSenderSession, commandMessageProducer);
            final Thread synchronizerThread = new Thread(synchronizer);
            synchronizerThread.start();

            // Create the receiver connections
            final DistributorWork worker = new DistributorWork(localDatabaseConnection, synchronizer);
            final String[] receiverURLs = new String[] {
                    prefs.getString(AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_1),
                    prefs.getString(AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_2)
                };
            final Connection[] receiverConnections = new Connection[receiverURLs.length];
            for (int i = 0; i < receiverURLs.length; i++) {
                final ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(receiverURLs[i]);
                final Connection connection = connectionFactory.createConnection();
                receiverConnections[i] = connection;
                final Session receiverSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                final Topic receiveTopic = receiverSession.createTopic(prefs.getString(AmsPreferenceKey.P_JMS_AMS_TOPIC_DISTRIBUTOR));
                final MessageConsumer consumer = receiverSession.createConsumer(receiveTopic);

                consumer.setMessageListener(worker);
                connection.start();
            }

            // TODO: There really should be two worker classes!
            new Thread(worker).start();

            Log.log(this, Log.INFO, "Distributor started");

            synchronized (this) {
                while (!stopped) {
                    this.wait();
                }
            }

            synchronizer.stop();
            synchronizerThread.join();


            for (final Connection receiverConnection : receiverConnections) {
                receiverConnection.close();
            }
            senderConnection.close();
        } catch (final SQLException e) {
            Log.log(this, Log.FATAL, "Could not connect to the database servers", e);
            return IApplication.EXIT_OK;
        } finally {
            AmsConnectionFactory.closeConnection(localDatabaseConnection);
            AmsConnectionFactory.closeConnection(masterDatabaseConnection);
        }

        Log.log(this, Log.INFO, "DistributorStart is exiting now");

        if (xmppService != null) {
            xmppService.disconnect();
        }

        Integer exitCode = IApplication.EXIT_OK;
        if(restart) {
            exitCode = IApplication.EXIT_RESTART;
        }

        return exitCode;
    }

    public void bindService(final ISessionService sessionService) {
    	final IPreferencesService pref = Platform.getPreferencesService();
    	final String xmppServer = pref.getString(DistributorPlugin.PLUGIN_ID, DistributorPreferenceKey.P_XMPP_SERVER, "krynfs.desy.de", null);
        final String xmppUser = pref.getString(DistributorPlugin.PLUGIN_ID, DistributorPreferenceKey.P_XMPP_USER, "anonymous", null);
        final String xmppPassword = pref.getString(DistributorPlugin.PLUGIN_ID, DistributorPreferenceKey.P_XMPP_PASSWORD, "anonymous", null);

    	try {
			sessionService.connect(xmppUser, xmppPassword, xmppServer);
			xmppService = sessionService;
		} catch (final Exception e) {
			Log.log(this, Log.WARN, "XMPP connection is not available: " + e.getMessage());
		}
    }

    public void unbindService(final ISessionService service) {
    	// Nothing to do here
    }
}
