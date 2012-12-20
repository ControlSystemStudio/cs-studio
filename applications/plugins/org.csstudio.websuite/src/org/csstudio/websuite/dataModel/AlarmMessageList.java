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

package org.csstudio.websuite.dataModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * List of alarm messages for alarm table. The class includes also the logic in
 * which case a message should be deleted, highlighted etc.
 * 
 * @author jhatje
 * 
 */
public class AlarmMessageList extends MessageList {
    
    private static final Logger LOG = LoggerFactory.getLogger(AlarmMessageList.class);
    
    protected Vector<AlarmMessage> _messages = new Vector<AlarmMessage>();
    
    /** number of alarm status changes in the message with the same pv name. */
    private int alarmStatusChanges = 0;
    
    private final Vector<AlarmMessage> _messagesToRemove = new Vector<AlarmMessage>();
    
    /**
     * Add a new Message to the collection of Messages.
     * 
     */
    @Override
    public synchronized void addMessage(BasicMessage newBasicMessage) {
        AlarmMessage newMessage = new AlarmMessage(newBasicMessage.getHashMap());
        if(checkValidity(newMessage)) {
            // An acknowledge message was received.
            if(newMessage.getProperty("ACK") != null && newMessage.getProperty("ACK").toUpperCase().equals("TRUE")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                AlarmMessage jmsm = setAcknowledge(newMessage);
                if(jmsm != null) {
                    super.updateMessage(jmsm);
                }
                return;
            }
            boolean equalMessageInTable = equalMessageNameInTable(newMessage);
            // do not insert messages with type: 'status', unless there is a
            // previous message with the same NAME in the table.
            if(newMessage.getProperty("TYPE").equalsIgnoreCase("status")) {
                if(equalMessageInTable == true) {
                    updateMessageInTableForDisconnected(newMessage);
                }
                return;
            }
            // is there an old message from same pv
            // (deleteOrGrayOutEqualMessages == true) -> display new message
            // anyway is new message NOT from Type NO_ALARM -> display message
            if( (deleteOrGrayOutEqualMessages(newMessage))
                    || (newMessage.getProperty("SEVERITY").equalsIgnoreCase("NO_ALARM")) == false) { //$NON-NLS-1$
                if(equalMessageInTable == false) {
                    newMessage.setProperty("COUNT", "0");
                } else {
                    newMessage.setProperty("COUNT", String.valueOf(alarmStatusChanges + 1));
                }
                _messages.add(_messages.size(), newMessage);
                super.addMessage(newMessage);
            }
        }
    }
    
    /**
     * Updates the existing message in the table with new properties for new
     * status. If there are two existing messages in table delete the older one.
     * 
     * @param newMessage
     * @throws JMSException
     */
    private synchronized void updateMessageInTableForDisconnected(AlarmMessage newMessage) {
        if(newMessage.getProperty("STATUS") == null) {
            return;
        }
        BasicMessage messageToRemove = null;
        for (AlarmMessage message : _messages) {
            String currentInList = message.getProperty("NAME");
            String currentMessage = newMessage.getProperty("NAME");
            if(currentInList.equalsIgnoreCase(currentMessage) == true) {
                // the new status is disconnected, set severity to "offline"
                if(newMessage.getProperty("STATUS").equalsIgnoreCase("DISCONNECTED")) {
                    // if this message in table is an old one delete it
                    if(message.isOutdated()) {
                        messageToRemove = message;
                    } else {
                        message.setProperty("STATUS", "DISCONNECTED");
                        updateMessage(message);
                    }
                    // the new status is not disconnected update properties with
                    // values of the new message.
                } else {
                    message.setProperty("SEVERITY", newMessage.getProperty("SEVERITY"));
                    message.setProperty("STATUS", newMessage.getProperty("STATUS"));
                    message.setProperty("TIMESTAMP", newMessage.getProperty("TIMESTMAP"));
                    updateMessage(message);
                }
            }
        }
        if(messageToRemove != null) {
            removeMessage(messageToRemove);
        }
    }
    
    /**
     * Remove a message from the list.
     */
    @Override
    public synchronized void removeMessage(BasicMessage jmsm) {
        _messages.remove(jmsm);
        super.removeMessage(jmsm);
    }
    
    /**
     * Remove an array of messages from the list.
     */
    @Override
    public synchronized void removeMessageArray(BasicMessage[] jmsm) {
        for (BasicMessage message : jmsm) {
            _messages.remove(message);
        }
        super.removeMessageArray(jmsm);
    }
    
    @Override
    public synchronized Vector<? extends BasicMessage> getJMSMessageList() {
        return _messages;
    }
    
    @Override
    public synchronized void deleteAllMessages(BasicMessage[] messages) {
        removeMessageArray(messages);
    }
    
    /**
     * Deletes all messages.
     */
    public synchronized void deleteAllMessages() {
        _messages.clear();
    }
    
    /**
     * Check if the new message is valid alarm message
     * 
     * @param newMessage
     */
    private synchronized boolean checkValidity(AlarmMessage newMessage) {
        if(newMessage == null) {
            return false;
        }
        if(newMessage.getProperty("TYPE") == null) {
            if( (newMessage.getProperty("ACK") == null)
                    || (!newMessage.getProperty("ACK").equals("TRUE"))) {
                return false;
            }
        }
        if(newMessage.getProperty("SEVERITY") == null) {
            return false;
        }
        return true;
    }
    
    /**
     * 
     * @param newMessage
     * @throws JMSException
     * @throws JMSException
     */
    protected synchronized AlarmMessage setAcknowledge(final AlarmMessage newMessage) {
        
        LOG.info("AlarmView Ack message received, MsgName: {} MsgTime: {}",
                 newMessage.getProperty("NAME"),
                 newMessage.getProperty("EVENTTIME"));
        for (AlarmMessage message : _messages) {
            if(message.getName().equals(newMessage.getProperty("NAME"))
                    && message.getProperty("SEVERITY").equals(newMessage.getProperty("SEVERITY"))) {
                if( (message.isOutdated() == true)
                        || (message.getProperty("SEVERITY_KEY").equalsIgnoreCase("NO_ALARM"))
                        || (message.getProperty("SEVERITY_KEY").equalsIgnoreCase("INVALID"))) {
                    _messagesToRemove.add(message);
                    LOG.debug("add message, removelist size: {}", _messagesToRemove.size());
                } else {
                    message.getHashMap().put("ACK_HIDDEN", "TRUE");
                    message.setProperty("ACK", "TRUE");
                    message.setAcknowledged(true);
                    return message;
                }
                break;
            }
        }
        for (BasicMessage message : _messagesToRemove) {
            removeMessage(message);
        }
        _messagesToRemove.clear();
        return null;
    }
    
    /**
     * Searching for a previous message in alarm table with the same NAME.
     * 
     * @param mm
     * @return boolean Is there a previous message
     */
    private synchronized boolean equalMessageNameInTable(AlarmMessage mm) {
        boolean messageInTable = false;
        for (AlarmMessage message : _messages) {
            String currentInList = message.getProperty("NAME");
            String currentMessage = mm.getProperty("NAME");
            if( (currentInList.equalsIgnoreCase(currentMessage) == true)
                    && (message.isOutdated() == false)) {
                String alarmChangeCount = message.getProperty("COUNT");
                try {
                    alarmStatusChanges = Integer.parseInt(alarmChangeCount);
                } catch (NumberFormatException e) {
                    alarmStatusChanges = 0;
                }
                messageInTable = true;
                break;
            }
        }
        return messageInTable;
    }
    
    /**
     * Delete previous messages from the same pv and with the same severity Mark
     * messages from the same pv and with a different severity that the label
     * provider can set a brighter color. Test if the EVENTTIME of the new
     * message is really newer than an existing message. (It is important to use
     * the <code>removeMessage</code> method from <code>MessageList</code> that
     * the changeListeners on the model were updated.)
     * 
     * @param mm
     *            The new MapMessage
     * @return Is there a previous message in the list with the same pv name
     */
    private synchronized boolean deleteOrGrayOutEqualMessages(AlarmMessage mm) {
        if(mm == null) {
            return false;
        }
        boolean equalPreviousMessage = false;
        Iterator<AlarmMessage> it = _messages.listIterator();
        List<AlarmMessage> jmsMessagesToRemove = new ArrayList<AlarmMessage>();
        List<AlarmMessage> jmsMessagesToRemoveAndAdd = new ArrayList<AlarmMessage>();
        String newPVName = mm.getProperty("NAME"); //$NON-NLS-1$
        String newSeverity = mm.getProperty("SEVERITY"); //$NON-NLS-1$
        
        if( (newPVName != null) && (newSeverity != null)) {
            while (it.hasNext()) {
                AlarmMessage jmsm = it.next();
                String pvNameFromList = jmsm.getProperty("NAME"); //$NON-NLS-1$
                // the 'real' severity in map message we get from the
                // JMSMessage via SEVERITY_KEY
                String severityFromList = jmsm.getProperty("SEVERITY_KEY"); //$NON-NLS-1$
                if( (pvNameFromList != null) && (severityFromList != null)) {
                    
                    // is there a previous alarm message from same pv?
                    if(newPVName.equalsIgnoreCase(pvNameFromList)) {
                        equalPreviousMessage = true;
                        
                        // is old message gray, are both severities equal ->
                        // remove
                        if( (jmsm.isOutdated()) || (newSeverity.equalsIgnoreCase(severityFromList))) {
                            jmsMessagesToRemove.add(jmsm);
                            
                        } else {
                            jmsMessagesToRemove.add(jmsm);
                            // is old message not acknowledged or is
                            // severity from old message not NO_ALARM ->
                            // add message to list (not delete message)
                            if(!jmsm.getProperty("ACK_HIDDEN").toUpperCase().equals("TRUE") && //$NON-NLS-1$ //$NON-NLS-2$
                                    severityFromList.equalsIgnoreCase("NO_ALARM") == false) { //$NON-NLS-1$
                                jmsm.setOutdated(true);
                                jmsMessagesToRemoveAndAdd.add(jmsm);
                            }
                        }
                    }
                }
            }
        }
        it = jmsMessagesToRemove.listIterator();
        while (it.hasNext()) {
            removeMessage(it.next());
        }
        
        it = jmsMessagesToRemoveAndAdd.listIterator();
        while (it.hasNext()) {
            AlarmMessage message = it.next();
            _messages.add(message);
            super.addMessage(message);
        }
        jmsMessagesToRemove.clear();
        jmsMessagesToRemoveAndAdd.clear();
        return equalPreviousMessage;
    }
    
    @Override
    public synchronized Integer getSize() {
        return _messages.size();
    }
}