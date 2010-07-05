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

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TimeZone;
import java.util.Vector;

import javax.annotation.Nonnull;

/**
 * List of JMSMessages. The Viewers (LabelProviders) of the list registers as listeners.
 *
 * @author jhatje
 *
 */
abstract public class MessageList {

    /**
     * Listeners to update on changes.
     */
    private final Set<IMessageViewer> changeListeners = new HashSet<IMessageViewer>();

    /**
     * Time when the list is started.
     */
    private final Date _startTime;

    public MessageList() {
        _startTime = (new GregorianCalendar(TimeZone.getTimeZone("ECT"))).getTime();
    }

    /**
     * Add a new Message to the collection of change listeners
     */
    public void addMessage(@Nonnull final BasicMessage jmsm) {
        final Iterator<IMessageViewer> iterator = changeListeners.iterator();
        while (iterator.hasNext()) {
            (iterator.next()).addJMSMessage(jmsm);
        }
    }

    /**
     * Add a new MessageList<HashMap> to the collection of change listeners
     */
    public void addMessageList(final BasicMessage[] messageList) {
        final Iterator<IMessageViewer> iterator = changeListeners.iterator();
        while (iterator.hasNext()) {
            (iterator.next()).addJMSMessages(messageList);
        }
    }

    /**
     * Call listeners to update the message.
     */
    public void updateMessage(final BasicMessage jmsm) {
        final Iterator<IMessageViewer> iterator = changeListeners.iterator();
        while (iterator.hasNext()) {
            (iterator.next()).updateJMSMessage(jmsm);
        }
    }

    /**
     * Remove a message from the list.
     */
    public void removeMessage(final BasicMessage jmsm) {
        final Iterator<IMessageViewer> iterator = changeListeners.iterator();
        while (iterator.hasNext()) {
            (iterator.next()).removeJMSMessage(jmsm);
        }
    }

    /**
     * Remove an array of messages from the list.
     */
    public void removeMessageArray(final BasicMessage[] jmsm) {
        final Iterator<IMessageViewer> iterator = changeListeners.iterator();
        while (iterator.hasNext()) {
            (iterator.next()).removeJMSMessage(jmsm);
        }
    }

    public void removeChangeListener(final IMessageViewer viewer) {
        changeListeners.remove(viewer);
    }

    public void addChangeListener(final IMessageViewer viewer) {
        changeListeners.add(viewer);
    }

    abstract public void deleteAllMessages(BasicMessage[] messages);

    abstract public Vector<? extends BasicMessage> getJMSMessageList();

    abstract public Integer getSize();

    public Date getStartTime() {
        return _startTime;
    }

}
