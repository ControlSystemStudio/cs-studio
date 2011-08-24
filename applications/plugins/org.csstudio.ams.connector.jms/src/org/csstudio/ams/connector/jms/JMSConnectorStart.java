
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

package org.csstudio.ams.connector.jms;

import java.net.InetAddress;
import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.csstudio.ams.AmsActivator;
import org.csstudio.ams.AmsConstants;
import org.csstudio.ams.Log;
import org.csstudio.ams.Utils;
import org.csstudio.ams.connector.jms.preferences.JmsConnectorPreferenceKey;
import org.csstudio.ams.internal.AmsPreferenceKey;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.preference.IPreferenceStore;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;

public class JMSConnectorStart implements IApplication, IGenericServiceListener<ISessionService>
{
    private static JMSConnectorStart _instance = null;

    private Context extContext = null;
    private ConnectionFactory extFactory = null;
    private Connection extConnection = null;
    private Session extSession = null;
    
    private MessageProducer extPublisherStatusChange = null;
    
    private ISessionService xmppService;
    
    private String managementPassword; 
    
    private boolean stopped;
    private boolean restart;

    public JMSConnectorStart()
    {
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

    public static JMSConnectorStart getInstance() {
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
     * @return The management password
     */
    public synchronized String getPassword() {
        return managementPassword;
    }

    public Object start(IApplicationContext context) throws Exception
    {
        Log.log(this, Log.INFO, "start");
        
        JmsConnectorPreferenceKey.showPreferences();
        
        JMSConnectorPlugin.getDefault().addSessionServiceListener(this);
        
        if (!initJms()) {
            Log.log(Log.FATAL, "Could not create external JMS connection for status messages, trying to restart.");
            return IApplication.EXIT_RESTART;
        }
        
        // TODO is this still required?
        sendStatusChange(1, "ok", System.currentTimeMillis());
        
        IPreferenceStore prefs = AmsActivator.getDefault().getPreferenceStore();
        
        ConnectionFactory senderConnectionFactory = new ActiveMQConnectionFactory(prefs.getString(AmsPreferenceKey.P_JMS_AMS_SENDER_PROVIDER_URL));
        Connection senderConnection = senderConnectionFactory.createConnection();
        senderConnection.start();
        String[] receiverURLs = new String[] {
                prefs.getString(AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_1),
                prefs.getString(AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_2)
            };
        Connection[] receiverConnections = new Connection[receiverURLs.length];
        for (int i = 0; i < receiverURLs.length; i++) {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(receiverURLs[i]);
            Connection connection = connectionFactory.createConnection();
            Session receiverSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic receiveTopic = receiverSession.createTopic(prefs.getString(AmsPreferenceKey.P_JMS_AMS_TOPIC_JMS_CONNECTOR));
            MessageConsumer consumer = receiverSession.createConsumer(receiveTopic);
            
            // Create a new sender session for each worker because each worker will be called in its own listener thread
            Session senderSession = senderConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            
            consumer.setMessageListener(new JMSConnectorWorker(senderSession));
            connection.start();
        }
        
        synchronized (this) {
            while (!stopped) {
                this.wait();
            }
        }
        
        for (int i = 0; i < receiverConnections.length; i++) {
            receiverConnections[i].close();
        }
        senderConnection.close();
        
        closeJms();

        Log.log(this, Log.INFO, "JMSConnectorStart is exiting now");
        
        if (xmppService != null) {
            xmppService.disconnect();
        }
        
        Integer exitCode = IApplication.EXIT_OK;
        if(restart) {
            exitCode = IApplication.EXIT_RESTART;
        }
        
        return exitCode;
    }
    
    private boolean initJms()
    {
        try
        {
            IPreferenceStore storeAct = AmsActivator.getDefault().getPreferenceStore();
            Hashtable<String, String> properties = new Hashtable<String, String>();
            properties.put(Context.INITIAL_CONTEXT_FACTORY, 
                    storeAct.getString(org.csstudio.ams.internal.AmsPreferenceKey.P_JMS_EXTERN_CONNECTION_FACTORY_CLASS));
            properties.put(Context.PROVIDER_URL, 
                    storeAct.getString(org.csstudio.ams.internal.AmsPreferenceKey.P_JMS_EXTERN_SENDER_PROVIDER_URL));
            extContext = new InitialContext(properties);
            
            extFactory = (ConnectionFactory) extContext.lookup(
                    storeAct.getString(org.csstudio.ams.internal.AmsPreferenceKey.P_JMS_EXTERN_CONNECTION_FACTORY));
            extConnection = extFactory.createConnection();
            
            // ADDED BY: Markus Moeller, 25.05.2007
            extConnection.setClientID("JMSConnectorStartSenderExternal");
            
            extSession = extConnection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            
            // CHANGED BY: Markus Moeller, 25.05.2007
            /*
            extPublisherStatusChange = extSession.createProducer((Topic)extContext.lookup(
                    storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_EXT_TOPIC_STATUSCHANGE)));
            */
            
            extPublisherStatusChange = extSession.createProducer(extSession.createTopic(
                    storeAct.getString(org.csstudio.ams.internal.AmsPreferenceKey.P_JMS_EXT_TOPIC_STATUSCHANGE)));
            if (extPublisherStatusChange == null) {
                Log.log(this, Log.FATAL, "could not create extPublisherStatusChange");
                return false;
            }

            extConnection.start();

            return true;
        }
        catch(Exception e)
        {
            Log.log(this, Log.FATAL, "could not init external Jms", e);
        }
        return false;
    }

    private void closeJms() {
        
        Log.log(this, Log.INFO, "exiting external jms communication");
        
        if (extPublisherStatusChange != null){
            try{extPublisherStatusChange.close();extPublisherStatusChange=null;}
        catch (JMSException e){Log.log(this, Log.WARN, e);}}    
        if (extSession != null){try{extSession.close();extSession=null;}
        catch (JMSException e){Log.log(this, Log.WARN, e);}}
        if (extConnection != null){try{extConnection.stop();}
        catch (JMSException e){Log.log(this, Log.WARN, e);}}
        if (extConnection != null){try{extConnection.close();extConnection=null;}
        catch (JMSException e){Log.log(this, Log.WARN, e);}}
        if (extContext != null){try{extContext.close();extContext=null;}
        catch (NamingException e){Log.log(this, Log.WARN, e);}}

        Log.log(this, Log.INFO, "jms external communication closed");
    }
    
    private boolean sendStatusChange(int status, String strStat, long lSetTime) throws Exception
    {
        MapMessage mapMsg = null;
        try
        {
            mapMsg = extSession.createMapMessage();
        }
        catch(Exception e)
        {
            Log.log(this, Log.FATAL, "could not createMapMessage", e);
        }
        if (mapMsg == null)
            return false;

        mapMsg.setString(AmsConstants.MSGPROP_CHECK_TYPE, "PStatus");
        mapMsg.setString(AmsConstants.MSGPROP_CHECK_PURL, InetAddress.getLocalHost().getHostAddress());
        mapMsg.setString(AmsConstants.MSGPROP_CHECK_PLUGINID, JMSConnectorPlugin.PLUGIN_ID);
        mapMsg.setString(AmsConstants.MSGPROP_CHECK_STATUSTIME, Utils.longTimeToUTCString(lSetTime));
        mapMsg.setString(AmsConstants.MSGPROP_CHECK_STATUS, String.valueOf(status));
        mapMsg.setString(AmsConstants.MSGPROP_CHECK_TEXT, strStat);

        Log.log(this, Log.INFO, "StatusChange - start external jms send. MessageProperties= " + Utils.getMessageString(mapMsg));

        try
        {
            extPublisherStatusChange.send(mapMsg);
        }
        catch(Exception e)
        {
            Log.log(this, Log.FATAL, "could not send to external jms", e);
            return false;
        }

        Log.log(this, Log.INFO, "send external jms message done");

        return true;
    }
    
    public void bindService(ISessionService sessionService) {
    	
        IPreferencesService pref = Platform.getPreferencesService();
    	String xmppServer = pref.getString(JMSConnectorPlugin.PLUGIN_ID, JmsConnectorPreferenceKey.P_XMPP_SERVER, "krynfs.desy.de", null);
        String xmppUser = pref.getString(JMSConnectorPlugin.PLUGIN_ID, JmsConnectorPreferenceKey.P_XMPP_USER, "anonymous", null);
        String xmppPassword = pref.getString(JMSConnectorPlugin.PLUGIN_ID, JmsConnectorPreferenceKey.P_XMPP_PASSWORD, "anonymous", null);
    	
    	try {
			sessionService.connect(xmppUser, xmppPassword, xmppServer);
			xmppService = sessionService;
			Log.log(this, Log.INFO, "XMPP connection created.");
		} catch (Exception e) {
			Log.log(this, Log.WARN, "XMPP connection is not available: " + e.getMessage());
		}
    }
    
    public void unbindService(ISessionService service) {
    	// Nothing to do here
    }
}
