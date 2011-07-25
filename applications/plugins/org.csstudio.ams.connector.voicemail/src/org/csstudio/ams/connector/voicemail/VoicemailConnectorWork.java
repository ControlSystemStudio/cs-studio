
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

import javax.jms.MapMessage;
import javax.jms.Message;
import org.csstudio.ams.AmsActivator;
import org.csstudio.ams.AmsConstants;
import org.csstudio.ams.Log;
import org.csstudio.ams.connector.voicemail.isdn.CallCenter;
import org.csstudio.ams.connector.voicemail.isdn.CallCenterException;
import org.csstudio.platform.utility.jms.JmsRedundantReceiver;
import org.csstudio.platform.utility.jms.JmsSimpleProducer;
import org.eclipse.jface.preference.IPreferenceStore;

public class VoicemailConnectorWork extends Thread implements AmsConstants {
    
    private VoicemailConnectorStart application = null;

    /** The producer sends messages to topic T_AMS_CON_REPLY */
    private JmsSimpleProducer amsPublisherReply;
    
    /** The consumer that listens to topic T_AMS_CONNECTOR_VOICEMAIL */
    private JmsRedundantReceiver amsReceiver; 
    
    /** The class that makes the telephone calls */
    private CallCenter callCenter;
    
    private boolean bStop;
    private boolean bStoppedClean;

    public VoicemailConnectorWork(VoicemailConnectorStart vmcs) {
        this.application = vmcs;
        callCenter = null;
        bStop = false;
        bStoppedClean = false;
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
                    
                    bInitedVmService = true; //initCallCenter();
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

                if (bInitedVmService && bInitedJms) {
                    
                    iErr = VoicemailConnectorStart.STAT_OK;
                    if (application.getStatus() == VoicemailConnectorStart.STAT_INIT)
                        application.setStatus(VoicemailConnectorStart.STAT_OK);

                    // Log.log(this, Log.DEBUG, "runs");
                    
                    Message message = null;
                    try {
                        message = amsReceiver.receive("amsSubscriberVm");                        
                    } catch(Exception e) {
                        Log.log(this, Log.FATAL, "could not receive from internal jms", e);
                        iErr = VoicemailConnectorStart.STAT_ERR_JMSCON;
                    }
                    
                    //TODO: TEST
                    if (message != null) {
                        Log.log(this, Log.DEBUG, "Message received: " + message.toString());
                        iErr = sendIsdnMsg(message);
                        Log.log(this, Log.DEBUG, "Message sent: " + iErr);
                    }

                    if (iErr == VoicemailConnectorStart.STAT_ERR_VM_SERVICE_SEND) {
                        closeCallCenter();
                        bInitedVmService = false;
                        closeJms();
                        bInitedJms = false;
                    }
                    
                    if (iErr == VoicemailConnectorStart.STAT_ERR_VM_SERVICE) {
                        closeCallCenter();
                        bInitedVmService = false;
                    }
                    
                    if (iErr == VoicemailConnectorStart.STAT_ERR_JMSCON
                    || iErr == VoicemailConnectorStart.STAT_ERR_VM_SERVICE_BADRSP) {
                        
                        // recover = reopen
                        closeJms();
                        bInitedJms = false;
                    }
                }

                if (iErr == VoicemailConnectorStart.STAT_ERR_VM_SERVICE_BADRSP) {
                    sleeptime = 15000;
                } else {
                    // server is busy
                    sleeptime = 100;
                }
                
                // set status in every loop
                // set error status, can be OK if no error
                application.setStatus(iErr);
            } catch (Exception e) {
                application.setStatus(VoicemailConnectorStart.STAT_ERR_UNKNOWN);
                Log.log(this, Log.FATAL, e);
                closeCallCenter();
                bInitedVmService = false;
                closeJms();
                bInitedJms = false;
            }
        }
        
        closeJms();
        closeCallCenter();
        bStoppedClean = true;
        
        Log.log(this, Log.INFO, "Voicemail connector exited");
    }

    @SuppressWarnings("unused")
    private boolean initCallCenter() {
        
        try {
            callCenter = new CallCenter();
        } catch(CallCenterException cce) {
            Log.log(Log.ERROR, "The CallCenter could not be initialized: " + cce.getMessage());
        }
        
        return callCenter.isActive();
    }
    
    public void closeCallCenter() {
        if (callCenter != null) {
            callCenter.closeCallCenter();
        }
    }
    
    private boolean initJms() {
        
        IPreferenceStore storeAct = AmsActivator.getDefault().getPreferenceStore();
        boolean success = false;
        
        boolean durable = Boolean.parseBoolean(storeAct.getString(org.csstudio.ams.internal.AmsPreferenceKey.P_JMS_AMS_CREATE_DURABLE));
        
        String url = storeAct.getString(org.csstudio.ams.internal.AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_1);
        String topic = storeAct.getString(org.csstudio.ams.internal.AmsPreferenceKey.P_JMS_AMS_TOPIC_REPLY);
        String factoryClass = storeAct.getString(org.csstudio.ams.internal.AmsPreferenceKey.P_JMS_AMS_CONNECTION_FACTORY_CLASS);
        
        amsPublisherReply = new JmsSimpleProducer("VoicemailConnectorWorkSenderInternal", url, factoryClass, topic);
        if (amsPublisherReply.isConnected() == false) {
            Log.log(this, Log.FATAL, "Could not create amsPublisherReply");
            return false;
        }
        
        try {
            
            amsReceiver = new JmsRedundantReceiver("VoicemailConnectorWorkReceiverInternal", storeAct.getString(org.csstudio.ams.internal.AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_1), storeAct.getString(org.csstudio.ams.internal.AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_2));
            success = amsReceiver.createRedundantSubscriber(
                    "amsSubscriberVm",
                    storeAct.getString(org.csstudio.ams.internal.AmsPreferenceKey.P_JMS_AMS_TOPIC_VOICEMAIL_CONNECTOR),
                    storeAct.getString(org.csstudio.ams.internal.AmsPreferenceKey.P_JMS_AMS_TSUB_VOICEMAIL_CONNECTOR),
                    durable);
            
            if(success == false) {
                Log.log(this, Log.FATAL, "Could not create amsSubscriberVm");
            }
            
        } catch(Exception e) {
            Log.log(this, Log.FATAL, "Could not init internal Jms: " + e.getMessage());
        }
        
        return success;
    }

    public void closeJms() {
        
        Log.log(this, Log.INFO, "Exiting internal jms communication");
        
        if (amsPublisherReply != null) {
            amsPublisherReply.closeAll();
        }
        
        if(amsReceiver != null) {
            amsReceiver.closeAll();
        }
        
        Log.log(this, Log.INFO, "JMS internal communication closed");
    }
    
    private boolean acknowledge(Message msg) {
        
        try {
            msg.acknowledge();
            return true;
        } catch(Exception e) {
            Log.log(this, Log.FATAL, "could not acknowledge: " + e.getMessage());
        }
        
        return false;
    }
    
    public int sendIsdnMsg(Message message) throws Exception {
        
        int result = VoicemailConnectorStart.STAT_OK;
        
        if (!(message instanceof MapMessage)) {
            Log.log(this, Log.WARN, "got unknown message " + message);
            if(!acknowledge(message)) {
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
        
        try {
            callCenter.makeCall(recNo, text, textType, chainIdAndPos, waitUntil);
        } catch(CallCenterException cce) {
            // TODO: What happens if we got an error?
            Log.log(this, Log.ERROR, "Cannot make the call: " + cce.getMessage());
            
            result = VoicemailConnectorStart.STAT_ERR_VM_SERVICE_SEND;
        }
        
        if(!acknowledge(message)) {
            result = VoicemailConnectorStart.STAT_ERR_JMSCON;
        }

        return result;
    }        
}
