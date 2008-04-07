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

import java.util.Hashtable;
import java.util.LinkedList;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
// import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
// import javax.jms.Topic;
// import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.csstudio.ams.Activator;
import org.csstudio.ams.AmsConstants;
import org.csstudio.ams.Log;
import org.csstudio.ams.connector.sms.internal.SampleService;
import org.csstudio.platform.libs.jms.JmsRedundantReceiver;
import org.eclipse.jface.preference.IPreferenceStore;
import org.smslib.InboundMessage;
import org.smslib.MessageClasses;
import org.smslib.MessageEncodings;
import org.smslib.MessageTypes;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.StatusReportMessage;
import org.smslib.modem.SerialModemGateway;

public class SmsConnectorWork extends Thread implements AmsConstants
{
    private SmsConnectorStart   scs                 = null;

    // private final int CONSUMER_CONNECTIONS = 2;
    
    // --- Sender ---
    private Context amsSenderContext = null;
    private ConnectionFactory amsSenderFactory = null;
    private Connection amsSenderConnection = null;
    private Session amsSenderSession = null;
    
    private MessageProducer amsPublisherReply = null;

    // --- Receiver ---
    private JmsRedundantReceiver amsReceiver = null;    
    /*private Context[]           amsReceiverContext    = new Context[CONSUMER_CONNECTIONS];
    private ConnectionFactory[] amsReceiverFactory    = new ConnectionFactory[CONSUMER_CONNECTIONS];
    private Connection[]        amsReceiverConnection = new Connection[CONSUMER_CONNECTIONS];
    private Session[]           amsReceiverSession    = new Session[CONSUMER_CONNECTIONS];

    // CHANGED BY: Markus Möller, 28.06.2007
    // private TopicSubscriber     amsSubscriberSms    = null;
    private MessageConsumer[]     amsSubscriberSms    = new MessageConsumer[CONSUMER_CONNECTIONS];
    */
    private Service modemService = null;
    // private CSoftwareService srv = null;

    private short sTest = 0; // 0 - normal behavior, other - for test
    private boolean bStop = false;
    private boolean bStoppedClean = false;
    
    /** Name of the management topic for sending commands */
    public static final String MANAGE_COMMAND_TOPIC = "T_AMS_CON_MANAGE";

    /** Name of the management topic for receiving answers */
    public static final String MANAGE_REPLY_TOPIC = "T_AMS_CON_MANAGE_REPLY";
    
    public SmsConnectorWork(SmsConnectorStart scs)
    {
        // Set the "parent" object
        this.scs = scs;
    }
    
    public void run()
    {
        boolean bInitedModem = false;
        boolean bInitedJms = false;        
        bStop = false;
        bStoppedClean = false;
        int iErr = SmsConnectorStart.STAT_OK;
        
        Log.log(this, Log.INFO, "start sms connector work");

        while(bStop == false)
        {
            try
            {
                if (!bInitedModem)
                {
                    bInitedModem = initModem();
                    if(!bInitedModem)
                    {
                        Log.log(this, Log.ERROR, "Cannot init modem.");
                        
                        iErr = SmsConnectorStart.STAT_ERR_MODEM;
                        scs.setStatus(iErr);                                    // set it for not overwriting with next error
                    }
                }

                if (!bInitedJms)
                {
                    bInitedJms = initJms();
                    if (!bInitedJms)
                    {
                        iErr = SmsConnectorStart.STAT_ERR_JMSCON;
                        scs.setStatus(iErr);                                    // set it for not overwriting with next error
                    }
                }

                sleep(100);

                if(bInitedModem && bInitedJms)
                {
                    iErr = SmsConnectorStart.STAT_OK;
                    if (scs.getStatus() == SmsConnectorStart.STAT_INIT)
                        scs.setStatus(SmsConnectorStart.STAT_OK);

                    Log.log(this, Log.DEBUG, "is running");

                    // First check the connectors manage topic
                    // Maybe a user wants to delete some messages
                    Message message = null;
                    try
                    {
                        message = amsReceiver.receive("amsConnectorManager");
                    }
                    catch(Exception e)
                    {
                        Log.log(this, Log.FATAL, "could not receive from internal jms: amsConnectorManager", e);
                        iErr = SmsConnectorStart.STAT_ERR_JMSCON;
                    }
                    
                    // If we got a message from the manager plugin, start the ConnectorMessageManager
                    if(message != null)
                    {
                        Log.log(Log.INFO, "Received a message from ConnectorMessageManager");
                        
                        ConnectorMessageManager manager = null;
                        try
                        {
                            manager = new ConnectorMessageManager(amsSenderConnection, amsReceiver);
                            manager.begin(message);
                            manager.closeJms();
                            
                            iErr = SmsConnectorStart.STAT_OK;
                        }
                        catch(JMSException jmse)
                        {
                            manager.closeJms();
                            Log.log(this, Log.FATAL, "the connector message manager does not work properly", jmse);
                            iErr = SmsConnectorStart.STAT_ERR_JMSCON;
                        }
                        
                        manager = null;
                    }
                    
                    if(iErr == SmsConnectorStart.STAT_ERR_JMSCON)
                    {
                        Log.log(this, Log.ERROR, "Closing JMS communication.");
                        closeJms();
                        bInitedJms = false;
                    }

                    if(iErr == SmsConnectorStart.STAT_OK)
                    {
                        // Now look for SMS messages
                        message = null;
                        try
                        {
                            message = amsReceiver.receive("amsSubscriberSms");
                        }
                        catch(Exception e)
                        {
                            Log.log(this, Log.FATAL, "could not receive from internal jms: amsSubscriberSms", e);
                            iErr = SmsConnectorStart.STAT_ERR_JMSCON;
                        }
                        
                        if (message != null)
                        {
                            iErr = sendSmsMsg(message);                             // send 1 SMS, other in the next run
                            sleep(100);
                        }
                        
                        if (iErr == SmsConnectorStart.STAT_OK)
                        {
                            iErr = readSmsMsg(/*1*/);                                   // read max. limit SMS, other in the next run
                            sleep(50);
                        }
                        
                        if (iErr == SmsConnectorStart.STAT_ERR_MODEM_SEND)
                        {
                            Log.log(this, Log.ERROR, "Closing Modem.");
                            closeModem();
                            bInitedModem = false;
                            closeJms();                                             // recover msg
                            bInitedJms = false;
                        }
                        
                        if (iErr == SmsConnectorStart.STAT_ERR_MODEM)
                        {
                            Log.log(this, Log.ERROR, "Closing Modem.");
                            closeModem();
                            bInitedModem = false;
                        }
                        
                        if (iErr == SmsConnectorStart.STAT_ERR_JMSCON)
                        {
                            Log.log(this, Log.ERROR, "Closing JMS communication.");
                            closeJms();
                            bInitedJms = false;
                        }
                    }
                }

                // set status in every loop
                scs.setStatus(iErr);                                            // set error status, can be OK if no error
            }
            catch(Exception e)
            {
                scs.setStatus(SmsConnectorStart.STAT_ERR_UNKNOWN);
                Log.log(this, Log.FATAL, e);

                closeModem();                                                   // Disconnect - Don't forget to disconnect!
                bInitedModem = false;
                closeJms();
                bInitedJms = false;
            }
        }
        
        // Close all
        closeModem();                                                   // Disconnect - Don't forget to disconnect!
        closeJms();
        bStoppedClean = true;
        
        Log.log(this, Log.INFO, "SMS connector exited");
    }
    
    /**
     * Init Sms-Modem
     * 
     * @return <code>true</code> if all o.k.,
     *   and <code>false</code> if Modem initialization failed.
     */
    private boolean initModem()
    {
        boolean result = false;
        
        try
        {
            if (sTest != 0)
            {
                Log.log(this, Log.FATAL, " --- RUNNING IN TEST MODE " + sTest + " --- ");
                return true;
            }
    
            ////////////////////////////////////////////////////////////////////////
            // strComPort   - COM-Port: "COM1", "COM2", "COM3", ... , "/dev/ttyS1", ...
            // iBaudRate        - Modem Baud-Rate: 9600, 57600, ... 
            // strManufac   - gsmDeviceManufacturer: "SonyEricsson", "Siemens", "Wavecom", "Nokia", ..., ""
            // strModel     - gsmDeviceModel: "GS64", "M1306B", "6310i", ..., ""
            // strSimPin        - SimCard Pin-Number: "1234", ...
            ////////////////////////////////////////////////////////////////////////
            IPreferenceStore store = SmsConnectorPlugin.getDefault().getPreferenceStore();
            String strComPort = store.getString(SampleService.P_MODEM_COMPORT);
            
            int iBaudRate = 9600;
            
            try
            {
                iBaudRate = Integer.parseInt(store.getString(SampleService.P_MODEM_COMBAUDRATE));
            }
            catch (NumberFormatException e)
            {
                Log.log(this, Log.WARN, "Value for Baudrate is not a number, take min 9600.");
            }
            
            String strManufac = store.getString(SampleService.P_MODEM_MANUFACTURE);
            String strModel = store.getString(SampleService.P_MODEM_MODEL);
            String strSimPin = store.getString(SampleService.P_MODEM_SIMPIM);
            
            modemService = new Service();
            
            for (int i = 1 ; i <= 3 ; i++)
            {
                try
                {
                    Log.log(this, Log.INFO, "start initModem("+strComPort+","
                            +iBaudRate+","
                            +strManufac+","
                            +strModel+") try=" + i);
                    // modemService = new CService(strComPort, iBaudRate, strManufac, strModel);
                    // modemService = new CSoftwareService(strComPort, iBaudRate, strManufac, strModel);
    
                    SerialModemGateway modem = new SerialModemGateway("modem." + strComPort.toLowerCase(), strComPort, iBaudRate, strManufac, strModel);
                    modem.setInbound(true);
                    modem.setOutbound(true);
                    modem.setSimPin(strSimPin);
                    // modem.setOutboundNotification(outboundNotification);
                    modemService.addGateway(modem);

                    // JUST A TEST
                    /*
                    Log.log(this, Log.INFO, "start initModem(COM1,9600,SonyEricsson,[]) try=" + i); 
                    SerialModemGateway modem2 = new SerialModemGateway("modem.com1", "COM1", iBaudRate, "SonyEricsson", "");
                    modem2.setInbound(true);
                    modem2.setOutbound(true);
                    modem2.setSimPin("13046");
                    // modem.setOutboundNotification(outboundNotification);
                    modemService.addGateway(modem2);                    
                    */
                    
                    result = true;
                    
                    break;
                }
                catch (Exception e)
                {
                    Log.log(this, Log.WARN, "Modem initialization failed. try=" + i);
                }

                sleep(5000);
            }
            
            Log.log(this, Log.INFO, "Modem(s) are initialized");

            if(result == true)
            {
                Log.log(this, Log.INFO, "Try to start service");
                modemService.startService();
                Log.log(this, Log.INFO, "service started");
            }
        }
        catch (Exception e)
        {
            Log.log(this, Log.FATAL, "could not init modem", e);
        }
        
        return result;
    }
    
    public void closeModem()
    {
        if (modemService != null)
        {
            try
            {
                modemService.stopService();

                Log.log(this, Log.INFO, "Modem communication closed.");
            }
            catch (Exception e)
            {
                Log.log(this, Log.WARN, e);
            }
        }
        
        modemService = null;
    }
    
    public synchronized void stopWorking()
    {
        bStop = true;
    }
    
    public boolean stoppedClean()
    {
        return bStoppedClean;
    }
    
    private boolean initJms()
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
            
            amsSenderConnection.setClientID("SmsConnectorWorkSenderInternal");
                        
            amsSenderSession = amsSenderConnection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            
            // CHANGED BY: Markus Möller, 25.05.2007
            /*
            amsPublisherReply = amsSession.createProducer((Topic)amsContext.lookup(
                    storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_AMS_TOPIC_REPLY)));
            */
            
            amsPublisherReply = amsSenderSession.createProducer(amsSenderSession.createTopic(
                    storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_AMS_TOPIC_REPLY)));
            if (amsPublisherReply == null)
            {
                Log.log(this, Log.FATAL, "could not create amsPublisherReply");
                return false;
            }
            
            amsSenderConnection.start();

            // Create the redundant receiver
            amsReceiver = new JmsRedundantReceiver("SmsConnectorWorkReceiverInternal", storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_AMS_PROVIDER_URL_1), storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_AMS_PROVIDER_URL_2));
           
            // Create first subscriber (default topic for the connector) 
            result = amsReceiver.createRedundantSubscriber("amsSubscriberSms", storeAct.getString(org.csstudio.ams.internal.SampleService.P_JMS_AMS_TOPIC_SMS_CONNECTOR));
            if(result == false)
            {
                Log.log(this, Log.FATAL, "could not create amsSubscriberSms");
                return false;
            }
            
            // Create second subscriber (topic for message management)
            // TODO: Replace constant with preference entry 
            result = amsReceiver.createRedundantSubscriber("amsConnectorManager", MANAGE_COMMAND_TOPIC);
            if(result == false)
            {
                Log.log(this, Log.FATAL, "could not create amsConnectorManager");
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
    
    private int sendSmsMsg(Message message) throws Exception
    {
        if (!(message instanceof MapMessage))
        {
            Log.log(this, Log.WARN, "got unknown message " + message);
            if (!acknowledge(message))                                          // deletes all received messages of the session
                return SmsConnectorStart.STAT_ERR_JMSCON;
            return SmsConnectorStart.STAT_OK;
        }
        else
        {
            MapMessage msg = (MapMessage) message;
            String text = msg.getString(MSGPROP_RECEIVERTEXT);
            String recNo = msg.getString(MSGPROP_RECEIVERADDR);
            String parsedRecNo = null;

            int iErr = SmsConnectorStart.STAT_ERR_UNKNOWN;
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
                            return SmsConnectorStart.STAT_OK;
                        iErr = SmsConnectorStart.STAT_ERR_JMSCON;
                    }
                }
                if (parsedRecNo != null)
                {
                    if (sendSms(text, parsedRecNo))
                    {
                        if (acknowledge(message))                               // deletes all received messages of the session
                            return SmsConnectorStart.STAT_OK;
    
                        iErr = SmsConnectorStart.STAT_ERR_JMSCON;
                    }
                    else
                    {
                        iErr = SmsConnectorStart.STAT_ERR_MODEM_SEND;
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
     * Send SMS with text to receiver address.
     * 
     * @param text      String
     * @param recNo     String
     * @return <code>true</code> if SMS was sent,
     *   and <code>false</code> if SMS was not sent.
     * @throws Exception
     */
    private boolean sendSms(String text, String recNo) throws Exception
    {
        boolean bRet = false;
        Log.log(this, Log.INFO, "start sendSms");

        scs.setStatus(SmsConnectorStart.STAT_SENDING);
        
        // Lets create a message for dispatch.
        // A message needs the recipient's number and the text. Recipient's
        // number should always be defined in international format.
        OutboundMessage msg = new OutboundMessage(recNo, text);

        // Set the message encoding.
        // We can use 7bit, 8bit and Unicode. 7bit should be enough for most
        // cases. Unicode is necessary for Far-East countries.
        msg.setEncoding(MessageEncodings.ENC7BIT);

        // Do we require a Delivery Status Report?
        msg.setStatusReport(true);                                              // Delivery Status Report

        // We can also define the validity period.
        // Validity period is always defined in hours.
        // The following statement sets the validity period to 8 hours.
        msg.setValidityPeriod(8);

        // Do we require a flash SMS? A flash SMS appears immediately on
        // recipient's phone.
        // Sometimes its called a forced SMS. Its kind of rude, so be
        // careful!
        // Keep in mind that flash messages are not supported by all
        // handsets.
        // msg.setFlashSms(true);

        // Some special applications are "listening" for messages on
        // specific ports.
        // The following statements set the Source and Destination port.
        // They should always be used in pairs!!!
        // Source and Destination ports are defined as 16bit ints in the
        // message header.
        // msg.setSourcePort(10000);
        // msg.setDestinationPort(11000);

        // Ok, finished with the message parameters, now send it!
        // If we have many messages to send, we could also construct a
        // LinkedList with many COutgoingMessage objects and pass it to
        // modemService.sendMessage().
        if (sTest == 0)
        {
            Log.log(this, Log.INFO, "call modem.sendMessage");
            int totalOutBefore = modemService.getOutboundMessageCount();// total number of outbound messages since restart
            
            // TODO: Eventuell die Liste aller Modems und ihre Zustände ausgeben
            // Log.log(this, Log.INFO, "Modem connected: " + modemService.getConnected());
            try
            {
                modemService.sendMessage(msg);
            }
            catch(Exception e)
            {
                Log.log(this, Log.ERROR, "could not sendMessage", e);
                
                return false; //only with exceptions at this line => modem error
            }
            
            int totalOutAfter = modemService.getOutboundMessageCount();// total number of outbound messages since restart
            if (totalOutBefore < totalOutAfter)
            {
                Log.log(this, Log.INFO, "sms sent to: '" + recNo + "' with text: '" + text + "'");
                bRet = true;
            }
            Log.log(this, Log.INFO, "totalOut sms = " + totalOutAfter);
        }
        else
        {
            return true; // in Test Mode - true
        }
        
        return bRet;
    }

    private int readSmsMsg(/*int limit*/) throws Exception
    {
        int iErr = SmsConnectorStart.STAT_ERR_UNKNOWN;
        for (int j = 1 ; j <= 5 ; j++)                                          //TEMPORARAY connections error try some times 
        {                                                                       // (short breaks of ethernet or gsm net)
            iErr = readModem(/*limit*/);
            if (SmsConnectorStart.STAT_OK == iErr)                              // only return if read successfully
                return SmsConnectorStart.STAT_OK;
            
            sleep(2000);
        }
        
        return iErr;
    }
    
    private boolean deleteMessage(InboundMessage msg)
    {
        try
        {
            if (sTest == 0)
                modemService.deleteMessage(msg);
            return true;
        }
        catch(Exception e)
        {
            Log.log(this, Log.FATAL, "could not deleteMessage", e);
        }
        return false;
    }
    /**
     * Reads SMS-Messages from the modem.
     * 
     * @param limit     int - Read up to number of messages. If limit is set to 0, read all messages.
     * @return int
     * @throws Exception
     */
    private int readModem(/*int limit*/) throws Exception
    {
        LinkedList<InboundMessage> msgList = new LinkedList<InboundMessage>();
        Log.log(this, Log.DEBUG, "start readModem call modem.readMessages");
        
        scs.setStatus(SmsConnectorStart.STAT_READING);
        
        try
        {
            if (sTest == 0)
                modemService.readMessages(msgList, MessageClasses.ALL /*, limit*/);// Read up to number of messages, read other SMS at the next run.
        }                                                                       // read out all messages in linked list
        catch(Exception e)
        {
            Log.log(this, Log.FATAL, "could not readMessages", e);
            return SmsConnectorStart.STAT_ERR_MODEM;
        }

        if (!msgList.isEmpty())
            Log.log(this, Log.INFO, "readModem done, message count = " + msgList.size());
        else
        {
            Log.log(this, Log.DEBUG, "readModem done, modem is empty");

            if (sTest != 0) // TODO FOR TEST ONLY
                generateTestMsg();
                
            return SmsConnectorStart.STAT_OK;
        }
        
        // Iterate and display.
        // The CMessage parent object has a toString() method which displays
        // all of its contents. Useful for debugging, but for a real world
        // application you should use the necessary getXXX methods.
        for (int i = 0; i < msgList.size(); i++)
        {
            InboundMessage smsMsg = (InboundMessage) msgList.get(i);
            String text = smsMsg.getText();

            if (smsMsg.getType() == MessageTypes.STATUSREPORT)
            {
                StatusReportMessage smsStat = (StatusReportMessage)smsMsg;
                Log.log(this, Log.INFO, "receive statusReport message: '" + text 
                        + "' originator/deliveryStatus/date/dateOrg/dateRec/smscRef = " 
                        + smsStat.getOriginator() + "/" + smsStat.getStatus().toString() + "/" 
                        + smsStat.getDate() + "/" + smsStat.getSent() + "/" + smsStat.getReceived() + "/" 
                        + smsStat.getRefNo());
                if (!deleteMessage((InboundMessage)msgList.get(i)))
                    return SmsConnectorStart.STAT_ERR_MODEM;
                continue;
            }
            
            if (smsMsg.getType() != MessageTypes.INBOUND)
            {
                Log.log(this, Log.INFO, "receive message unknown type: '" + text 
                        + "' originator/type/date/smscRef = " 
                        + smsMsg.getOriginator() + "/" + smsMsg.getType() + "/" + smsMsg.getDate() + "/" + smsMsg.getMpRefNo());
                if (!deleteMessage((InboundMessage)msgList.get(i)))
                    return SmsConnectorStart.STAT_ERR_MODEM;
                continue;
            }
            else
                Log.log(this, Log.INFO, "receive incoming message: '" + text 
                        + "' originator/date/smscRef = " 
                        + smsMsg.getOriginator() + "/" + smsMsg.getDate() + "/" + smsMsg.getMpRefNo());

            
            // Reply_Message-Format: "<ChainIdAndPos>*<ConfirmCode>"
            // Example: "12345001*123"
            // Change-Status_Message-Format: "<GroupNum>*<UserNum>*<Status>*<StatusCode>"
            // Example: "12*34*1*123"
            // Change-Status_Message-Format: "<GroupNum>*<UserNum>*<Status>*<StatusCode>*<Reason>"
            // Example: "12*34*0*123*Im ill"

            //max 3 Zahlen(Long) + 2 Text
            // zahl*zahl reply
            // zahl*zahl*zahl*text status change (! letzter ohne stern)
            // zahl*zahl*zahl*text*text status change , nur im letzten text sterne zugelassen
            String[] arr = new String[5];
            int iStarPos = -1;
            int iLastStarPos = -1;
            int iCnt = 0;
            byte bFormat = 0;                                                   //no valid format

            // ADDED BY Markus Moeller, 2007-12-05
            boolean changeGroupState = false;
            
            // ADDED BY Markus Moeller, 2007-12-05
            if(text.trim().startsWith("g*") || text.trim().startsWith("G*"))
            {
                changeGroupState = true;
                
                text = text.trim().substring(2);
            }
            
            for (iCnt = 0;iCnt < 3;iCnt++)
            {
                iStarPos = text.indexOf("*", iLastStarPos + 1);
                if (iStarPos < 0)
                {
                    if (text.length() > iLastStarPos)
                        arr[iCnt] = text.substring(iLastStarPos + 1, text.length());

                    if (iCnt == 1)
                        bFormat = 1;                                            //format reply
                    break;
                }

                arr[iCnt] = text.substring(iLastStarPos + 1, iStarPos);
                iLastStarPos = iStarPos;
            }                                                                   //              |
            if (iStarPos > 0 && text.length() > iStarPos)                       //zahl*zahl*zahl*text
            {
                bFormat = 2;                                                    //format status change

                iStarPos = text.indexOf("*", iLastStarPos + 1);     
                if (iStarPos < 0)
                {
                    arr[iCnt] = text.substring(iLastStarPos + 1, text.length()); //(! letzter ohne stern)
                }   
                else                                                            //zahl*zahl*zahl*text*text
                {
                    arr[iCnt] = text.substring(iLastStarPos + 1, iStarPos);     
                    if (iStarPos > 0 && text.length() > iStarPos)
                    {
                        arr[iCnt+1] = text.substring(iStarPos + 1, text.length());
                    }
                }
            }
            
            //number checking
            if (bFormat == 1)
            {
                try {
                    if (!arr[0].equals("#"))        //accept all
                        Long.parseLong(arr[0]);
                    Long.parseLong(handyButton2NumberMode(arr[1]));
                }
                catch (NumberFormatException e)
                {
                    Log.log(this, Log.WARN, "expected is not a number");
                    bFormat = 0;
                }
            }
            else if (bFormat == 2)
            {
                try {
                    Long.parseLong(arr[0]);
                    Long.parseLong(arr[1]);
                    Long.parseLong(arr[2]);
                }
                catch (NumberFormatException e)
                {
                    Log.log(this, Log.WARN, "expected is not a number");
                    bFormat = 0;
                }
            }
            
            if (bFormat <= 0)
            {
                Log.log(this, Log.WARN, "incoming message from: '" + smsMsg.getOriginator() + "' has unknown Format: '" + text + "'");
            }
            else
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
                    return SmsConnectorStart.STAT_ERR_JMSCON;
                
                if (bFormat == 1)
                {
                    mapMsg.setString(MSGPROP_MESSAGECHAINID_AND_POS, arr[0]);
                    mapMsg.setString(MSGPROP_CONFIRMCODE, handyButton2NumberMode(arr[1]));

                    mapMsg.setString(MSGPROP_REPLY_TYPE, MSG_REPLY_TYPE_SMS);
                    mapMsg.setString(MSGPROP_REPLY_ADRESS, smsMsg.getOriginator());

                    Log.log(this, Log.INFO, "message parsed as alarm reply, start internal jms send");
                }
                else if (bFormat == 2)
                {
                    // ADDED BY Markus Moeller, 2007-12-05
                    if(changeGroupState == true)
                    {
                        mapMsg.setString(MSGPROP_CHANGESTAT_ACTION, "group");
                    }
                    else
                    {
                        mapMsg.setString(MSGPROP_CHANGESTAT_ACTION, "user");
                    }

                    mapMsg.setString(MSGPROP_CHANGESTAT_GROUPNUM, arr[0]);
                    mapMsg.setString(MSGPROP_CHANGESTAT_USERNUM, arr[1]);
                    mapMsg.setString(MSGPROP_CHANGESTAT_STATUS, arr[2]);
                    mapMsg.setString(MSGPROP_CHANGESTAT_STATUSCODE, arr[3]);
                    if (arr[4] == null)
                        mapMsg.setString(MSGPROP_CHANGESTAT_REASON, "");
                    else
                        mapMsg.setString(MSGPROP_CHANGESTAT_REASON, arr[4]);

                    mapMsg.setString(MSGPROP_REPLY_TYPE, MSG_REPLY_TYPE_SMS);
                    mapMsg.setString(MSGPROP_REPLY_ADRESS, smsMsg.getOriginator());

                    Log.log(this, Log.INFO, "message parsed as change status, start internal jms send");
                }

                try
                {
                    amsPublisherReply.send(mapMsg);
                }
                catch(Exception e)
                {
                    Log.log(this, Log.FATAL, "could not send to internal jms", e);
                    return SmsConnectorStart.STAT_ERR_JMSCON;
                }

                Log.log(this, Log.INFO, "send internal jms message done");
            }
            
            Log.log(this, Log.DEBUG, "start delete");
            if (!deleteMessage((InboundMessage)msgList.get(i)))
                return SmsConnectorStart.STAT_ERR_MODEM;

            Log.log(this, Log.DEBUG, "delete done");
        }
        
        Log.log(this, Log.DEBUG, "readReply . . . exit");

        return SmsConnectorStart.STAT_OK;
    }

    private String handyButton2NumberMode(String str)
    {
        String ret = "";
        byte[] b = str.toLowerCase().getBytes();

        for (int i = 0; i < b.length; i++)
            switch (b[i])
            {
                default:
                    ret += (b[i] - 48); // CHANGED BY Markus Moeller, 2007-11-20
                    break;
                case '.':
                case ' ':
                    ret += "1"; 
                    break;
                case 'a':
                case 'b':
                case 'c':
                    ret += "2"; 
                    break;
                case 'd':
                case 'e':
                case 'f':
                    ret += "3"; 
                    break;
                case 'g':
                case 'h':
                case 'i':
                    ret += "4"; 
                    break;
                case 'j':
                case 'k':
                case 'l':
                    ret += "5"; 
                    break;
                case 'm':
                case 'n':
                case 'o':
                    ret += "6"; 
                    break;
                case 'p':
                case 'q':
                case 'r':
                case 's':
                    ret += "7"; 
                    break;
                case 't':
                case 'u':
                case 'v':
                    ret += "8"; 
                    break;
                case 'w':
                case 'x':
                case 'y':
                case 'z':
                    ret += "9"; 
                    break;
            }
        
        return ret;
    }
    
    private void generateTestMsg()
    {
        try
        {
            Log.log(this, Log.FATAL, " --- RUNNING IN TEST MODE " + sTest + " --- ");
    
            String smsTxt = "";
            String strOriginator = "";
            
            if (sTest == 1)
            {
                // Reply_Message-Format: "<ChainIdAndPos>*<ConfirmCode>"
                // Example: "12345001*123"
                //
                smsTxt = "1001*5678";
                strOriginator = "01736079941";
            }
            else if (sTest == 2)
            {
                // Change-Status_Message-Format: "<GroupNum>*<UserNum>*<Status>*<StatusCode>*<Reason>"
                // Example: "12*34*0*123*Im ill."   (Logout with reason)
                // or       "12*34*1*123*"          (Login without reason)
                // or       "12*34*1*123"           (Login without reason)
                //
                smsTxt = "1*1*0*1234*Ich bin krank.";
                strOriginator = "01736079941";
            }
            
            int[] arr = new int[5];                                             // should be 1 more than in "switch (iStarPos)" statement
            int iStarPos = -1;
            
            for (int x = 0 ; x < 5 ; x++)
            {
                iStarPos = smsTxt.indexOf("*", iStarPos+1);
                if (iStarPos < 0)
                {
                    iStarPos = x;// count of "*"
                    break;
                }
                
                arr[x] = iStarPos;
            }
            
            switch (iStarPos)
            {
                case 4: 
                    // Change-Status_Message-Format: "<GroupNum>*<UserNum>*<Status>*<StatusCode>*<Reason>"
                    // Example: "12*34*0*123*Im ill"
                    if ( (arr[0] > 0) 
                            && (arr[1] > arr[0]+1) 
                            && (arr[2] > arr[1]+1) 
                            && (arr[3] > arr[2]+1)                              // at least one char for each
                            && (arr[3]+1 <= smsTxt.length()) )                  // (reason can be "")
                    {
                        MapMessage mapMsg = amsSenderSession.createMapMessage();
                        mapMsg.setString(MSGPROP_CHANGESTAT_GROUPNUM, smsTxt.substring(0, arr[0]));
                        mapMsg.setString(MSGPROP_CHANGESTAT_USERNUM, smsTxt.substring(arr[0]+1, arr[1]));
                        mapMsg.setString(MSGPROP_CHANGESTAT_STATUS, smsTxt.substring(arr[1]+1, arr[2]));
                        mapMsg.setString(MSGPROP_CHANGESTAT_STATUSCODE, smsTxt.substring(arr[2]+1, arr[3]));
                        mapMsg.setString(MSGPROP_CHANGESTAT_REASON, smsTxt.substring(arr[3]+1, smsTxt.length()));// with reason
                                
                        mapMsg.setString(MSGPROP_REPLY_TYPE, MSG_REPLY_TYPE_SMS);
                        mapMsg.setString(MSGPROP_REPLY_ADRESS, strOriginator);
    
                        Log.log(this, Log.INFO, "start Change-Status");
                        amsPublisherReply.send(mapMsg);
                        Log.log(this, Log.INFO, "Change-Status done");
                        break;
                    }
                    Log.log(this, Log.WARN, "SMS-Reply from: '" + strOriginator + "' has unknown Format: '" + smsTxt + "'");
                    break;
                case 3:
                    // Change-Status_Message-Format: "<GroupNum>*<UserNum>*<Status>*<StatusCode>"
                    // Example: "12*34*1*123"
                    if ( (arr[0] > 0) 
                            && (arr[1] > arr[0]+1) 
                            && (arr[2] > arr[1]+1) 
                            && (arr[2]+1 < smsTxt.length()) )                   // at least one char for each
                    {
                        MapMessage mapMsg = amsSenderSession.createMapMessage();
                        mapMsg.setString(MSGPROP_CHANGESTAT_GROUPNUM, smsTxt.substring(0, arr[0]));
                        mapMsg.setString(MSGPROP_CHANGESTAT_USERNUM, smsTxt.substring(arr[0]+1, arr[1]));
                        mapMsg.setString(MSGPROP_CHANGESTAT_STATUS, smsTxt.substring(arr[1]+1, arr[2]));
                        mapMsg.setString(MSGPROP_CHANGESTAT_STATUSCODE, smsTxt.substring(arr[2]+1, smsTxt.length()));
                        mapMsg.setString(MSGPROP_CHANGESTAT_REASON, "");    // without a reason
                                
                        mapMsg.setString(MSGPROP_REPLY_TYPE, MSG_REPLY_TYPE_SMS);
                        mapMsg.setString(MSGPROP_REPLY_ADRESS, strOriginator);
    
                        Log.log(this, Log.INFO, "start Change-Status");
                        amsPublisherReply.send(mapMsg);
                        Log.log(this, Log.INFO, "Change-Status done");
                        break;
                    }
                    Log.log(this, Log.WARN, "SMS-Reply from: '" + strOriginator + "' has unknown Format: '" + smsTxt + "'");
                    break;
                case 1:
                    // Reply_Message-Format: "<ChainIdAndPos>*<ConfirmCode>"
                    // Example: "12345001*123"
                    if ((arr[0] > MSG_POS_LENGTH_FOR_MSGPROP)                   // at least (MSG_POS_LENGTH_FOR_MSGPROP + 1) char for ChainIdAndPos 
                            && (arr[0]+1 < smsTxt.length()))                    // at least one char for ConfirmCode
                    {
                        MapMessage mapMsg = amsSenderSession.createMapMessage();
                        mapMsg.setString(MSGPROP_MESSAGECHAINID_AND_POS, smsTxt.substring(0, arr[0]));
                        mapMsg.setString(MSGPROP_CONFIRMCODE, smsTxt.substring(arr[0]+1, smsTxt.length()));
    
                        mapMsg.setString(MSGPROP_REPLY_TYPE, MSG_REPLY_TYPE_SMS);
                        mapMsg.setString(MSGPROP_REPLY_ADRESS, strOriginator);
    
                        Log.log(this, Log.INFO, "start reply");
                        amsPublisherReply.send(mapMsg);
                        Log.log(this, Log.INFO, "reply done");
                        break;
                    }
                    Log.log(this, Log.WARN, "SMS-Reply from: '" + strOriginator + "' has unknown Format: '" + smsTxt + "'");
                    break;
                default:// -1, 0, 2, 5
                    Log.log(this, Log.WARN, "SMS-Reply from: '" + strOriginator + "' has unknown Format: '" + smsTxt + "'");
            }// switch (iStarPos)
        }// (sTest != 0)
        catch(Exception e)
        {
            Log.log(this, Log.FATAL, e);
        }
    }
   
    public void abortModem()
    {
        // String result = null;
        
        Log.log(Log.INFO, "Try to abort modem connection.");
        
        /*
        try
        {
            result = modemService.sendCustomCmd("+++");
            
            Log.log(Log.INFO, "+++ " + result.trim());
            
            result = modemService.sendCustomCmd("AT*E2RESET=?\r");
            
            Log.log(Log.INFO, "AT*E2RESET=? " + result.trim());
            
            result = modemService.sendCustomCmd("AT*E2RESET\r");
            
            Log.log(Log.INFO, "AT*E2RESET" + result.trim());
        }
        catch(Exception e)
        {
            Log.log(Log.ERROR, e);
        }
        */
        
        modemService = null;
        
        Log.log(Log.INFO, "Leaving abortModem()");
    }
}
