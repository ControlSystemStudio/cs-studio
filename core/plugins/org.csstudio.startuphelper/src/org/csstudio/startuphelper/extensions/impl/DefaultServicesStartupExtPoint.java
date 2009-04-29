package org.csstudio.startuphelper.extensions.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.csstudio.platform.startupservice.ServiceProxy;
import org.csstudio.platform.startupservice.StartupServiceEnumerator;
import org.csstudio.startuphelper.extensions.ServicesStartupExtPoint;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;

/**
 * 
 * <code>DefaultServicesStartupExtPoint</code> runs all available {@link ServiceProxy}.
 * The proxies are run according to their priority. High priority proxies are run
 * first and the low priority later. All proxies are run cuncurrently in the same thread. 
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class DefaultServicesStartupExtPoint implements ServicesStartupExtPoint {

	/*
	 * (non-Javadoc)
	 * @see org.csstudio.startup.extensions.ServicesStartupExtPoint#startServices(org.eclipse.swt.widgets.Display, org.eclipse.equinox.app.IApplicationContext, java.util.Map)
	 */
	public Object startServices(Display display, IApplicationContext context, Map<String, Object> parameters) throws Exception {
		ServiceProxy[] proxies = StartupServiceEnumerator.getServices();
		List<ServiceProxy> lowPriorityProxy = new ArrayList<ServiceProxy>();
		
		for(ServiceProxy proxy : proxies) {
			if(proxy.isHighPriority()) {
				proxy.run();
			} else {
				lowPriorityProxy.add(proxy);
			}
		}
		
		// TODO: implement this so that each low priority proxy
		// is created and ran in a separate thread
		for(ServiceProxy proxy : lowPriorityProxy) {
			// TODO: add thread creation code here!
			proxy.run();
		}
		return null;
	}
}
