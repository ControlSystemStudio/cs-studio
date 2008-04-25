package org.csstudio.nams.service.messaging.declaration;

import org.csstudio.nams.common.material.AlarmNachricht;

public interface Producer {
	public void close();
	public boolean isClosed();
	public void sendMessage(AlarmNachricht message);
	// TODO Irgendwann: public void sendVorgangsmappe(Vorgangsmappe vorgangsmappe);
}
