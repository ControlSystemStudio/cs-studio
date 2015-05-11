/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.csstudio.simplepv.utilitypv;

import java.util.logging.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author Xihui Chen
 *
 */
public class Activator implements BundleActivator {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.csstudio.simplepv.utilitypv"; //$NON-NLS-1$

    final private static Logger logger = Logger.getLogger(PLUGIN_ID);

    @Override
    public void start(BundleContext context) throws Exception {

    }

    @Override
    public void stop(BundleContext context) throws Exception {

    }

    public static Logger getLogger() {
        return logger;
    }
}
