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
package org.csstudio.alarm.treeview.service;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.GuardedBy;

import org.csstudio.alarm.service.declaration.AlarmMessageKey;
import org.csstudio.alarm.service.declaration.IAlarmListener;
import org.csstudio.alarm.service.declaration.IAlarmMessage;
import org.csstudio.alarm.treeview.model.IAlarmSubtreeNode;
import org.csstudio.alarm.treeview.views.AbstractPendingUpdate;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;

/**
 * Listens for alarm messages and prepares the necessary updates to the tree in response to those
 * messages. If no alarm tree is present, updates are queued internally for later application.
 *
 * @author Joerg Rathlev
 */
public class AlarmMessageListener implements IAlarmListener {

    /**
     * The worker used by this listener.
     */
    private final QueueWorker _queueWorker;

    private final IAlarmSubtreeNode _treeRoot;

    /**
     * Applies the pending updates to the tree.
     */
    private static final class QueueWorker implements Runnable {

        /**
         * The worker thread which runs this runnable. This is set to <code>null</code> when this
         * runnable should stop.
         */
        private volatile Thread _worker;

        /**
         * The queued updates.
         */
        private final BlockingQueue<AbstractPendingUpdate> _pendingUpdates;


        /**
         * If set to <code>true</code>,
         * this worker waits until the suspension is set to <code>false</code>.
         */
        @GuardedBy("this")
        private boolean _suspension;

        /**
         * Creates a new queue worker.
         */
        QueueWorker() {
            _pendingUpdates = new LinkedBlockingQueue<AbstractPendingUpdate>();
        }

        /**
         * Takes pending updates from the queue and applies them.
         */
        @Override
        public void run() {
            final Thread thisThread = Thread.currentThread();
            while (_worker == thisThread) {
                try {
                    final AbstractPendingUpdate update = _pendingUpdates.take();

                    // synchronize access to the updater
                    synchronized (this) {
                        // wait until we have an updater
                        while (_suspension && _worker == thisThread) {
                            wait();
                        }

                        //LOG.debug("applying update: " + update);
                        update.apply();
                    }
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        /**
         * Adds a pending update to this worker's queue.
         *
         * @param update the update.
         */
        void enqueue(@Nonnull final AbstractPendingUpdate update) {
            _pendingUpdates.add(update);

            /*
             * Implementation note: This method will be called by a thread owned by the JMS
             * implementation. Pending updates are handed over to the updater via the queue and the
             * worker thread even when doing so would not be strictly necessary. If the queue is
             * empty and an updater is available, the update could be applied directly by the JMS
             * thread. However, checking whether the queue is empty and an updater is available, and
             * preventing this from changing during the execution of this method would require
             * additional synchronisation, so such an implementation would not necessarily have
             * better performance characteristics than the current implementation, but would be a
             * lot more complex.
             */
        }


        synchronized void setSuspension(final boolean suspension) {
            _suspension = suspension;
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
     * @param rootNode
     */
    public AlarmMessageListener(@Nonnull final IAlarmSubtreeNode rootNode) {
        _treeRoot = rootNode;
        _queueWorker = new QueueWorker();
        _queueWorker.start();
    }

    /**
     * Sets the updater which this listener will use.
     *
     * @param updater the updater which this listener will use.
     */
    public void startUpdateProcessing() {
        _queueWorker.setSuspension(false);
    }

    /**
     * Sets the updater which this listener will use.
     *
     * @param updater the updater which this listener will use.
     */
    public void suspendUpdateProcessing() {
        _queueWorker.setSuspension(true);
    }

    /**
     * Stops this listener. Once stopped, the listener cannot be restarted.
     */
    @Override
    public void stop() {
        _queueWorker.stop();
    }

    /**
     * Called when a message is received. The message is interpreted as an alarm message. If the
     * message contains valid information, the respective updates of the alarm tree are triggered.
     */
    @Override
    public void onMessage(@Nonnull final IAlarmMessage message) {
        //LOG.debug("received: " + message);
        processAlarmMessage(message);
    }

    /**
     * Processes an alarm message.
     */
    private void processAlarmMessage(@Nonnull final IAlarmMessage message) {
        final String name = message.getString(AlarmMessageKey.NAME);
        if (message.isAcknowledgement()) {
            //LOG.debug("received ack: name=" + name);
            _queueWorker.enqueue(AbstractPendingUpdate.createAcknowledgementUpdate(name, _treeRoot));
        } else {

            final EpicsAlarmSeverity severity = message.getSeverity();
            final Date eventtime = message.getEventtimeOrCurrentTime();
//            LOG.debug("received alarm: name=" + name + ", severity=" + severity + ", eventtime="
//                    + eventtime);
            _queueWorker.enqueue(AbstractPendingUpdate.createAlarmUpdate(name, severity, eventtime, _treeRoot));
        }
    }

}
