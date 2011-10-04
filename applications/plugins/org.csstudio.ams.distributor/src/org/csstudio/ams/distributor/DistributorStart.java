
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
    
    public final static int STAT_INIT = 0;
    public final static int STAT_OK = 1;
    public final static int STAT_GROUP_BLOCKED = 1;
    public final static int STAT_ERR_APPLICATION_DB = 2;
    public final static int STAT_ERR_APPLICATION_DB_SEND = 3;
    public final static int STAT_ERR_CONFIG_DB = 4;
    public final static int STAT_ERR_JMSCON_INT = 5;                            // jms communication to ams internal jms partners
    public final static int STAT_ERR_JMSCON_EXT = 6;                            // jms communication to external jms (AlarmTopic)
    public final static int STAT_ERR_JMSCON_FREE_SEND = 7;                      // jms communication to free topics
    public final static int STAT_ERR_FLG_RPL = 8;                               // could not update (application-db) db flag (ReplicationState)
    public final static int STAT_ERR_FLG_BUP = 9;                               // could not update (config-db) db flag (BupState)
    public final static int STAT_ERR_UNKNOWN = 10;
    public final static int STAT_FALSE = 999;                                   // replaces boolean false in methods
    
    private static DistributorStart _instance = null;
    
    private ISessionService xmppService;
    
    private String managementPassword;

    private boolean stopped;
    private boolean restart;
    
    public DistributorStart() {
        _instance = this;
        xmppService = null;
        
        IPreferencesService pref = Platform.getPreferencesService();
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
    public Object start(IApplicationContext context) throws Exception {
        Log.log(this, Log.INFO, "Starting Distributor ...");
        
        DistributorPlugin.getDefault().addSessionServiceListener(this);
        
        DistributorPreferenceKey.showPreferences();
        IPreferenceStore prefs = AmsActivator.getDefault().getPreferenceStore();
        
        java.sql.Connection localDatabaseConnection = null;
        java.sql.Connection masterDatabaseConnection = null;
        try {
            localDatabaseConnection = AmsConnectionFactory.getApplicationDB();
            masterDatabaseConnection = AmsConnectionFactory.getConfigurationDB();
            
            // Create a JMS sender connection
            ConnectionFactory senderConnectionFactory = new ActiveMQConnectionFactory(prefs.getString(AmsPreferenceKey.P_JMS_AMS_SENDER_PROVIDER_URL));
            Connection senderConnection = senderConnectionFactory.createConnection();
            senderConnection.start();
            
            // Create a sender session and destination for the command topic
            Session commandSenderSession = senderConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            String commandTopicName = prefs.getString(AmsPreferenceKey.P_JMS_AMS_TOPIC_COMMAND);
            Topic commandTopic = commandSenderSession.createTopic(commandTopicName);
            MessageProducer commandMessageProducer = commandSenderSession.createProducer(commandTopic);
            
            // Create the synchronizer object for the database synchronization
            ConfigurationSynchronizer synchronizer =
                new ConfigurationSynchronizer(localDatabaseConnection, masterDatabaseConnection, commandSenderSession, commandMessageProducer);
            Thread synchronizerThread = new Thread(synchronizer);
            synchronizerThread.start();
            
            // Create the receiver connections
            DistributorWork worker = new DistributorWork(localDatabaseConnection, synchronizer);
            String[] receiverURLs = new String[] {
                    prefs.getString(AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_1),
                    prefs.getString(AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_2)
                };
            Connection[] receiverConnections = new Connection[receiverURLs.length];
            for (int i = 0; i < receiverURLs.length; i++) {
                ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(receiverURLs[i]);
                Connection connection = connectionFactory.createConnection();
                receiverConnections[i] = connection;
                Session receiverSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Topic receiveTopic = receiverSession.createTopic(prefs.getString(AmsPreferenceKey.P_JMS_AMS_TOPIC_DISTRIBUTOR));
                MessageConsumer consumer = receiverSession.createConsumer(receiveTopic);
                
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
            
            
            for (int i = 0; i < receiverConnections.length; i++) {
                receiverConnections[i].close();
            }
            senderConnection.close();
        } catch (SQLException e) {
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
    
    public void bindService(ISessionService sessionService) {
    	IPreferencesService pref = Platform.getPreferencesService();
    	String xmppServer = pref.getString(DistributorPlugin.PLUGIN_ID, DistributorPreferenceKey.P_XMPP_SERVER, "krynfs.desy.de", null);
        String xmppUser = pref.getString(DistributorPlugin.PLUGIN_ID, DistributorPreferenceKey.P_XMPP_USER, "anonymous", null);
        String xmppPassword = pref.getString(DistributorPlugin.PLUGIN_ID, DistributorPreferenceKey.P_XMPP_PASSWORD, "anonymous", null);
   	
    	try {
			sessionService.connect(xmppUser, xmppPassword, xmppServer);
			xmppService = sessionService;
		} catch (Exception e) {
			Log.log(this, Log.WARN, "XMPP connection is not available: " + e.getMessage());
		}
    }
    
    public void unbindService(ISessionService service) {
    	// Nothing to do here
    }
}
