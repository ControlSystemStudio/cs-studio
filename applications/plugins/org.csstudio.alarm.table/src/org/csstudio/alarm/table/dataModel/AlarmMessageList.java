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
import java.util.List;
import java.util.Vector;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * List of alarm messages for alarm table. The class includes also the logic in
 * which case a message should be deleted, highlighted etc.
 *
 * @author jhatje
 *
 */
public class AlarmMessageList extends AbstractMessageList {


    private static final Logger LOG = LoggerFactory.getLogger(AlarmMessageList.class);
    
    private final Vector<AlarmMessage> _messages = new Vector<AlarmMessage>();

    /** number of alarm status changes in the message with the same pv name. */
    private int alarmStatusChanges = 0;

    private boolean _showOutdatedMessages = true;

    public AlarmMessageList() {
        // nothing to do
    }

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
            if (Boolean.valueOf(propAck)) {
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
            if ( propType != null && propType.equalsIgnoreCase("status")) {
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
            if (deleteOrGrayOutEqualMessages(newMessage)
                    || ! propSev.equalsIgnoreCase(EpicsAlarmSeverity.NO_ALARM.name())) { //$NON-NLS-1$
                if (msgExists) {
                    newMessage.setProperty("COUNT", String.valueOf(alarmStatusChanges + 1));
                } else {
                    newMessage.setProperty("COUNT", "0");
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
            if ( currentInList != null && currentInList.equalsIgnoreCase(currentMessage)) {
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

    @Override
    public boolean canHandleOutdatedMessaged() {
        return true;
    }

    @Override
    public void showOutdatedMessages(final boolean show) {
        _showOutdatedMessages = show;
    }

    /**
     * Check if the new message is valid alarm message
     * NAME and SEVERITY have to exist
     * TYPE may be omitted if there is prop ACK==TRUE
     * (TODO (bknerr) refactor to be a type value 'TYPE==ACK'
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
        final Vector<AlarmMessage> messagesToRemove = new Vector<AlarmMessage>();

        final String newNameProp = newMessage.getProperty(NAME.getDefiningName());
        final String newTimeProp = newMessage.getProperty(EVENTTIME.getDefiningName());
        LOG.debug("AlarmView Ack message received, MsgName: {} MsgTime: {}", newNameProp, newTimeProp);

        for (final AlarmMessage message : _messages) {
            final String sevProp = message.getProperty(SEVERITY.getDefiningName());
            final String newSevProp = newMessage.getProperty(SEVERITY.getDefiningName());
            final String nameProp = message.getName();
            if ( nameProp != null && nameProp.equals(newNameProp) && sevProp.equals(newSevProp)) {
                final String sevKeyProp = message.getProperty("SEVERITY_KEY");
                if ( sevKeyProp != null
                        && (message.isOutdated()
                                || sevKeyProp.equalsIgnoreCase(EpicsAlarmSeverity.NO_ALARM.name()) || sevKeyProp
                                .equalsIgnoreCase(EpicsAlarmSeverity.INVALID.name()))) {

                    messagesToRemove.add(message);
                    LOG.debug("add message, removelist size: {}", messagesToRemove.size());
                } else {
                    message.getHashMap().put("ACK_HIDDEN", Boolean.TRUE.toString());
                    message.setProperty(ACK.getDefiningName(), Boolean.TRUE.toString());
                    message.setAcknowledged(true);
                    return message;
                }
                break;
            }
        }
        for (final BasicMessage message : messagesToRemove) {
            removeMessage(message);
        }
        messagesToRemove.clear();
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
            if ( currentName != null && currentName.equalsIgnoreCase(name)
                    && !message.isOutdated()) {
                messageInTable = true;
                calculateAlarmChangeCount(message);
                break;
            }
        }
        return messageInTable;
    }

    private void calculateAlarmChangeCount(@Nonnull final AlarmMessage message) {
        final String alarmChangeCount = message.getProperty("COUNT");
        try {
            alarmStatusChanges = Integer.parseInt(alarmChangeCount);
        } catch (final NumberFormatException e) {
            alarmStatusChanges = 0;
        }
    }

    /**
     * Delete previous messages from the same pv and with the same severity Mark
     * messages from the same pv and with a different severity that the label
     * provider can set a brighter color. Test if the EVENTTIME of the new
     * message is really newer than an existing message. (It is important to use
     * the <code>removeMessage</code> method from <code>MessageList</code> that
     * the changeListeners on the model were updated.)
     *
     * @param newMessage the new message
     * @return true, if there is a previous message in the list with the same pv name
     */
    private boolean deleteOrGrayOutEqualMessages(@Nonnull final AlarmMessage newMessage) {

        final String newPVName = newMessage.getProperty(NAME.getDefiningName());
        final String newSeverity = newMessage.getProperty(SEVERITY.getDefiningName());
        // guard: only a newMessage with name and severity is processed
        if (!canProcessMessage(newPVName, newSeverity)) {
            return false;
        }

        boolean equalPreviousMessage = false;
        final List<AlarmMessage> messagesToRemove = new ArrayList<AlarmMessage>();
        final List<AlarmMessage> messagesToAdd = new ArrayList<AlarmMessage>();

        for (final AlarmMessage message : _messages) {
            final String pvNameFromList = message.getProperty(NAME.getDefiningName());
            // TODO (jpenning) ML check with jh: why do we access this special key?
            // the 'real' severity in map message we get from the JMSMessage via SEVERITY_KEY
            final String severityFromList = message.getProperty("SEVERITY_KEY"); //$NON-NLS-1$
            if (canProcessMessage(pvNameFromList, severityFromList)) {

                // is there a previous alarm message from same pv?
                if (newPVName.equalsIgnoreCase(pvNameFromList)) {
                    equalPreviousMessage = true;
                    messagesToRemove.add(message);

                    if (isMessageOutdated(message, newSeverity, severityFromList)) {

                        message.setOutdated(true);
                        if (_showOutdatedMessages) {
                            messagesToAdd.add(message);
                        }
                    }
                }
            }
        }

        // clean up
        addAndRemoveCollectedMessages(messagesToRemove, messagesToAdd);

        return equalPreviousMessage;
    }

    private boolean canProcessMessage(@Nonnull final String pvName, @Nonnull final String severity) {
        return pvName != null && severity != null;
    }

    private boolean isMessageOutdated(@Nonnull final AlarmMessage message,
                                      @Nonnull final String newSeverity,
                                      @Nonnull final String severityFromList) {
        return !message.isOutdated() && isNotEqualSeverity(newSeverity, severityFromList)
                && isNotNoAlarm(severityFromList) && isNotHiddenAck(message);
    }

    private boolean isNotEqualSeverity(@Nonnull final String newSeverity,
                                       @Nonnull final String severityFromList) {
        return !newSeverity.equalsIgnoreCase(severityFromList);
    }

    private boolean isNotNoAlarm(@Nonnull final String severityFromList) {
        return !severityFromList.equalsIgnoreCase(EpicsAlarmSeverity.NO_ALARM.name());
    }

    private boolean isNotHiddenAck(@Nonnull final AlarmMessage message) {
        return !Boolean.valueOf(message.getProperty("ACK_HIDDEN"));
    }

    private void addAndRemoveCollectedMessages(@Nonnull final List<AlarmMessage> messagesToRemove,
                                               @Nonnull final List<AlarmMessage> messagesToAdd) {
        for (final AlarmMessage message : messagesToRemove) {
            removeMessage(message);
        }
        for (final AlarmMessage message : messagesToAdd) {
            _messages.add(message);
            super.addMessage(message);
        }
    }

}
