
/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchroton,
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

package org.csstudio.application.weightrequest;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.remotercp.common.tracker.GenericServiceTracker;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;

public class Activator implements BundleActivator {

    public static final String PLUGIN_ID = "org.csstudio.application.weightrequest";

    private static Activator plugin;

    private static BundleContext bundleContext;

    private GenericServiceTracker<ISessionService> _genericServiceTracker;

    public static Activator getDefault() {
      return plugin;
    }

    public static BundleContext getContext() {
      return bundleContext;
    }

    @Override
    public void start(BundleContext context) throws Exception {
        plugin = this;
        bundleContext = context;
        this._genericServiceTracker =
                new GenericServiceTracker<ISessionService>(context, ISessionService.class);
        this._genericServiceTracker.open();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        bundleContext = null;
        this._genericServiceTracker.close();
    }

    public void addSessionServiceListener(
             IGenericServiceListener<ISessionService> sessionServiceListener) {
        this._genericServiceTracker.addServiceListener(sessionServiceListener);
    }
}