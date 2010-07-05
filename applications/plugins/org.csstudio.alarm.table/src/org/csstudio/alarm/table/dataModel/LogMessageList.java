/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. WITHOUT WARRANTY OF ANY
 * KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, THE USER ASSUMES
 * THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY
 * CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER
 * EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
 * MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY AT
 * HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.alarm.table.dataModel;

import java.util.Vector;

import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.alarm.service.declaration.AlarmMessageKey;

public class LogMessageList extends MessageList {

    private static final Logger LOG = Logger.getLogger(LogMessageList.class);

    protected Vector<BasicMessage> _messages = new Vector<BasicMessage>();

    private final Integer _maximumNumberOfMessages;

    public LogMessageList(final Integer maximumNumberOfMessages) {
        _maximumNumberOfMessages = maximumNumberOfMessages;
    }

    /**
     * Add a new Message to the collection of Messages
     *
     */
    @Override
    synchronized public void addMessage(@Nonnull final BasicMessage newMessage) {

        // for debugging
        BasicMessage tempMsg = null;
        final String newAckProp = newMessage.getProperty(AlarmMessageKey.ACK.getDefiningName());
        final String newNameProp = newMessage.getProperty(AlarmMessageKey.NAME.getDefiningName());
        final String newTimeProp = newMessage.getProperty(AlarmMessageKey.EVENTTIME.getDefiningName());
        try {
            if ((newAckProp != null) && Boolean.valueOf(newAckProp)) {
                LOG.debug("received acknowledge message");
                for (final BasicMessage message : _messages) {
                    // for debugging
                    tempMsg = message;
                    final String timeProp = message.getProperty(AlarmMessageKey.EVENTTIME.getDefiningName());
                    final String nameProp = message.getName();
                    if ((nameProp != null) && nameProp.equals(newNameProp) &&
                        timeProp.equals(newTimeProp)) {

                        message.getHashMap().put(AlarmMessageKey.ACK.getDefiningName(), Boolean.TRUE.toString());
                        super.updateMessage(newMessage);
                        break;
                    }
                }
            } else {
                limitMessageListSize();
                _messages.add(_messages.size(), newMessage);
                super.addMessage(newMessage);
            }
        } catch (final NullPointerException e) {
            LOG.debug("Null Pointer Excetion in add message "
                                                      + e.getMessage());
            if (newAckProp != null) {
                LOG.debug("ACK property of new received message: "
                          + newAckProp);
            } else {
                LOG.debug("ACK property of new received message is not set");
            }
            if (newNameProp != null) {
                LOG.debug("NAME property of new received message: "
                          + newNameProp);
            } else {
                LOG.debug("NAME property of new received message is not set");
            }
            if (newTimeProp != null) {
                LOG.debug("EVENTTIME property of new received message: "
                          + newTimeProp);
            } else {
                LOG.debug("EVENTTIME property of new received message is not set");
            }
            if (tempMsg != null) {
                if (tempMsg.getProperty(AlarmMessageKey.ACK.getDefiningName()) != null) {
                    LOG.debug("ACK property of current table message: "
                                                              + tempMsg.getProperty(AlarmMessageKey.ACK.getDefiningName()));
                } else {
                    LOG.debug("ACK property of current table message is not set");
                }
                if (tempMsg.getProperty(AlarmMessageKey.NAME.getDefiningName()) != null) {
                    LOG.debug("NAME property of current table message: "
                                                              + tempMsg.getProperty(AlarmMessageKey.NAME.getDefiningName()));
                } else {
                    LOG.debug("NAME property of current table message is not set");
                }
                if (tempMsg.getProperty(AlarmMessageKey.EVENTTIME.getDefiningName()) != null) {
                    LOG.debug("EVENTTIME property of current table message: "
                                                              + tempMsg.getProperty(AlarmMessageKey.EVENTTIME.getDefiningName()));
                } else {
                    LOG.debug("EVENTTIME property of current table message is not set");
                }
            } else {
                LOG.debug("current table message is NULL");
            }
        }
    }

    /**
     * Remove a message from the list.
     */
    @Override
    public void removeMessage(final BasicMessage jmsm) {
        _messages.remove(jmsm);
        super.removeMessage(jmsm);
    }

    /**
     * Remove an array of messages from the list.
     */
    @Override
    public void removeMessageArray(final BasicMessage[] jmsm) {
        for (final BasicMessage message : jmsm) {
            _messages.remove(message);
        }
        super.removeMessageArray(jmsm);
    }

    @Override
    public Vector<? extends BasicMessage> getJMSMessageList() {
        return _messages;
    }

    @Override
    public void deleteAllMessages(final BasicMessage[] messages) {
        removeMessageArray(messages);
    }

    /**
     * If message list size is bigger than in the preferences defined delete oldest messages
     */
    private void limitMessageListSize() {
        while (_messages.size() > _maximumNumberOfMessages) {
            if (_messages.get(0) != null) {
                removeMessage(_messages.get(0));
            }
        }
    }

    @Override
    public Integer getSize() {
        return _messages.size();
    }
}
