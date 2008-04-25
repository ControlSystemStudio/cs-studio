package org.csstudio.nams.service.messaging.declaration;

import org.csstudio.nams.common.material.AlarmNachricht;

public interface Consumer {
	public void close();
	public boolean isClosed();
	public AlarmNachricht recieveMessage();
	// TODO Irgendwann: public Vorgangsmappe recieveVorgangsmappe();
}
