
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

package org.csstudio.ams.systemmonitor.check;

import java.util.Hashtable;

import javax.jms.MapMessage;
import javax.jms.Message;

import org.csstudio.ams.systemmonitor.AmsSystemMonitorActivator;
import org.csstudio.ams.systemmonitor.AmsSystemMonitorException;
import org.csstudio.ams.systemmonitor.internal.PreferenceKeys;
import org.csstudio.ams.systemmonitor.jms.MessageHelper;
import org.csstudio.ams.systemmonitor.status.MonitorStatusEntry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

/**
 * @author Markus
 *
 */
public class SmsConnectorCheck extends AbstractCheckProcessor
{
    public SmsConnectorCheck(String senderClientId,
                             String receiverClientId,
                             String subscriberName)
                                     throws AmsSystemMonitorException {
        super(senderClientId, receiverClientId, subscriberName);
    }

    @Override
    public void doCheck(MonitorStatusEntry statusEntry) throws AmsSystemMonitorException
    {
        Hashtable<String, String> messageContent = null;
        CheckResult result = null;
        Message message = null;
        MapMessage mapMessage = null;
        Object waitObject = null;
        long endTime = 0;
        long currentTime = 0;
        int waitTime = 0;
        boolean success = false;
        
        LOG.info("Starting check of SmsConnector.");

        IPreferencesService pref = Platform.getPreferencesService();
        waitTime = pref.getInt(AmsSystemMonitorActivator.PLUGIN_ID, PreferenceKeys.P_SMS_WAIT_TIME, -1, null);
        if(waitTime == -1) {
            waitTime = 60000;
            LOG.warn("Could not get the wait time. Using default: " + waitTime + " ms");
        }

        LOG.info("Wait time for modem check: " + waitTime + " ms");

        messageContent = messageHelper.getNewCheckMessage(MessageHelper.MessageType.SMS_DELIVERY_WORKER, statusEntry);
        // checkTimeStamp = convertDateStringToLong(messageContent.get("EVENTTIME"));

        // Send a new check message only if we do not wait for a older check message
        if(statusEntry.getCheckStatus() != CheckResult.TIMEOUT) {
            success = amsPublisher.sendMessage(messageContent);
            if(success) {
                LOG.info("Message sent.");
            } else {
                LOG.error("Message could NOT be sent.");
                throw new AmsSystemMonitorException("Message could NOT be sent.", AmsSystemMonitorException.ERROR_CODE_SYSTEM_MONITOR);
            }
        } else {
            LOG.info("A new message has NOT been sent. Looking for an old check message.");
        }
        
        // Not more then 3 minutes to wait, please
        if(waitTime > 180000) {
            waitTime = 180000;
            LOG.warn("The whole wait time is too long (not more then 3 minutes). Using: " + waitTime + " ms");
        }
                    
        waitObject = new Object();        
        endTime = System.currentTimeMillis() + waitTime;
        result = CheckResult.NONE;

        LOG.info("Start wait cycle.");

        // Get all messages
        do
        {
            synchronized(waitObject)
            {
                try
                {
                    waitObject.wait(1000);
                }
                catch(InterruptedException ie) {
                    // Can be ignored
                }
            }

            message = amsReceiver.receive("amsSystemMonitor");
            if(message != null)
            {
                // Compare the incoming message with the sent message
                LOG.info("Message received.");
                
                if(message instanceof MapMessage)
                {
                    mapMessage = (MapMessage)message;
                    result = messageHelper.getAnswerFromSmsConnector(mapMessage, messageContent);
                    if(result == CheckResult.NONE)
                    {
                        LOG.warn("Received message is NOT a message from the SmsConnector.");
                        LOG.warn(message.toString());
                    }
                    else
                    {
                        LOG.info("SmsConnector answered: " + result);
                        LOG.info(message.toString());
                    }
                }
                else
                {
                    LOG.warn("Message is not a MapMessage object: " + message.getClass().getName());
                } 
                
                acknowledge(message);
            }
            
            currentTime = System.currentTimeMillis();
        }
        while((result == CheckResult.NONE) && (currentTime <= endTime));
        
        if(result == CheckResult.NONE)
        {
            // Timeout?
            if(currentTime > endTime)
            {
                throw new AmsSystemMonitorException("Timeout!", AmsSystemMonitorException.ERROR_CODE_TIMEOUT);
            }

            throw new AmsSystemMonitorException("No response from the SmsConnector.", AmsSystemMonitorException.ERROR_CODE_SMS_CONNECTOR_ERROR);
        }
        else if(result == CheckResult.ERROR)
        {
            throw new AmsSystemMonitorException("ERROR - " + messageHelper.getErrorText(), AmsSystemMonitorException.ERROR_CODE_SMS_CONNECTOR_ERROR);
        }
        else if(result == CheckResult.WARN)
        {
            throw new AmsSystemMonitorException("WARN - " + messageHelper.getErrorText(), AmsSystemMonitorException.ERROR_CODE_SMS_CONNECTOR_WARN);
        }
    }
    
    @Override
    public void closeJms() {
        super.closeJms();
    }
}
