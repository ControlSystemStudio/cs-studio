
/* 
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron, 
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

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import org.csstudio.ams.AmsActivator;
import org.csstudio.ams.AmsConstants;
import org.csstudio.ams.Log;
import org.csstudio.ams.connector.sms.internal.SmsConnectorPreferenceKey;
import org.csstudio.ams.connector.sms.service.Environment;
import org.csstudio.ams.connector.sms.service.JmsSender;
import org.csstudio.ams.internal.AmsPreferenceKey;
import org.csstudio.platform.utility.jms.JmsRedundantReceiver;
import org.csstudio.platform.utility.jms.JmsSimpleProducer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.smslib.InboundMessage;
import org.smslib.InboundMessage.MessageClasses;
import org.smslib.Message.MessageEncodings;
import org.smslib.Message.MessageTypes;
import org.smslib.InboundBinaryMessage;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.StatusReportMessage;
import org.smslib.desy.gateway.DatabaseGateway;
import org.smslib.modem.SerialModemGateway;

public class SmsConnectorWork extends Thread implements AmsConstants {
    
    private SmsConnectorStart scs = null;

    // private final int CONSUMER_CONNECTIONS = 2;
    
    // --- Sender ---
    private JmsSimpleProducer amsPublisherReply;

    // --- Receiver ---
    private JmsRedundantReceiver amsReceiver = null;    

    private Service modemService = null;
    
    /** Container for SMS */
    private SmsContainer smsContainer;
    
    /** Reading period (in ms) for the modem */
    private long readWaitingPeriod;
    
    /** This class contains all modem ids (names) */
    private ModemInfoContainer modemInfo;
    
    /** Status information of the current modem test */
    private ModemTestStatus testStatus;
    
    private boolean bStop = false;
    private boolean bStoppedClean = false;
    
    /** Name of the management topic for sending commands */
    public static final String MANAGE_COMMAND_TOPIC = "T_AMS_CON_MANAGE";
    public static final String MANAGE_COMMAND_TOPIC_SUB = "T_AMS_TSUB_CON_MANAGE";

    /** Name of the management topic for receiving answers */
    public static final String MANAGE_REPLY_TOPIC = "T_AMS_CON_MANAGE_REPLY";
    public static final String MANAGE_REPLY_TOPIC_SUB = "T_AMS_TSUB_CON_MANAGE_REPLY";
    
    /** Text for the test SMS */
    public static final String SMS_TEST_TEXT = "[MODEMTEST{$CHECKID,$GATEWAYID}]";
    
    public SmsConnectorWork(SmsConnectorStart starter)
    {
        // Set the "parent" object
        this.scs = starter;
        smsContainer = new SmsContainer();
        readWaitingPeriod = 0;
        modemInfo = new ModemInfoContainer();
        testStatus = new ModemTestStatus();
    }
    
    @Override
    public void run() {
        
        boolean bInitedModem = false;
        boolean bInitedJms = false;        
        long lastReadingTime = 0;
        int iErr = SmsConnectorStart.STAT_OK;
        
        bStop = false;
        bStoppedClean = false;

        Log.log(this, Log.INFO, "Start sms connector work");

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

                sleep(1);

                if(bInitedModem && bInitedJms)
                {
                    iErr = SmsConnectorStart.STAT_OK;
                    if (scs.getStatus() == SmsConnectorStart.STAT_INIT)
                        scs.setStatus(SmsConnectorStart.STAT_OK);

                    Message message = null;
                    try {
                        message = amsReceiver.receive("amsSubscriberSmsModemtest");
                        iErr = smsContainer.addModemtestSms(message);
                    } catch(Exception e) {
                        Log.log(this, Log.FATAL, "Could not receive from internal jms: amsSubscriberSmsModemtest", e);
                        iErr = SmsConnectorStart.STAT_ERR_JMSCON;
                    }
                    
                    // TODO: The methods should throw an exception
                    // Now look for SMS messages
                    message = null;
                    try
                    {
                        message = amsReceiver.receive("amsSubscriberSms");
                        iErr = smsContainer.addSms(message);
                    }
                    catch(Exception e)
                    {
                        Log.log(this, Log.FATAL, "Could not receive from internal jms: amsSubscriberSms", e);
                        iErr = SmsConnectorStart.STAT_ERR_JMSCON;
                    }
                    
                    if(smsContainer.hasContent())
                    {
                        //FIXME:
                        //TODO:
                        // send 1 SMS, other in the next run
                        // iErr = sendSmsMsg(message);
                        iErr = sendSmsMsg();
                        sleep(100);
                    }
                    
                    if (iErr == SmsConnectorStart.STAT_OK)
                    {
                        if((System.currentTimeMillis() - lastReadingTime) > readWaitingPeriod)
                        {
                            // read max. limit SMS, other in the next run
                            iErr = readSmsMsg(/*1*/);
                            lastReadingTime = System.currentTimeMillis();
                        }                            
                    }
                    
                    if (iErr == SmsConnectorStart.STAT_ERR_MODEM_SEND)
                    {
                        Log.log(this, Log.ERROR, "Closing Modem.");
                        closeModem();
                        bInitedModem = false;
                        closeJms();
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

                // set status in every loop
                // set error status, can be OK if no error
                scs.setStatus(iErr);
            }
            catch(Exception e)
            {
                scs.setStatus(SmsConnectorStart.STAT_ERR_UNDEFINED);
                Log.log(this, Log.FATAL, e);

                closeModem();
                bInitedModem = false;
                closeJms();
                bInitedJms = false;
            }
        }
        
        // Store the remaining messages
        if(smsContainer.hasContent() || smsContainer.hasBadMessages())
        {
            if(smsContainer.storeContent())
            {
                Log.log(this, Log.INFO, "SMS objects have been stored.");
                
                smsContainer = null;
            }
            else
            {
                Log.log(this, Log.WARN, "SMS objects have NOT been stored.");
            }
        }

        // Close all
        closeModem();
        closeJms();
        bStoppedClean = true;
        
        Log.log(this, Log.INFO, "SMS connector exited");
    }
    
    public boolean storeRemainingMessages()
    {
        boolean success = false;
        
        if(smsContainer != null)
        {
            if(smsContainer.hasContent() || smsContainer.hasBadMessages())
            {
                success = smsContainer.storeContent();
            }
        }
        
        return success;
    }
    
    /**
     * Init Sms-Modem
     * 
     * @return <code>true</code> if all o.k.,
     *   and <code>false</code> if Modem initialization failed.
     */
    private boolean initModem()
    {
        String[] strComPort = null;
        String[] strManufac = null;
        String[] strModel = null;
        String[] strSimPin = null;
        String[] strPhoneNumber = null;
        String m = null;
        int[] iBaudRate = null;
        int modemCount = 1;
        
        boolean result = false;

        IPreferenceStore store = SmsConnectorPlugin.getDefault().getPreferenceStore();

        try
        {
            ////////////////////////////////////////////////////////////////////////
            // strComPort   - COM-Port: "COM1", "COM2", "COM3", ... , "/dev/ttyS1", ...
            // iBaudRate        - Modem Baud-Rate: 9600, 57600, ... 
            // strManufac   - gsmDeviceManufacturer: "SonyEricsson", "Siemens", "Wavecom", "Nokia", ..., ""
            // strModel     - gsmDeviceModel: "GS64", "M1306B", "6310i", ..., ""
            // strSimPin        - SimCard Pin-Number: "1234", ...
            ////////////////////////////////////////////////////////////////////////
            try
            {
                readWaitingPeriod = Long.parseLong(store.getString(SmsConnectorPreferenceKey.P_MODEM_READ_WAITING_PERIOD));
                
                Log.log(this, Log.INFO, "Waiting period for reading: " + readWaitingPeriod);
            }
            catch(NumberFormatException e)
            {
                readWaitingPeriod = 10000;
                Log.log(this, Log.WARN, "Waiting period for reading is not valid. Using default: " + readWaitingPeriod);
            }

            try
            {
                modemCount = Integer.parseInt(store.getString(SmsConnectorPreferenceKey.P_MODEM_COUNT));
                
                modemCount = (modemCount < 0) ? 0 : modemCount;
                modemCount = (modemCount > 3) ? 3 : modemCount;
                
                Log.log(this, Log.INFO, "Number of modems: " + modemCount);
            }
            catch (NumberFormatException e)
            {
                modemCount = 1;
                Log.log(this, Log.WARN, "Number of modems not defined. Using default: " + modemCount);
            }

            strComPort = new String[modemCount];
            strManufac = new String[modemCount];
            strModel = new String[modemCount];
            strSimPin = new String[modemCount];
            strPhoneNumber = new String[modemCount];
            iBaudRate = new int[modemCount];
            
            // TODO: Better error handling and value checks
            for(int i = 0;i < modemCount;i++)
            {
                strComPort[i] = store.getString(SmsConnectorPreferenceKey.P_PREFERENCE_STRING + (i + 1) + "ComPort");
                strComPort[i] = (strComPort[i] == null) ? "" : strComPort[i];
                
                try
                {
                    iBaudRate[i] = Integer.parseInt(store.getString(SmsConnectorPreferenceKey.P_PREFERENCE_STRING + (i + 1) + "ComBaudrate"));
                }
                catch (NumberFormatException e)
                {
                    iBaudRate[i] = 9600;
                    Log.log(this, Log.WARN, "Value for Baudrate is not a number, take default: " + iBaudRate[i]);
                }
                
                strManufac[i] = store.getString(SmsConnectorPreferenceKey.P_PREFERENCE_STRING + (i + 1) + "Manufacture");
                strManufac[i] = (strManufac[i] == null) ? "" : strManufac[i];
                
                strModel[i] = store.getString(SmsConnectorPreferenceKey.P_PREFERENCE_STRING + (i + 1) + "Model");
                strModel[i] = (strModel[i] == null) ? "" : strModel[i];

                strSimPin[i] = store.getString(SmsConnectorPreferenceKey.P_PREFERENCE_STRING + (i + 1) + "SimPin");
                strSimPin[i] = (strSimPin[i] == null) ? "" : strSimPin[i];

                strPhoneNumber[i] = store.getString(SmsConnectorPreferenceKey.P_PREFERENCE_STRING + (i + 1) + "Number");
                strPhoneNumber[i] = (strPhoneNumber[i] == null) ? "" : strPhoneNumber[i];
            }
            
            modemService = Service.getInstance();
            
            modemCount = 1;
            for(int i = 0;i < modemCount;i++) {
                if(strComPort[i].length() > 0) {
                    Log.log(this, Log.INFO, "start initModem(" + strComPort[i] + ","
                            + iBaudRate[i] + ","
                            + strManufac[i] + ","
                            + strModel[i] + ")");
                    // modemService = new CSoftwareService(strComPort, iBaudRate, strManufac, strModel);
                    m = "modem." + strComPort[i].toLowerCase();
                    //SerialModemGateway modem = new SerialModemGateway(m , strComPort[i], iBaudRate[i], strManufac[i], strModel[i]);
                    DatabaseGateway modem = new DatabaseGateway(m,
                                                                "jdbc:derby://localhost/gateway",
                                                                "APP",
                                                                "APP");
                    modem.setInbound(true);
                    modem.setOutbound(true);
                    // modem.setSimPin(strSimPin[i]);
                    // modem.setOutboundNotification(outboundNotification);
                    modemService.addGateway(modem);
                    modemInfo.addModemName(m, strPhoneNumber[i]);
                                        
                    sleep(2000);
                } else {
                    Log.log(this, Log.WARN, "No COM port defined for modem " + (i + 1) + ".");
                }
            }
            
            result = true;
            
            Log.log(this, Log.INFO, "Modem(s) are initialized");
            
            modemService.setGatewayStatusNotification(new GatewayStatusNotification(modemInfo.getModemNames()));
            
            if((result == true) && (modemCount > 0))
            {
                Log.log(this, Log.INFO, "Try to start service");
                modemService.startService();
                Log.log(this, Log.INFO, "service started");
            }
        }
        catch(Exception e)
        {
            Log.log(this, Log.FATAL, "could not init modem", e);
            
            JmsSender sender = new JmsSender("SmsConnectorAlarmSender", store.getString(AmsPreferenceKey.P_JMS_AMS_SENDER_PROVIDER_URL), "ALARM");
            if(sender.isConnected())
            {
                if(sender.sendMessage("alarm", "SmsConnectorWork: Cannot init modem [" + e.getMessage() + "]", "MAJOR") == false)
                {
                    Log.log(this, Log.ERROR, "Cannot send alarm message.");
                }
                else
                {
                    Log.log(this, Log.INFO, "Alarm message sent.");
                }
            }
            else
            {
                Log.log(this, Log.WARN, "Alarm message sender is NOT connected.");
            }
            
            sender.closeAll();
            sender = null;
            
            result = false;
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
    
    public synchronized void stopWorking() {
        bStop = true;
    }
    
    public boolean stoppedClean() {
        return bStoppedClean;
    }
    
    private boolean initJms() {
        
        boolean result = false;
        
        IPreferenceStore storeAct = AmsActivator.getDefault().getPreferenceStore();
        String factoryClass = storeAct.getString(AmsPreferenceKey.P_JMS_AMS_CONNECTION_FACTORY_CLASS);
        String url = storeAct.getString(AmsPreferenceKey.P_JMS_AMS_SENDER_PROVIDER_URL);
        String topic = storeAct.getString(AmsPreferenceKey.P_JMS_AMS_TOPIC_REPLY);
        
        amsPublisherReply = new JmsSimpleProducer("SmsConnectorWorkSenderInternal", url,
                                                  factoryClass, topic);
        if (amsPublisherReply == null) {
            Log.log(this, Log.FATAL, "Could not create amsPublisherReply");
            return false;
        }

        try
        {
            
            boolean durable = Boolean.parseBoolean(storeAct.getString(AmsPreferenceKey.P_JMS_AMS_CREATE_DURABLE));

            // Create the redundant receiver
            amsReceiver = new JmsRedundantReceiver("SmsConnectorWorkReceiverInternal", storeAct.getString(AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_1), storeAct.getString(AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_2));
           
            // Create first subscriber (default topic for the connector) 
            result = amsReceiver.createRedundantSubscriber(
                    "amsSubscriberSms",
                    storeAct.getString(AmsPreferenceKey.P_JMS_AMS_TOPIC_SMS_CONNECTOR),
                    storeAct.getString(AmsPreferenceKey.P_JMS_AMS_TSUB_SMS_CONNECTOR),
                    durable);
            if(result == false)
            {
                Log.log(this, Log.FATAL, "could not create amsSubscriberSms");
                return false;
            }
            
            // Create second subscriber (topic for the modem test) 
            result = amsReceiver.createRedundantSubscriber(
                    "amsSubscriberSmsModemtest",
                    storeAct.getString(AmsPreferenceKey.P_JMS_AMS_TOPIC_CONNECTOR_DEVICETEST),
                    storeAct.getString(AmsPreferenceKey.P_JMS_AMS_TSUB_SMS_CONNECTOR_DEVICETEST),
                    durable);
            if(result == false)
            {
                Log.log(this, Log.FATAL, "could not create amsSubscriberSmsModemtest");
                return false;
            }

            // Create third subscriber (topic for message management)
            // TODO: Replace constant with preference entry 
            result = amsReceiver.createRedundantSubscriber(
                    "amsConnectorManager",
                    MANAGE_COMMAND_TOPIC,
                    MANAGE_COMMAND_TOPIC_SUB,
                    durable);
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
            
            JmsSender sender = new JmsSender("SmsConnectorAlarmSender", storeAct.getString(AmsPreferenceKey.P_JMS_AMS_SENDER_PROVIDER_URL), "ALARM");
            if(sender.isConnected())
            {
                if(sender.sendMessage("alarm", "SmsConnectorWork: Cannot init internal Jms [" + e.getMessage() + "]", "MAJOR") == false)
                {
                    Log.log(this, Log.ERROR, "Cannot send alarm message.");
                }
                else
                {
                    Log.log(this, Log.INFO, "Alarm message sent.");
                }
            }
            else
            {
                Log.log(this, Log.WARN, "Alarm message sender is NOT connected.");
            }
            
            sender.closeAll();
            sender = null;
        }
        
        return false;
    }

    public void closeJms() {
        
        Log.log(this, Log.INFO, "Exiting internal JMS communication");

        if(amsReceiver != null) {
            amsReceiver.closeAll();
        }
        
        if (amsPublisherReply != null) {
            amsPublisherReply.closeAll();
        }
        
        Log.log(this, Log.INFO, "JMS internal communication closed");
    }
    
    /**
     * 
     * @param msg
     * @return
     */
    
    /**
     * Sends the SMS stored in the SmsContainer.
     * 
     * @return Error code
     */
    private int sendSmsMsg() throws Exception
    {
        Sms sms = null;
        int iErr = SmsConnectorStart.STAT_OK;
        boolean success = false;
        
        if(smsContainer.hasContent())
        {
            Log.log(Log.DEBUG, "Vor Versenden\n" + smsContainer.showContent());

            sms = smsContainer.getFirstSms();
            if(sms != null)
            {
                Log.log(this, Log.DEBUG, "getFirstSms(): SMS: " + sms.toString());

                // Check if we have a 'start modem test' message.
                //if(sms.getMessage().startsWith("MODEM_CHECK"))
                if(sms.isModemTest())
                {
                    Pattern pattern = null;
                    String r = null;
                            
                    // pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}");
                    // Get the check id from the SMS text (with curly brackets)
                    pattern = Pattern.compile("\\{[a-fA-F0-9]+\\}");
                    Matcher m = pattern.matcher(sms.getMessage());
                    if(m.find())
                    {
                        r = m.group();
                        
                        // Remove the curly brackets
                        r = r.substring(1, r.length() - 1);
                        if(testStatus.isActive() == false || testStatus.isTimeOut())
                        {
                            sendModemTestSms(r);
                        }
                        else
                        {
                            
                            Log.log(this, Log.WARN, "A modem check is still active. Ignoring the new modem check.");
                        }
                        
                        smsContainer.removeSms(sms);
                    }
                }
                else // A normal SMS to send
                {
                    if((sms.getType() == Sms.Type.OUT) && (sms.getState() != Sms.State.SENT) && (sms.getState() != Sms.State.BAD))
                    {
                        success = sendSms(sms);
                        
                        // JUST A TEST
                        if(success == false)
                        {
                            sms.setState(Sms.State.FAILED);
                        }
                        // JUST A TEST
                        
                        //if(sendSms(sms))
                        if(success)
                        {
                            smsContainer.removeSms(sms);
                        }
                        else
                        {
                            // iErr = SmsConnectorStart.STAT_ERR_MODEM_SEND;
                            
                            if(sms.getState() == Sms.State.BAD)
                            {
                                smsContainer.removeSms(sms);
                                smsContainer.addBadSms(sms);
                                Log.log(this, Log.WARN, "A SMS could not be sent. Set it to the state BAD and removed it from th list.");
                                Log.log(this, Log.WARN, sms.toString());
                                
                                IPreferenceStore store = AmsActivator.getDefault().getPreferenceStore();

                                JmsSender sender = new JmsSender("SmsConnectorAlarmSender", store.getString(AmsPreferenceKey.P_JMS_AMS_SENDER_PROVIDER_URL), "ALARM");
                                if(sender.isConnected())
                                {
                                    if(sender.sendMessage("alarm", "SmsConnectorWork: SMS set to state BAD: " + sms.toString(), "MINOR") == false)
                                    {
                                        Log.log(this, Log.ERROR, "Cannot send alarm message.");
                                    }
                                    else
                                    {
                                        Log.log(this, Log.INFO, "Alarm message sent.");
                                    }
                                }
                                else
                                {
                                    Log.log(this, Log.WARN, "Alarm message sender is NOT connected.");
                                }
                                
                                sender.closeAll();
                                sender = null;
                            }
                        }
                    }
                }
            }
        }
        
        return iErr;
    }
    
    /**
     * 
     * @param checkId
     */
    private void sendModemTestSms(String checkId)
    {
        OutboundMessage outMsg = null;
        String name = null;
        String number = null;
        String text = null;
        
        testStatus.reset();
        testStatus.setCheckId(checkId);
        
        Log.log(this, Log.INFO, "Number of modems to test: " + modemInfo.getModemCount());
        for(int i = 0;i < modemInfo.getModemCount();i++)
        {
            name = modemInfo.getModemName(i);
            if(name != null)
            {
                number = modemInfo.getPhoneNumber(name);

                text = SMS_TEST_TEXT;
                text = text.replaceAll("\\$CHECKID", testStatus.getCheckId());
                text = text.replaceAll("\\$GATEWAYID", name);
                
                outMsg = new OutboundMessage(number, text);
                outMsg.setEncoding(MessageEncodings.ENC7BIT);
                outMsg.setStatusReport(false);
                outMsg.setValidityPeriod(8);
                
                try
                {
                    Log.log(this, Log.INFO, "Sending to modem '" + name + "': " + text);
                    if(modemService.sendMessage(outMsg, name))
                    {
                        testStatus.addGatewayId(name);
                    }
                }
                catch(Exception e)
                {
                    Log.log(this, Log.WARN, "Could not send SMS test message to modem '" + name + "'.");
                    testStatus.addBadModem(name);
                }
                
                outMsg = null;
            }
        }
        
        if(testStatus.getGatewayCount() > 0)
        {
            testStatus.setActive(true);
            testStatus.setTimeOut(System.currentTimeMillis() + 120000); // 2 minutes
        }
        else
        {
            sendTestAnswer(testStatus.getCheckId(), "No modem could send the test SMS.", "MAJOR", "ERROR");
            testStatus.reset();
        }
    }
    
    private void sendTestAnswer(String checkId, String text, String severity, String value)
    {
        IPreferenceStore storeAct = org.csstudio.ams.AmsActivator.getDefault().getPreferenceStore();
        String topicName = storeAct.getString(AmsPreferenceKey.P_JMS_AMS_TOPIC_MONITOR);

        try {
            
            MapMessage mapMessage = amsPublisherReply.createMapMessage();
            mapMessage.setString("TYPE", "event");
            mapMessage.setString("EVENTTIME", amsPublisherReply.getCurrentDateAsString());
            mapMessage.setString("TEXT", text);
            mapMessage.setString("SEVERITY", severity);
            mapMessage.setString("VALUE", value);
            mapMessage.setString("CLASS", checkId);
            mapMessage.setString("HOST", Environment.getInstance().getHostName());
            mapMessage.setString("USER", Environment.getInstance().getUserName());
            mapMessage.setString("NAME", "AMS_SYSTEM_CHECK_ANSWER");
            mapMessage.setString("APPLICATION-ID", "SmsConnector");
            mapMessage.setString("DESTINATION", "AmsSystemMonitor");
            
            amsPublisherReply.sendMessage(topicName, mapMessage);
        } catch(JMSException jmse) {
            Log.log(this, Log.ERROR, "Answer message could NOT be sent.");
        }
    }
        
    @SuppressWarnings("unused")
    private int sendSmsMsg(Message message) throws Exception
    {
        if (!(message instanceof MapMessage)) {
            
            Log.log(this, Log.WARN, "got unknown message " + message);
            
            // Deletes all received messages of the session
            if (!amsReceiver.acknowledge(message))
                return SmsConnectorStart.STAT_ERR_JMSCON;
            return SmsConnectorStart.STAT_OK;
        }
        
        MapMessage msg = (MapMessage) message;
        String text = msg.getString(MSGPROP_RECEIVERTEXT);
        String recNo = msg.getString(MSGPROP_RECEIVERADDR);
        String parsedRecNo = null;

        int iErr = SmsConnectorStart.STAT_ERR_UNDEFINED;
        for (int j = 1 ; j <= 5 ; j++) //only for short net breaks
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
                    if (amsReceiver.acknowledge(message)) // deletes all received messages of the session
                        return SmsConnectorStart.STAT_OK;
                    iErr = SmsConnectorStart.STAT_ERR_JMSCON;
                }
            }
            if (parsedRecNo != null)
            {
                if (sendSms(text, parsedRecNo))
                {
                    if (amsReceiver.acknowledge(message))                               // deletes all received messages of the session
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
    
    public boolean sendSms(Sms sms) throws Exception
    {
        boolean success;
        
        success = sendSms(sms.getMessage(), sms.getPhoneNumber());
        if(success)
        {
            sms.setState(Sms.State.SENT);
        }
        else
        {
            sms.setState(Sms.State.FAILED);
        }
        
        return success;
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
        // Delivery Status Report
        
        // Changed by Markus Moeller, 2009-01-30
        // To avoid restarts of the modems
        // msg.setStatusReport(true);
        msg.setStatusReport(false);

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
        if(totalOutBefore < totalOutAfter)
        {
            Log.log(this, Log.INFO, "sms sent to: '" + recNo + "' with text: '" + text + "'");
            bRet = true;
        }
        else
        {
            // If no modems are defined, return true anyhow
            if(modemInfo.getModemCount() == 0)
            {
                bRet = true;
            }
        }
        
        Log.log(this, Log.INFO, "totalOut sms = " + totalOutAfter);
        
        return bRet;
    }

    private int readSmsMsg(/*int limit*/) throws Exception
    {
        int iErr = SmsConnectorStart.STAT_ERR_UNDEFINED;
        for (int j = 1 ; j <= 5 ; j++)                                          //TEMPORARAY connections error try some times 
        {                                                                       // (short breaks of ethernet or gsm net)
            iErr = readModem(/*limit*/);
            if (SmsConnectorStart.STAT_OK == iErr)                              // only return if read successfully
                return SmsConnectorStart.STAT_OK;
            
            sleep(5000);
        }
        
        return iErr;
    }
    
    private boolean deleteMessage(InboundMessage msg)
    {
        try
        {
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
        
        if(testStatus.isActive())
        {
            Log.log(this, Log.INFO, "Self test is active");
            
            if(testStatus.isTimeOut())
            {
                Log.log(this, Log.WARN, "Current test timed out.");
                Log.log(this, Log.DEBUG, "Remaining gateways: " + testStatus.getGatewayCount());
                Log.log(this, Log.DEBUG, "Bad gateways before moving: " + testStatus.getBadModemCount());
                testStatus.moveGatewayIdToBadModems();
                Log.log(this, Log.DEBUG, "Remaining gateways after moving: " + testStatus.getGatewayCount());
                Log.log(this, Log.DEBUG, "Bad gateways after moving: " + testStatus.getBadModemCount());
                if(testStatus.getBadModemCount() == modemInfo.getModemCount())
                {
                    Log.log(this, Log.ERROR, "No modem is working properly.");
                    this.sendTestAnswer(testStatus.getCheckId(), "No modem is working properly.", "MAJOR", "ERROR");
                }
                else
                {
                    String list = "";
                    for(String name : testStatus.getBadModems())
                    {
                        list = list + name + " ";
                    }
                    
                    Log.log(this, Log.WARN, "Modems not working properly: " + list);
                    this.sendTestAnswer(testStatus.getCheckId(), "Modems not working properly: " + list, "MINOR", "WARN");
                }
                
                Log.log(this, Log.INFO, "Reset current test.");
                testStatus.reset();
            }
        }

        try
        {
            // Read up to number of messages, read other SMS at the next run.
            // Read out all messages in linked list
            try
            {
                modemService.readMessages(msgList, MessageClasses.ALL /*, limit*/);
            }
            catch(NumberFormatException nfe)
            {
                // Sometimes we get this exception, if the PDU contains invalid characters
                Log.log(this, Log.FATAL, "[*** NumberFormatException ***]: " + nfe.getMessage());
            }
        }
        catch(Exception e)
        {
            Log.log(this, Log.FATAL, "could not readMessages", e);
            return SmsConnectorStart.STAT_ERR_MODEM;
        }

        if (!msgList.isEmpty())
        {
            Log.log(this, Log.INFO, "readModem done, message count = " + msgList.size());
        }
        else
        {
            Log.log(this, Log.DEBUG, "readModem done, modem is empty");
                
            return SmsConnectorStart.STAT_OK;
        }
                
        // Iterate and display.
        // The CMessage parent object has a toString() method which displays
        // all of its contents. Useful for debugging, but for a real world
        // application you should use the necessary getXXX methods.
        for (int i = 0; i < msgList.size(); i++)
        {
            InboundMessage smsMsg = msgList.get(i);
            String text = null;
            
            // BEWARE: We can get an InboundBinaryMessage and will run into problems
            //         if we want to get the message text. The getText() method
            //         of InboundBinaryMessage just throws an exception!!!!!!!
            if(smsMsg instanceof InboundBinaryMessage)
            {
                text = "[InboundBinaryMessage] Cannot read the content.";
            }
            else
            {
                text = smsMsg.getText();
            }
            
            if(smsMsg.getType() == MessageTypes.STATUSREPORT)
            {
                StatusReportMessage smsStat = (StatusReportMessage)smsMsg;
                Log.log(this, Log.INFO, "receive statusReport message: '" + text 
                        + "' originator/deliveryStatus/date/dateOrg/dateRec/smscRef = " 
                        + smsStat.getOriginator() + "/" + smsStat.getStatus().toString() + "/" 
                        + smsStat.getDate() + "/" + smsStat.getSent() + "/" + smsStat.getReceived() + "/" 
                        + smsStat.getRefNo());
                if (!deleteMessage(msgList.get(i)))
                    return SmsConnectorStart.STAT_ERR_MODEM;
                continue;
            }
            
            if (smsMsg.getType() != MessageTypes.INBOUND) {
                
                Log.log(this, Log.INFO, "receive message unknown type: '" + text 
                        + "' originator/type/date/smscRef = " 
                        + smsMsg.getOriginator() + "/" + smsMsg.getType() + "/" + smsMsg.getDate() + "/" + smsMsg.getMpRefNo());
                if (!deleteMessage(msgList.get(i)))
                    return SmsConnectorStart.STAT_ERR_MODEM;
                continue;
            }
            
            Log.log(this, Log.INFO, "receive incoming message: '" + text 
                        + "' originator/date/smscRef = " 
                        + smsMsg.getOriginator() + "/" + smsMsg.getDate() + "/" + smsMsg.getMpRefNo());


            // Have a look at the current check status
            if(testStatus.isActive())
            {
                if(testStatus.isTimeOut() == false)
                {
                    if(testStatus.isTestAnswer(text))
                    {
                        Log.log(this, Log.INFO, "Self test SMS");
                        Log.log(this, Log.INFO, "Gateways waiting for answer: " + testStatus.getGatewayCount());
                        testStatus.checkAndRemove(text);
                        Log.log(this, Log.INFO, "Gateways waiting for answer after remove: " + testStatus.getGatewayCount());
                        if((testStatus.getGatewayCount() == 0))
                        {
                            if(testStatus.getBadModemCount() == 0)
                            {
                                Log.log(this, Log.INFO, "All modems are working fine.");
                                this.sendTestAnswer(testStatus.getCheckId(), "All modems are working fine.", "NO_ALARM", "OK");
                            }
                            else
                            {
                                String list = "";
                                for(String name : testStatus.getBadModems())
                                {
                                    list = list + name + " ";
                                }
                                
                                Log.log(this, Log.WARN, "Modems not working properly: " + list);
                                this.sendTestAnswer(testStatus.getCheckId(), "Modems not working properly: " + list, "MINOR", "WARN");
                            }
                            
                            Log.log(this, Log.INFO, "Reset current test.");
                            testStatus.reset();
                        }
                    }
                }
            }

            if(testStatus.isTestAnswer(text))
            {
                if(!deleteMessage(msgList.get(i)))
                {
                    return SmsConnectorStart.STAT_ERR_MODEM;
                }
                
                continue;
            }
            
//            if(testStatus.isTestAnswer(text))
//            {
//                Log.log(this, Log.INFO, "Self test SMS");
//                //TODO:
//                // Handle incoming test SMS
//                if(testStatus.isActive())
//                {
//                    Log.log(this, Log.INFO, "Self test is active");
//                    
//                    if(testStatus.isTimeOut() == false)
//                    {
//                        Log.log(this, Log.INFO, "Gateways waiting for answer: " + testStatus.getGatewayCount());
//                        testStatus.checkAndRemove(text);
//                        Log.log(this, Log.INFO, "Gateways waiting for answer after remove: " + testStatus.getGatewayCount());
//                        if((testStatus.getGatewayCount() == 0))
//                        {
//                            if(testStatus.getBadModemCount() == 0)
//                            {
//                                Log.log(this, Log.INFO, "All modems are working fine.");
//                                this.sendTestAnswer(testStatus.getAnswerEventTime(), "All modems are working fine.", "NO_ALARM", "OK");
//                            }
//                            else
//                            {
//                                String list = "";
//                                for(String name : testStatus.getBadModems())
//                                {
//                                    list = list + name + " ";
//                                }
//                                
//                                Log.log(this, Log.WARN, "Modems not working properly: " + list);
//                                this.sendTestAnswer(testStatus.getAnswerEventTime(), "Modems, not working properly: " + list, "MINOR", "WARN");
//                            }
//                            
//                            Log.log(this, Log.INFO, "Reset current test.");
//                            testStatus.reset();
//                        }
//                    }
//                    else
//                    {
//                        Log.log(this, Log.WARN, "Current test timed out.");
//                        Log.log(this, Log.INFO, "Remaining gateways: " + testStatus.getGatewayCount());
//                        Log.log(this, Log.INFO, "Bad gateways before moving: " + testStatus.getBadModemCount());
//                        testStatus.moveGatewayIdToBadModems();
//                        Log.log(this, Log.INFO, "Remaining gateways after moving: " + testStatus.getGatewayCount());
//                        Log.log(this, Log.INFO, "Bad gateways after moving: " + testStatus.getBadModemCount());
//                        if(testStatus.getBadModemCount() == modemInfo.getModemCount())
//                        {
//                            Log.log(this, Log.ERROR, "No modem is working properly.");
//                            this.sendTestAnswer(testStatus.getAnswerEventTime(), "No modem is working properly.", "MAJOR", "ERROR");
//                        }
//                        else
//                        {
//                            String list = "";
//                            for(String name : testStatus.getBadModems())
//                            {
//                                list = list + name + " ";
//                            }
//                            
//                            Log.log(this, Log.WARN, "Modems, not working properly: " + list);
//                            this.sendTestAnswer(testStatus.getAnswerEventTime(), "Modems, not working properly: " + list, "MINOR", "WARN");
//                        }
//                        
//                        Log.log(this, Log.INFO, "Reset current test.");
//                        testStatus.reset();
//                    }
//                }
//                
//                if(!deleteMessage((InboundMessage)msgList.get(i)))
//                {
//                    return SmsConnectorStart.STAT_ERR_MODEM;
//                }
//                
//                continue;
//            }
            
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
                    mapMsg = amsPublisherReply.createMapMessage();
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
                    amsPublisherReply.sendMessage(mapMsg);
                }
                catch(Exception e)
                {
                    Log.log(this, Log.FATAL, "could not send to internal jms", e);
                    return SmsConnectorStart.STAT_ERR_JMSCON;
                }

                Log.log(this, Log.INFO, "send internal jms message done");
            }
            
            Log.log(this, Log.DEBUG, "start delete");
            if (!deleteMessage(msgList.get(i)))
                return SmsConnectorStart.STAT_ERR_MODEM;

            Log.log(this, Log.DEBUG, "Delete done");
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

    /*
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
    */
}
