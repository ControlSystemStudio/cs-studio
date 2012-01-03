
/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */

package org.csstudio.ams.delivery.sms;

import java.util.ArrayList;
import java.util.Collection;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import org.csstudio.ams.AmsActivator;
import org.csstudio.ams.delivery.BaseAlarmMessage.State;
import org.csstudio.ams.delivery.device.IDeliveryDevice;
import org.csstudio.ams.delivery.device.IReadableDevice;
import org.csstudio.ams.delivery.jms.JmsProperties;
import org.csstudio.ams.delivery.jms.JmsSender;
import org.csstudio.ams.delivery.service.Environment;
import org.csstudio.ams.delivery.sms.internal.SmsConnectorPreferenceKey;
import org.csstudio.ams.internal.AmsPreferenceKey;
import org.csstudio.platform.utility.jms.JmsSimpleProducer;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smslib.InboundMessage;
import org.smslib.InboundMessage.MessageClasses;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.Message.MessageEncodings;
import org.smslib.modem.SerialModemGateway;

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @version 1.0
 * @since 18.12.2011
 */
public class SmsDeliveryDevice implements IDeliveryDevice<SmsAlarmMessage>,
                                          IReadableDevice<InboundMessage> {

    /** The static class logger */
    private static final Logger LOG = LoggerFactory.getLogger(SmsDeliveryDevice.class);
    
    private static final int MAX_MODEM_NUMBER = 3;
    
    /** Text for the test SMS */
    private static final String SMS_TEST_TEXT = "[MODEMTEST{$CHECKID,$GATEWAYID}]";

    private JmsProperties jmsProps;
    
    private Service modemService;
    
    /** This class contains all modem ids (names) */
    private ModemInfoContainer modemInfo;

    /** Reading period (in ms) for the modem */
    private long readWaitingPeriod;

    public SmsDeliveryDevice(ModemInfoContainer deviceInfo, JmsProperties jms) {
        modemService = null;
        modemInfo = deviceInfo;
        jmsProps = jms;
        initModem();
    }
        
    @Override
    public boolean deleteMessage(InboundMessage message) {
        
        boolean success = false;
        try {
            success = modemService.deleteMessage(message);
        } catch (Exception e) {
            LOG.error("[*** " + e.getClass().getSimpleName() + " ***]: " + e.getMessage());
        }

        return success;
    }
    
    @Override
    public int sendMessages(Collection<SmsAlarmMessage> msgList) {
        
        int sent = 0;
        
        for (SmsAlarmMessage o : msgList) {
            if (sendMessage(o)) {
                sent++;
            }
        }
        
        return sent;
    }
    
    @Override
    public boolean sendMessage(SmsAlarmMessage message) {
        
        boolean success = false;
        
        OutboundMessage msg = new OutboundMessage(message.getReceiverAddress(), message.getMessageText());
        msg.setEncoding(MessageEncodings.ENC7BIT);
        // Changed by Markus Moeller, 2009-01-30
        // To avoid restarts of the modems
        // msg.setStatusReport(true);
        msg.setStatusReport(false);
        msg.setValidityPeriod(8);
        
        // Total number of outbound messages since restart
        int totalOutBefore = modemService.getOutboundMessageCount();
        
        // TODO: Eventuell die Liste aller Modems und ihre Zustände ausgeben
        try {
            LOG.info("Try to send SMS...");
            modemService.sendMessage(msg);
            int totalOutAfter = modemService.getOutboundMessageCount();
            if (totalOutBefore < totalOutAfter) {
                LOG.info("SMS sent to: '{}' with text: '{}'", message.getReceiverAddress(), message.getMessageText());
                message.setMessageState(State.SENT);
                success = true;
            } else {
                success = false;
                message.setMessageState(State.FAILED);

                // If no modems are defined, return true anyhow
                if(modemInfo.getModemCount() == 0) {
                    success = true;
                    message.setMessageState(State.SENT);
                }
            }
            LOG.info("Number of sent SMS: {}", totalOutAfter);
        } catch(Exception e) {
            LOG.error("[*** {} ***]: Could not send message: {}", e.getClass().getSimpleName(), e.getMessage());
        }

        return success;
    }

    public void sendDeviceTestMessage(ModemTestStatus testStatus) {
        
        OutboundMessage outMsg = null;
        String name = null;
        String number = null;
        String text = null;
        
        LOG.info("Number of modems to test: {}", modemInfo.getModemCount());
        for(int i = 0;i < modemInfo.getModemCount();i++) {
            name = modemInfo.getModemName(i);
            if(name != null) {
                number = modemInfo.getPhoneNumber(name);

                text = SMS_TEST_TEXT;
                text = text.replaceAll("\\$CHECKID", testStatus.getCheckId());
                text = text.replaceAll("\\$GATEWAYID", name);
                
                outMsg = new OutboundMessage(number, text);
                outMsg.setEncoding(MessageEncodings.ENC7BIT);
                outMsg.setStatusReport(false);
                outMsg.setValidityPeriod(8);
                
                try {
                    LOG.info("Sending to modem '{}': {}", name, text);
                    if(modemService.sendMessage(outMsg, name)) {
                        testStatus.addGatewayId(name);
                    }
                } catch(Exception e) {
                    LOG.warn("Could not send SMS test message to modem '{}'.", name);
                    testStatus.addBadModem(name);
                }
                
                outMsg = null;
            }
        }
        
        if(testStatus.getGatewayCount() > 0) {
            testStatus.setActive(true);
            testStatus.setTimeOut(System.currentTimeMillis() + 120000); // 2 minutes
        } else {
            sendTestAnswer(testStatus.getCheckId(), "No modem could send the test SMS.", "MAJOR", "ERROR");
            testStatus.reset();
        }
    }

    public void sendTestAnswer(String checkId, String text, String severity, String value) {
        
        JmsSimpleProducer producer = new JmsSimpleProducer("SmsDeliveryDevice@"
                                                           + Environment.getInstance().getHostName(),
                                                           jmsProps.getJmsUrl(),
                                                           jmsProps.getJmsFactoryClass(),
                                                           jmsProps.getJmsTopic());
        
        try {
            
            MapMessage mapMessage = producer.createMapMessage();
            mapMessage.setString("TYPE", "event");
            mapMessage.setString("EVENTTIME", producer.getCurrentDateAsString());
            mapMessage.setString("TEXT", text);
            mapMessage.setString("SEVERITY", severity);
            mapMessage.setString("VALUE", value);
            mapMessage.setString("CLASS", checkId);
            mapMessage.setString("HOST", Environment.getInstance().getHostName());
            mapMessage.setString("USER", Environment.getInstance().getUserName());
            mapMessage.setString("NAME", "AMS_SYSTEM_CHECK_ANSWER");
            mapMessage.setString("APPLICATION-ID", "SmsConnector");
            mapMessage.setString("DESTINATION", "AmsSystemMonitor");
            
            producer.sendMessage(mapMessage);
        } catch(JMSException jmse) {
            LOG.error("Answer message could NOT be sent: {}", jmse.getMessage());
        }
    }

    /**
     * Reads the oldest message from any storage location.
     */
    @Override
    public InboundMessage readMessage() {
        
        ArrayList<InboundMessage> msgList = new ArrayList<InboundMessage>();
        try {
            modemService.readMessages(msgList, MessageClasses.ALL);
        } catch (Exception e) {
            LOG.error("[*** " + e.getClass().getSimpleName() + " ***]: " + e.getMessage());
        }
        
        InboundMessage result = null;
        if (msgList.size() > 0) {
            long timeStamp = System.currentTimeMillis();
            for (InboundMessage o : msgList) {
                if (o.getDate().getTime() <= timeStamp) {
                    result = o;
                    timeStamp = o.getDate().getTime();
                }
            }
        }
    
        return result;
    }

    @Override
    public int readMessages(Collection<InboundMessage> msgList) {
        
        int read = 0;
        try {
            read = modemService.readMessages(msgList, MessageClasses.ALL);
        } catch (Exception e) {
            LOG.error("[*** " + e.getClass().getSimpleName() + " ***]: " + e.getMessage());
        }
        
        return read;
    }

    @Override
    public void stopDevice() {
        if (modemService != null) {
            try {
                modemService.stopService();
            } catch (Exception e) {
                LOG.warn("[*** {} ***]: {}", e.getClass().getSimpleName(), e.getMessage());
            }
        }
    }

    private boolean initModem() {
        
        String[] strComPort = null;
        String[] strManufac = null;
        String[] strModel = null;
        String[] strSimPin = null;
        String[] strPhoneNumber = null;
        String m = null;
        int[] iBaudRate = null;
        int modemCount = 1;
        
        boolean result = false;

        IPreferencesService prefs = Platform.getPreferencesService();
        try {
            ////////////////////////////////////////////////////////////////////////
            // strComPort   - COM-Port: "COM1", "COM2", "COM3", ... , "/dev/ttyS1", ...
            // iBaudRate        - Modem Baud-Rate: 9600, 57600, ... 
            // strManufac   - gsmDeviceManufacturer: "SonyEricsson", "Siemens", "Wavecom", "Nokia", ..., ""
            // strModel     - gsmDeviceModel: "GS64", "M1306B", "6310i", ..., ""
            // strSimPin        - SimCard Pin-Number: "1234", ...
            ////////////////////////////////////////////////////////////////////////
            
            readWaitingPeriod = prefs.getLong(SmsDeliveryActivator.PLUGIN_ID,
                                              SmsConnectorPreferenceKey.P_MODEM_READ_WAITING_PERIOD,
                                              10000L,
                                              null);
            LOG.info("Waiting period for reading: " + readWaitingPeriod);
       
            modemCount = prefs.getInt(SmsDeliveryActivator.PLUGIN_ID,
                                      SmsConnectorPreferenceKey.P_MODEM_COUNT,
                                      0, null);
            modemCount = (modemCount < 0) ? 0 : modemCount;
            modemCount = (modemCount > MAX_MODEM_NUMBER) ? MAX_MODEM_NUMBER : modemCount;
            LOG.info("Number of modems: " + modemCount);

            strComPort = new String[modemCount];
            strManufac = new String[modemCount];
            strModel = new String[modemCount];
            strSimPin = new String[modemCount];
            strPhoneNumber = new String[modemCount];
            iBaudRate = new int[modemCount];
            
            // TODO: Better error handling and value checks
            for(int i = 0;i < modemCount;i++) {
                
                strComPort[i] = prefs.getString(SmsDeliveryActivator.PLUGIN_ID,
                                                SmsConnectorPreferenceKey.P_PREFERENCE_STRING
                                                + (i + 1) + "ComPort",
                                                "",
                                                null);
                
                iBaudRate[i] = prefs.getInt(SmsDeliveryActivator.PLUGIN_ID,
                                            SmsConnectorPreferenceKey.P_PREFERENCE_STRING
                                            + (i + 1) + "ComBaudrate",
                                            9600,
                                            null);
                
                strManufac[i] = prefs.getString(SmsDeliveryActivator.PLUGIN_ID,
                                                SmsConnectorPreferenceKey.P_PREFERENCE_STRING
                                                + (i + 1) + "Manufacture",
                                                "",
                                                null);
                
                strModel[i] = prefs.getString(SmsDeliveryActivator.PLUGIN_ID,
                                              SmsConnectorPreferenceKey.P_PREFERENCE_STRING
                                              + (i + 1) + "Model",
                                              "",
                                              null);
                
                strSimPin[i] = prefs.getString(SmsDeliveryActivator.PLUGIN_ID,
                                               SmsConnectorPreferenceKey.P_PREFERENCE_STRING
                                               + (i + 1) + "SimPin",
                                               "",
                                               null);

                strPhoneNumber[i] = prefs.getString(SmsDeliveryActivator.PLUGIN_ID,
                                                    SmsConnectorPreferenceKey.P_PREFERENCE_STRING
                                                    + (i + 1) + "Number",
                                                    "",
                                                    null);
            }
            
            modemService = Service.getInstance();
            
            for(int i = 0;i < modemCount;i++) {
                if(strComPort[i].length() > 0) {
                    LOG.info("Start initModem(" + strComPort[i] + ","
                            + iBaudRate[i] + ","
                            + strManufac[i] + ","
                            + strModel[i] + ")");
                    // modemService = new CSoftwareService(strComPort, iBaudRate, strManufac, strModel);
                    m = "modem." + strComPort[i].toLowerCase();
                    SerialModemGateway modem = new SerialModemGateway(m , strComPort[i], iBaudRate[i], strManufac[i], strModel[i]);
                    modem.setInbound(true);
                    modem.setOutbound(true);
                    modem.setSimPin(strSimPin[i]);
                    // modem.setOutboundNotification(outboundNotification);
                    modemService.addGateway(modem);
                    modemInfo.addModemName(m, strPhoneNumber[i]);
                                        
                    sleep(2000);
                } else {
                    LOG.warn("No COM port defined for modem " + (i + 1) + ".");
                }
            }
            
            result = true;
            
            LOG.info("Modem(s) are initialized");
            
            modemService.setGatewayStatusNotification(new GatewayStatusNotification(modemInfo.getModemNames()));
            
            if((result == true) && (modemCount > 0)) {
                LOG.info("Try to start service");
                modemService.startService();
                LOG.info("Service started");
            }
        } catch(Exception e) {
            LOG.error("Could not init modem: {}", e);
            
            JmsSender sender = new JmsSender("SmsConnectorAlarmSender",
                                             prefs.getString(AmsActivator.PLUGIN_ID,
                                                             AmsPreferenceKey.P_JMS_AMS_SENDER_PROVIDER_URL,
                                                             "failover:(tcp://localhost:62616,tcp://localhost:64616)",
                                                             null),
                                                             "ALARM");
            
            if(sender.isConnected()) {
                if(sender.sendMessage("alarm",
                                      "SmsConnectorWork: Cannot init modem [" + e.getMessage() + "]",
                                      "MAJOR") == false) {
                    LOG.error("Cannot send alarm message.");
                } else {
                    LOG.info("Alarm message sent.");
                }
            } else {
                LOG.warn("Alarm message sender is NOT connected.");
            }
            
            sender.closeAll();
            sender = null;
            
            result = false;
        }
        
        return result;
    }
    
    private void sleep(long ms) {
        synchronized (this) {
            try {
                this.wait(ms);
            } catch (InterruptedException e) {
                // Ignore me
            }
        }
    }
}
