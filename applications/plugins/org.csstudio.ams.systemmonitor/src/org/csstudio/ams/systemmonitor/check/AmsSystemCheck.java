
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
import org.csstudio.ams.systemmonitor.message.CheckMessage;
import org.csstudio.ams.systemmonitor.message.MapMessageConverter;
import org.csstudio.ams.systemmonitor.status.MonitorStatusEntry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;

public class AmsSystemCheck extends AbstractCheckProcessor {
    
    private ClassNumberHistory classNumberHistory;
    
    public AmsSystemCheck(String senderClientId,
                          String receiverClientId,
                          String subscriberName) throws AmsSystemMonitorException {
        super(senderClientId, receiverClientId, subscriberName);
        
        String workspaceLocation;
        // Retrieve the location of the workspace directory
        try {
            workspaceLocation = Platform.getLocation().toPortableString();
            if(workspaceLocation.endsWith("/") == false) {
                workspaceLocation = workspaceLocation + "/";
            }
        } catch(IllegalStateException ise) {
            LOG.warn("Workspace location could not be found. Using working directory '.'");
            workspaceLocation = "./";
        }
        
        classNumberHistory = new ClassNumberHistory(workspaceLocation);
    }

    @Override
    public void doCheck(MonitorStatusEntry currentStatusEntry) throws AmsSystemMonitorException {
        
        LOG.info("Starting check of AMS System.");
        
        IPreferencesService pref = Platform.getPreferencesService();
        long amsWaitTime = pref.getLong(AmsSystemMonitorActivator.PLUGIN_ID,
                                        PreferenceKeys.P_AMS_WAIT_TIME,
                                        2000L,
                                        null);
        
        LOG.info("Waiting time for receiving messages for the AMS system check: " + amsWaitTime + " ms");
        
        Hashtable<String, String> messageContent =
                messageHelper.getNewCheckMessage(MessageHelper.MessageType.SYSTEM);
        // checkTimeStamp = convertDateStringToLong(messageContent.get("EVENTTIME"));
            
        // Check old messages if the last check was not answered, otherwise delete all old messages
        boolean success = amsPublisher.sendMessage(messageContent);
        if(success) {
            LOG.info("Message sent.");
        } else {
            LOG.error("Message could NOT be sent.");
            throw new AmsSystemMonitorException("Message could NOT be sent.",
                                                AmsSystemMonitorException.ERROR_CODE_SYSTEM_MONITOR);
        }
        
        Object waitObject = new Object();
        Message message = null;
        success = false;
        // Get all messages
        do {
            
            synchronized(waitObject) {
                try {
                    waitObject.wait(amsWaitTime);
                } catch(InterruptedException ie) {
                    // Can be ignored
                }
            }

            message = amsReceiver.receive("amsSystemMonitor");
            if (message != null) {
                
                // Compare the incoming message with the sent message
                LOG.info("Message received:");
                
                if (message instanceof MapMessage) {
                    MapMessage mapMessage = (MapMessage) message;
                    CheckMessage receivedMsg = MapMessageConverter.convertMapMessage(mapMessage);
                    
                    if (messageHelper.isAmsAnswer(messageContent, receivedMsg)) {
                        LOG.info(" It is the freshly sent message.");
                        classNumberHistory.removeClassNumber(receivedMsg.getValue("CLASS"));
                        success = true;
                    } else if (classNumberHistory.containsClassNumber(receivedMsg.getValue("CLASS"))) {
                        LOG.info(" It has been found in the history.");
                        classNumberHistory.removeClassNumber(receivedMsg.getValue("CLASS"));
                        success = true;
                    } else {
                        LOG.warn("Received message is NOT equal.");
                    }
                } else {
                    LOG.warn("Message is not a MapMessage object: " + message.getClass().getName());
                }
                
                acknowledge(message);
            }
            
        } while ((message != null) && (success == false));
        
        if (success) {
            classNumberHistory.removeAll();
        } else {
            classNumberHistory.put(System.currentTimeMillis(), messageContent.get("CLASS"));
        }

        LOG.info("Class number history stored: {}", classNumberHistory.storeContent());
        
        if (!success) {
            throw new AmsSystemMonitorException("The sent message has not been received yet. NOT really a timeout!", AmsSystemMonitorException.ERROR_CODE_TIMEOUT);
        }
    }
    
    @Override
    public void closeJms() {
        super.closeJms();
    }
}
