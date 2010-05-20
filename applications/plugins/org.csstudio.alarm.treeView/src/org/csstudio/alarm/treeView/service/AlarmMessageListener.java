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

package org.csstudio.alarm.treeView.service;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.alarm.service.declaration.AlarmMessageException;
import org.csstudio.alarm.service.declaration.IAlarmListener;
import org.csstudio.alarm.service.declaration.IAlarmMessage;
import org.csstudio.alarm.service.declaration.IAlarmMessage.Key;
import org.csstudio.alarm.treeView.EventtimeUtil;
import org.csstudio.alarm.treeView.model.Severity;
import org.csstudio.alarm.treeView.views.AlarmTreeUpdater;
import org.csstudio.alarm.treeView.views.PendingUpdate;
import org.csstudio.platform.logging.CentralLogger;

/**
 * Listens for alarm messages and prepares the necessary updates to the tree in response to those
 * messages. If no alarm tree is present, updates are queued internally for later application.
 *
 * @author Joerg Rathlev
 */
public class AlarmMessageListener implements IAlarmListener {

    /**
     * The logger used by this listener.
     */
    private static final Logger LOG = CentralLogger.getInstance().getLogger(AlarmMessageListener.class);

    /**
     * The worker used by this listener.
     */
    private final QueueWorker _queueWorker;

    /**
     * Applies the pending updates to the tree.
     */
    private final class QueueWorker implements Runnable {

        /**
         * The worker thread which runs this runnable. This is set to <code>null</code> when this
         * runnable should stop.
         */
        private volatile Thread _worker;

        /**
         * The queued updates.
         */
        private final BlockingQueue<PendingUpdate> _pendingUpdates;

        /**
         * The alarm tree updater which will be used by this worker. If set to <code>null</code>,
         * this worker waits until an updater is set.
         */
        private AlarmTreeUpdater _updater;

        /**
         * Creates a new queue worker.
         */
        QueueWorker() {
            _pendingUpdates = new LinkedBlockingQueue<PendingUpdate>();
        }

        /**
         * Takes pending updates from the queue and applies them.
         */
        public void run() {
            final Thread thisThread = Thread.currentThread();
            while (_worker == thisThread) {
                try {
                    final PendingUpdate update = _pendingUpdates.take();

                    // synchronize access to the updater
                    synchronized (this) {
                        // wait until we have an updater
                        while ( (_updater == null) && (_worker == thisThread)) {
                            wait();
                        }

                        LOG.debug("applying update: " + update);
                        update.apply(_updater);
                        // TODO (bknerr) : the other way around...
                        // _updater.apply(update);
                    }
                } catch (final InterruptedException e) {
                    // nothing to do
                }
            }
        }

        /**
         * Adds a pending update to this worker's queue.
         *
         * @param update the update.
         */
        void enqueue(final PendingUpdate update) {
            _pendingUpdates.add(update);

            /*
             * Implementation note: This method will be called by a thread owned by the JMS
             * implementation. Pending updates are handed over to the updater via the queue and the
             * worker thread even when doing so would not be strictly necessary. If the queue is
             * empty and an updater is available, the update could be applied directly by the JMS
             * thread. However, checking whether the queue is empty and an updater is available, and
             * preventing this from changing during the execution of this method would require
             * additional synchronization, so such an implementation would not necessarily have
             * better performance characteristics than the current implementation, but would be a
             * lot more complex.
             */
        }

        /**
         * Sets the updater that will be used by this worker. If the updater is set to
         * <code>null</code>, this worker will suspend until an updater is set.
         *
         * @param updater the updater.
         */
        synchronized void setUpdater(final AlarmTreeUpdater updater) {
            _updater = updater;
            notify();
        }

        /**
         * Starts this worker.
         */
        void start() {
            _worker = new Thread(this, "Alarm Tree Queue Worker");
            _worker.start();
        }

        /**
         * Stops this worker.
         */
        void stop() {
            final Thread t = _worker;
            _worker = null;
            t.interrupt();
        }
    }

    /**
     * Creates a new alarm message listener.
     */
    public AlarmMessageListener() {
        _queueWorker = new QueueWorker();
        _queueWorker.start();
    }

    /**
     * Sets the updater which this listener will use.
     *
     * @param updater the updater which this listener will use.
     */
    public void setUpdater(final AlarmTreeUpdater updater) {
        _queueWorker.setUpdater(updater);
    }

    /**
     * Stops this listener. Once stopped, the listener cannot be restarted.
     */
    public void stop() {
        _queueWorker.stop();
    }

    /**
     * Called when a message is received. The message is interpreted as an alarm message. If the
     * message contains valid information, the respective updates of the alarm tree are triggered.
     */
    public void onMessage(@Nonnull final IAlarmMessage message) {
        LOG.debug("received: " + message);
        processAlarmMessage(message);
    }

    /**
     * Processes an alarm message.
     */
    private void processAlarmMessage(@Nonnull final IAlarmMessage message) {
        final String name = message.getString(Key.NAME);
        if (isAcknowledgement(message)) {
            LOG.debug("received ack: name=" + name);
            _queueWorker.enqueue(PendingUpdate.createAcknowledgementUpdate(name));
        } else {
            final String severityValue = message.getString(Key.SEVERITY);
            if (severityValue == null) {
                LOG.warn("Received alarm message which did not contain " + Key.SEVERITY.name() +  "! Message ignored. Message was: "
                         + message);
                return;
            }
            final Severity severity = Severity.parseSeverity(severityValue);
            final String eventtimeValue = message.getString(Key.EVENTTIME);
            Date eventtime = null;
            if (eventtimeValue != null) {
                eventtime = EventtimeUtil.parseTimestamp(eventtimeValue);
            }
            if (eventtime == null) {
                // eventtime is null if the message did not contain an EVENTTIME
                // field or if the EVENTTIME could not be parsed.
                LOG.warn("Received alarm message which did not contain a valid " + Key.EVENTTIME.name() + ", using current time instead. Message was: "
                                      + message);
                eventtime = new Date();
            }
            LOG.debug("received alarm: name=" + name + ", severity=" + severity + ", eventtime=" + eventtime);
            _queueWorker.enqueue(PendingUpdate.createAlarmUpdate(name, severity, eventtime));
        }
    }

    /**
     * Returns whether the given message is an alarm acknowledgement.
     */
    private boolean isAcknowledgement(final IAlarmMessage message) {
        String ack = null;
        try {
            ack = message.getString("ACK");
        } catch (final AlarmMessageException e) {
            // ok, ack will be null
            return false;
        }
        return (ack != null) && ack.equals("TRUE");
    }

    public void registerAlarmListener(final IAlarmListener alarmListener) {
        // Nothing to do
    }

    public void deRegisterAlarmListener(final IAlarmListener alarmListener) {
        // Nothing to do
    }

}
