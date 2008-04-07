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
 
package org.csstudio.ams.filtermanager;

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
import org.csstudio.ams.Activator;
import org.csstudio.ams.AmsConstants;
import org.csstudio.ams.Log;
import org.csstudio.ams.SynchObject;
import org.csstudio.ams.Utils;
import org.csstudio.platform.libs.epics.EpicsPlugin;
import org.csstudio.platform.startupservice.IStartupServiceListener;
import org.csstudio.platform.startupservice.StartupServiceEnumerator;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.preference.IPreferenceStore;

public class FilterManagerStart implements IApplication
{
    public final static int STAT_INIT = 0;
    public final static int STAT_OK = 1;
    public final static int STAT_ERR_APPLICATION_DB = 2;
    public final static int STAT_ERR_APPLICATION_DB_CHECKCMDTOPIC = 3;
    public final static int STAT_ERR_JMSCON_INT = 4;                            // jms communication to ams internal jms partners
    public final static int STAT_ERR_JMSCON_INT_SEND = 5;
    public final static int STAT_ERR_JMSCON_EXT = 6;                            // jms communication to external jms (AlarmTopic, ExtCommandTopic)
    public final static int STAT_ERR_FLG_RPL = 7;                               // could not update db flag (ReplicationState)
    public final static int STAT_ERR_UNKNOWN = 8;

    public final static long WAITFORTHREAD = 10000;

    private static FilterManagerStart _instance = null;
    
    private Context             extContext                  = null;
    private ConnectionFactory   extFactory                  = null;
    private Connection          extConnection               = null;
    private Session             extSession                  = null;
    
    private MessageProducer     extPublisherStatusChange    = null;
    
    private SynchObject         sObj                        = null;
    private int                 lastStatus                  = 0;

    private boolean bStop;
    private boolean restart;
    
    public FilterManagerStart()
    {
        _instance = this;
        
        sObj = new SynchObject(STAT_INIT, System.currentTimeMillis());
        
        // For XMPP login
        for(IStartupServiceListener s : StartupServiceEnumerator.getServices())
        {
            s.run();
        }
    }
    
    public static FilterManagerStart getInstance()
    {
        return _instance;
    }
    
    public void stop()
    {
        return;
    }

    public synchronized void setRestart()
    {
        restart = true;
        bStop = true;
    }

    public synchronized void setShutdown()
    {
        restart = false;
        bStop = true;
    }
    
    public Object start(IApplicationContext context) throws Exception
    {
        Log.log(this, Log.INFO, "start");
        FilterManagerWork fmw = null;
        boolean bInitedJms = false;
        lastStatus = getStatus();                                               // use synchronized method
        
        Log.log(this, Log.INFO, "Call: InstallPreferences");
        EpicsPlugin.getDefault().installPreferences();
        Log.log(this, Log.INFO, "Finished: InstallPreferences");

        bStop = false;
        restart = false;
        
        while(bStop == false)
        {
            try
            {
                if (fmw == null)
                {
                    fmw = new FilterManagerWork(this);
                    fmw.start();
                }
                
                if (!bInitedJms)
                {
                    bInitedJms = initJms();
                }
        
                // Log.log(this, Log.DEBUG, "run");
                Thread.sleep(1000);
                
                SynchObject actSynch = new SynchObject(0, 0);
                if (!sObj.hasStatusSet(actSynch, 300, STAT_ERR_UNKNOWN))        // if status has not changed in the last 5 minutes
                {                                                               // every 5 minutes if blocked
                    Log.log(this, Log.FATAL, "TIMEOUT: status has not changed the last 5 minutes.");
                }

                String statustext = "unknown";
                int status = actSynch.getStatus();
				if (status != lastStatus)                         // if status value changed
                {
                    switch (status)
                    {
                        case STAT_INIT:
                            statustext = "init";
                            break;
                        case STAT_OK:
                            statustext = "ok";
                            break;
                        case STAT_ERR_APPLICATION_DB:
                        case STAT_ERR_APPLICATION_DB_CHECKCMDTOPIC:
                            statustext = "err_application_db";
                            break;
                        case STAT_ERR_JMSCON_INT:
                        case STAT_ERR_JMSCON_INT_SEND:
                            statustext = "err_jms_internal";
                            break;
                        case STAT_ERR_JMSCON_EXT:
                            statustext = "err_jms_external";
                            break;
                        case STAT_ERR_FLG_RPL:
                            statustext = "err_flag_rpl_state";
                            break;
                    }
                    Log.log(this, Log.INFO, "set status to " + statustext + "(" + status + ")");
                    lastStatus = status;
                    if (bInitedJms)
                    {
                        if (!sendStatusChange(status, statustext, actSynch.getTime()))
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

        Log.log(this, Log.INFO, "FilterManagerStart is exiting now");
        
        if(fmw != null)
        {
            // Clean stop of the working thread
            fmw.stopWorking();
            
            try
            {
                fmw.join(WAITFORTHREAD);
            }
            catch(InterruptedException ie) { }
    
            if(fmw.stoppedClean())
            {
                Log.log(this, Log.FATAL, "Restart/Exit: Thread stopped clean.");
                
                fmw = null;
            }
            else
            {
                Log.log(this, Log.FATAL, "Restart/Exit: Thread did NOT stop clean.");
                fmw.closeJmsExternal();
                fmw.closeJmsInternal();
                fmw.closeApplicationDb();
                fmw = null;
            }
        }
        
        if(restart)
            return EXIT_RESTART;
        else
            return EXIT_OK;
    }

    public int getStatus()
    {
        return sObj.getSynchStatus();
    }
    public void setStatus(int status)
    {
    	//System.out.println("FilterManagerStart.setStatus()");
        sObj.setSynchStatus(status);                                            // set always, to update time
    }
    
    private boolean initJms()
    {
        try
        {
            IPreferenceStore storeAct = Activator.getDefault().getPreferenceStore();
            Hashtable<String, String> properties = new Hashtable<String, String>();
            properties.put(Context.INITIAL_CONTEXT_FACTORY, 
                    storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_EXTERN_CONNECTION_FACTORY_CLASS));
            
            //CHANGED BY: Markus Möller, 13.08.2007
            //properties.put(Context.PROVIDER_URL, 
            //        storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_EXTERN_PROVIDER_URL_1));
            
            properties.put(Context.PROVIDER_URL, 
                    storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_EXTERN_SENDER_PROVIDER_URL));

            extContext = new InitialContext(properties);
            
            extFactory = (ConnectionFactory) extContext.lookup(
                    storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_EXTERN_CONNECTION_FACTORY));
            extConnection = extFactory.createConnection();
            extConnection.setClientID("AmsFilterManagerStartSenderExternal");
            
            extSession = extConnection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            
            // CHANGED BY Markus Möller, 2007-05-24
            /*extPublisherStatusChange = extSession.createProducer((Topic)extContext.lookup(
                    storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_EXT_TOPIC_STATUSCHANGE)));
            */
            
            extPublisherStatusChange = extSession.createProducer(extSession.createTopic(
                    storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_EXT_TOPIC_STATUSCHANGE)));
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
        
        if (extPublisherStatusChange != null){try{extPublisherStatusChange.close();}
        catch (JMSException e){Log.log(this, Log.WARN, e);}finally{extPublisherStatusChange=null;}}    
        if (extSession != null){try{extSession.close();}
        catch (JMSException e){Log.log(this, Log.WARN, e);}finally{extSession=null;}}
        if (extConnection != null){try{extConnection.stop();}
        catch (JMSException e){Log.log(this, Log.WARN, e);}}
        if (extConnection != null){try{extConnection.close();}
        catch (JMSException e){Log.log(this, Log.WARN, e);}finally{extConnection=null;}}
        if (extContext != null){try{extContext.close();}
        catch (NamingException e){Log.log(this, Log.WARN, e);}finally{extContext=null;}}

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
        mapMsg.setString(AmsConstants.MSGPROP_CHECK_PLUGINID, FilterManagerPlugin.PLUGIN_ID);
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
}
