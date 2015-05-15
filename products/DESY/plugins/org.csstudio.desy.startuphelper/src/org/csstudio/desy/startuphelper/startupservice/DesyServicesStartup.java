/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.desy.startuphelper.startupservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.csstudio.startup.module.ServicesStartupExtPoint;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;

/**
 * <code>DefaultServicesStartup</code> runs all available {@link ServiceProxy}.
 * The proxies are run according to their priority. High priority proxies are run
 * first and the low priority later. All proxies are run concurrently in the same thread.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 */
public class DesyServicesStartup implements ServicesStartupExtPoint {
    /** {@inheritDoc} */
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
