package org.csstudio.nams.service.messaging.declaration;

import org.csstudio.nams.common.material.AlarmNachricht;

public interface Consumer {
	public void close();
	public boolean isClosed();
	// FIXME einen eigenen Nachrichten Typ da nicht nur Alarmnachrichten rein kommen
	@Deprecated
	public AlarmNachricht recieveMessage();
}
