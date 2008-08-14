package org.csstudio.utility.casnooper;

import org.csstudio.platform.startupservice.IStartupServiceListener;
import org.csstudio.platform.startupservice.StartupServiceEnumerator;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public class CaSnooperTask implements IApplication {
	
	private static SnooperServer snooperServerInstance = null;

	public Object start(IApplicationContext context) throws Exception {
		
		
		System.out.println("Start caSnooper");
		snooperServerInstance = SnooperServer.getInstance();
		
		for (IStartupServiceListener s : StartupServiceEnumerator.getServices()) {
			s.run();
		}
		
		snooperServerInstance.execute();
		
		return null;
	}

	public void stop() {
		snooperServerInstance.destroy();

	}

}
