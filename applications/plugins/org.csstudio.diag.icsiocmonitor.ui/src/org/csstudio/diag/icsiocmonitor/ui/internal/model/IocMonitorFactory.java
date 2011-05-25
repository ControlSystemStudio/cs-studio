/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.diag.icsiocmonitor.ui.internal.model;

import org.csstudio.diag.icsiocmonitor.ui.internal.Activator;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.remotercp.service.connection.session.ISessionService;

/**
 * Factory class which creates an {@link IocMonitor} and connects it to the
 * necessary support objects. This class basically acts as a bridge which is
 * used by the UI code (the view) to use objects which require OSGi services.
 * 
 * @author Joerg Rathlev
 */
public class IocMonitorFactory {

	private static InterconnectionServerTracker icsTracker;
	private static ServiceTracker sessionServiceTracker;
	private static IocMonitor iocMonitor;

	/**
	 * Creates and starts the IOC monitor.
	 * 
	 * @return the IOC monitor object.
	 */
	public static synchronized IocMonitor createAndStartMonitor() {
		stopAndDisposeIocMonitor();
		iocMonitor = new IocMonitor();
		icsTracker = new InterconnectionServerTracker(iocMonitor);
		
		sessionServiceTracker = new ServiceTracker(
				Activator.getDefault().getBundleContext(),
				ISessionService.class.getName(), null) {
			
			@Override
			public Object addingService(ServiceReference reference) {
				Object service = super.addingService(reference);
				icsTracker.bindService((ISessionService) service);
				return service;
			}
			
			@Override
			public void removedService(ServiceReference reference,
					Object service) {
				icsTracker.unbindService((ISessionService) service);
				super.removedService(reference, service);
			}
		};
		sessionServiceTracker.open();
		return iocMonitor;
	}
	
	/**
	 * Stops the IOC monitor created by this factory.
	 */
	public static synchronized void stopAndDisposeIocMonitor() {
		if (iocMonitor != null) {
			// This also unbinds the service from the icsTracker
			sessionServiceTracker.close();
			
			sessionServiceTracker = null;
			icsTracker = null;
			iocMonitor = null;
		}
	}
}
