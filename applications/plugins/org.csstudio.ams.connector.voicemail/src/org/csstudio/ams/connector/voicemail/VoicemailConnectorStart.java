
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
 
package org.csstudio.ams.connector.voicemail;

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
import org.csstudio.ams.connector.voicemail.internal.VoicemailConnectorPreferenceKey;
import org.csstudio.ams.internal.AmsPreferenceKey;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.preference.IPreferenceStore;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;

public class VoicemailConnectorStart implements IApplication,
                                                IGenericServiceListener<ISessionService> {

    public final static int STAT_INIT = 0;
    public final static int STAT_OK = 1;
    public final static int STAT_ERR_VM_SERVICE = 2;            //at read, only vm new
    public final static int STAT_ERR_VM_SERVICE_BADRSP = 3;     //bad but response - server busy, try again recover jms 
    public final static int STAT_ERR_VM_SERVICE_SEND = 4;       //reopen all
    public final static int STAT_ERR_JMSCON = 5;                // jms communication to ams internal jms partners
    public final static int STAT_ERR_UNKNOWN = 6;

    public final static long WAITFORTHREAD = 10000;
    public final static boolean CREATE_DURABLE = true;
    
    private static VoicemailConnectorStart _instance = null;

    private Context extContext = null;
    private ConnectionFactory extFactory = null;
    private Connection extConnection = null;
    private Session extSession = null;
    
    private MessageProducer extPublisherStatusChange = null;
    
    private ISessionService xmppService;
    
    private SynchObject sObj = null;
    private int lastStatus = 0;
    
    private boolean bStop;
    private String managementPassword; 
    private boolean restart;

    public VoicemailConnectorStart() {
        _instance = this;
        sObj = new SynchObject(STAT_INIT, System.currentTimeMillis());
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

    public static VoicemailConnectorStart getInstance() {
        return _instance;
    }
    
    public synchronized void setRestart() {
        restart = true;
        bStop = true;
    }

    public synchronized void setShutdown() {
        restart = false;
        bStop = true;
    }
    
    /**
     * 
     * @return A String object that contains the management password
     */
    public synchronized String getPassword() {
        return managementPassword;
    }

    @SuppressWarnings("static-access")
    public Object start(IApplicationContext context) throws Exception {
        
        VoicemailConnectorWork scw = null;
        boolean bInitedJms = false;
        
        // use synchronized method
        lastStatus = getStatus();
        int iTimeouts = 0;

        Log.log(this, Log.INFO, "Starting application " + VoicemailConnectorPlugin.PLUGIN_ID);
        VoicemailConnectorPlugin.getDefault().addSessionServiceListener(this);
        context.applicationRunning();
        
        bStop = false;
        restart = false;

        while(bStop == false) {
            
            try {
                
                if (scw == null) {
                    scw = new VoicemailConnectorWork(this);
                    scw.start();
                }

                // > S10min
                if (iTimeouts > 1) {
                    
                    scw.interrupt();
                    scw = null;
                    try {
                        scw.sleep(20000);
                    } catch(Exception e) {
                        // Can be ignored
                    }
                    
                    iTimeouts = 0;
                }
                
                if (!bInitedJms) {
                    bInitedJms = initJms();
                }
        
                // Log.log(this, Log.DEBUG, "run");
                Thread.sleep(1000);
                
                SynchObject actSynch = new SynchObject(0, 0);
                if (!sObj.hasStatusSet(actSynch, 300, STAT_ERR_UNKNOWN))        // if status has not changed in the last 5 minutes
                {                                                               // every 5 minutes if blocked
                    Log.log(this, Log.FATAL, "TIMEOUT: status has not changed the last 5 minutes.");
                    iTimeouts++;
                }
                else
                    iTimeouts = 0;

                String statustext = "unknown";
                if (actSynch.getStatus() != lastStatus)                         // if status value changed
                {
                    switch (actSynch.getStatus())
                    {
                        case STAT_INIT:
                            statustext = "init";
                            break;
                        case STAT_OK:
                            statustext = "ok";
                            break;
                        case STAT_ERR_VM_SERVICE:
                            statustext = "err_vm_service";
                            break;
                        case STAT_ERR_VM_SERVICE_SEND:
                            statustext = "err_vm_service_send";
                            break;
                        case STAT_ERR_VM_SERVICE_BADRSP:
                            statustext = "err_vm_service_badrsp";
                            break;
                        case STAT_ERR_JMSCON:
                            statustext = "err_jms";
                            break;
                    }
                    Log.log(this, Log.INFO, "set status to " + statustext + "(" + actSynch.getStatus() + ")");
                    lastStatus = actSynch.getStatus();
                    if (bInitedJms)
                    {
                        if (!sendStatusChange(actSynch.getStatus(), statustext, actSynch.getTime()))
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

        Log.log(this, Log.INFO, "VoicemailConnectorStart is exiting now");
        
        if(scw != null) {
            
            // Clean stop of the working thread
            scw.stopWorking();
            
            try {
                scw.join(WAITFORTHREAD);
            } catch(InterruptedException ie) {
                // Can be ignored
            }
    
            if(scw.stoppedClean()) {
                Log.log(this, Log.FATAL, "Restart/Exit: Thread stopped clean.");
                scw = null;
            } else {
                Log.log(this, Log.FATAL, "Restart/Exit: Thread did NOT stop clean.");
                scw.closeJms();
                scw.closeCallCenter();
                scw = null;
            }
        }
        
        if (xmppService != null) {
            xmppService.disconnect();
        }
        
        Integer exitCode = IApplication.EXIT_OK;
        if(restart) {
            exitCode = EXIT_RESTART;
        }
        
        return exitCode;
    }

    public int getStatus()
    {
        return sObj.getSynchStatus();
    }
    
    public void setStatus(int status)
    {
    	// set always, to update time
        sObj.setSynchStatus(status);
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
            extConnection.setClientID("VoicemailConnectorStartSenderExternal");
            
            extSession = extConnection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            
            // CHANGED BY: Markus Moeller, 25.05.2007
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
        mapMsg.setString(AmsConstants.MSGPROP_CHECK_PLUGINID, VoicemailConnectorPlugin.PLUGIN_ID);
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
    	String xmppServer = pref.getString(VoicemailConnectorPlugin.PLUGIN_ID, VoicemailConnectorPreferenceKey.P_XMPP_SERVER, "krynfs.desy.de", null);
        String xmppUser = pref.getString(VoicemailConnectorPlugin.PLUGIN_ID, VoicemailConnectorPreferenceKey.P_XMPP_USER, "anonymous", null);
        String xmppPassword = pref.getString(VoicemailConnectorPlugin.PLUGIN_ID, VoicemailConnectorPreferenceKey.P_XMPP_PASSWORD, "anonymous", null);
   	
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
