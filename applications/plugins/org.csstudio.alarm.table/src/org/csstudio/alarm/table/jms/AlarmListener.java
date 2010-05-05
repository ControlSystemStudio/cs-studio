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

import org.csstudio.alarm.service.declaration.AlarmMessageException;
import org.csstudio.alarm.service.declaration.IAlarmListener;
import org.csstudio.alarm.service.declaration.IAlarmMessage;
import org.csstudio.alarm.table.dataModel.BasicMessage;
import org.csstudio.alarm.table.dataModel.MessageList;
import org.csstudio.platform.logging.CentralLogger;

/**
 * Listens for alarm messages and adds incoming messages to the internally hold message list.
 * Clients may register further listeners to provide view-based operations, e.g. sound.
 * 
 * @author Joerg Penning
 */
public class AlarmListener implements IAlarmTableListener {
    
    /**
     * The logger used by this listener.
     */
    private final CentralLogger _log = CentralLogger.getInstance();
    
    /**
     * This is the destination for the messages
     */
    private MessageList _messageList;
    
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
    public void stop() {
        // Nothing to do
    }
    
    /**
     * Called when a message is received. The message is interpreted as an alarm message. If the
     * message contains valid information, the respective updates of the alarm tree are triggered.
     */
    public void onMessage(final IAlarmMessage message) {
        _log.debug(this, "received: " + message);
        try {
            // TODO jp Kann hier null kommen?
            if (message == null) {
                _log.error(this, "Error processing message (was null)");
            } else {
                processAlarmMessage(message);
                callListeners(message);
            }
        } catch (AlarmMessageException e) {
            _log.error(this, "Error processing message", e);
        }
    }
    
    private void callListeners(final IAlarmMessage message) {
        for (IAlarmListener listener : _listeners) {
            listener.onMessage(message);
        }
    }
    
    private void processAlarmMessage(final IAlarmMessage message) throws AlarmMessageException {
        _log.debug(this, "Received map message: EVENTTIME: " + message.getString("EVENTTIME")
                + " NAME: " + message.getString("NAME") + " ACK: " + message.getString("ACK"));
        _messageList.addMessage(new BasicMessage(message.getMap()));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setMessageList(final MessageList messageList) {
        _messageList = messageList;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void registerAlarmListener(final IAlarmListener alarmListener) {
        _listeners.add(alarmListener);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void deRegisterAlarmListener(final IAlarmListener alarmListener) {
        _listeners.remove(alarmListener);
    }
    
}
