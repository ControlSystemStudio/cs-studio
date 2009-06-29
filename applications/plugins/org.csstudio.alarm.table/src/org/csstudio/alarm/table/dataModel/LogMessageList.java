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

import java.util.Iterator;
import java.util.Vector;

import org.csstudio.platform.logging.CentralLogger;

public class LogMessageList extends MessageList {

    protected Vector<BasicMessage> _messages = new Vector<BasicMessage>();

    private final Integer _maximumNumberOfMessages;

    public LogMessageList(Integer maximumNumberOfMessages) {
        // super(propNames);
        _maximumNumberOfMessages = maximumNumberOfMessages;
    }

    /**
     * Add a new Message to the collection of Messages
     * 
     */
    synchronized public void addMessage(BasicMessage newMessage) {
        if (newMessage != null) {
            if (newMessage.getProperty("ACK") != null
                    && newMessage.getProperty("ACK").toUpperCase().equals(
                            "TRUE")) {
                CentralLogger.getInstance().debug(this,
                        "received acknowledge message");
                for (BasicMessage message : _messages) {
                    if (message.getName()
                            .equals(newMessage.getProperty("NAME"))
                            && message.getProperty("EVENTTIME").equals(
                                    newMessage.getProperty("EVENTTIME"))) {
                        message.getHashMap().put("ACK", "TRUE"); //$NON-NLS-1$ //$NON-NLS-2$
                        super.updateMessage(newMessage);
                        break;
                    }
                }
            } else {
                limitMessageListSize();
                _messages.add(_messages.size(), newMessage);
                super.addMessage(newMessage);
            }
        }
    }

    /**
     * Remove a message from the list.
     */
    public void removeMessage(BasicMessage jmsm) {
        _messages.remove(jmsm);
        super.removeMessage(jmsm);
    }

    /**
     * Remove an array of messages from the list.
     */
    public void removeMessageArray(BasicMessage[] jmsm) {
        for (BasicMessage message : jmsm) {
            _messages.remove(message);
        }
        super.removeMessageArray(jmsm);
    }

    public Vector<? extends BasicMessage> getJMSMessageList() {
        return _messages;
    }

    public void deleteAllMessages(BasicMessage[] messages) {
        removeMessageArray(messages);
    }

    /**
     * If message list size is bigger than in the preferences defined delete
     * oldest messages
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
