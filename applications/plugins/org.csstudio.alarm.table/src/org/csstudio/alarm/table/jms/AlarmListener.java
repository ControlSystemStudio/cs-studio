/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY. THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN
 * "../AS IS" BASIS. WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN
 * ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS
 * DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS
 * AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE,
 * SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE
 * THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
 * DISTRIBUTION OF THIS PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY
 * FIND A COPY AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.alarm.table.jms;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.IAlarmListener;
import org.csstudio.alarm.service.declaration.IAlarmMessage;
import org.csstudio.alarm.table.dataModel.AbstractMessageList;
import org.csstudio.alarm.table.dataModel.BasicMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listens for alarm messages and adds incoming messages to the internally hold message list.
 * Clients may register further listeners to provide view-based operations, e.g. sound.
 *
 * @author Joerg Penning
 */
public class AlarmListener implements IAlarmTableListener {

    private static final Logger LOG = LoggerFactory.getLogger(AlarmListener.class);

    /**
     * This is the destination for the messages
     */
    private AbstractMessageList _messageList;

    /**
     * Registered listeners will be notified when a message comes in
     */
    private final List<IAlarmListener> _listeners = new ArrayList<IAlarmListener>();

    /**
     * Creates a new alarm message listener.
     */
    public AlarmListener() {
        // Nothing to do
    }

    /**
     * Stops this listener. Once stopped, the listener cannot be restarted.
     */
    @Override
    public void stop() {
        // Nothing to do
    }

    /**
     * Called when a message is received. The message is interpreted as an alarm message. If the
     * message contains valid information, the respective updates of the alarm tree are triggered.
     */
    @Override
    public void onMessage(@CheckForNull final IAlarmMessage message) {
//        LOG.debug("received: " + message);
        if (message == null) {
            LOG.error("Error processing message (was null)");
        } else {
            processAlarmMessage(message);
            callListeners(message);
        }
    }

    private void callListeners(@Nonnull final IAlarmMessage message) {
        for (IAlarmListener listener : _listeners) {
            listener.onMessage(message);
        }
    }

    private void processAlarmMessage(@Nonnull final IAlarmMessage message) {
        _messageList.addMessage(new BasicMessage(message.getMap()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setMessageList(@Nonnull final AbstractMessageList messageList) {
        _messageList = messageList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void registerAlarmListener(@Nonnull final IAlarmListener alarmListener) {
        _listeners.add(alarmListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void deRegisterAlarmListener(@Nonnull final IAlarmListener alarmListener) {
        _listeners.remove(alarmListener);
    }

}
