package org.csstudio.nams.service.messaging.declaration;

import org.csstudio.nams.common.decision.Vorgangsmappe;
import org.csstudio.nams.common.material.SystemNachricht;



public interface Producer {
	public void close();
	public boolean isClosed();
	
	public void sendeVorgangsmappe(Vorgangsmappe vorgangsmappe);
	public void sendeSystemnachricht(SystemNachricht vorgangsmappe);
}
