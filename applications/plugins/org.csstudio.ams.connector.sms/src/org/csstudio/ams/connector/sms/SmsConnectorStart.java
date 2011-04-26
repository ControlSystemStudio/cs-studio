
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
 
package org.csstudio.ams.connector.sms;

import java.net.InetAddress;
import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.csstudio.ams.AmsActivator;
import org.csstudio.ams.AmsConstants;
import org.csstudio.ams.Log;
import org.csstudio.ams.SynchObject;
import org.csstudio.ams.Utils;
import org.csstudio.ams.connector.sms.internal.SmsConnectorPreferenceKey;
import org.csstudio.ams.internal.AmsPreferenceKey;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.preference.IPreferenceStore;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;

public class SmsConnectorStart implements IApplication, IGenericServiceListener<ISessionService> 
{
    public final static int STAT_INIT = 0;
    public final static int STAT_OK = 1;
    public final static int STAT_ERR_MODEM = 2;
    public final static int STAT_ERR_MODEM_SEND = 3;
    public final static int STAT_ERR_JMSCON = 4; // jms communication to ams internal jms partners
    public final static int STAT_ERR_UNDEFINED = 5;
    public final static int STAT_SENDING = 6;
    public final static int STAT_READING = 7;

    public static String STATE_TEXT[] = { "Init", "OK", "Modem Error: General",
                                          "Modem Error: Sending", "JMS Error",
                                          "Unknown", "Sending", "Reading" };

    public final static long WAITFORTHREAD = 10000;
    public final static boolean CREATE_DURABLE = true;

    private static SmsConnectorStart _me;

    private Context extContext = null;
    private ConnectionFactory extFactory = null;
    private Connection extConnection = null;
    private Session extSession = null;
    
    private MessageProducer extPublisherStatusChange = null;
    
    private SynchObject sObj = null;
    private String managementPassword; 
    private int lastStatus = 0;
    
    private boolean bStop = false;
    private boolean restart = false;
    
    public SmsConnectorStart()
    {
        _me = this;
        sObj = new SynchObject(STAT_INIT, System.currentTimeMillis());
        bStop = false;
        
        IPreferencesService pref = Platform.getPreferencesService();
        managementPassword = pref.getString(AmsActivator.PLUGIN_ID, AmsPreferenceKey.P_AMS_MANAGEMENT_PASSWORD, "", null);
        if(managementPassword == null) {
            managementPassword = "";
        }
    }

    public Object start(IApplicationContext context) throws Exception
    {
        SmsConnectorWork scw = null;
        boolean bInitedJms = false;

        Log.log(this, Log.INFO, "start");
        Log.log(this, Log.INFO, "Location: " + Platform.getInstanceLocation().toString());
        
        SmsConnectorPreferenceKey.showPreferences();

        // use synchronized method
        lastStatus = getStatus();

        while(bStop == false)
        {
            try
            {
                if (scw == null)
                {
                    scw = new SmsConnectorWork(this);
                    scw.start();
                }
                
                if (!bInitedJms)
                {
                    bInitedJms = initJms();
                }
        
                // Log.log(this, Log.DEBUG, "is running");
                Thread.sleep(1000);
                
                SynchObject actSynch = new SynchObject(0, 0);
                // If the status have not been changed during the last 3 minutes
                // OR
                // If the current status signals a modem error
                // THEN
                // Do a automatic restart
                if(!sObj.hasStatusSet(actSynch, 180, STAT_ERR_UNDEFINED) || sObj.getSynchStatus() == STAT_ERR_MODEM)
                {
                    // every 2 minutes if blocked
                    Log.log(this, Log.FATAL, "TIMEOUT: status has not changed the last 3 minutes.");
                    
                    scw.stopWorking();
                    
                    try
                    {
                        scw.join(WAITFORTHREAD);
                    }
                    catch(InterruptedException ie) { }

                    if(scw.stoppedClean())
                    {
                        Log.log(this, Log.FATAL, "ERROR: Thread stopped clean.");
                        
                        scw = null;
                    }
                    else
                    {
                        Log.log(this, Log.FATAL, "ERROR: Thread did NOT stop clean.");
                        scw.closeJms();
                        scw = null;
                    }
                    
                    this.setStatus(STAT_ERR_MODEM);
                    
                    Log.log(Log.FATAL, "Modem error. Restart SmsConnector");
                    
                    this.setRestart();
                }
                
                String statustext = "unknown";
                if(actSynch.getStatus() != lastStatus) // if status value changed
                {
                    switch(actSynch.getStatus())
                    {
                        case STAT_INIT:
                            statustext = "init";
                            break;
                            
                        case STAT_OK:
                            statustext = "ok";
                            break;
                        
                        case STAT_READING:
                            statustext = "reading";
                            break;
                            
                        case STAT_SENDING:
                            statustext = "sending";
                            break;
                            
                        case STAT_ERR_MODEM:
                        case STAT_ERR_MODEM_SEND:
                            statustext = "err_modem";
                            break;
                            
                        case STAT_ERR_JMSCON:
                            statustext = "err_jms";
                            break;
                    }
                    
                    Log.log(this, Log.DEBUG, "set status to " + statustext + "(" + actSynch.getStatus() + ") - Last status: " + lastStatus);
                    
                    lastStatus = actSynch.getStatus();
                    
                    if(bInitedJms)
                    {
                        if(!sendStatusChange(actSynch.getStatus(), statustext, actSynch.getTime()))
                        {
                            closeJms();
                            bInitedJms = false;
                        }
                    }
                }
            }
            catch(Exception e)
            {
                Log.log(this, Log.FATAL, e);
                
                closeJms();
                bInitedJms = false;
            }
        }

        if(scw != null)
        {
            // Clean stop of the working thread
            scw.stopWorking();
            
            try
            {
                scw.join(WAITFORTHREAD);
            }
            catch(InterruptedException ie) { }
    
            if(scw.stoppedClean())
            {
                Log.log(this, Log.FATAL, "Restart/Exit: Thread stopped clean.");
                
                scw = null;
            }
            else
            {
                Log.log(this, Log.FATAL, "Restart/Exit: Thread did NOT stop clean.");
                scw.closeModem();
                scw.closeJms();
                scw.storeRemainingMessages();
                scw = null;
            }
        }
        
        Log.log(this, Log.INFO, "Leaving start()");
        
        if(restart)
            return EXIT_RESTART;
        else
            return EXIT_OK;
    }

    
    public void stop()
    {
        Log.log(Log.INFO, "method: stop()");
        return;
    }

    public static SmsConnectorStart getInstance()
    {
        return _me;
    }
    
    /**
     * Sets the restart flag to force a restart.
     */
    public synchronized void setRestart()
    {
        restart = true;
        bStop = true;
    }
    
    /**
     * Sets the shutdown flag to force a shutdown.
     */
    public synchronized void setShutdown()
    {
        restart = false;
        bStop = true;
    }

    /**
     * 
     * @return
     */
    public synchronized String getPassword()
    {
        return managementPassword;
    }

    public int getStatus()
    {
        return sObj.getSynchStatus();
    }
    
    public void setStatus(int status)
    {
        sObj.setSynchStatus(status);                                            // set always, to update time
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
            
            // ADDED BY: Markus M�ller, 25.05.2007
            extConnection.setClientID("SmsConnectorStartSenderExternal");
            
            extSession = extConnection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            
            // CHANGED BY: Markus M�ller, 25.05.2007
            /*
            extPublisherStatusChange = extSession.createProducer((Topic)extContext.lookup(
                    storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_EXT_TOPIC_STATUSCHANGE)));
            */
            
            extPublisherStatusChange = extSession.createProducer(extSession.createTopic(
                    storeAct.getString(org.csstudio.ams.internal.AmsPreferenceKey.P_JMS_EXT_TOPIC_STATUSCHANGE)));
            if (extPublisherStatusChange == null)
            {
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

    private void closeJms()
    {
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
        mapMsg.setString(AmsConstants.MSGPROP_CHECK_PLUGINID, SmsConnectorPlugin.PLUGIN_ID);
        mapMsg.setString(AmsConstants.MSGPROP_CHECK_STATUSTIME, Utils.longTimeToUTCString(lSetTime));
        mapMsg.setString(AmsConstants.MSGPROP_CHECK_STATUS, String.valueOf(status));
        mapMsg.setString(AmsConstants.MSGPROP_CHECK_TEXT, strStat);

        Log.log(this, Log.DEBUG, "StatusChange - start external jms send. MessageProperties= " + Utils.getMessageString(mapMsg));

        try
        {
            extPublisherStatusChange.send(mapMsg);
        }
        catch(Exception e)
        {
            Log.log(this, Log.FATAL, "could not send to external jms", e);
            return false;
        }

        Log.log(this, Log.DEBUG, "send external jms message done");

        return true;
    }
    
    public void bindService(ISessionService sessionService) {
    	IPreferencesService pref = Platform.getPreferencesService();
    	String xmppServer = pref.getString(SmsConnectorPlugin.PLUGIN_ID, SmsConnectorPreferenceKey.P_XMPP_SERVER, "krynfs.desy.de", null);
        String xmppUser = pref.getString(SmsConnectorPlugin.PLUGIN_ID, SmsConnectorPreferenceKey.P_XMPP_USER, "anonymous", null);
        String xmppPassword = pref.getString(SmsConnectorPlugin.PLUGIN_ID, SmsConnectorPreferenceKey.P_XMPP_PASSWORD, "anonymous", null);
    	
    	try {
			sessionService.connect(xmppUser, xmppPassword, xmppServer);
		} catch (Exception e) {
			CentralLogger.getInstance().warn(this,
					"XMPP connection is not available, " + e.toString());
		}
    }
    
    public void unbindService(ISessionService service) {
    	service.disconnect();
    }
}
