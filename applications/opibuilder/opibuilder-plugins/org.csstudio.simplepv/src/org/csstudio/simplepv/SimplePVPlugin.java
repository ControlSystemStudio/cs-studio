/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.simplepv;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**The plugin activator of simplepv.
 * @author Xihui Chen
 *
 */
public class SimplePVPlugin implements BundleActivator {

    public static final String PLUGIN_ID = "org.csstudio.simplepv";

    @Override
    public void start(BundleContext context) throws Exception {

    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if(AbstractPVFactory.SIMPLE_PV_THREAD!=null)
            AbstractPVFactory.SIMPLE_PV_THREAD.shutdown();
    }

}
