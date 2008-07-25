package org.csstudio.alarm.table;

import java.util.Vector;

import org.csstudio.alarm.table.dataModel.JMSMessage;
import org.csstudio.alarm.table.dataModel.JMSMessageList;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

public class RemoveAcknowledgedMessagesTask extends Job {

	private JMSMessageList _messagesInTable;
	private Vector<JMSMessage> _jmsMessagesToRemove;
	private long _lastRemovedMessageInMillisec;
	private long _closeThresholdInMillisec = 5 * 1000;
	private boolean _isCanceled = false;

	public RemoveAcknowledgedMessagesTask(JMSMessageList jmsml,
			Vector<JMSMessage> jmsMessagesToRemove) {
		super("RemoveAckMessages");
		_messagesInTable = jmsml;
		_jmsMessagesToRemove = jmsMessagesToRemove;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		_lastRemovedMessageInMillisec = System.currentTimeMillis();
		while ((System.currentTimeMillis() - _lastRemovedMessageInMillisec) < _closeThresholdInMillisec) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			CentralLogger.getInstance().debug(
					this,
					"timer task run method, removelist size: "
							+ _jmsMessagesToRemove.size());
			if (_jmsMessagesToRemove.size() > 0) {
				System.out.println("remove Messa size: "
						+ _jmsMessagesToRemove.size());
				removeMessages();
				_lastRemovedMessageInMillisec = System.currentTimeMillis();
				// } else {
				// if ((System.currentTimeMillis() -
				// _lastRemovedMessageInMillisec) >
				// _closeThresholdInMillisec) {
				// this.cancel();
				// _isCanceled = true;
				// }
			}
		}
		return Status.OK_STATUS;
	}

	private void removeMessages() {
		CentralLogger.getInstance().debug(
				this,
				"removeMessage started, removelist size: "
						+ _jmsMessagesToRemove.size());

		final JMSMessage[] a = _jmsMessagesToRemove.toArray(new JMSMessage[0]);

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					_messagesInTable.removeJMSMessage(a);
				} catch (Exception e) {
					e.printStackTrace();
					JmsLogsPlugin.logException("", e); //$NON-NLS-1$
				}
			}
		});

		for (JMSMessage message : a) {
			_jmsMessagesToRemove.remove(message);
		}
		// jmsMessagesToRemove.clear();
		CentralLogger.getInstance().debug(
				this,
				"removeMessage ended, removelist size: "
						+ _jmsMessagesToRemove.size());

	}

	public boolean is_isCanceled() {
		return _isCanceled;
	}

	public void set_isCanceled(boolean canceled) {
		_isCanceled = canceled;
	}

}
