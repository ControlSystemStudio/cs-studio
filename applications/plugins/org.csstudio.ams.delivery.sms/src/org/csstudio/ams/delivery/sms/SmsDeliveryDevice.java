
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

import java.util.Collection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import org.csstudio.ams.AmsActivator;
import org.csstudio.ams.delivery.device.DeviceException;
import org.csstudio.ams.delivery.device.IDeliveryDevice;
import org.csstudio.ams.delivery.jms.JmsAsyncConsumer;
import org.csstudio.ams.delivery.service.JmsSender;
import org.csstudio.ams.delivery.sms.internal.SmsConnectorPreferenceKey;
import org.csstudio.ams.internal.AmsPreferenceKey;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smslib.Service;
import org.smslib.modem.SerialModemGateway;

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @version 1.0
 * @since 18.12.2011
 */
public class SmsDeliveryDevice implements IDeliveryDevice<SmsAlarmMessage>, MessageListener {

    /** The static class logger */
    private static final Logger LOG = LoggerFactory.getLogger(SmsDeliveryDevice.class);
    
    private static final int MAX_MODEM_NUMBER = 3;
    
    /** The consumer is necessary to receive the device test messages */
    private JmsAsyncConsumer amsConsumer;
    
    private Service modemService;
    
    /** This class contains all modem ids (names) */
    private ModemInfoContainer modemInfo;

    /** Status information of the current modem test */
    private ModemTestStatus testStatus;

    /** Reading period (in ms) for the modem */
    private long readWaitingPeriod;

    public SmsDeliveryDevice(final JmsAsyncConsumer consumer) {
        amsConsumer = consumer;
        modemService = null;
        initJmsForModemTest();
        initModem();
    }
    
    @Override
    public int sendMessages(Collection<SmsAlarmMessage> msgList) throws DeviceException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean deleteMessage(SmsAlarmMessage message) throws DeviceException {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean sendMessage(SmsAlarmMessage message) throws DeviceException {
        return true;
    }

    @Override
    public SmsAlarmMessage readMessage() {
        return null;
    }

    @Override
    public void readMessages(Collection<SmsAlarmMessage> msgList) throws DeviceException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void stopDevice() {
        if (amsConsumer != null) {
            amsConsumer.closeSubscriber("amsSubscriberSmsModemtest");
        }
    }

    @Override
    public void onMessage(Message msg) {
        LOG.info("Message received: {}", msg);
        acknowledge(msg);
    }
    
    private void acknowledge(Message message) {
        try {
            message.acknowledge();
        } catch (JMSException jmse) {
            LOG.warn("Cannot acknowledge message: {}", message);
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

            modemCount = 1;
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
    
    private boolean initJmsForModemTest() {
        
        boolean success = false;
        
        IPreferencesService prefs = Platform.getPreferencesService();
        
        final boolean durable = prefs.getBoolean(AmsActivator.PLUGIN_ID,
                                                 AmsPreferenceKey.P_JMS_AMS_CREATE_DURABLE,
                                                 false,
                                                 null);

        // Create second subscriber (topic for the modem test) 
        success = amsConsumer.createRedundantSubscriber(
                "amsSubscriberSmsDeviceTest",
                prefs.getString(AmsActivator.PLUGIN_ID,
                                AmsPreferenceKey.P_JMS_AMS_TOPIC_CONNECTOR_DEVICETEST,
                                "T_AMS_CON_DEVICETEST",
                                null),
                prefs.getString(AmsActivator.PLUGIN_ID,
                                AmsPreferenceKey.P_JMS_AMS_TSUB_SMS_CONNECTOR_DEVICETEST,
                                "SUB_AMS_CON_SMS_DEVICETEST",
                                null),
                durable);
        if(success) {
            amsConsumer.addMessageListener("amsSubscriberSmsDeviceTest", this);
        } else {
            LOG.error("Could not create amsSubscriberSmsDeviceTest");
        }

        return success;
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
