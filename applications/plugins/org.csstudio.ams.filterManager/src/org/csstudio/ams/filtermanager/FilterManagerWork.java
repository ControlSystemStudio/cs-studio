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

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
// import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
// import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.csstudio.ams.Activator;
import org.csstudio.ams.AmsConstants;
import org.csstudio.ams.Log;
import org.csstudio.ams.Utils;
import org.csstudio.ams.dbAccess.AmsConnectionFactory;
import org.csstudio.ams.dbAccess.configdb.AggrFilterConditionDAO;
import org.csstudio.ams.dbAccess.configdb.AggrFilterConditionTObject;
import org.csstudio.ams.dbAccess.configdb.AggrFilterTObject;
import org.csstudio.ams.dbAccess.configdb.FilterConditionTimeBasedItemsDAO;
import org.csstudio.ams.dbAccess.configdb.FilterConditionTimeBasedItemsTObject;
import org.csstudio.ams.dbAccess.configdb.FlagDAO;
import org.csstudio.ams.dbAccess.configdb.HistoryDAO;
import org.csstudio.ams.dbAccess.configdb.HistoryTObject;
import org.csstudio.ams.dbAccess.configdb.MessageDAO;
import org.csstudio.ams.filter.IFilterCondition;
import org.csstudio.platform.libs.jms.JmsRedundantReceiver;
import org.eclipse.jface.preference.IPreferenceStore;

public class FilterManagerWork extends Thread implements AmsConstants
{
    private final static int CMD_INIT = 0;
    private final static int CMD_IDLE = 1;                                      // normal work
    private final static int CMD_RPL_START = 2;                                 // start replication
    private final static int CMD_RPL_WAITFOR_DIST = 3;                          // wait for dist
    private final static int CMD_RPL_END = 4;                                   // replication end
    
    //private final int CONSUMER_CONNECTIONS = 2;
    
    private int iCmd = CMD_INIT;
    
    private FilterManagerStart  fms                     = null;
    private java.sql.Connection conDb                   = null;
    
    //jms internal communication
    // --- Sender connection ---
    private Context             amsSenderContext        = null;
    private ConnectionFactory   amsSenderFactory        = null;
    private Connection          amsSenderConnection     = null;
    private Session             amsSenderSession        = null;
    
    private MessageProducer     amsPublisherDist        = null;

    // --- Receiver Connection ---
    private JmsRedundantReceiver amsReceiver = null;
    /*private Context[]           amsReceiverContext      = new Context[CONSUMER_CONNECTIONS];;
    private ConnectionFactory[] amsReceiverFactory      = new ConnectionFactory[CONSUMER_CONNECTIONS];
    private Connection[]        amsReceiverConnection   = new Connection[CONSUMER_CONNECTIONS];
    private Session[]           amsReceiverSession      = new Session[CONSUMER_CONNECTIONS];
        
    // CHANGED BY: Markus M�ller, 28.06.2007
    // private TopicSubscriber     amsSubscriberCommand    = null;
    private MessageConsumer[]   amsSubscriberCommand    = new MessageConsumer[CONSUMER_CONNECTIONS];
    */
    //jms external communication
    private JmsRedundantReceiver extReceiver = null;
    /*private Context[]            extContext              = new Context[CONSUMER_CONNECTIONS];
    private ConnectionFactory[]  extFactory              = new ConnectionFactory[CONSUMER_CONNECTIONS];
    private Connection[]         extConnection           = new Connection[CONSUMER_CONNECTIONS];
    private Session[]            extSession              = new Session[CONSUMER_CONNECTIONS];
    
    // CHANGED BY: Markus M�ller, 28.06.2007
    // private TopicSubscriber     extSubscriberAlarmFmr   = null;
    private MessageConsumer[]    extSubscriberAlarmFmr   = new MessageConsumer[CONSUMER_CONNECTIONS];
    // private TopicSubscriber     extSubscriberCommand    = null;
    private MessageConsumer[]    extSubscriberCommand    = new MessageConsumer[CONSUMER_CONNECTIONS];
    */
    private boolean bStop = false;
    private boolean bStoppedClean = false;

    private Map<Integer,String> hmAggrFCTList = null;
    private List<AggrFilterTObject> aggrFilterList = null;
    private HashMap<Integer, IFilterCondition> hmFConditions = null;

    public FilterManagerWork(FilterManagerStart fms)
    {
        this.fms = fms;
    }
    
    public void run()
    {
        boolean bInitedDb = false;
        boolean bInitedJmsInt = false;
        boolean bInitedJmsExt = false;
        int iErr = FilterManagerStart.STAT_OK;
        Log.log(this, Log.INFO, "start filter manager work");
        bStop = false;
        
        while(bStop == false)
        {
            try
            {
                if (!bInitedDb)
                {
                    bInitedDb = initApplicationDb();
                    if (bInitedDb)
                    {
                        bInitedDb = initFilterList();                           // always init filter list after application db initialization
                        if (bInitedDb)
                            bInitedDb = initRplStateFlag();                     // get last replication state flag value
                    }
                    
                    if (!bInitedDb)                                             // if one of the three functions return false
                        iErr = FilterManagerStart.STAT_ERR_APPLICATION_DB;
                }
                if (bInitedDb && iCmd == CMD_RPL_END)
                {
                    bInitedDb = initFilterList();                               // always init filter list after replication
                    if (!bInitedDb)
                        iErr = FilterManagerStart.STAT_ERR_APPLICATION_DB;
                    else
                        iCmd = CMD_IDLE;                                        // replication done, init filter list from application db done
                }
                if (bInitedDb && !bInitedJmsInt)
                {
                    bInitedJmsInt = initJmsInternal();
                    if (!bInitedJmsInt)
                        iErr = FilterManagerStart.STAT_ERR_JMSCON_INT;
                }
                if (bInitedDb && bInitedJmsInt && !bInitedJmsExt)
                {
                    bInitedJmsExt = initJmsExternal();
                    if (!bInitedJmsExt)
                        iErr = FilterManagerStart.STAT_ERR_JMSCON_EXT;
                }
                
                sleep(100);
                
                if (bInitedDb && bInitedJmsInt && bInitedJmsExt)
                {
                    iErr = FilterManagerStart.STAT_OK;
                    
//                    Log.log(this, Log.DEBUG, "runs");
                    
                    // work 1 of 3 -> check command topic for replication start msg
                    if (iErr == FilterManagerStart.STAT_OK && iCmd == CMD_IDLE)
                        iErr = checkCommandTopic();
                    
                    if (iErr == FilterManagerStart.STAT_OK && iCmd == CMD_RPL_START)
                        iErr = sendRplStartToDist();
                    
                    if (iErr == FilterManagerStart.STAT_OK && iCmd == CMD_RPL_WAITFOR_DIST)
                        iErr = waitForDist();
                    
                    if (iErr == FilterManagerStart.STAT_OK && iCmd == CMD_RPL_END)
                        continue;
                    
                    // work 2 of 3 -> work on alarm topic
                    if (iErr == FilterManagerStart.STAT_OK)
                    {
                        Message message = null;
                        try
                        {
                            message = extReceiver.receive("extSubscriberAlarmFmr");                                
                        }
                        catch(Exception e)
                        {
                            iErr = FilterManagerStart.STAT_ERR_JMSCON_EXT;
                        }
                                                    
                        if (message != null)
                            iErr = workOnMsg(message);                          // filter 1 messages, other in the next run
                    }
                    
                    // work 3 of 3 -> work on active time based filter conditions
                    if (iErr == FilterManagerStart.STAT_OK)
                    {
                        iErr = workOnTimeBasedActive(amsSenderSession, amsPublisherDist);
                    }
                }

                if (iErr == FilterManagerStart.STAT_ERR_JMSCON_INT_SEND)
                {
                    closeJmsInternal();
                    bInitedJmsInt = false;
                    closeJmsExternal();                                         // recover msg
                    bInitedJmsExt = false;
                }
                if (iErr == FilterManagerStart.STAT_ERR_APPLICATION_DB_CHECKCMDTOPIC)
                {
                    closeApplicationDb();
                    bInitedDb = false;
                    closeJmsExternal();                                         // recover msg
                    bInitedJmsExt = false;
                }
                
                // if (iErr == FilterManagerStart.STAT_ERR_FLG_RPL) do close all
                if (iErr == FilterManagerStart.STAT_ERR_APPLICATION_DB ||
                    iErr == FilterManagerStart.STAT_ERR_FLG_RPL)
                {
                    closeApplicationDb();
                    bInitedDb = false;
                }
                if (iErr == FilterManagerStart.STAT_ERR_JMSCON_INT ||
                    iErr == FilterManagerStart.STAT_ERR_FLG_RPL)
                {
                    closeJmsInternal();
                    bInitedJmsInt = false;
                }
                if (iErr == FilterManagerStart.STAT_ERR_JMSCON_EXT ||
                    iErr == FilterManagerStart.STAT_ERR_FLG_RPL)
                {
                    closeJmsExternal();
                    bInitedJmsExt = false;
                }

                // set status in every loop
                //Log.log(this, Log.INFO, "FilterManagerWork.run() State: "+iErr);
                fms.setStatus(iErr);                                            // set error status, can be OK if no error
            }
            catch (Exception e)
            {
            	//Log.log(this, Log.INFO, "FilterManagerWork.run() State: "+FilterManagerStart.STAT_ERR_UNKNOWN);
                fms.setStatus(FilterManagerStart.STAT_ERR_UNKNOWN);
                Log.log(this, Log.FATAL, e);

                closeApplicationDb();
                bInitedDb = false;
                
                closeJmsInternal();
                bInitedJmsInt = false;
                
                closeJmsExternal();
                bInitedJmsExt = false;
            }
        }
        
        closeJmsExternal();
        closeJmsInternal();
        closeApplicationDb();
        bStoppedClean = true;
        
        Log.log(this, Log.INFO, "sms connector exited");
    }
    
    private boolean initFilterList() throws Exception
    {
        try
        {
            hmFConditions = new HashMap<Integer, IFilterCondition>();
            hmAggrFCTList = AggrFilterConditionDAO.selectFCTList(conDb);
            aggrFilterList = AggrFilterConditionDAO.selectFilterList(conDb);
            moveTimeBasedFilterConditionsToEnd(aggrFilterList);                 // because of wrong return type, else no normal fc would work
            return true;
        }
        catch (SQLException e)
        {
            Log.log(this, Log.FATAL, "could not initialize from application db", e);
        }
        return false;
    }
    
    private boolean initRplStateFlag() throws Exception
    {
        try
        {
            short sFlag = FlagDAO.selectFlag(conDb, FLG_RPL);
            switch (sFlag)
            {
                // FLAGVALUE_SYNCH_IDLE, FLAGVALUE_SYNCH_DIST_NOTIFY_FMR, other:
                default:
                    iCmd = CMD_IDLE;                                            // go to idle if all o.k.
                    break;
                case FLAGVALUE_SYNCH_FMR_RPL:
                    iCmd = CMD_RPL_START;
                    break;
                case FLAGVALUE_SYNCH_FMR_TO_DIST_SENDED:
                case FLAGVALUE_SYNCH_DIST_RPL:
                    iCmd = CMD_RPL_WAITFOR_DIST;
                    break;
            }
            return true;
        }
        catch (SQLException e)
        {
            Log.log(this, Log.FATAL, "could not get flag value from application db", e);
        }
        return false;
    }
    
    private boolean initApplicationDb()
    {
        try
        {
            conDb = AmsConnectionFactory.getApplicationDB();
            if (conDb == null)
            {
                Log.log(this, Log.FATAL, "could not init application database");
                return false;
            }
            return true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Log.log(this, Log.FATAL, "could not init application database");
        }
        return false;
    }
    
    public void closeApplicationDb()
    {
        AmsConnectionFactory.closeConnection(conDb);
        conDb = null;
        Log.log(this, Log.INFO, "application database connection closed");
    }
    
    private boolean initJmsInternal()
    {
        IPreferenceStore storeAct = Activator.getDefault().getPreferenceStore();
        Hashtable<String, String> properties = null;
        boolean result = false;
        
        try
        {
            properties = new Hashtable<String, String>();
            properties.put(Context.INITIAL_CONTEXT_FACTORY, 
                    storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_AMS_CONNECTION_FACTORY_CLASS));
            properties.put(Context.PROVIDER_URL, 
                    storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_AMS_SENDER_PROVIDER_URL));
            amsSenderContext = new InitialContext(properties);
            
            amsSenderFactory = (ConnectionFactory) amsSenderContext.lookup(
                    storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_AMS_CONNECTION_FACTORY));
            amsSenderConnection = amsSenderFactory.createConnection();
            
            // ADDED BY Markus M�ller, 2007-05-24
            amsSenderConnection.setClientID("AmsFilterManagerWorkSenderInternal");
            
            amsSenderSession = amsSenderConnection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            
            /* CHANGED BY Markus M�ller, 2007-05-24
            
            amsPublisherDist = amsSession.createProducer((Topic)amsContext.lookup(
                    storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_AMS_TOPIC_DISTRIBUTOR)));
            */
            
            // CHANGED BY Markus M�ller, 2007-10-30
            //   Changed to the topic for the message minder
            // amsPublisherDist = amsSenderSession.createProducer(amsSenderSession.createTopic(
            //        storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_AMS_TOPIC_DISTRIBUTOR)));
            amsPublisherDist = amsSenderSession.createProducer(amsSenderSession.createTopic(
                    storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_AMS_TOPIC_MESSAGEMINDER)));
            
            if (amsPublisherDist == null)
            {
                Log.log(this, Log.FATAL, "could not create amsPublisherDist");
                return false;
            }
            
            amsSenderConnection.start();

            // CHANGED BY Markus M�ller, 2007-05-24
            /*
            amsSubscriberCommand = amsSession.createDurableSubscriber((Topic)amsContext.lookup(
                    storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_AMS_TOPIC_COMMAND)),
                    storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_AMS_TSUB_CMD_FMR_RELOAD_END));
            */
            
            // CHANGED BY: Markus M�ller, 28.06.2007
            /*
            amsSubscriberCommand = amsSession.createDurableSubscriber(amsSession.createTopic(
                    storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_AMS_TOPIC_COMMAND)),
                    storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_AMS_TSUB_CMD_FMR_RELOAD_END));
            */
            
            // --- Receiver ---
            amsReceiver = new JmsRedundantReceiver("AmsFilterManagerWorkReceiverInternal", storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_AMS_PROVIDER_URL_1),
                    storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_AMS_PROVIDER_URL_2));
            
            if(!amsReceiver.isConnected())
            {
                Log.log(this, Log.FATAL, "could not create amsReceiver");
                return false;
            }
            
            result = amsReceiver.createRedundantSubscriber(
                    "amsSubscriberCommand",
                    storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_AMS_TOPIC_COMMAND),
                    storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_AMS_TSUB_CMD_FMR_RELOAD_END),
                    FilterManagerStart.CREATE_DURABLE);

            if(result == false)
            {
                Log.log(this, Log.FATAL, "could not create amsSubscriberCommand");
                return false;
            }
            
            return true;
        }
        catch(Exception e)
        {
            Log.log(this, Log.FATAL, "could not init internal Jms", e);
        }
        return false;
    }
    
    public void closeJmsInternal()
    {
        Log.log(this, Log.INFO, "exiting internal jms communication");
        
        if(amsReceiver != null)
        {
            amsReceiver.closeAll();
        }
        
        // Closing sender connection
        if (amsPublisherDist != null){try{amsPublisherDist.close();}
        catch (JMSException e){Log.log(this, Log.WARN, e);}finally{amsPublisherDist=null;}}

        if (amsSenderSession != null){try{amsSenderSession.close();}
        catch (JMSException e){Log.log(this, Log.WARN, e);}finally{amsSenderSession=null;}}
        if (amsSenderConnection != null){try{amsSenderConnection.stop();}
        catch (JMSException e){Log.log(this, Log.WARN, e);}}
        if (amsSenderConnection != null){try{amsSenderConnection.close();}
        catch (JMSException e){Log.log(this, Log.WARN, e);}finally{amsSenderConnection=null;}}
        if (amsSenderContext != null){try{amsSenderContext.close();}
        catch (NamingException e){Log.log(this, Log.WARN, e);}finally{amsSenderContext=null;}}

        Log.log(this, Log.INFO, "jms internal communication closed");
    }

    private boolean initJmsExternal()
    {
        IPreferenceStore storeAct = Activator.getDefault().getPreferenceStore();
        boolean result = false;
        
        extReceiver = new JmsRedundantReceiver("AmsFilterManagerWorkReceiverExternal", storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_EXTERN_PROVIDER_URL_1), storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_EXTERN_PROVIDER_URL_2));
        
        result = extReceiver.createRedundantSubscriber(
                "extSubscriberAlarmFmr",
                storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_EXT_TOPIC_ALARM),
                storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_EXT_TSUB_ALARM_FMR),
                FilterManagerStart.CREATE_DURABLE);        
        
        if (result == false)
        {
            Log.log(this, Log.FATAL, "could not create extSubscriberAlarmFmr");
            return false;
        }

        result = extReceiver.createRedundantSubscriber(
                "extSubscriberCommand",
                storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_EXT_TOPIC_COMMAND),
                storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_EXT_TSUB_CMD_FMR_START_RELOAD),
                FilterManagerStart.CREATE_DURABLE);        
        
        if (result == false)
        {
            Log.log(this, Log.FATAL, "could not create extSubscriberCommand");
            return false;
        }
        
        return true;
    }
    
    public void closeJmsExternal()
    {
        Log.log(this, Log.INFO, "exiting external jms communication");

        if(extReceiver != null)
        {
            extReceiver.closeAll();
        }
        
        Log.log(this, Log.INFO, "jms external communication closed");
    }
    
    /**
     * Sets the boolean variable that controlls the main loop to true
     */
    public synchronized void stopWorking()
    {
        bStop = true;
    }
    
    /**
     * Returns the shutdown state.
     * 
     * @return True, if the shutdown have occured clean otherwise false
     */
    public boolean stoppedClean()
    {
        return bStoppedClean;
    }

    private static void logHistoryRplStart(java.sql.Connection conDb, boolean bStart)
    {
        try
        {
            HistoryTObject history = new HistoryTObject();
    
            history.setTimeNew(new Date(System.currentTimeMillis()));
            history.setType("Config Synch");
    
            if (bStart)
                history.setDescription("Filtermanager stops normal work, wait for Distributor.");
            else
                history.setDescription("Filtermanager got config replication end, goes to normal work.");
            
            HistoryDAO.insert(conDb, history);
            Log.log(Log.INFO, history.getDescription());                        // history.getHistoryID() + ". "
        }
        catch(Exception ex)
        {
            Log.log(Log.FATAL, "exception at history logging start=" + bStart, ex);
        }
    }

    private int checkCommandTopic() throws Exception
    {
        try
        {
            Message message = null;
            while(null != (message = extReceiver.receive("extSubscriberCommand")))         // receiveNoWait has a bug with acknowledging in openjms 3
            {
                if (!(message instanceof MapMessage))
                {
                    Log.log(this, Log.WARN, "got unknown message " + message);
                    if (!acknowledge(message))                                  // deletes all received messages of the session
                        return FilterManagerStart.STAT_ERR_JMSCON_EXT;
                }
                else
                {
                    MapMessage mMsg = (MapMessage)message;
                    String val = mMsg.getString(MSGPROP_TCMD_COMMAND);
                    if (val != null && val.equals(MSGVALUE_TCMD_RELOAD_CFG_START))
                    {
                        Utils.logMessage("incoming message: ", mMsg);           // only log relevant msg
                        boolean bRet = FlagDAO.bUpdateFlag(conDb, FLG_RPL, FLAGVALUE_SYNCH_IDLE, FLAGVALUE_SYNCH_FMR_RPL);
                        if (bRet)
                        {
                            iCmd = CMD_RPL_START;
                            logHistoryRplStart(conDb, true);
                            Log.log(this, Log.DEBUG, "accept reload cfg");
                            
                            if (!acknowledge(message))
                                return FilterManagerStart.STAT_ERR_JMSCON_EXT;
                            break;                                              // end while
                        }
                        else
                        {
                            Log.log(this, Log.FATAL, "ignore start msg, could not update db flag to " + FLAGVALUE_SYNCH_FMR_RPL);
                            return FilterManagerStart.STAT_ERR_FLG_RPL;         // force new initialization, no recover() needed
                        }
                    }
                    else
                    {
                        Log.log(this, Log.DEBUG, "got unknown (maybe reload_cfg_end) command " + val);
                        if (!acknowledge(message))                              // deletes all received messages of the session
                            return FilterManagerStart.STAT_ERR_JMSCON_EXT;
                    }
                }// else                   
            }// while
        }// try
        catch (JMSException e)
        {
            Log.log(this, Log.FATAL, "could not receive command RELOAD_CFG_START from extSubscriberCommand", e);
            return FilterManagerStart.STAT_ERR_JMSCON_EXT;
        }
        catch (SQLException e)
        {
            Log.log(this, Log.FATAL, "could not bUpdateFlag", e);
            return FilterManagerStart.STAT_ERR_APPLICATION_DB_CHECKCMDTOPIC;
        }
        
        return FilterManagerStart.STAT_OK;
    }
    
    private int sendRplStartToDist() throws Exception
    {
        try
        {
            // delete all CmdStartMessages
            Message msg = null;
            Log.log(this, Log.DEBUG, "delete all msg");
            while(null != (msg = extReceiver.receive("extSubscriberCommand")))              
            {
                if(!acknowledge(msg))
                    return FilterManagerStart.STAT_ERR_JMSCON_EXT;
            }
            
            try
            {
                MapMessage mapMsg = amsSenderSession.createMapMessage();
                mapMsg.setString(MSGPROP_COMMAND, MSGVALUE_TCMD_RELOAD_CFG_START);
                amsPublisherDist.send(mapMsg);
                boolean bRet = FlagDAO.bUpdateFlag(conDb, FLG_RPL, FLAGVALUE_SYNCH_FMR_RPL, FLAGVALUE_SYNCH_FMR_TO_DIST_SENDED);
                if (bRet)
                {
                    iCmd = CMD_RPL_WAITFOR_DIST;
                }
                else
                {
                    Log.log(this, Log.FATAL, "update not successful, could not update db flag to " + FLAGVALUE_SYNCH_FMR_TO_DIST_SENDED);
                    return FilterManagerStart.STAT_ERR_FLG_RPL;                 // force new initialization, no recover() needed
                }
            }// try
            catch (JMSException e)
            {
                Log.log(this, Log.FATAL, "could not send command RELOAD_CFG_START to amsPublisherDist", e);
                return FilterManagerStart.STAT_ERR_JMSCON_INT;
            }
        }// try
        catch (SQLException e)
        {
            Log.log(this, Log.FATAL, "could not bUpdateFlag", e);
            return FilterManagerStart.STAT_ERR_APPLICATION_DB;
        }
        return FilterManagerStart.STAT_OK;
    }
    
    private int waitForDist() throws Exception
    {
        try
        {
            boolean deleteAll = false;

            Log.log(this, Log.DEBUG, "Waiting for Distributor");
            while(!deleteAll)
            {
                Message message = null;
                    
                while(null != (message = amsReceiver.receive("amsSubscriberCommand")))     // receiveNoWait has a bug with acknowledging in openjms 3
                {
                    if(!deleteAll)
                    {
                        if (!(message instanceof MapMessage))
                            Log.log(this, Log.WARN, "got unknown message " + message);
                        else
                        {
                            MapMessage mMsg = (MapMessage)message;
                            String val = mMsg.getString(MSGPROP_TCMD_COMMAND);
                            if (val != null && val.equals(MSGVALUE_TCMD_RELOAD_CFG_END))
                            {
                                Utils.logMessage("incoming message: ", mMsg);   // only log relevant msg
                                Log.log(this, Log.DEBUG, "got notify from dist");
                                iCmd = CMD_RPL_END;                             // do initialize application db
                                logHistoryRplStart(conDb, false);
                                deleteAll = true;
                            }
                            else
                                Log.log(this, Log.DEBUG, "got unknown (maybe reload_cfg_start) command " + val);
                        }
                    }
                    if (!acknowledge(message))                                  // deletes all received messages of the session
                        return FilterManagerStart.STAT_ERR_JMSCON_INT;
                }// while
                
                if (deleteAll)
                    break;                                                      // end while
//                Log.log(this, Log.DEBUG, "waitForDist: sleep(100)");
                sleep(100);
            }// while (true)
        }// try
        catch (JMSException e)
        {
            Log.log(this, Log.FATAL, "could not receive command RELOAD_CFG_END from amsSubscriberCommand", e);
            return FilterManagerStart.STAT_ERR_JMSCON_INT;
        }
        return FilterManagerStart.STAT_OK;
    }
    
    private boolean publishToDistributor(MapMessage map, int iFilterID) throws JMSException
    {
        MapMessage msg = Utils.cloneMessage(map, amsSenderSession);
        msg.setString(MSGPROP_FILTERID, ""+iFilterID);
        amsPublisherDist.send(msg);
        Log.log(this, Log.INFO, "alarm sent to distributor topic with filterid = " + iFilterID);
        Log.log(Log.INFO, "FilterManagerWork.publishToDistributor() ReceiverAddr="+msg.getString(AmsConstants.MSGPROP_RECEIVERADDR));
        return true;
    }
    
    private boolean acknowledge(Message msg)
    {
        try
        {
            msg.acknowledge();
            return true;
        }
        catch(Exception e)
        {
            Log.log(this, Log.FATAL, "could not acknowledge", e);
        }
        return false;
    }
    
    private int workOnMsg(Message message) throws Exception
    {
        if (!(message instanceof MapMessage))
            Log.log(this, Log.WARN, "got unknown message " + message);
        else
        {
            MapMessage msg = (MapMessage) message;
            
            Utils.logMessage("FilterManager receives MapMessage", msg);
            boolean bSent = false;
            try
            {
                bSent = workOnMsgRam(conDb, msg);
            }
            catch (JMSException e)                                              // catch only JMSException
            {
                Log.log(this, Log.FATAL, "could not publishToDistributor", e);
                return FilterManagerStart.STAT_ERR_JMSCON_INT_SEND;
            }
            
            if (!bSent)
                Log.log(this, Log.INFO, "alarm not sent (delete only if no active time based condition)");
        }
        
        if (acknowledge(message))                                               // deletes all received messages of the session
            return FilterManagerStart.STAT_OK;
        return FilterManagerStart.STAT_ERR_JMSCON_EXT;
    }
    
    private boolean workOnMsgRam(java.sql.Connection conDb, MapMessage msg) throws Exception
    {
        boolean sent = false;
        boolean match = false;
        AggrFilterTObject aggrFilter = null;
        
        AggrFilterConditionTObject aggrFilterCondition = null;
        Iterator<?> iter = aggrFilterList.iterator();
        while (iter.hasNext())
        {
            aggrFilter = (AggrFilterTObject)iter.next();
            
            Iterator<?> iterFC = aggrFilter.getFilterConditions().iterator();
            match = false;
            
            while (iterFC.hasNext())
            {
                aggrFilterCondition = (AggrFilterConditionTObject)iterFC.next();
                IFilterCondition iFc = null;
                Integer key = new Integer(aggrFilterCondition.getFilterConditionID());
                Object obj = hmFConditions.get(key);
                
                if (obj != null)
                    iFc = (IFilterCondition)obj;
                
                if (iFc == null)
                {
                    String className = (String)hmAggrFCTList.get(new Integer(aggrFilterCondition.getFilterConditionTypeRef()));
                    if (className == null)                                      //@ERR_CONFIG
                        break;
                    
                    Object newObj = Class.forName(className).newInstance();
                    if (!(newObj instanceof IFilterCondition))
                        break;                                                  //@ERR_WARN
                    
                    iFc = (IFilterCondition)newObj;
                    iFc.init(conDb, aggrFilterCondition.getFilterConditionID(), aggrFilter.getFilterId());
                    
                    hmFConditions.put(key, iFc);                                // only if not in list checked by HashMap
                }
                
                match = iFc.match(msg);
                if (!match)                                                     // all have to match
                    break;
            }                                                                   // while (iterFC.hasNext())
            
            if (match)
                sent = publishToDistributor(msg, aggrFilter.getFilterId()) || sent; //send in each case
            
        }                                                                       // while (iter.hasNext())
        return sent;
    }
    
    private void moveTimeBasedFilterConditionsToEnd(List<AggrFilterTObject> aggrFilterList) throws Exception
    {
        if(aggrFilterList == null)
            return;
        
        Iterator<?> iter = aggrFilterList.iterator();
        while (iter.hasNext())
        {
            AggrFilterTObject aggrFilter = (AggrFilterTObject)iter.next();
            
            List<AggrFilterConditionTObject> fcList = aggrFilter.getFilterConditions();
            
            int stopIdx = fcList.size();
            int idx = 0;
            
            while(idx < stopIdx)
            {
                //timebased filter condition? yes->move to end
                if(fcList.get(idx).getFilterConditionTypeRef() == FILTERCONDITIONTYPEID_TIMEBASED)
                {
                    fcList.add(fcList.remove(idx));
                    stopIdx--;
                }
                else
                    idx++;
            }
        }
    }
    
    private int workOnTimeBasedActive(Session session, MessageProducer publisher) throws Exception
    {
        try
        {
            Iterator<FilterConditionTimeBasedItemsTObject> iter = 
                FilterConditionTimeBasedItemsDAO.selectTimeOutOrConfirmedForAlarm(conDb).iterator();
        
            while(iter.hasNext())
            {
                FilterConditionTimeBasedItemsTObject item = iter.next();
                short newState = -1;                                            // failed
                
                Log.log(this, Log.INFO, "Found item=" + item.getItemID() + "  State=" + item.getState());
                
                switch(item.getState())
                {
                    case AmsConstants.STATE_WAITING:
                        // HistoryLog Timeout TimeBased-Filter
                        HistoryTObject history = new HistoryTObject();
                        history.setTimeNew(new Date(System.currentTimeMillis()));
                        history.setType("TimeBased");
                        history.setDescription("Timeout for Msg " + item.getMessageRef()
                                + " (FC=" + item.getFilterConditionRef()
                                + "/F=" + item.getFilterRef() + ")");
                        HistoryDAO.insert(conDb, history);
                        
                        if(item.getTimeOutAction() == AmsConstants.TIMEBEHAVIOR_TIMEOUT_THEN_ALARM)
                            newState = AmsConstants.STATE_TIMEOUT_ALARM;
                        else
                            newState = AmsConstants.STATE_TIMEOUT;
                        break;
                    case AmsConstants.STATE_CONFIRMED:                          // for safety (should never executed, the DAO Class made it)
                        if(item.getTimeOutAction() != AmsConstants.TIMEBEHAVIOR_CONFIRMED_THEN_ALARM)
                            continue;
                        newState = AmsConstants.STATE_CONFIRMED_ALARM;
                        break;
                }
                
                if(newState == AmsConstants.STATE_CONFIRMED_ALARM || newState == STATE_TIMEOUT_ALARM)
                {
                    try
                    {
                        MapMessage mapMsg = session.createMapMessage();
                        MessageDAO.select(conDb, item.getMessageRef(), mapMsg);
                        
                        publishToDistributor(mapMsg, item.getFilterRef());
//                      mapMsg.setString(AmsConstants.MSGPROP_FILTERID, ""+ item.getFilterRef());
//                      publisher.send(mapMsg);
//                      Log.log(this, Log.INFO, "alarm sent to distributor topic with filterid = " + item.getFilterRef());
                    }
                    catch (JMSException e)
                    {
                        Log.log(this, Log.FATAL, "could not publishToDistributor", e);
                        return FilterManagerStart.STAT_ERR_JMSCON_INT;
                    }
                }
                FilterConditionTimeBasedItemsDAO.updateState(conDb, item.getItemID(), item.getState(), newState);
            }
            return FilterManagerStart.STAT_OK;
        }
        catch(SQLException ex)
        {
            Log.log(this, Log.FATAL, "could not workOnTimeBasedActive", ex);
        }
        return FilterManagerStart.STAT_ERR_APPLICATION_DB;
    }
}
