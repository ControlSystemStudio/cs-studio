package org.csstudio.nams.service.messaging.declaration;

import org.csstudio.nams.common.material.SystemNachricht;


public interface Producer {
	public void close();
	public boolean isClosed();
	
	// TODO Irgendwann:	public void sendeVorgangsmappe(Vorgangsmappe vorgangsmappe);
	public void sendeSystemnachricht(SystemNachricht vorgangsmappe);
}
