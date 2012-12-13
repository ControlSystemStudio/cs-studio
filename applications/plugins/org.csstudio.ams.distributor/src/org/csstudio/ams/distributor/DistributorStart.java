
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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.csstudio.ams.AmsActivator;
import org.csstudio.ams.Log;
import org.csstudio.ams.configReplicator.ConfigReplicator;
import org.csstudio.ams.configReplicator.ReplicationException;
import org.csstudio.ams.dbAccess.AmsConnectionFactory;
import org.csstudio.ams.dbAccess.ConfigDbProperties;
import org.csstudio.ams.distributor.preferences.DistributorPreferenceKey;
import org.csstudio.ams.internal.AmsPreferenceKey;
import org.csstudio.utility.jms.IConnectionMonitor;
import org.csstudio.utility.jms.TransportEvent;
import org.csstudio.utility.jms.sharedconnection.ISharedConnectionHandle;
import org.csstudio.utility.jms.sharedconnection.SharedJmsConnections;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.framework.Bundle;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;

public class DistributorStart implements IApplication,
                                         IConnectionMonitor,
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

    @Override
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
    public synchronized String getPassword() {
        return managementPassword;
    }

    /**
     *
     */
    @SuppressWarnings("null")
    @Override
    public Object start(final IApplicationContext context) throws Exception {
        
        Log.log(this, Log.INFO, "Starting Distributor ...");

        DistributorPlugin.getDefault().addSessionServiceListener(this);

        DistributorPreferenceKey.showPreferences();

        final IPreferencesService ps = Platform.getPreferencesService();
        final String dbType = ps.getString(AmsActivator.PLUGIN_ID,
                                           AmsPreferenceKey.P_CONFIG_DATABASE_TYPE,
                                           "",
                                           null);
        final String dbCon = ps.getString(AmsActivator.PLUGIN_ID,
                                          AmsPreferenceKey.P_CONFIG_DATABASE_CONNECTION,
                                          "", null);
        final String user = ps.getString(AmsActivator.PLUGIN_ID,
                                         AmsPreferenceKey.P_CONFIG_DATABASE_USER,
                                         "",
                                         null);
        final String pwd = ps.getString(AmsActivator.PLUGIN_ID,
                                        AmsPreferenceKey.P_CONFIG_DATABASE_PASSWORD,
                                        "",
                                        null);
        
        ConfigDbProperties dbProp = new ConfigDbProperties(dbType, dbCon, user, pwd);
        java.sql.Connection localDatabaseConnection = null;
        java.sql.Connection cacheDatabaseConnection = null;
        
        try {
            localDatabaseConnection = AmsConnectionFactory.getApplicationDB();
            cacheDatabaseConnection = AmsConnectionFactory.getMemoryCacheDB();
            
            // Get the path for the SQL script
            Bundle amsBundle = Platform.getBundle(AmsActivator.PLUGIN_ID);
            Enumeration<URL> entries = amsBundle.findEntries("resource", "createMemoryCache.sql", true);
            File sqlFile = null;
            if (entries.hasMoreElements()) {
                
                try {
                    URL fileUrl = FileLocator.toFileURL(entries.nextElement());
					Log.log(Log.DEBUG, fileUrl.toString());
					try {
						URI fileUri = fileUrl.toURI();
						sqlFile = new File(fileUri);
					} catch(URISyntaxException uriException) {
						sqlFile = new File(fileUrl.getPath());
					}
                } catch (IOException e) {
                    throw new ReplicationException(e);
                }
            }
            
            // Create the cache db
            ConfigReplicator.createMemoryCacheDb(cacheDatabaseConnection, sqlFile);
            ConfigReplicator.replicateConfigurationToHsql(localDatabaseConnection, cacheDatabaseConnection);

            // Prepare JMS connections
            String publisherUrl = ps.getString(AmsActivator.PLUGIN_ID,
                                               AmsPreferenceKey.P_JMS_AMS_SENDER_PROVIDER_URL,
                                               "",
                                               null);
            
            final String[] consumerURLs = new String[] {
                    ps.getString(AmsActivator.PLUGIN_ID,
                                 AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_1,
                                 "",
                                 null),
                    ps.getString(AmsActivator.PLUGIN_ID,
                                 AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_2,
                                 "",
                                 null)
            };
            
            SharedJmsConnections.staticInjectPublisherUrlAndClientId(publisherUrl, "AmsDistributorPublisher");
            SharedJmsConnections.staticInjectConsumerUrlAndClientId(consumerURLs[0], consumerURLs[1], "AmsDistributorConsumer");
            
            final ISharedConnectionHandle publisherHandle = SharedJmsConnections.sharedSenderConnection();
            // Create a JMS sender connection
//            final ConnectionFactory senderConnectionFactory = new ActiveMQConnectionFactory(publisherUrl);
//            final Connection senderConnection = senderConnectionFactory.createConnection();
//            senderConnection.start();

            // Create a sender session and destination for the command topic
            final Session commandSenderSession = publisherHandle.createSession(false, Session.AUTO_ACKNOWLEDGE);
            final String commandTopicName = ps.getString(AmsActivator.PLUGIN_ID,
                                                         AmsPreferenceKey.P_JMS_AMS_TOPIC_COMMAND,
                                                         "",
                                                         null);
            
            final Topic commandTopic = commandSenderSession.createTopic(commandTopicName);
            final MessageProducer commandMessageProducer = commandSenderSession.createProducer(commandTopic);

            // Create the synchronizer object for the database synchronization
            final ConfigurationSynchronizer synchronizer =
                new ConfigurationSynchronizer(localDatabaseConnection,
                                              cacheDatabaseConnection,
                                              dbProp,
                                              commandSenderSession,
                                              commandMessageProducer);
            final Thread synchronizerThread = new Thread(synchronizer);
            synchronizerThread.start();
            
            // Create the receiver connections
            final DistributorWork worker = new DistributorWork(localDatabaseConnection,
                                                               cacheDatabaseConnection,
                                                               synchronizer);
            
            final String distributorTopic = ps.getString(AmsActivator.PLUGIN_ID,
                                                         AmsPreferenceKey.P_JMS_AMS_TOPIC_DISTRIBUTOR,
                                                         "",
                                                         null);
            
            final ISharedConnectionHandle[] consumerHandles = SharedJmsConnections.sharedReceiverConnections();
            final Session[] consumerSessions = new Session[consumerHandles.length];
            final MessageConsumer[] consumer = new MessageConsumer[consumerHandles.length];
            for (int i = 0; i < consumerHandles.length; i++) {
                consumerSessions[i] = consumerHandles[i].createSession(false, Session.AUTO_ACKNOWLEDGE);
                final Topic receiveTopic = consumerSessions[i].createTopic(distributorTopic);
                consumer[i] = consumerSessions[i].createConsumer(receiveTopic);
                consumer[i].setMessageListener(worker);
            }
            
            // TODO: There really should be two worker classes!
            // new Thread(worker).start();
            worker.start();
            
            Log.log(this, Log.INFO, "Distributor started");

            synchronized (this) {
                while (!stopped) {
                    this.wait();
                }
            }
            
            worker.stopWorking();
            worker.join(10000);
            synchronizer.stop();
            synchronizerThread.join();

            // Close the JMS connections and clients
            for (final MessageConsumer o : consumer) {
                try {o.close();}catch(Exception e){/*Ignore Me!*/}
            }

            for (final Session session : consumerSessions) {
                try {session.close();}catch(Exception e){/*Ignore Me!*/}
            }
            
            for (final ISharedConnectionHandle conHandle : consumerHandles) {
                conHandle.release();
            }
            
            if (commandSenderSession != null) {
                try {commandSenderSession.close();}catch(Exception e){/*Ignore Me!*/}
            }
            
            publisherHandle.release();
        } catch (final SQLException e) {
            Log.log(this, Log.FATAL, "Could not connect to the database servers", e);
        } finally {
            AmsConnectionFactory.closeConnection(cacheDatabaseConnection);
            AmsConnectionFactory.closeConnection(localDatabaseConnection);
        }

        Log.log(this, Log.INFO, "DistributorStart is exiting now");

        if (xmppService != null) {
            synchronized (xmppService) {
                try {
                    xmppService.wait(500L);
                } catch (InterruptedException ie) {
                    Log.log(this, Log.WARN, "xmppService.wait() was interrupted."); 
                }
            }
            xmppService.disconnect();
        }

        Integer exitCode = IApplication.EXIT_OK;
        if(restart) {
            exitCode = IApplication.EXIT_RESTART;
        }

        return exitCode;
    }

    @Override
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

    @Override
    public void unbindService(final ISessionService service) {
    	// Nothing to do here
    }

    @Override
    public void onConnected(TransportEvent event) {
        Log.log(Log.WARN, "onConnected(): " + event);
    }

    @Override
    public void onDisconnected(TransportEvent event) {
        Log.log(Log.WARN, "onDisconnected(): " + event);
    }
}
