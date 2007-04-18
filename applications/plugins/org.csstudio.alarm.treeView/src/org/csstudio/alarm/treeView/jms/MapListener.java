package org.csstudio.alarm.treeView.jms;

import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.eclipse.swt.widgets.Display;

public class MapListener implements MessageListener {

	IMapMessageNotifier notifier;

	public MapListener(IMapMessageNotifier notifier) {
		super();
		// TODO Auto-generated constructor stub
		this.notifier = notifier;
	}

	public void onMessage(final Message message) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				MapMessage msg;
		
				if (message instanceof MapMessage){
					msg = (MapMessage)message;
					if (notifier!=null){
						notifier.notifyObject(msg);
					}
				}
				
			}
		});
		
	}

}
