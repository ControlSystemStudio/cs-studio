package org.csstudio.alarm.table;

import java.util.TimerTask;
import java.util.Vector;

import org.csstudio.alarm.table.dataModel.JMSMessage;
import org.csstudio.alarm.table.dataModel.JMSMessageList;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.swt.widgets.Display;

public class RemoveAcknowledgedMessagesTask extends TimerTask {

	private JMSMessageList _messagesInTable;
	private Vector<JMSMessage> _jmsMessagesToRemove;
	private long _lastRemovedMessageInMillisec;
	private long _closeThresholdInMillisec = 2 * 1000;
	private boolean _isCanceled = false;


	public RemoveAcknowledgedMessagesTask(JMSMessageList jmsml, Vector<JMSMessage> jmsMessagesToRemove) {
		_messagesInTable = jmsml;
		_jmsMessagesToRemove = jmsMessagesToRemove;
	}
	
	@Override
	public void run() {
				CentralLogger.getInstance().debug(this, "timer task run method, removelist size: " + _jmsMessagesToRemove.size());
				if(_jmsMessagesToRemove.size() > 0) {
//					System.out.println("remove Messa size: " + _jmsMessagesToRemove.size());
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
					removeMessages();
						}
						});
					_lastRemovedMessageInMillisec = System.currentTimeMillis();
				} else {
					if ((System.currentTimeMillis() - _lastRemovedMessageInMillisec) >
					_closeThresholdInMillisec) {
						this.cancel();
						_isCanceled  = true;
					}
				}
			}
		

	private void removeMessages() {
		CentralLogger.getInstance().debug(this, "XXXXXXXXXXXXX in removeMessage start, removelist size: " + _jmsMessagesToRemove.size());
		JMSMessage[] a = new JMSMessage[1];
		a = _jmsMessagesToRemove.toArray(a);
		_messagesInTable.removeJMSMessage(a);
		for (JMSMessage message : a) {
			_jmsMessagesToRemove.remove(message);
		}
//		jmsMessagesToRemove.clear();
		CentralLogger.getInstance().debug(this, "XXXXXXXXXXXXX in removeMessage end, removelist size: " + _jmsMessagesToRemove.size());
		
	}

	public boolean is_isCanceled() {
		return _isCanceled;
	}

	public void set_isCanceled(boolean canceled) {
		_isCanceled = canceled;
	}
	
}
