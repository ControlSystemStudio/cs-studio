
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
abstract public class MessageList {

    /**
     * Listeners to update on changes.
     */
    private Set<IMessageViewer> changeListeners = new HashSet<IMessageViewer>();

    /**
     * Add a new Message to the collection of Messages
     */
    public void addMessage(BasicMessage jmsm) {
        Iterator<IMessageViewer> iterator = changeListeners.iterator();
        while (iterator.hasNext()) {
            ((IMessageViewer) iterator.next()).addJMSMessage(jmsm);
        }
    }

     /**
     * Add a new MessageList<HashMap> to the collection of Messages
     */
     public void addMessageList(BasicMessage[] messageList) {
        Iterator<IMessageViewer> iterator = changeListeners.iterator();
        while (iterator.hasNext()) {
            ((IMessageViewer) iterator.next())
                    .addJMSMessages(messageList);
        }
    }

    /**
     * Call listeners to update the message.
     */
    public void updateMessage(BasicMessage jmsm) {
        Iterator<IMessageViewer> iterator = changeListeners.iterator();
        while (iterator.hasNext())
            ((IMessageViewer) iterator.next()).updateJMSMessage(jmsm);
    }

    /**
     * Remove a message from the list.
     */
    public void removeMessage(BasicMessage jmsm) {
        Iterator<IMessageViewer> iterator = changeListeners.iterator();
        while (iterator.hasNext())
            ((IMessageViewer) iterator.next()).removeJMSMessage(jmsm);
    }

    /**
     * Remove an array of messages from the list.
     */
    public void removeMessageArray(BasicMessage[] jmsm) {
        Iterator<IMessageViewer> iterator = changeListeners.iterator();
        while (iterator.hasNext())
            ((IMessageViewer) iterator.next()).removeJMSMessage(jmsm);
    }

    public void removeChangeListener(IMessageViewer viewer) {
        changeListeners.remove(viewer);
    }

    public void addChangeListener(IMessageViewer viewer) {
        changeListeners.add(viewer);
    }

    abstract public void deleteAllMessages(BasicMessage[] messages);

    abstract public Vector<? extends BasicMessage> getJMSMessageList();


    abstract public Integer getSize();
}
