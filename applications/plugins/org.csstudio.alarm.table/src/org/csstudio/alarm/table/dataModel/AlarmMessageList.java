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
package org.csstudio.alarm.table.dataModel;

import static org.csstudio.alarm.service.declaration.AlarmMessageKey.ACK;
import static org.csstudio.alarm.service.declaration.AlarmMessageKey.EVENTTIME;
import static org.csstudio.alarm.service.declaration.AlarmMessageKey.NAME;
import static org.csstudio.alarm.service.declaration.AlarmMessageKey.SEVERITY;
import static org.csstudio.alarm.service.declaration.AlarmMessageKey.STATUS;
import static org.csstudio.alarm.service.declaration.AlarmMessageKey.TYPE;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.alarm.service.declaration.Severity;
import org.csstudio.platform.logging.CentralLogger;

/**
 * List of alarm messages for alarm table. The class includes also the logic in
 * which case a message should be deleted, highlighted etc.
 *
 * @author jhatje
 *
 */
public class AlarmMessageList extends AbstractMessageList {

    private static final Logger LOG = CentralLogger.getInstance().getLogger(AlarmMessageList.class);

    private final Vector<AlarmMessage> _messages = new Vector<AlarmMessage>();

    /** number of alarm status changes in the message with the same pv name. */
    private int alarmStatusChanges = 0;

    private final Vector<AlarmMessage> _messagesToRemove = new Vector<AlarmMessage>();

    /**
     * Add a new Message to the collection of Messages.
     *
     */
    @Override
    public synchronized void addMessage(@Nonnull final BasicMessage newBasicMessage) {

        final AlarmMessage newMessage = new AlarmMessage(newBasicMessage.getHashMap());
        if (checkValidity(newMessage)) {
            // An acknowledge message was received.
            final String propAck = newMessage.getProperty(ACK.getDefiningName());
            if ((propAck != null) && Boolean.valueOf(propAck)) {
                final AlarmMessage jmsm = setAcknowledge(newMessage);
                if (jmsm != null) {
                    super.updateMessage(jmsm);
                }
                return;
            }
            final boolean msgExists = existsMessageWithSameName(newMessage);
            // do not insert messages with type: 'status', unless there is a
            // previous message with the same NAME in the table.
            final String propType = newMessage.getProperty(TYPE.getDefiningName());
            if ((propType != null) && propType.equalsIgnoreCase("status")) {
                if (msgExists) {
                    updateMessageInTableForDisconnected(newMessage);
                }
                return;
            }
            // is there an old message from same pv
            // (deleteOrGrayOutEqualMessages == true) -> display new message
            // anyway is new message NOT from Type NO_ALARM -> display message
            final String propSev = newMessage.getProperty(SEVERITY.getDefiningName());
            if (propSev == null) {
                throw new IllegalStateException("Message without Severity found. Validity check failure!");
            }
            if (deleteOrGrayOutEqualMessages(newMessage) ||
                !(propSev.equalsIgnoreCase(Severity.NO_ALARM.name()))) { //$NON-NLS-1$
                if (!msgExists) {
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
     */
    private void updateMessageInTableForDisconnected(@Nonnull final AlarmMessage newMessage) {
        final String propStatus = newMessage.getProperty(STATUS.getDefiningName());
        if (propStatus == null) {
            return;
        }
        BasicMessage messageToRemove = null;
        for (final AlarmMessage message : _messages) {
            final String currentInList = message.getProperty(NAME.getDefiningName());
            final String currentMessage = newMessage.getProperty(NAME.getDefiningName());
            if ((currentInList != null) && currentInList.equalsIgnoreCase(currentMessage)) {
                // the new status is disconnected, set severity to "offline"
                if (propStatus.equalsIgnoreCase("DISCONNECTED")) {
                    // if this message in table is an old one delete it
                    if (message.isOutdated()) {
                        messageToRemove = message;
                    } else {
                        message.setProperty(STATUS.getDefiningName(), "DISCONNECTED");
                        updateMessage(message);
                    }
                    // the new status is not disconnected update properties with
                    // values of the new message.
                } else {
                    message.setProperty(SEVERITY.getDefiningName(),
                                        newMessage.getProperty(SEVERITY.getDefiningName()));
                    message.setProperty(STATUS.getDefiningName(), propStatus);
                    message.setProperty("TIMESTAMP", newMessage.getProperty("TIMESTMAP"));
                    updateMessage(message);
                }
            }
        }
        if (messageToRemove != null) {
            removeMessage(messageToRemove);
        }
    }

    /**
     * Remove a message from the list.
     */
    @Override
    public void removeMessage(@Nonnull final BasicMessage jmsm) {
        _messages.remove(jmsm);
        super.removeMessage(jmsm);
    }

    /**
     * Remove an array of messages from the list.
     */
    @Override
    public void removeMessages(@Nonnull final BasicMessage[] jmsm) {
        for (final BasicMessage message : jmsm) {
            _messages.remove(message);
        }
        super.removeMessages(jmsm);
    }

    @Override
    public Vector<? extends BasicMessage> getMessageList() {
        return _messages;
    }

    /**
     * Check if the new message is valid alarm message
     * NAME and SEVERITY have to exist
     * TYPE may be omitted if there is prop ACK==TRUE
     * (TODO (bknerr) : refactor to be a type value 'TYPE==ACK'
     *
     * @param newMessage
     */
    private boolean checkValidity(@Nonnull final AlarmMessage newMessage) {
        if (newMessage.getProperty(NAME.getDefiningName()) == null) {
            return false;
        }

        if (newMessage.getProperty(SEVERITY.getDefiningName()) == null) {
            return false;
        }
        if (newMessage.getProperty(TYPE.getDefiningName()) == null) {
            final String propAck = newMessage.getProperty(ACK.getDefiningName());
            if (!Boolean.valueOf(propAck)) { // propAck null is ok
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param newMessage
     */
    @CheckForNull
    protected AlarmMessage setAcknowledge(@Nonnull final AlarmMessage newMessage) {

        final String newNameProp = newMessage.getProperty(NAME.getDefiningName());
        final String newTimeProp = newMessage.getProperty(EVENTTIME.getDefiningName());
        LOG.debug("AlarmView Ack message received, MsgName: " +
                  newNameProp + " MsgTime: " + newTimeProp);

        for (final AlarmMessage message : _messages) {
            final String sevProp = message.getProperty(SEVERITY.getDefiningName());
            final String newSevProp = newMessage.getProperty(SEVERITY.getDefiningName());
            final String nameProp = message.getName();
            if ((nameProp != null) && nameProp.equals(newNameProp) && sevProp.equals(newSevProp)) {
                final String sevKeyProp = message.getProperty("SEVERITY_KEY");
                if ((sevKeyProp != null) &&
                    (message.isOutdated() ||
                    sevKeyProp.equalsIgnoreCase(Severity.NO_ALARM.name()) ||
                    sevKeyProp.equalsIgnoreCase(Severity.INVALID.name()) )) {

                    _messagesToRemove.add(message);
                    LOG.debug("add message, removelist size: " + _messagesToRemove.size());
                } else {
                    message.getHashMap().put("ACK_HIDDEN", Boolean.TRUE.toString());
                    message.setProperty(ACK.getDefiningName(), Boolean.TRUE.toString());
                    message.setAcknowledged(true);
                    return message;
                }
                break;
            }
        }
        for (final BasicMessage message : _messagesToRemove) {
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
    private boolean existsMessageWithSameName(@Nonnull final AlarmMessage mm) {
        boolean messageInTable = false;
        final String name = mm.getProperty(NAME.getDefiningName());
        for (final AlarmMessage message : _messages) {
            final String currentName = message.getProperty(NAME.getDefiningName());
            if ((currentName != null) && currentName.equalsIgnoreCase(name) && !message.isOutdated()) {
                messageInTable = true;
                final String alarmChangeCount = message.getProperty("COUNT");
                try {
                    alarmStatusChanges = Integer.parseInt(alarmChangeCount);
                } catch (final NumberFormatException e) {
                    alarmStatusChanges = 0;
                }
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
    private boolean deleteOrGrayOutEqualMessages(@Nonnull final AlarmMessage mm) {

        boolean equalPreviousMessage = false;
        final List<AlarmMessage> jmsMessagesToRemove = new ArrayList<AlarmMessage>();
        final List<AlarmMessage> jmsMessagesToRemoveAndAdd = new ArrayList<AlarmMessage>();
        final String newPVName = mm.getProperty(NAME.getDefiningName());
        final String newSeverity = mm.getProperty(SEVERITY.getDefiningName());

        Iterator<AlarmMessage> it = _messages.listIterator();
        if ((newPVName != null) && (newSeverity != null)) {
            while (it.hasNext()) {
                final AlarmMessage jmsm = it.next();
                final String pvNameFromList = jmsm.getProperty(NAME.getDefiningName());
                // the 'real' severity in map message we get from the
                // JMSMessage via SEVERITY_KEY
                final String severityFromList = jmsm.getProperty("SEVERITY_KEY"); //$NON-NLS-1$
                if ((pvNameFromList != null) && (severityFromList != null)) {

                    // is there a previous alarm message from same pv?
                    if (newPVName.equalsIgnoreCase(pvNameFromList)) {
                        equalPreviousMessage = true;

                        // is old message gray, are both severities equal ->
                        // remove
                        if ((jmsm.isOutdated())
                                || (newSeverity.equalsIgnoreCase(severityFromList))) {
                            jmsMessagesToRemove.add(jmsm);

                        } else {
                            jmsMessagesToRemove.add(jmsm);
                            // is old message not acknowledged or is
                            // severity from old message not NO_ALARM ->
                            // add message to list (not delete message)
                            final String propAckHidden = jmsm.getProperty("ACK_HIDDEN");
                            
                            if (!severityFromList.equalsIgnoreCase(Severity.NO_ALARM.name())
                                    && ( (propAckHidden == null) || (!Boolean
                                            .valueOf(propAckHidden)))) {
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
            final AlarmMessage message = it.next();
            _messages.add(message);
            super.addMessage(message);
        }
        jmsMessagesToRemove.clear();
        jmsMessagesToRemoveAndAdd.clear();
        return equalPreviousMessage;
    }

}
