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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 * List of JMSMessages. The Viewers (LabelProviders) of the list registers as
 * listeners.
 * 
 * @author jhatje
 * 
 */
public class ArchiveMessageList extends MessageList {

    protected Vector<BasicMessage> _messages = new Vector<BasicMessage>();

    // /**
    // * Initial property names of the table columns.
    // */
    // String[] propertyNames;

    // public ArchiveMessageList(String[] propNames) {
    // propertyNames = propNames;
    // }

    /**
     * Add a new Message to the collection of Messages
     */
    public void addMessage(BasicMessage jmsm) {
        if (jmsm == null) {
            return;
        } else {
            _messages.add(_messages.size(), jmsm);
            super.addMessage(jmsm);
        }
    }

    /**
     * Add a new MessageList<HashMap> to the collection of Messages
     */
    public void addMessageList(ArrayList<HashMap<String, String>> messageList) {
        BasicMessage[] listOfNewMessages = new BasicMessage[messageList.size()];
        int i = 0;
        for (HashMap<String, String> messageProperties : messageList) {
            BasicMessage newMessage = new BasicMessage(messageProperties);
            _messages.add(_messages.size(), newMessage);
            listOfNewMessages[i] = newMessage;
            i++;
        }
        super.addMessageList(listOfNewMessages);
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

    public void clearList() {
        _messages.clear();
    }
}
