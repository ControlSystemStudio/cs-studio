
/* 
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron, 
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

package org.csstudio.ams.application.monitor;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.remotercp.common.tracker.GenericServiceTracker;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;

public class AmsMonitorActivator implements BundleActivator {

    private static AmsMonitorActivator instance;
    
    private static BundleContext context;
	
    /** The plug-in ID */
    public static final String PLUGIN_ID = "org.csstudio.ams.application.monitor";

    /** Service tracker for the XMPP login */
    private GenericServiceTracker<ISessionService> _genericServiceTracker;

	public static BundleContext getContext() {
		return context;
	}

    public static AmsMonitorActivator getDefault() {
        return instance;
    }

	@Override
    public void start(BundleContext bundleContext) throws Exception {
	    AmsMonitorActivator.instance = this;
	    AmsMonitorActivator.context = bundleContext;
	    _genericServiceTracker = new GenericServiceTracker<ISessionService>(context, ISessionService.class);
	    _genericServiceTracker.open();
	}

	@Override
    public void stop(BundleContext bundleContext) throws Exception {
	    AmsMonitorActivator.instance = null;
		AmsMonitorActivator.context = null;
		_genericServiceTracker.close();
	}

	public void addSessionServiceListener(
	        IGenericServiceListener<ISessionService> sessionServiceListener) {
	    _genericServiceTracker.addServiceListener(sessionServiceListener);
	}
}
