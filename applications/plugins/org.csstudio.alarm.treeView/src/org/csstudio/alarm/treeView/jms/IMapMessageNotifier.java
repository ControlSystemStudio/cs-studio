package org.csstudio.alarm.treeView.jms;

import javax.jms.MapMessage;

//whoever wants to be notified about MapMessage must implement this interface
public abstract interface IMapMessageNotifier {

	public abstract void notifyObject(MapMessage msg);
	
}
