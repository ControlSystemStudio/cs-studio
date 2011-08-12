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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Vector;

import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.AlarmMessageKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * List of JMSMessages. The Viewers (LabelProviders) of the list registers as
 * listeners.
 *
 * @author jhatje
 *
 */
public class ArchiveMessageList extends AbstractMessageList {

    private static final Logger LOG = LoggerFactory.getLogger(ArchiveMessageList.class);
    
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    protected Vector<BasicMessage> _messages = new Vector<BasicMessage>();

    /**
     * Add a new Message to the collection of Messages
     */
    @Override
    public void addMessage(@Nonnull final BasicMessage jmsm) {
        _messages.add(_messages.size(), jmsm);
        super.addMessage(jmsm);
    }

    /**
     * Add a new MessageList<HashMap> to the collection of Messages
     */
    public void addMessageList(@Nonnull final ArrayList<HashMap<String, String>> messageList) {
        final BasicMessage[] newMessages = new BasicMessage[messageList.size()];
        int i = 0;
        for (final HashMap<String, String> messageProperties : messageList) {
            final BasicMessage newMessage = new BasicMessage(messageProperties);
            _messages.add(_messages.size(), newMessage);
            newMessages[i] = newMessage;
            i++;
        }
        addMessages(newMessages);
        // TODO (jpenning) ML why is getResource called here?
        getClass().getResource("");
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

    public void deleteAllMessages() {
        _messages.clear();
    }

    /**
     * Search for the message with the latest EVENTTIME in the list.
     *
     * @return latest timestamp in message list
     */
    public GregorianCalendar getLatestMessageDate() {
        final GregorianCalendar previousMessageTime = new GregorianCalendar(1980, 2, 2);
        final GregorianCalendar currentMessageTime = new GregorianCalendar();
        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        for (final BasicMessage message : _messages) {
            final String time = message.getProperty(AlarmMessageKey.EVENTTIME.getDefiningName());
            try {
                final Date date = sdf.parse(time == null ? "" : time);
                currentMessageTime.setTime(date);
                if (currentMessageTime.compareTo(previousMessageTime) > 0) {
                    previousMessageTime.setTime(currentMessageTime.getTime());
                }
            } catch (final ParseException e) {
                LOG.warn("cannot parse date string");
            }
        }
        previousMessageTime.setTimeInMillis(previousMessageTime.getTimeInMillis() - 1000);
        return previousMessageTime;
    }
}
