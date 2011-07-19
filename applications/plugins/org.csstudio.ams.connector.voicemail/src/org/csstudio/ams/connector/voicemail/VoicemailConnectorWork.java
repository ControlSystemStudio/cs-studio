
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Hashtable;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
// import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.csstudio.ams.AmsActivator;
import org.csstudio.ams.AmsConstants;
import org.csstudio.ams.Log;
import org.csstudio.ams.connector.voicemail.internal.VoicemailConnectorPreferenceKey;
import org.csstudio.ams.connector.voicemail.isdn.CallCenter;
import org.csstudio.ams.connector.voicemail.isdn.CallCenterException;
import org.csstudio.platform.utility.jms.JmsRedundantReceiver;
import org.eclipse.jface.preference.IPreferenceStore;

public class VoicemailConnectorWork extends Thread implements AmsConstants {
    
    private VoicemailConnectorStart application = null;

    // --- Sender ---
    private Context amsSenderContext = null;
    private ConnectionFactory amsSenderFactory = null;
    private Connection amsSenderConnection = null;
    private Session amsSenderSession = null;

    private MessageProducer amsPublisherReply = null;

    // CHANGED BY: Markus Moeller, 06.11.2007
    // private TopicSubscriber amsSubscriberVm = null;
    // private MessageConsumer amsSubscriberVm = null;
    private JmsRedundantReceiver amsReceiver = null; 
    
    private CallCenter callCenter = null;
    
    private Socket server = null; 
    private DataInputStream inStream = null; 
    private DataOutputStream outStream = null; 
    private int telegramCnt = 0;
    
    private Fifo fifo = new Fifo();
    
    private short sTest = 0; // 0 - normal behavior, other - for test

    private boolean bStop = false;
    private boolean bStoppedClean = false;

    public VoicemailConnectorWork(VoicemailConnectorStart vmcs) {
        this.application = vmcs;
    }
    
    /**
     * Sets the boolean variable that controlls the main loop to true
     */
    public synchronized void stopWorking() {
        bStop = true;
    }
    
    /**
     * Returns the shutdown state.
     * 
     * @return True, if the shutdown have occured clean otherwise false
     */
    public boolean stoppedClean() {
        return bStoppedClean;
    }

    @Override
    public void run() {
        
        boolean bInitedVmService = false;
        boolean bInitedJms = false;
        int iErr = VoicemailConnectorStart.STAT_OK;
        int sleeptime = 100;

        Log.log(this, Log.INFO, "Starting voicemail connector work");

        bStop = false;
        
        while(bStop == false) {
            
            try {
                if (!bInitedVmService) {
                    
                    bInitedVmService = initCallCenter();
                    // bInitedVmService = initVmService();
                    if (!bInitedVmService) {
                        
                        iErr = VoicemailConnectorStart.STAT_ERR_VM_SERVICE;
                        
                        // set it for not overwriting with next error
                        application.setStatus(iErr);
                    }
                }

                if (!bInitedJms) {
                    
                    bInitedJms = initJms();
                    if (!bInitedJms) {
                        iErr = VoicemailConnectorStart.STAT_ERR_JMSCON;
                        
                        // set it for not overwriting with next error
                        application.setStatus(iErr);
                    }
                }

                sleep(sleeptime);

                if (bInitedVmService && bInitedJms)
                {
                    iErr = VoicemailConnectorStart.STAT_OK;
                    if (application.getStatus() == VoicemailConnectorStart.STAT_INIT)
                        application.setStatus(VoicemailConnectorStart.STAT_OK);

                    // Log.log(this, Log.DEBUG, "runs");
                    
                    Message message = null;
                    try
                    {
                        message = amsReceiver.receive("amsSubscriberVm");                        
                    }
                    catch(Exception e)
                    {
                        Log.log(this, Log.FATAL, "could not receive from internal jms", e);
                        iErr = VoicemailConnectorStart.STAT_ERR_JMSCON;
                    }
                    
                    /*
                    if (message != null)
                    {
                        iErr = sendVmMsg(message);
                    }
                    */
                    
                    //TODO: TEST
                    if (message != null)
                    {
                        Log.log(this, Log.DEBUG, "Message received: " + message.toString());
                        iErr = sendIsdnMsg(message);
                        Log.log(this, Log.DEBUG, "Message sent: " + iErr);
                    }
                    
                    /*
                    if (iErr != VoicemailConnectorStart.STAT_ERR_JMSCON)
                    {
                        while(!fifo.empty())
                        {
                            Telegram tel = (Telegram)fifo.fetch();
                            
                            // send it to background
                            iErr = readVmMsg(tel);
                            if (iErr != VoicemailConnectorStart.STAT_OK)
                            {
                                fifo.pushfront(tel);
                                break;
                            }
                        }
                    }
                    
                    if (iErr == VoicemailConnectorStart.STAT_OK)
                    {
                        // read max. limit vm, other in the next run
                        iErr = readVmMsg(null);
                    }
                    */
                    
                    //TODO: TEST
                    /*
                    if (iErr == VoicemailConnectorStart.STAT_OK)
                    {
                        // read max. limit vm, other in the next run
                        iErr = readIsdnMsg(null);
                    }
                    */

                    if (iErr == VoicemailConnectorStart.STAT_ERR_VM_SERVICE_SEND)
                    {
                        closeVmService();
                        bInitedVmService = false;
                        closeJms();
                        bInitedJms = false;
                    }
                    
                    if (iErr == VoicemailConnectorStart.STAT_ERR_VM_SERVICE)
                    {
                        closeVmService();
                        bInitedVmService = false;
                    }
                    
                    if (iErr == VoicemailConnectorStart.STAT_ERR_JMSCON
                    || iErr == VoicemailConnectorStart.STAT_ERR_VM_SERVICE_BADRSP)
                    {
                        // recover = reopen
                        closeJms();
                        bInitedJms = false;
                    }
                }

                if (iErr == VoicemailConnectorStart.STAT_ERR_VM_SERVICE_BADRSP)
                {
                    sleeptime = 15000;
                }
                else
                {
                    // server is busy
                    sleeptime = 100;
                }
                
                // set status in every loop
                // set error status, can be OK if no error
                application.setStatus(iErr);
            }
            catch (Exception e)
            {
                application.setStatus(VoicemailConnectorStart.STAT_ERR_UNKNOWN);
                Log.log(this, Log.FATAL, e);

                // Disconnect - Don't forget to disconnect!
                closeVmService();
                bInitedVmService = false;
                closeJms();
                bInitedJms = false;
            }
        }
        
        closeJms();
        closeVmService();
        bStoppedClean = true;
        
        Log.log(this, Log.INFO, "Voicemail connector exited");
    }

    private boolean initCallCenter()
    {
        boolean result = false;
        
        try
        {
            callCenter = new CallCenter();
            result = true;
        }
        catch(CallCenterException cce)
        {
            result = false;
        }
        
        return result;
    }
    
    /**
     * Init Voicemail-service
     * 
     * @return <code>true</code> if all o.k.,
     *   and <code>false</code> if Modem initialization failed.
     */
    private boolean initVmService()
    {
        try
        {
            if (sTest != 0)
            {
                Log.log(this, Log.FATAL, " --- RUNNING IN TEST MODE " + sTest + " --- ");
                return true;
            }
    
            telegramCnt = 0;

            IPreferenceStore store = VoicemailConnectorPlugin.getDefault().getPreferenceStore();
            String strAdress = store.getString(VoicemailConnectorPreferenceKey.P_VM_SERVICE);
            String strPort = store.getString(VoicemailConnectorPreferenceKey.P_VM_PORT);
            
            for (int i = 1 ; i <= 3 ; i++)
            {
                try
                {
                    Log.log(this, Log.INFO, "start initVmService("+strAdress+", " + strPort +" ) try=" + i);

                    server = new Socket( strAdress, Integer.parseInt(strPort)); 
                    inStream  = new DataInputStream(server.getInputStream()); 
                    outStream = new DataOutputStream(server.getOutputStream()); 
    
                    return true;
                }
                catch (Exception e)
                {
                    Log.log(this, Log.WARN, "VmService initialization failed. try=" + i, e);
                }

                sleep(5000);
            }
        }
        catch (Exception e)
        {
            Log.log(this, Log.FATAL, "could not init VmService", e);
        }

        return false;
    }
    
    public void closeVmService()
    {
        if (inStream != null)
        {
            try
            {
                inStream.close(); 
                Log.log(this, Log.INFO, "VmService inStream communication closed.");
            }
            catch (Exception e)
            {
                Log.log(this, Log.WARN, e);
            }
        }
        inStream = null;
        
        if (outStream != null)
        {
            try
            {
                outStream.close(); 
                Log.log(this, Log.INFO, "VmService outStream communication closed.");
            }
            catch (Exception e)
            {
                Log.log(this, Log.WARN, e);
            }
        }
        outStream = null;
        
        if (server != null)
        {
            try
            {
                server.close(); 
                Log.log(this, Log.INFO, "VmService communication closed.");
            }
            catch (Exception e)
            {
                Log.log(this, Log.WARN, e);
            }
        }
        server = null;
    }
    
    private boolean initJms()
    {
        IPreferenceStore storeAct = AmsActivator.getDefault().getPreferenceStore();
        Hashtable<String, String> properties = null;
        boolean result = false;
        
        boolean durable = Boolean.parseBoolean(storeAct.getString(org.csstudio.ams.internal.AmsPreferenceKey.P_JMS_AMS_CREATE_DURABLE));

        
        try
        {
            storeAct = AmsActivator.getDefault().getPreferenceStore();
            properties = new Hashtable<String, String>();
            properties.put(Context.INITIAL_CONTEXT_FACTORY, 
                    storeAct.getString(org.csstudio.ams.internal.AmsPreferenceKey.P_JMS_AMS_CONNECTION_FACTORY_CLASS));
            properties.put(Context.PROVIDER_URL, 
                    storeAct.getString(org.csstudio.ams.internal.AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_1));
            amsSenderContext = new InitialContext(properties);
            
            amsSenderFactory = (ConnectionFactory) amsSenderContext.lookup(
                    storeAct.getString(org.csstudio.ams.internal.AmsPreferenceKey.P_JMS_AMS_CONNECTION_FACTORY));
            amsSenderConnection = amsSenderFactory.createConnection();
            
            // ADDED BY: Markus Moeller, 25.05.2007
            amsSenderConnection.setClientID("VoicemailConnectorWorkSenderInternal");
            
            amsSenderSession = amsSenderConnection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            
            // CHANGED BY: Markus Moeller, 25.05.2007
            /*
            amsPublisherReply = amsSession.createProducer((Topic)amsContext.lookup(
                    storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_AMS_TOPIC_REPLY)));
            */
            
            amsPublisherReply = amsSenderSession.createProducer(amsSenderSession.createTopic(
                    storeAct.getString(org.csstudio.ams.internal.AmsPreferenceKey.P_JMS_AMS_TOPIC_REPLY)));
            if (amsPublisherReply == null)
            {
                Log.log(this, Log.FATAL, "could not create amsPublisherReply");
                return false;
            }

            amsSenderConnection.start();

            // CHANGED BY: Markus Moeller, 25.05.2007
            /*
            amsSubscriberVm = amsSession.createDurableSubscriber((Topic)amsContext.lookup(
                    storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_AMS_TOPIC_VOICEMAIL_CONNECTOR)),
                    storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_AMS_TSUB_VOICEMAIL_CONNECTOR));
            */
            
            // CHANGED BY: Markus Moeller, 28.06.2007
            /*
            amsSubscriberVm = amsSession.createDurableSubscriber(amsSession.createTopic(
                    storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_AMS_TOPIC_VOICEMAIL_CONNECTOR)),
                    storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_AMS_TSUB_VOICEMAIL_CONNECTOR));
            
            
            amsSubscriberVm = amsSession.createConsumer(amsSession.createTopic(
                    storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_AMS_TOPIC_VOICEMAIL_CONNECTOR)));

            if (amsSubscriberVm == null)
            {
                Log.log(this, Log.FATAL, "could not create amsSubscriberVm");
                return false;
            }
            */
            amsReceiver = new JmsRedundantReceiver("VoicemailConnectorWorkReceiverInternal", storeAct.getString(org.csstudio.ams.internal.AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_1), storeAct.getString(org.csstudio.ams.internal.AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_2));
            result = amsReceiver.createRedundantSubscriber(
                    "amsSubscriberVm",
                    storeAct.getString(org.csstudio.ams.internal.AmsPreferenceKey.P_JMS_AMS_TOPIC_VOICEMAIL_CONNECTOR),
                    storeAct.getString(org.csstudio.ams.internal.AmsPreferenceKey.P_JMS_AMS_TSUB_VOICEMAIL_CONNECTOR),
                    durable);
            if(result == false)
            {
                Log.log(this, Log.FATAL, "could not create amsSubscriberVm");
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

    public void closeJms()
    {
        Log.log(this, Log.INFO, "exiting internal jms communication");
        
        if(amsReceiver != null)
        {
            amsReceiver.closeAll();
        }
        
        // if (amsSubscriberVm != null){try{amsSubscriberVm.close();amsSubscriberVm=null;}
        // catch (JMSException e){Log.log(this, Log.WARN, e);}}
        
        if (amsPublisherReply != null){try{amsPublisherReply.close();amsPublisherReply=null;}
        catch (JMSException e){Log.log(this, Log.WARN, e);}}    
        if (amsSenderSession != null){try{amsSenderSession.close();amsSenderSession=null;}
        catch (JMSException e){Log.log(this, Log.WARN, e);}}
        if (amsSenderConnection != null){try{amsSenderConnection.stop();}
        catch (JMSException e){Log.log(this, Log.WARN, e);}}
        if (amsSenderConnection != null){try{amsSenderConnection.close();amsSenderConnection=null;}
        catch (JMSException e){Log.log(this, Log.WARN, e);}}
        if (amsSenderContext != null){try{amsSenderContext.close();amsSenderContext=null;}
        catch (NamingException e){Log.log(this, Log.WARN, e);}}

        Log.log(this, Log.INFO, "jms internal communication closed");
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
    
    public int sendIsdnMsg(Message message) throws Exception
    {
        int result = VoicemailConnectorStart.STAT_OK;
        
        if (!(message instanceof MapMessage))
        {
            Log.log(this, Log.WARN, "got unknown message " + message);
            if(!acknowledge(message))
            {
                return VoicemailConnectorStart.STAT_ERR_JMSCON;
            }
            
            return VoicemailConnectorStart.STAT_OK;
        }

        MapMessage msg = (MapMessage) message;
        String text = msg.getString(MSGPROP_RECEIVERTEXT);
        String recNo = msg.getString(MSGPROP_RECEIVERADDR);
        String chainIdAndPos = msg.getString(MSGPROP_MESSAGECHAINID_AND_POS);
        String textType = msg.getString(MSGPROP_TEXTTYPE);
        String waitUntil = msg.getString(MSGPROP_GROUP_WAIT_TIME);
        
        try
        {
            callCenter.makeCall(recNo, text, textType, chainIdAndPos, waitUntil);
        }
        catch(CallCenterException cce)
        {
            // TODO: What happens if we got an error?
            Log.log(this, Log.ERROR, "Cannot make the call: " + cce.getMessage());
            
            result = VoicemailConnectorStart.STAT_ERR_VM_SERVICE_SEND;
        }
        
        if(!acknowledge(message))
        {
            result = VoicemailConnectorStart.STAT_ERR_JMSCON;
        }

        return result;
    }
    
    private int sendVmMsg(Message message) throws Exception
    {
        if (!(message instanceof MapMessage))
        {
            Log.log(this, Log.WARN, "got unknown message " + message);
            if (!acknowledge(message))                                          // deletes all received messages of the session
                return VoicemailConnectorStart.STAT_ERR_JMSCON;
            return VoicemailConnectorStart.STAT_OK;
        }
        else
        {
            MapMessage msg = (MapMessage) message;
            String text = msg.getString(MSGPROP_RECEIVERTEXT);
            String recNo = msg.getString(MSGPROP_RECEIVERADDR);
            String chainIdAndPos = msg.getString(MSGPROP_MESSAGECHAINID_AND_POS);
            String textType = msg.getString(MSGPROP_TEXTTYPE);
            String parsedRecNo = null;

            int iErr = VoicemailConnectorStart.STAT_ERR_UNKNOWN;
            for (int j = 1 ; j <= 5 ; j++)                                      //only for short net breaks
            {
                if (parsedRecNo == null)
                {
                    try
                    {
                        parsedRecNo = parsePhoneNumber(recNo);
                    }
                    catch (Exception e)
                    {
                        Log.log(this, Log.FATAL, "Parsing phone number - failed.");
                        if (acknowledge(message))                               // deletes all received messages of the session
                            return VoicemailConnectorStart.STAT_OK;
                        iErr = VoicemailConnectorStart.STAT_ERR_JMSCON;
                    }
                }
                if (parsedRecNo != null)
                {
                    int iChainIdAndPos = 0;
                    try{
                        if (chainIdAndPos != null && chainIdAndPos.length() > 0)
                            iChainIdAndPos = Integer.parseInt(chainIdAndPos);
                    }catch (Exception e){
                        Log.log(this, Log.WARN, "could not parse chainIdAndPos(take 0): " + chainIdAndPos);
                    }
                    int itextType = 0;
                    try{
                        itextType = Integer.parseInt(textType);
                    }catch (Exception e){
                        Log.log(this, Log.WARN, "could not parse textType(take 0): " + textType);
                    }

                    int iSend = sendVmSocket(text, parsedRecNo, iChainIdAndPos, itextType);
                    if (iSend == 1)
                    {
                        if (acknowledge(message))                               // deletes all received messages of the session
                            return VoicemailConnectorStart.STAT_OK;
    
                        iErr = VoicemailConnectorStart.STAT_ERR_JMSCON;
                    }
                    else if (iSend == 0)
                    {
                        iErr = VoicemailConnectorStart.STAT_ERR_VM_SERVICE_SEND;
                    }
                    else if (iSend == 2)
                    {
                        iErr = VoicemailConnectorStart.STAT_ERR_VM_SERVICE_BADRSP;
                    }
                }
                
                sleep(2000);
            }
            
            return iErr;
        }
    }
    
    private String parsePhoneNumber(String mobile) throws Exception
    {
        StringBuffer sbMobile = new StringBuffer(mobile);
        StringBuffer sbTest = new StringBuffer("+0123456789");
        int i = 0;

        if (sbMobile.length() > 0)                                              // first char (can be +0123456789)
        {
            if (sbTest.indexOf(String.valueOf(sbMobile.charAt(i))) < 0)         // first char found in sbTest
                sbMobile.deleteCharAt(0);                                       // if not found
            else
                i++;
            
            sbTest.deleteCharAt(0);                                             // delete '+'
        }
        
        while (i < sbMobile.length())                                           // other chars (can be 0123456789)
        {
            if (sbTest.indexOf(String.valueOf(sbMobile.charAt(i))) < 0)         // char found in sbTest
            {
                sbMobile.deleteCharAt(i);                                       // if not found
                continue;                                                       // do not i++
            }
            i++;
        }
        return sbMobile.toString();
    }
    
    /**
     * Send Vm with text to receiver address.
     * 
     * @param text      String
     * @param recNo     String
     * @return <code>true</code> if Vm was sent,
     *   and <code>false</code> if Vm was not sent.
     * @throws Exception
     */
    private int sendVmSocket(String text, String recNo, int chainIdAndPos, int textType) throws Exception
    {
        int iRet = 0; //hard err reopen socket, 1-ok, 2-not the rigth answer tr again
        Log.log(this, Log.INFO, "start sendVmSocket");

        if (sTest == 0)
        {
            Log.log(this, Log.INFO, "call voicemail service");
            
            TextToReceiver ttr = null;
            try{
                telegramCnt++;
                ttr = new TextToReceiver(telegramCnt, 
                                        text,
                                        recNo,
                                        chainIdAndPos,
                                        textType);

                byte baBytes[] = ttr.getWriteBytes();
                Log.log(this, Log.INFO, "sending telegram dump " + Telegram.toHexDump(baBytes));

                outStream.writeBytes(new String(baBytes));
            }
            catch(Exception e)
            {
                Log.log(this, Log.INFO, "could not sendMessage", e);
                return 0;                                                   //only with exceptions at this line => modem error
            }

            try{
                
                Telegram telRead = null;
                
                int i = 0;
                while (telRead == null && i < 25)
                {
                    i++;
                    try{sleep(200);}catch(Exception e){}
                    telRead = Telegram.readNextTelegram(inStream);

                    if (telRead != null && telRead instanceof TextToReceiverReceipt)
                    {
                        TextToReceiverReceipt receipt = (TextToReceiverReceipt)telRead;
                        if (receipt.getTelegramCnt() < ttr.getTelegramCnt())                    //ignore old receipts
                            telRead = null;
                    }
                    else if (telRead != null)
                    {
                        fifo.pushback(telRead);
                        telRead = null;
                    }
                }
                
                iRet = 2;//not right answer, try again
                if (telRead != null && telRead instanceof TextToReceiverReceipt)
                {
                    TextToReceiverReceipt receipt = (TextToReceiverReceipt)telRead;

                    if (receipt.getTelegramCnt() == ttr.getTelegramCnt()
                    && receipt.getStatus() == TextToReceiverReceipt.STATUS_OK)
                    {
                        iRet = 1;
                        Log.log(this, Log.INFO, "accepted TextToReceiverReceipt(act Cnt/wanted Cnt/Status(0=ok)):" 
                                + receipt.getTelegramCnt() + "/" + ttr.getTelegramCnt() + "/" + receipt.getStatus());
                    }
                    else
                    {
                        Log.log(this, Log.WARN, "inacceptable TextToReceiverReceipt(act Cnt/wanted Cnt/Status(0=ok)):" 
                                + receipt.getTelegramCnt() + "/" + ttr.getTelegramCnt() + "/" + receipt.getStatus());
                    }
                }
            }
            catch(Exception e)
            {
                Log.log(this, Log.INFO, "could not get sendMessage receipt", e);
                return 0;                                                   //only with exceptions at this line => modem error
            }
            
            Log.log(this, Log.INFO, "call voicemail service done");
        }
        else
        {
            return 1;                                                       // in Test Mode - true
        }
        
        return iRet;
    }

    private int readVmMsg(Telegram tel) throws Exception
    {
        int iErr = VoicemailConnectorStart.STAT_ERR_UNKNOWN;
        for (int j = 1 ; j <= 5 ; j++)                                          //TEMPORARAY connections error try some times 
        {                                                                       // (short breaks of ethernet or gsm net)
            iErr = readVmSocket(tel);
            if (VoicemailConnectorStart.STAT_OK == iErr)                                // only return if read successfully
                return VoicemailConnectorStart.STAT_OK;
            
            sleep(2000);
        }
        
        return iErr;
    }
    
    /**
     * Reads Vm-Messages from the modem.
     * 
     * @param limit     int - Read up to number of messages. If limit is set to 0, read all messages.
     * @return int
     * @throws Exception
     */
    private int readVmSocket(Telegram tel) throws Exception
    {
        Telegram telRead = tel;
        if (telRead == null)
            try
            {
                telRead = Telegram.readNextTelegram(inStream);
            }                                                                       // read out all messages in linked list
            catch(Exception e)
            {
                Log.log(this, Log.FATAL, "could not readMessages", e);
                return VoicemailConnectorStart.STAT_ERR_VM_SERVICE;
            }

        if (telRead != null && (telRead instanceof ReplyAlarm || telRead instanceof ChangeReceiverStatus))
        {
            MapMessage mapMsg = null;
            try
            {
                mapMsg = amsSenderSession.createMapMessage();
            }
            catch(Exception e)
            {
                Log.log(this, Log.FATAL, "could not createMapMessage", e);
            }
            if (mapMsg == null)
                return VoicemailConnectorStart.STAT_ERR_JMSCON;
                
            if (telRead instanceof ReplyAlarm)
            {
                ReplyAlarm reply = (ReplyAlarm)telRead;
                mapMsg.setString(MSGPROP_MESSAGECHAINID_AND_POS, ""+reply.getChainIdAndPos());
                mapMsg.setString(MSGPROP_CONFIRMCODE, reply.getConfirmCode());

                mapMsg.setString(MSGPROP_REPLY_TYPE, MSG_REPLY_TYPE_VOICEMAIL);
                mapMsg.setString(MSGPROP_REPLY_ADRESS, reply.getOriginator());
                    
                Log.log(this, Log.INFO, "alarm reply, start internal jms send");
            }
            else if (telRead instanceof ChangeReceiverStatus)
            {
                ChangeReceiverStatus changes = (ChangeReceiverStatus)telRead;
                mapMsg.setString(MSGPROP_CHANGESTAT_GROUPNUM, ""+changes.getGrpNo());
                mapMsg.setString(MSGPROP_CHANGESTAT_USERNUM, ""+changes.getUsrNo());
                mapMsg.setString(MSGPROP_CHANGESTAT_STATUS, ""+changes.getStatusNew());
                mapMsg.setString(MSGPROP_CHANGESTAT_STATUSCODE, changes.getStatusCode());
                mapMsg.setString(MSGPROP_CHANGESTAT_REASON, changes.getReason());
                
                mapMsg.setString(MSGPROP_REPLY_TYPE, MSG_REPLY_TYPE_VOICEMAIL);
                mapMsg.setString(MSGPROP_REPLY_ADRESS, changes.getOriginator());
                
                Log.log(this, Log.INFO, "change receiver status, start internal jms send");
            }

            try
            {
                amsPublisherReply.send(mapMsg);
            }
            catch(Exception e)
            {
                Log.log(this, Log.FATAL, "could not send to internal jms", e);
                return VoicemailConnectorStart.STAT_ERR_JMSCON;
            }
            
            Log.log(this, Log.INFO, "send internal jms message done");
        }
        else if (telRead != null)
            Log.log(this, Log.WARN, "incoming message no=(" + telRead.getTelegramCnt() + "): has unacceptable id: '" 
                    + telRead.getTelegramID() + "'");

            
        // Log.log(this, Log.DEBUG, "readReply . . . exit");
        return VoicemailConnectorStart.STAT_OK;
    }
}
