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

package org.csstudio.alarm.treeView.jms;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.csstudio.alarm.treeView.model.Severity;
import org.csstudio.platform.logging.CentralLogger;

/**
 * Listens for alarm messages and prepares the necessary updates to the tree in
 * response to those messages. If no alarm tree is present, updates are queued
 * internally for later application.
 * 
 * @author Joerg Rathlev
 */
final class AlarmMessageListener implements MessageListener {
	
	/**
	 * The logger used by this listener.
	 */
	private final CentralLogger _log = CentralLogger.getInstance();
	
	/**
	 * The worker used by this listener.
	 */
	private QueueWorker _worker;
	
	/**
	 * Applies the pending updates to the tree.
	 */
	private final class QueueWorker implements Runnable {

		/**
		 * The worker thread which runs this runnable. This is set to
		 * <code>null</code> when this runnable should stop.
		 */
		private volatile Thread _worker;
		
		/**
		 * The queued updates.
		 */
		private final BlockingQueue<PendingUpdate> _pendingUpdates;
		
		/**
		 * The alarm tree updater which will be used by this worker. If set to
		 * <code>null</code>, this worker waits until an updater is set.
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
			Thread thisThread = Thread.currentThread();
			while (_worker == thisThread) {
				try {
					PendingUpdate update = _pendingUpdates.take();
					
					// synchronize access to the updater
					synchronized (this) {
						// wait until we have an updater
						while (_updater == null && _worker == thisThread) {
							wait();
						}
						
						_log.debug(this, "applying update: " + update);
						update.apply(_updater);
					}
				} catch (InterruptedException e) {
					// nothing to do
				}
			}
		}

		/**
		 * Adds a pending update to this worker's queue.
		 * 
		 * @param update
		 *            the update.
		 */
		void enqueue(final PendingUpdate update) {
			_pendingUpdates.add(update);
			
			/* Implementation note: This method will be called by a thread owned
			 * by the JMS implementation. Pending updates are handed over to the
			 * updater via the queue and the worker thread even when doing so
			 * would not be strictly necessary. If the queue is empty and an
			 * updater is available, the update could be applied directly by the
			 * JMS thread. However, checking whether the queue is empty and an
			 * updater is available, and preventing this from changing during
			 * the execution of this method would require additional
			 * synchronization, so such an implementation would not necessarily
			 * have better performance characteristics than the current
			 * implementation, but would be a lot more complex.
			 */
		}

		/**
		 * Sets the updater that will be used by this worker. If the updater is
		 * set to <code>null</code>, this worker will suspend until an updater
		 * is set.
		 * 
		 * @param updater
		 *            the updater.
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
			Thread t = _worker;
			_worker = null;
			t.interrupt();
		}
	}

	/**
	 * Creates a new alarm message listener.
	 */
	AlarmMessageListener() {
		_worker = new QueueWorker();
		_worker.start();
	}

	/**
	 * Sets the updater which this listener will use.
	 * 
	 * @param updater
	 *            the updater which this listener will use.
	 */
	void setUpdater(final AlarmTreeUpdater updater) {
		_worker.setUpdater(updater);
	}
	
	/**
	 * Stops this listener. Once stopped, the listener cannot be restarted.
	 */
	void stop() {
		_worker.stop();
	}

	/**
	 * Called when a JMS message is received. The message is interpreted as an
	 * alarm message. If the message contains valid information, the respective
	 * updates of the alarm tree are triggered.
	 * 
	 * @param message
	 *            the JMS message.
	 */
	public void onMessage(final Message message) {
		_log.debug(this, "received: " + message);
		if (message instanceof MapMessage) {
			try {
				processAlarmMessage((MapMessage) message);
			} catch (JMSException e) {
				_log.error(this, "error processing JMS message", e);
			}
		} else {
			_log.warn(this,
					"received message which is not a MapMessage: " + message);
		}
	}

	/**
	 * Processes an alarm message.
	 * 
	 * @param message
	 *            the alarm message.
	 * @throws JMSException
	 *             if a JMS error occurs.
	 */
	private void processAlarmMessage(final MapMessage message)
			throws JMSException {
		String name = message.getString("NAME");
		if (isAcknowledgement(message)) {
			_log.info(this, "received ack: name=" + name);
			_worker.enqueue(PendingUpdate.createAcknowledgementUpdate(name));
		} else {
			Severity severity = Severity.parseSeverity(message.getString("SEVERITY"));
			_log.info(this, "received alarm: name=" + name + ", severity=" + severity);
			_worker.enqueue(PendingUpdate.createAlarmUpdate(name, severity));
		}
	}

	/**
	 * Returns whether the given message is an alarm acknowledgement.
	 * 
	 * @param message
	 *            the message.
	 * @return <code>true</code> if the message is an alarm acknowledgement,
	 *         <code>false</code> otherwise.
	 */
	private boolean isAcknowledgement(final MapMessage message) {
		try {
			String ack = message.getString("ACK");
			return ack != null && ack.equals("TRUE");
		} catch (JMSException e) {
			return false;
		}
	}

}
